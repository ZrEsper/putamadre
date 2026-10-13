import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

public final class Patch implements Opcodes {
    static int hooks;
    static final String STORE="dev/zomboid/storage/", KEEP="dev/zomboid/scavenging/KeepLoot", NPC="dev/zomboid/remains/delivery/NpcCorpses";
    static MethodInsnNode call(String owner,String name,String desc) { return new MethodInsnNode(INVOKESTATIC,owner,name,desc,false); }
    static void clear(MethodNode method) { method.instructions.clear(); method.tryCatchBlocks.clear(); if(method.localVariables!=null) method.localVariables.clear(); method.visibleLocalVariableAnnotations=null; method.invisibleLocalVariableAnnotations=null; }
    static MethodNode delegate(ClassNode owner,String name,String target,String targetName,String desc) {
        MethodNode original=owner.methods.stream().filter(m->m.name.equals(name)&&m.desc.equals(desc)).findFirst().orElseThrow();
        MethodNode method=new MethodNode(original.access,name,desc,null,null);
        int slot=(original.access&ACC_STATIC)==0?1:0;
        for(Type arg:Type.getArgumentTypes(desc)) { method.instructions.add(new VarInsnNode(arg.getOpcode(ILOAD),slot)); slot+=arg.getSize(); }
        method.instructions.add(call(target,targetName,desc));
        method.instructions.add(new InsnNode(Type.getReturnType(desc).getOpcode(IRETURN)));
        original.name=name+"Original14"; owner.methods.add(method); hooks++; return method;
    }
    static void patch(ClassNode c,String kind) {
        if(kind.equals("quietly")) {
            if(c.name.equals("dev/zomboid/scavenging/Loot")) {
                delegate(c,"inventory",KEEP,"inventory","(Ldev/zomboid/scavenging/Search$Target;)Ldev/zomboid/scavenging/Loot$Inv;");
                delegate(c,"insert",KEEP,"insert","(Ldev/zomboid/scavenging/Loot$Inv;Ljava/lang/Object;Ldev/zomboid/scavenging/Search$Target;Ljava/lang/Object;)V");
                for(MethodNode m:c.methods) {
                    if(m.name.equals("bonus")) for(AbstractInsnNode n:m.instructions.toArray()) if(n.getOpcode()==RETURN) {
                        InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(new VarInsnNode(ALOAD,1));h.add(call(KEEP,"reopen","(Ldev/zomboid/scavenging/Search$Target;Ljava/lang/Object;)V"));m.instructions.insertBefore(n,h);hooks++;
                    }
                    if(m.name.equals("upgrade")) for(AbstractInsnNode n:m.instructions.toArray()) if(n instanceof MethodInsnNode invoke && invoke.owner.equals(c.name)&&invoke.name.equals("inventory")) invoke.name="inventoryOriginal14";
                    if(m.name.equals("upgrade")) {
                        InsnList h=new InsnList();LabelNode next=new LabelNode();h.add(new VarInsnNode(ALOAD,0));h.add(new FieldInsnNode(GETFIELD,"dev/zomboid/scavenging/Search$Job","target","Ldev/zomboid/scavenging/Search$Target;"));h.add(call(KEEP,"expanded","(Ldev/zomboid/scavenging/Search$Target;)Z"));h.add(new JumpInsnNode(IFEQ,next));h.add(new InsnNode(ICONST_0));h.add(new InsnNode(IRETURN));h.add(next);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);hooks++;
                    }
                }
            }
            if(c.name.equals("cn/kafei/interact/Interaction")) for(MethodNode m:c.methods) if(m.name.equals("supported")) {
                InsnList h=new InsnList();LabelNode next=new LabelNode();h.add(new VarInsnNode(ALOAD,0));h.add(call(STORE+"CorpseOutline","block","(Ljava/lang/Object;)Z"));h.add(new JumpInsnNode(IFEQ,next));h.add(new InsnNode(ICONST_1));h.add(new InsnNode(IRETURN));h.add(next);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);hooks++;
            }
        }
        if(kind.equals("iso") && c.name.equals("dev/zomboid/isotacz/Bridge")) for(MethodNode m:c.methods) if(m.name.equals("selectBreakTarget")) { m.instructions.insert(call("dev/zomboid/isotacz/BodyCursor","select","()V"));hooks++; }
        if(kind.equals("npc") && c.name.equals("mod/cdv/tnpc/tactical/SurvivorPatch")) delegate(c,"death","mod/cdv/tnpc/tactical/CorpseDelivery","prepare","(Ljava/lang/Object;)V");
        if(kind.equals("remains")) {
            if(c.name.equals("dev/zomboid/remains/ZombieRemainsLoot")) for(MethodNode m:c.methods) {
                if(m.name.equals("add")) {
                    clear(m);m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(new VarInsnNode(ALOAD,1));m.instructions.add(call(NPC,"add","(Ljava/lang/Object;Ljava/lang/Object;)V"));m.instructions.add(new InsnNode(RETURN));hooks++;
                }
                if(m.name.equals("death")) {
                    InsnList h=new InsnList();LabelNode next=new LabelNode();h.add(new VarInsnNode(ALOAD,0));h.add(call(NPC,"death","(Ljava/lang/Object;)Z"));h.add(new JumpInsnNode(IFEQ,next));h.add(new InsnNode(RETURN));h.add(next);h.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(h);hooks++;
                }
                if(m.name.equals("cleanup")) { InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(call(NPC,"tick","(Ljava/lang/Object;)V"));m.instructions.insert(h);hooks++; }
            }
            if(c.name.equals("dev/zomboid/remains/ZombieRemainsLoot$CorpseData")) {
                c.fields.add(new FieldNode(ACC_PUBLIC, "storage14$pending", "Ljava/lang/Object;", null,null));
                for(MethodNode m:c.methods) {
                    if(m.name.equals("load")) for(AbstractInsnNode n:m.instructions.toArray()) if(n.getOpcode()==ARETURN) {
                        InsnList h=new InsnList();h.add(new InsnNode(DUP));h.add(new VarInsnNode(ALOAD,0));h.add(call(NPC,"load","(Ljava/lang/Object;Ljava/lang/Object;)V"));m.instructions.insertBefore(n,h);hooks++;
                    }
                    if(m.name.equals("save")) for(AbstractInsnNode n:m.instructions.toArray()) if(n.getOpcode()==ARETURN) {
                        InsnList h=new InsnList();h.add(new InsnNode(DUP));h.add(new VarInsnNode(ALOAD,0));h.add(call(NPC,"save","(Ljava/lang/Object;Ljava/lang/Object;)V"));m.instructions.insertBefore(n,h);hooks++;
                    }
                }
            }
        }
    }
    public static void main(String[] args)throws Exception {
        String kind=args[0];Path out=Path.of(args[2]);Files.createDirectories(out.getParent());
        int checked=0;
        try(ZipFile z=new ZipFile(args[1]);ZipOutputStream w=new ZipOutputStream(Files.newOutputStream(out))) {
            for(var entries=z.entries();entries.hasMoreElements();) {
                ZipEntry entry=entries.nextElement();byte[] bytes=z.getInputStream(entry).readAllBytes();
                if(entry.getName().endsWith(".class")) {
                    ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);int before=hooks;patch(c,kind);
                    if(hooks!=before) {
                        ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(writer);bytes=writer.toByteArray();
                        ClassNode check=new ClassNode();new ClassReader(bytes).accept(check,0);
                        for(MethodNode m:check.methods) if(m.instructions.size()>0) { new Analyzer<>(new BasicVerifier()).analyze(check.name,m);checked++; }
                        System.out.println("Patched "+c.name);
                    }
                }
                w.putNextEntry(new ZipEntry(entry.getName()));w.write(bytes);w.closeEntry();
            }
        }
        int expected=switch(kind) { case "quietly","remains"->5; default->1; };
        if(hooks!=expected) throw new IllegalStateException("Expected "+expected+" hooks, got "+hooks);
        System.out.println("Verified "+checked+" original methods, "+hooks+" hooks");
    }
}
