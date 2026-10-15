import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;import java.util.*;import java.util.zip.*;import java.nio.file.*;
public class Patch implements Opcodes{
 static int hooks;static byte[] patch(byte[] b)throws Exception{ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int before=hooks;
 for(MethodNode m:c.methods){
  if(c.name.equals("dev/zomboid/mutants/Screamers")&&m.name.equals("attributes"))for(AbstractInsnNode a:m.instructions)if(a instanceof LdcInsnNode l){if(Double.valueOf(.48).equals(l.cst)){l.cst=.50;hooks++;}else if(Double.valueOf(48).equals(l.cst)){l.cst=40.0;hooks++;}}
  if(c.name.equals("dev/zomboid/mutants/Screamers$Screamer")&&m.name.equals("tick"))for(AbstractInsnNode a:m.instructions.toArray()){
   if(a instanceof LdcInsnNode l&&Double.valueOf(1.35).equals(l.cst)){l.cst=1.50;hooks++;}
   if(a instanceof LdcInsnNode l&&Double.valueOf(9).equals(l.cst)){l.cst=1.44;hooks++;}
   if(a instanceof MethodInsnNode mc&&mc.owner.equals("dev/zomboid/mutants/HordeScent")&&mc.name.equals("alert")){
    AbstractInsnNode radius=a.getPrevious(),target=radius.getPrevious();if(!(target instanceof VarInsnNode v)||v.getOpcode()!=ALOAD||!(radius instanceof LdcInsnNode))throw new IllegalStateException("rally pattern");v.var=0;mc.owner="dev/zomboid/mutants/horror/Rally";mc.desc="(Ljava/lang/Object;Ljava/lang/Object;D)V";hooks++;
   }
  }
 }
 if(before==hooks)return b;ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode v=new ClassNode();new ClassReader(w.toByteArray()).accept(v,0);for(MethodNode m:v.methods)if((m.access&(ACC_NATIVE|ACC_ABSTRACT))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(v.name,m);return w.toByteArray();}
 public static void main(String[]a)throws Exception{Map<String,byte[]>out=new TreeMap<>();try(ZipFile z=new ZipFile(a[0])){for(ZipEntry e:Collections.list(z.entries())){byte[]b=z.getInputStream(e).readAllBytes();out.put(e.getName(),e.getName().endsWith(".class")?patch(b):b);}}
 out.put("dev/zomboid/mutants/horror/Rally.class",Files.readAllBytes(Path.of(a[2],"dev/zomboid/mutants/horror/Rally.class")));if(hooks!=5)throw new IllegalStateException("hooks "+hooks);try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){for(var e:out.entrySet()){ZipEntry n=new ZipEntry(e.getKey());n.setTime(0);z.putNextEntry(n);z.write(e.getValue());z.closeEntry();}}System.out.println("Verified all five sprint, health, proximity and positional-rally edits");}
}
