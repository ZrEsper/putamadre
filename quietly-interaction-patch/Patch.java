import java.nio.file.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.util.CheckClassAdapter;import org.objectweb.asm.tree.analysis.*;
public class Patch implements Opcodes{
 static void cancellable(MethodNode m){for(AnnotationNode a:m.visibleAnnotations)if(a.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;")){if(a.values==null)a.values=new java.util.ArrayList<>();int i=a.values.indexOf("cancellable");if(i>=0)a.values.set(i+1,true);else{a.values.add("cancellable");a.values.add(true);}return;}throw new IllegalStateException("Inject annotation absent");}
 public static void main(String[] args)throws Exception{
  try(ZipFile z=new ZipFile(args[0])){
   for(String name:new String[]{"TransferMenuMixin","TransferHandMixin"}){
    ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry("cn/kafei/mixin/"+name+".class")).readAllBytes()).accept(c,0);int count=0;
    for(MethodNode m:c.methods){
     if(name.equals("TransferMenuMixin")&&m.name.equals("quietly$beforeMove")){
      InsnList b=new InsnList();for(int i=0;i<5;i++)b.add(new VarInsnNode(i==1||i==2?ILOAD:ALOAD,i));b.add(new MethodInsnNode(INVOKESTATIC,"cn/kafei/interact/TransferGuard","blocked","(Ljava/lang/Object;IILjava/lang/Object;Ljava/lang/Object;)Z",false));LabelNode normal=new LabelNode();b.add(new JumpInsnNode(IFEQ,normal));b.add(new VarInsnNode(ALOAD,5));b.add(new MethodInsnNode(INVOKEVIRTUAL,"org/spongepowered/asm/mixin/injection/callback/CallbackInfo","cancel","()V",false));b.add(new InsnNode(RETURN));b.add(normal);b.add(new FrameNode(F_SAME,0,null,0,null));
      AbstractInsnNode reset=null;for(AbstractInsnNode i:m.instructions.toArray())if(i instanceof FieldInsnNode f&&f.getOpcode()==PUTFIELD&&f.name.equals("quietly$before")){reset=i;break;}if(reset==null)throw new IllegalStateException("snapshot reset not found");m.instructions.insert(reset,b);cancellable(m);count++;
     }
     if(name.equals("TransferHandMixin")&&m.name.equals("quietly$beforeDrop")){
      InsnList b=new InsnList();b.add(new VarInsnNode(ALOAD,0));b.add(new MethodInsnNode(INVOKESTATIC,"cn/kafei/interact/TransferGuard","hand","(Ljava/lang/Object;)Z",false));LabelNode normal=new LabelNode();b.add(new JumpInsnNode(IFEQ,normal));b.add(new VarInsnNode(ALOAD,2));b.add(new FieldInsnNode(GETSTATIC,"java/lang/Boolean","FALSE","Ljava/lang/Boolean;"));b.add(new MethodInsnNode(INVOKEVIRTUAL,"org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable","setReturnValue","(Ljava/lang/Object;)V",false));b.add(new InsnNode(RETURN));b.add(normal);b.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(b);cancellable(m);count++;
     }
    }
    if(count!=1)throw new IllegalStateException("guard hook "+name+" "+count);ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode verified=new ClassNode();new ClassReader(w.toByteArray()).accept(new CheckClassAdapter(new ClassWriter(0)),0);new ClassReader(w.toByteArray()).accept(verified,0);for(MethodNode m:verified.methods)if(m.instructions.size()>0)new Analyzer<>(new BasicVerifier()).analyze(verified.name,m);
    Path out=Path.of(args[1],"cn/kafei/mixin/"+name+".class");Files.createDirectories(out.getParent());Files.write(out,w.toByteArray());System.out.println("PASS cancellable pre-move guard: "+name);
   }
  }
 }
}
