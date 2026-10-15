import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;import java.util.*;import java.nio.file.*;import java.util.zip.*;
public class Patch implements Opcodes{
 static int hooks;
 static byte[] patch(byte[] bytes)throws Exception{
  ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);boolean changed=false;
  if(!c.name.equals("dev/zomboid/hordes/SiegeZombie")&&!c.name.equals("dev/zomboid/hordes/ZombieIndividual"))return bytes;
  for(MethodNode m:c.methods){
   if(c.name.equals("dev/zomboid/hordes/ZombieIndividual")&&m.name.equals("stats")){
    LabelNode other=new LabelNode();InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,0));x.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/hordes/ScreamerStats","apply","(Ljava/lang/Object;)Z",false));x.add(new JumpInsnNode(IFEQ,other));x.add(new InsnNode(RETURN));x.add(other);x.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(x);hooks++;changed=true;
   }
   if(c.name.equals("dev/zomboid/hordes/SiegeZombie")&&m.name.equals("tick"))for(AbstractInsnNode a:m.instructions.toArray())if(a instanceof FieldInsnNode f&&f.name.equals("horizontalCollision")&&f.getOpcode()==GETFIELD){m.instructions.set(a,new MethodInsnNode(INVOKESTATIC,"dev/zomboid/hordes/BreachAI","blocked","(Lnet/minecraft/world/entity/Mob;)Z",false));hooks++;changed=true;}
   if(c.name.equals("dev/zomboid/hordes/SiegeZombie")&&m.name.equals("tick"))for(AbstractInsnNode a:m.instructions.toArray())if(a instanceof MethodInsnNode mc&&mc.owner.equals("dev/zomboid/hordes/GlobalZombie")&&mc.name.equals("apply")){
    AbstractInsnNode load=a.getPrevious();if(!(load instanceof VarInsnNode v)||v.getOpcode()!=ALOAD)throw new IllegalStateException("mob local");InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,v.var));x.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/hordes/ScreamerStats","apply","(Ljava/lang/Object;)Z",false));x.add(new InsnNode(POP));m.instructions.insert(a,x);hooks++;changed=true;
   }
   if(m.name.equals("breakBlock")){
    LabelNode yes=new LabelNode();InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,0));x.add(new VarInsnNode(ALOAD,1));x.add(new MethodInsnNode(INVOKESTATIC,"dev/zomboid/hordes/BreachAI","permitted","(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/server/level/ServerLevel;)Z",false));x.add(new JumpInsnNode(IFNE,yes));for(int i=0;i<3;i++)x.add(new VarInsnNode(ALOAD,i));x.add(new MethodInsnNode(INVOKESTATIC,c.name,"clearCracks","(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/nbt/CompoundTag;)V",false));x.add(new InsnNode(RETURN));x.add(yes);x.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(x);hooks++;changed=true;
   }
  }
  if(!changed)throw new IllegalStateException("missing siege hooks");ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode v=new ClassNode();new ClassReader(w.toByteArray()).accept(v,0);for(MethodNode m:v.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(v.name,m);return w.toByteArray();
 }
 public static void main(String[] args)throws Exception{
  Map<String,byte[]> entries=new TreeMap<>();try(ZipFile z=new ZipFile(args[0])){for(ZipEntry e:Collections.list(z.entries())){byte[] b=z.getInputStream(e).readAllBytes();entries.put(e.getName(),e.getName().endsWith(".class")?patch(b):b);}}
  for(String n:new String[]{"BreachAI","ScreamerStats"})entries.put("dev/zomboid/hordes/"+n+".class",Files.readAllBytes(Path.of(args[2],"dev/zomboid/hordes/"+n+".class")));
  if(hooks!=4)throw new IllegalStateException("hooks: "+hooks);
  try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(args[1])))){for(var e:entries.entrySet()){ZipEntry n=new ZipEntry(e.getKey());n.setTime(0);z.putNextEntry(n);z.write(e.getValue());z.closeEntry();}}
  System.out.println("Verified four siege/screamer hooks and unchanged unrelated classes");
 }
}
