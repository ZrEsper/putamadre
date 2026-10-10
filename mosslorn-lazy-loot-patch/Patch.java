import java.nio.file.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Patch implements Opcodes {
 public static void main(String[] args)throws Exception{
  String entry=args[0].equals("jade")?"snownee/jade/addon/universal/ItemStorageProvider.class":"cn/kafei/interact/Interaction.class";
  try(ZipFile z=new ZipFile(args[1])){
   ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(entry)).readAllBytes()).accept(c,0);int count=0;
   for(MethodNode m:c.methods){
    if(args[0].equals("jade")&&m.name.equals("putData")){
     String lootDesc=null;for(AbstractInsnNode i:m.instructions.toArray())if(i instanceof MethodInsnNode call&&call.name.equals("getLootTable")&&call.owner.equals("net/minecraft/world/RandomizableContainer"))lootDesc=call.desc;
     if(lootDesc==null)throw new IllegalStateException("Loot getter missing");
     InsnList b=new InsnList();LabelNode normal=new LabelNode();String a="snownee/jade/api/Accessor";
     b.add(new VarInsnNode(ALOAD,0));b.add(new MethodInsnNode(INVOKEINTERFACE,a,"getTarget","()Ljava/lang/Object;",true));b.add(new TypeInsnNode(INSTANCEOF,"net/minecraft/world/RandomizableContainer"));b.add(new JumpInsnNode(IFEQ,normal));
     b.add(new VarInsnNode(ALOAD,0));b.add(new MethodInsnNode(INVOKEINTERFACE,a,"getTarget","()Ljava/lang/Object;",true));b.add(new TypeInsnNode(CHECKCAST,"net/minecraft/world/RandomizableContainer"));b.add(new MethodInsnNode(INVOKEINTERFACE,"net/minecraft/world/RandomizableContainer","getLootTable",lootDesc,true));b.add(new JumpInsnNode(IFNULL,normal));
     b.add(new VarInsnNode(ALOAD,0));b.add(new MethodInsnNode(INVOKEINTERFACE,a,"getServerData","()Lnet/minecraft/nbt/CompoundTag;",true));b.add(new LdcInsnNode("Loot"));b.add(new InsnNode(ICONST_1));b.add(new MethodInsnNode(INVOKEVIRTUAL,"net/minecraft/nbt/CompoundTag","putBoolean","(Ljava/lang/String;Z)V",false));b.add(new InsnNode(RETURN));b.add(normal);b.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(b);count++;
    }
    if(!args[0].equals("jade")&&m.name.equals("pending")&&m.desc.equals("(Ljava/lang/Object;)Ljava/lang/String;")){
     m.instructions.clear();m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();m.instructions.add(new LdcInsnNode("Contenido por revisar"));m.instructions.add(new InsnNode(ARETURN));m.maxStack=1;m.maxLocals=1;count++;
    }
   }
   if(count!=1)throw new IllegalStateException("Expected exactly one hook: "+count);
   ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);byte[] bytes=w.toByteArray();ClassNode check=new ClassNode();new ClassReader(bytes).accept(check,0);for(MethodNode m:check.methods)if(m.instructions.size()>0)new Analyzer<>(new BasicVerifier()).analyze(check.name,m);
   try(ZipOutputStream out=new ZipOutputStream(Files.newOutputStream(Path.of(args[2])))){
    var es=z.entries();while(es.hasMoreElements()){var e=es.nextElement();ZipEntry dest=new ZipEntry(e.getName());dest.setTime(1791590400000L);out.putNextEntry(dest);out.write(e.getName().equals(entry)?bytes:z.getInputStream(e).readAllBytes());out.closeEntry();}
   }System.out.println("PASS patch and ASM verification: "+entry);
  }
 }
}
