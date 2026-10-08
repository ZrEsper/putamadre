import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Applies a narrowly scoped binary patch to the supplied custom mod, preserving other entries. */
public final class PatchGenerator implements Opcodes {
    private static final String MACHINE = "dev/zomboid/survival/MachineEntity";
    private static final String PREFIX = "dev/zomboid/survival/";
    private static MethodNode method(ClassNode node, String name) {
        return node.methods.stream().filter(m -> m.name.equals(name)).findFirst().orElseThrow();
    }
    private static void beforeReturns(MethodNode method, String helper) {
        for (AbstractInsnNode instruction : method.instructions.toArray()) {
            if (instruction.getOpcode() == RETURN) {
                InsnList code = new InsnList();
                code.add(new VarInsnNode(ALOAD, 0));
                code.add(new VarInsnNode(ALOAD, 1));
                code.add(new MethodInsnNode(INVOKESTATIC, PREFIX + "GeneratorFuel", helper,
                        "(L" + MACHINE + ";Lnet/minecraft/nbt/CompoundTag;)V", false));
                method.instructions.insertBefore(instruction, code);
            }
        }
    }
    private static AbstractInsnNode nextReal(AbstractInsnNode n) {
        do { n=n.getNext(); } while(n!=null && n.getOpcode()<0);
        return n;
    }
    private static void getter(ClassNode node, String methodName, String field) {
        MethodNode method = new MethodNode(ACC_PUBLIC, methodName, "()I", null, null);
        method.instructions.add(new VarInsnNode(ALOAD, 0));
        method.instructions.add(new FieldInsnNode(GETFIELD, MACHINE, field, "I"));
        method.instructions.add(new InsnNode(IRETURN));
        node.methods.add(method);
    }
    private static byte[] patchMachine(byte[] original) {
        ClassNode node = new ClassNode();
        new ClassReader(original).accept(node, 0);
        if (node.fields.stream().anyMatch(f -> f.name.equals("zsFuelCarry"))) throw new IllegalStateException("Already patched");
        node.fields.add(new FieldNode(ACC_PUBLIC, "zsFuelCarry", "I", null, null));
        node.fields.add(new FieldNode(ACC_PUBLIC, "zsFuelTotal", "I", null, null));
        getter(node, "zsGetBurn", "burn");
        getter(node, "zsGetEnergy", "stored");
        MethodNode setter = new MethodNode(ACC_PUBLIC, "zsSetBurn", "(I)V", null, null);
        setter.instructions.add(new VarInsnNode(ALOAD, 0));
        setter.instructions.add(new VarInsnNode(ILOAD, 1));
        setter.instructions.add(new FieldInsnNode(PUTFIELD, MACHINE, "burn", "I"));
        setter.instructions.add(new InsnNode(RETURN));
        node.methods.add(setter);

        MethodNode menu = method(node, "createMenu");
        LabelNode vanilla = new LabelNode();
        InsnList replacement = new InsnList();
        replacement.add(new VarInsnNode(ALOAD, 0));
        replacement.add(new MethodInsnNode(INVOKEVIRTUAL, MACHINE, "generator", "()Z", false));
        replacement.add(new JumpInsnNode(IFEQ, vanilla));
        replacement.add(new TypeInsnNode(NEW, PREFIX + "GeneratorMenu"));
        replacement.add(new InsnNode(DUP));
        replacement.add(new VarInsnNode(ILOAD, 1));
        replacement.add(new VarInsnNode(ALOAD, 2));
        replacement.add(new VarInsnNode(ALOAD, 0));
        replacement.add(new TypeInsnNode(NEW, PREFIX + "GeneratorData"));
        replacement.add(new InsnNode(DUP));
        replacement.add(new VarInsnNode(ALOAD, 0));
        replacement.add(new MethodInsnNode(INVOKESPECIAL, PREFIX + "GeneratorData", "<init>", "(L" + MACHINE + ";)V", false));
        replacement.add(new MethodInsnNode(INVOKESPECIAL, PREFIX + "GeneratorMenu", "<init>",
                "(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;)V", false));
        replacement.add(new InsnNode(ARETURN));
        replacement.add(vanilla);
        replacement.add(new FrameNode(F_SAME, 0, null, 0, null));
        menu.instructions.insert(replacement);
        beforeReturns(method(node, "saveAdditional"), "save");
        beforeReturns(method(node, "loadAdditional"), "load");

        int range = 0, fuel = 0, pause = 0, refuel = 0, effects = 0, ambient = 0;
        MethodNode tick = method(node, "tick");
        for (AbstractInsnNode instruction : tick.instructions.toArray()) {
            if (instruction instanceof FieldInsnNode field && field.getOpcode() == PUTFIELD && field.name.equals("stored")
                    && field.getPrevious() instanceof MethodInsnNode call && call.owner.equals("java/lang/Math") && call.name.equals("min")) {
                int clockIndex = tick.localVariables.stream().filter(v -> v.name.equals("now") && v.desc.equals("J")).findFirst().orElseThrow().index;
                InsnList effect = new InsnList();
                effect.add(new VarInsnNode(ALOAD, 0));
                effect.add(new FieldInsnNode(GETFIELD, MACHINE, "level", "Lnet/minecraft/world/level/Level;"));
                effect.add(new VarInsnNode(ALOAD, 0));
                effect.add(new FieldInsnNode(GETFIELD, MACHINE, "worldPosition", "Lnet/minecraft/core/BlockPos;"));
                effect.add(new VarInsnNode(LLOAD, clockIndex));
                effect.add(new MethodInsnNode(INVOKESTATIC, PREFIX + "GeneratorEffects", "tick", "(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;J)V", false));
                tick.instructions.insert(field, effect); effects++;
            }
            // Keep the appliance ambience, suppress the old blast-furnace sound for generators.
            if (instruction instanceof LdcInsnNode constant && Long.valueOf(80L).equals(constant.cst)) {
                AbstractInsnNode jumpInstruction = nextReal(nextReal(nextReal(nextReal(constant))));
                if (jumpInstruction instanceof JumpInsnNode jump && jump.getOpcode() == IFNE) {
                    InsnList onlyAppliances = new InsnList();
                    onlyAppliances.add(new VarInsnNode(ALOAD, 0));
                    onlyAppliances.add(new MethodInsnNode(INVOKEVIRTUAL, MACHINE, "generator", "()Z", false));
                    onlyAppliances.add(new JumpInsnNode(IFNE, jump.label));
                    tick.instructions.insert(jump, onlyAppliances); ambient++;
                }
            }

            if (instruction instanceof LdcInsnNode constant && Double.valueOf(144.0).equals(constant.cst)) {
                constant.cst = 625.0; range++;
            }
            if (instruction instanceof LdcInsnNode constant && Integer.valueOf(64000).equals(constant.cst)
                    && constant.getPrevious() instanceof FieldInsnNode field && field.name.equals("stored")
                    && constant.getNext().getOpcode() == IF_ICMPGE) {
                constant.cst = 63921; refuel++;
            }
            if (instruction instanceof FieldInsnNode field && field.getOpcode() == PUTFIELD && field.name.equals("burn")
                    && field.getPrevious() instanceof VarInsnNode local && local.getOpcode() == ILOAD) {
                // Original stack: [this, fuel]. Add the helper's machine argument before fuel.
                tick.instructions.insertBefore(local, new VarInsnNode(ALOAD, 0));
                tick.instructions.insertBefore(field, new MethodInsnNode(INVOKESTATIC, PREFIX + "GeneratorFuel", "consume", "(L" + MACHINE + ";I)I", false));
                fuel++;
            }
            if (instruction instanceof JumpInsnNode jump && jump.getOpcode() == IFLE
                    && jump.getPrevious() instanceof FieldInsnNode field && field.name.equals("burn")
                    && nextReal(jump) instanceof VarInsnNode
                    && nextReal(nextReal(jump)).getOpcode() == DUP) {
                InsnList guard = new InsnList();
                guard.add(new VarInsnNode(ALOAD, 0));
                guard.add(new FieldInsnNode(GETFIELD, MACHINE, "stored", "I"));
                guard.add(new LdcInsnNode(63920));
                guard.add(new JumpInsnNode(IF_ICMPGT, jump.label));
                tick.instructions.insert(jump, guard); pause++;
            }
        }
        if (range != 1 || fuel != 1 || pause != 1 || refuel != 1 || effects != 1 || ambient != 1) {
            throw new IllegalStateException("Unexpected binary: range="+range+", fuel="+fuel+", pause="+pause+", refuel="+refuel+", effects="+effects+", ambient="+ambient);
        }
        updateText(node);
        // Retain the original frames; new branches target known frames and menu has an explicit frame.
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
    private static void updateText(ClassNode node) {
        for (MethodNode method : node.methods) for (AbstractInsnNode instruction : method.instructions) {
            if (instruction instanceof LdcInsnNode constant && constant.cst instanceof String text) {
                constant.cst = text.replace("12 bloques", "25 bloques").replace("Combustible de horno → 80 FE/t · alcance 25 bloques", "64 carbón: 30 min activos · 80 FE/t · alcance 25 bloques");
            }
            if (instruction instanceof InvokeDynamicInsnNode dynamic) {
                for (int i=0;i<dynamic.bsmArgs.length;i++) if (dynamic.bsmArgs[i] instanceof String text) {
                    dynamic.bsmArgs[i] = text.replace("12 bloques", "25 bloques").replace("Combustible de horno → 80 FE/t · alcance 25 bloques", "64 carbón: 30 min activos · 80 FE/t · alcance 25 bloques");
                }
            }
        }
    }
    private static byte[] patchClient(byte[] original) {
        ClassNode node=new ClassNode(); new ClassReader(original).accept(node,0);
        if (node.methods.stream().anyMatch(m->m.name.equals("opening"))) throw new IllegalStateException("Use the First Aid UI restored base JAR");
        updateText(node);
        ClassWriter writer=new ClassWriter(0); node.accept(writer);return writer.toByteArray();
    }
    public static void main(String[] args) throws Exception {
        if(args.length!=3) throw new IllegalArgumentException("base.jar classes-directory output.jar");
        Path classes=Path.of(args[1]);
        try(ZipFile original=new ZipFile(args[0]);ZipOutputStream output=new ZipOutputStream(Files.newOutputStream(Path.of(args[2])))) {
            Enumeration<? extends ZipEntry> entries=original.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry=entries.nextElement();byte[] data=original.getInputStream(entry).readAllBytes();
                if(entry.getName().equals(MACHINE+".class")) data=PatchWaterPower.machine(patchMachine(data));
                if(entry.getName().equals(PREFIX+"Client.class")) data=patchClient(data);
                if(entry.getName().equals(PREFIX+"MachineEntity$1.class")) data=PatchWaterPower.energy(data);
                if(entry.getName().equals(PREFIX+"MachineBlock.class")) data=PatchWaterPower.block(data);
                if(entry.getName().equals(PREFIX+"Survival.class")) data=PatchWaterPower.survival(data);
                if(entry.getName().equals(PREFIX+"Events.class")) data=PatchWaterPower.events(data);
                if(entry.getName().equals(PREFIX+"DrinkItem.class")) data=PatchWaterPower.drink(data);
                if(entry.getName().equals(PREFIX+"mixin/FactoryMixin.class")) data=PatchWaterPower.factory(data);
                if(entry.getName().equals("META-INF/neoforge.mods.toml")) {
                    data=new String(data,java.nio.charset.StandardCharsets.UTF_8).replace("version=\"1.0.0\"","version=\"1.0.3\"").getBytes(java.nio.charset.StandardCharsets.UTF_8);
                }
                ZipEntry copy=new ZipEntry(entry.getName());copy.setTime(entry.getTime());output.putNextEntry(copy);output.write(data);output.closeEntry();
            }
            try(var files=Files.walk(classes.resolve(PREFIX))) {
                for(Path file:files.filter(p->(p.getFileName().toString().startsWith("PowerPolicy") || p.getFileName().toString().startsWith("MachineIds") || p.getFileName().toString().startsWith("WaterGameplay") || p.getFileName().toString().startsWith("WaterItems") || p.getFileName().toString().startsWith("Generator") || p.getFileName().toString().startsWith("FoodStacking") || p.getFileName().toString().startsWith("FoodMenuMergeMixin") || p.getFileName().toString().startsWith("FoodSlotMergeMixin") || p.getFileName().toString().startsWith("FoodInventoryMergeMixin") || p.getFileName().toString().startsWith("FoodDroppedMergeMixin")) && p.toString().endsWith(".class")).sorted().toList()) {
                    ZipEntry entry=new ZipEntry(classes.relativize(file).toString().replace('\\','/'));entry.setTime(0);output.putNextEntry(entry);output.write(Files.readAllBytes(file));output.closeEntry();
                }
            }
        }
        System.out.println("Patched generator UI, range (25 blocks), fuel duration, persistence and full-buffer pause.");
    }
}
