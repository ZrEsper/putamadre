import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Changes verified call sites in the supplied custom mod; preserves its original stack frames. */
public final class PatchWaterPower implements Opcodes {
    private static final String P="dev/zomboid/survival/", M=P+"MachineEntity";
    private static ClassNode read(byte[] b){ClassNode n=new ClassNode();new ClassReader(b).accept(n,0);return n;}
    private static byte[] write(ClassNode n){ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);n.accept(w);return w.toByteArray();}
    private static MethodNode method(ClassNode n,String name){return n.methods.stream().filter(m->m.name.equals(name)).findFirst().orElseThrow();}
    private static InsnList capacity(boolean inner,int subtract) {
        InsnList code=new InsnList();code.add(new VarInsnNode(ALOAD,0));
        if(inner)code.add(new FieldInsnNode(GETFIELD,M+"$1","this$0","L"+M+";"));
        code.add(new MethodInsnNode(INVOKESTATIC,P+"PowerPolicy","capacity","(L"+M+";)I",false));
        if(subtract>0){code.add(new LdcInsnNode(subtract));code.add(new InsnNode(ISUB));}return code;
    }
    public static byte[] machine(byte[] b) {
        ClassNode n=read(b);
        for(String field:new String[]{"zsRawWater","zsCleanWater","zsWaterProgress"})n.fields.add(new FieldNode(ACC_PUBLIC,field,"I",null,null));
        MethodNode powered=new MethodNode(ACC_PUBLIC,"zsPowered","()Z",null,null);
        powered.instructions.add(new VarInsnNode(ALOAD,0));powered.instructions.add(new FieldInsnNode(GETFIELD,M,"powered","Z"));powered.instructions.add(new InsnNode(IRETURN));n.methods.add(powered);
        int capacities=0;
        for(MethodNode method:n.methods)for(AbstractInsnNode instruction:method.instructions.toArray()) {
            if(instruction instanceof LdcInsnNode constant && constant.cst instanceof Integer value
                    && (value==64000 || value==63920 || value==63921)) {
                method.instructions.insertBefore(constant,capacity(false,64000-value));method.instructions.remove(constant);capacities++;
            }
        }
        if(capacities!=4)throw new IllegalStateException("Expected four machine capacity limits, got "+capacities);
        // A dispenser is a fluid tank, not another food-storage inventory.
        InsnList rejectItems=new InsnList();LabelNode normalItems=new LabelNode();
        rejectItems.add(new VarInsnNode(ALOAD,0));rejectItems.add(new MethodInsnNode(INVOKEVIRTUAL,M,"id","()Ljava/lang/String;",false));
        rejectItems.add(new MethodInsnNode(INVOKESTATIC,P+"MachineIds","dispenser","(Ljava/lang/String;)Z",false));
        rejectItems.add(new JumpInsnNode(IFEQ,normalItems));rejectItems.add(new InsnNode(ICONST_0));rejectItems.add(new InsnNode(IRETURN));
        rejectItems.add(normalItems);rejectItems.add(new FrameNode(F_SAME,0,null,0,null));method(n,"canPlaceItem").instructions.insert(rejectItems);
        MethodNode tick=method(n,"tick");
        LocalVariableNode demand=tick.localVariables.stream().filter(v->v.name.equals("demand") && v.desc.equals("I")).findFirst().orElseThrow();
        InsnList lowDemand=new InsnList();lowDemand.add(new VarInsnNode(ALOAD,0));lowDemand.add(new MethodInsnNode(INVOKESTATIC,P+"PowerPolicy","demand","(L"+M+";)I",false));lowDemand.add(new VarInsnNode(ISTORE,demand.index));
        tick.instructions.insert(demand.start,lowDemand);
        AbstractInsnNode lastReturn=null;for(AbstractInsnNode instruction:tick.instructions)if(instruction.getOpcode()==RETURN)lastReturn=instruction;
        InsnList waterTick=new InsnList();waterTick.add(new VarInsnNode(ALOAD,0));waterTick.add(new MethodInsnNode(INVOKESTATIC,P+"WaterGameplay","tick","(L"+M+";)V",false));tick.instructions.insertBefore(lastReturn,waterTick);
        return write(n);
    }
    public static byte[] energy(byte[] b) {
        ClassNode n=read(b);int changed=0;
        for(MethodNode method:n.methods)for(AbstractInsnNode instruction:method.instructions.toArray()) {
            if(instruction instanceof LdcInsnNode constant && Integer.valueOf(64000).equals(constant.cst)) {
                method.instructions.insertBefore(constant,capacity(true,0));method.instructions.remove(constant);changed++;
            }
        }
        if(changed!=2)throw new IllegalStateException("Expected two energy capability limits, got "+changed);return write(n);
    }
    public static byte[] factory(byte[] b) {
        ClassNode n=read(b);int changed=0;
        for(MethodNode method:n.methods)for(AbstractInsnNode instruction:method.instructions.toArray())if(instruction instanceof MethodInsnNode call && call.owner.equals("java/util/Set") && call.name.equals("contains")) {
            call.setOpcode(INVOKESTATIC);call.owner=P+"MachineIds";call.name="contains";call.desc="(Ljava/util/Set;Ljava/lang/Object;)Z";call.itf=false;changed++;
        }
        if(changed!=1)throw new IllegalStateException("Expected one machine factory membership test");return write(n);
    }
    public static byte[] survival(byte[] b) {
        ClassNode n=read(b);int changed=0;
        for(MethodNode method:n.methods)for(AbstractInsnNode instruction:method.instructions.toArray())if(instruction instanceof MethodInsnNode call && call.owner.equals("java/util/Set") && call.name.equals("stream")) {
            call.setOpcode(INVOKESTATIC);call.owner=P+"MachineIds";call.name="registered";call.desc="(Ljava/util/Set;)Ljava/util/stream/Stream;";call.itf=false;changed++;
        }
        if(changed!=1)throw new IllegalStateException("Expected one machine registration stream");return write(n);
    }
    public static byte[] events(byte[] b) {
        ClassNode n=read(b);MethodNode refill=method(n,"refill");refill.instructions.clear();refill.tryCatchBlocks.clear();refill.localVariables.clear();
        refill.instructions.add(new VarInsnNode(ALOAD,0));refill.instructions.add(new MethodInsnNode(INVOKESTATIC,P+"WaterGameplay","refill",refill.desc,false));refill.instructions.add(new InsnNode(RETURN));return write(n);
    }
    private static void nullableGuard(MethodNode method,InsnList code,String resultType) {
        LabelNode original=new LabelNode();code.add(new InsnNode(DUP));code.add(new JumpInsnNode(IFNULL,original));code.add(new InsnNode(ARETURN));code.add(original);
        code.add(new FrameNode(F_SAME1,0,null,1,new Object[]{resultType}));code.add(new InsnNode(POP));method.instructions.insert(code);
    }
    public static byte[] drink(byte[] b) {
        ClassNode n=read(b);InsnList code=new InsnList();code.add(new VarInsnNode(ALOAD,1));code.add(new VarInsnNode(ALOAD,2));code.add(new VarInsnNode(ALOAD,3));
        code.add(new MethodInsnNode(INVOKESTATIC,P+"WaterGameplay","guardDrink","(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;",false));
        nullableGuard(method(n,"use"),code,"net/minecraft/world/InteractionResultHolder");return write(n);
    }
    public static byte[] block(byte[] b) {
        ClassNode n=read(b);InsnList code=new InsnList();code.add(new VarInsnNode(ALOAD,2));code.add(new VarInsnNode(ALOAD,3));code.add(new VarInsnNode(ALOAD,4));
        code.add(new MethodInsnNode(INVOKESTATIC,P+"WaterGameplay","dispenser","(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/InteractionResult;",false));
        nullableGuard(method(n,"useWithoutItem"),code,"net/minecraft/world/InteractionResult");return write(n);
    }
}
