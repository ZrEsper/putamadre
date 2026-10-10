import java.nio.file.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
public class Probe implements Opcodes{
 public static void main(String[] a)throws Exception{
  ClassNode src=new ClassNode();try(ZipFile z=new ZipFile(a[0])){new ClassReader(z.getInputStream(z.getEntry("snownee/jade/addon/universal/ItemStorageProvider.class")).readAllBytes()).accept(src,0);}
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);w.visit(V21,ACC_PUBLIC,"GuardProbe",null,"java/lang/Object",null);w.visitField(ACC_PUBLIC|ACC_STATIC,"extensions","I",null,null).visitEnd();MethodVisitor mv=w.visitMethod(ACC_PUBLIC|ACC_STATIC,"run","(Lsnownee/jade/api/Accessor;)V",null,null);mv.visitCode();
  MethodNode m=src.methods.stream().filter(x->x.name.equals("putData")).findFirst().orElseThrow();int returns=0;
  for(AbstractInsnNode i:m.instructions.toArray()){
   i.accept(mv);if(i.getOpcode()==RETURN)returns++;
   if(returns==1&&i instanceof FrameNode)break;
  }
  if(returns!=1)throw new AssertionError("No leading guard");mv.visitFieldInsn(GETSTATIC,"GuardProbe","extensions","I");mv.visitInsn(ICONST_1);mv.visitInsn(IADD);mv.visitFieldInsn(PUTSTATIC,"GuardProbe","extensions","I");mv.visitInsn(RETURN);mv.visitMaxs(0,0);mv.visitEnd();w.visitEnd();Files.createDirectories(Path.of(a[1]));Files.write(Path.of(a[1],"GuardProbe.class"),w.toByteArray());
  ClassNode quiet=new ClassNode();try(ZipFile z=new ZipFile(a[2])){new ClassReader(z.getInputStream(z.getEntry("cn/kafei/interact/Interaction.class")).readAllBytes()).accept(quiet,0);}ClassWriter q=new ClassWriter(0);q.visit(V21,ACC_PUBLIC,"PendingProbe",null,"java/lang/Object",null);quiet.methods.stream().filter(x->x.name.equals("pending")).findFirst().orElseThrow().accept(q);q.visitEnd();Files.write(Path.of(a[1],"PendingProbe.class"),q.toByteArray());
 }
}
