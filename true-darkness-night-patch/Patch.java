import java.nio.file.*;import java.util.jar.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Patch implements Opcodes {
 public static void main(String[] a)throws Exception {
  String entry="com/hyrrx/hardcoretruedarkness/client/ClientDarknessState$RenderState.class";
  try(JarFile in=new JarFile(a[0]);ZipOutputStream out=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){
   for(var es=in.entries();es.hasMoreElements();){JarEntry e=es.nextElement();byte[] b=in.getInputStream(e).readAllBytes();
    if(e.getName().equals(entry)){
     ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);MethodNode m=c.methods.stream().filter(x->x.name.equals("intensity")&&x.desc.equals("()F")).findFirst().orElseThrow();
     m.instructions.clear();m.tryCatchBlocks.clear();if(m.localVariables!=null)m.localVariables.clear();InsnList h=m.instructions;LabelNode unchanged=new LabelNode();
     h.add(new VarInsnNode(ALOAD,0));h.add(new FieldInsnNode(GETFIELD,c.name,"night","Z"));h.add(new JumpInsnNode(IFEQ,unchanged));
     h.add(new VarInsnNode(ALOAD,0));h.add(new FieldInsnNode(GETFIELD,c.name,"cave","Z"));h.add(new JumpInsnNode(IFNE,unchanged));
     h.add(new VarInsnNode(ALOAD,0));h.add(new FieldInsnNode(GETFIELD,c.name,"intensity","F"));h.add(new LdcInsnNode(0.82f));h.add(new InsnNode(FMUL));h.add(new InsnNode(FRETURN));
     h.add(unchanged);h.add(new VarInsnNode(ALOAD,0));h.add(new FieldInsnNode(GETFIELD,c.name,"intensity","F"));h.add(new InsnNode(FRETURN));
     ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);c.accept(w);b=w.toByteArray();
     ClassNode checked=new ClassNode();new ClassReader(b).accept(checked,0);for(MethodNode n:checked.methods)if((n.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(checked.name,n);
    }
    out.putNextEntry(new ZipEntry(e.getName()));out.write(b);out.closeEntry();
   }
  }
 }
}
