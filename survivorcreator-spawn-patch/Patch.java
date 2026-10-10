import java.nio.file.*;import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.util.CheckClassAdapter;
public class Patch implements Opcodes{
 static final String S="dev/survivorcreator/spawn/";
 public static void main(String[] a)throws Exception{
  try(ZipFile z=new ZipFile(a[0])){
   for(String name:new String[]{"client/CreatorScreen","Events","hearing/CreationAudio","client/ClientHooks"}){
    ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry("dev/survivorcreator/"+name+".class")).readAllBytes()).accept(c,0);int changes=0;
    for(MethodNode m:c.methods){
     if(name.equals("client/CreatorScreen")&&m.name.equals("confirm"))for(AbstractInsnNode i:m.instructions.toArray())if(i instanceof MethodInsnNode x&&x.owner.equals("net/neoforged/neoforge/network/PacketDistributor")&&x.name.equals("sendToServer")){x.owner=S+"SpawnScreen";x.name="open";changes++;}
     if(name.equals("Events")&&m.name.equals("handleConfirm")){
      InsnList b=new InsnList();b.add(new VarInsnNode(ALOAD,0));b.add(new VarInsnNode(ALOAD,1));b.add(new MethodInsnNode(INVOKESTATIC,S+"Spawns","filter","(Ljava/lang/Object;Ldev/survivorcreator/net/Net$Confirm;)Ldev/survivorcreator/net/Net$Confirm;",false));b.add(new VarInsnNode(ASTORE,1));LabelNode ok=new LabelNode();b.add(new VarInsnNode(ALOAD,1));b.add(new JumpInsnNode(IFNONNULL,ok));b.add(new InsnNode(RETURN));b.add(ok);b.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(b);changes++;
      for(AbstractInsnNode i:m.instructions.toArray())if(i instanceof MethodInsnNode x&&x.owner.equals("dev/survivorcreator/effects/Effects")&&x.name.equals("firstGrant")){InsnList h=new InsnList();h.add(new VarInsnNode(ALOAD,0));h.add(new MethodInsnNode(INVOKESTATIC,S+"Spawns","place","(Ljava/lang/Object;)V",false));m.instructions.insert(i,h);changes++;}
     }
     if(name.equals("hearing/CreationAudio")&&(m.name.equals("start")||m.name.equals("menu"))){
      InsnList b=new InsnList();LabelNode rest=new LabelNode();b.add(new MethodInsnNode(INVOKESTATIC,S+(m.name.equals("menu")?"SpawnScreen":"EndingAudio"),m.name.equals("menu")?"isMap":"start","()Z",false));b.add(new JumpInsnNode(IFEQ,rest));if(m.name.equals("menu"))b.add(new InsnNode(ICONST_1));b.add(new InsnNode(m.name.equals("menu")?IRETURN:RETURN));b.add(rest);b.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(b);changes++;
     }
     if(name.equals("client/ClientHooks")&&m.name.equals("playEndingMusic")){
      m.instructions.clear();m.tryCatchBlocks.clear();m.localVariables=null;m.instructions.add(new MethodInsnNode(INVOKESTATIC,S+"EndingAudio","track","()Ljava/lang/Object;",false));m.instructions.add(new TypeInsnNode(CHECKCAST,"net/minecraft/client/resources/sounds/SoundInstance"));m.instructions.add(new InsnNode(ARETURN));changes++;
     }
    }
    int expected=name.equals("Events")||name.equals("hearing/CreationAudio")?2:1;if(changes!=expected)throw new IllegalStateException(name+" hooks "+changes);
    ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);new ClassReader(w.toByteArray()).accept(new CheckClassAdapter(new ClassWriter(0)),0);
    Path out=Path.of(a[1],"dev/survivorcreator/"+name+".class");Files.createDirectories(out.getParent());Files.write(out,w.toByteArray());System.out.println("PASS "+name+": "+changes+" hooks");
   }
  }
 }
}
