import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;import java.nio.file.*;import java.util.*;import java.util.zip.*;
public class Patch implements Opcodes{
 static int hooks;static final String P="dev/tactical/tacz/";
 static byte[] patch(byte[]b)throws Exception{ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int before=hooks;
 for(MethodNode m:c.methods){
 if(c.name.equals(P+"ImpactMarks$Mark")&&m.name.equals("<init>"))for(AbstractInsnNode n:m.instructions.toArray())if(n.getOpcode()==RETURN){InsnList x=new InsnList();x.add(new VarInsnNode(ALOAD,0));x.add(new MethodInsnNode(INVOKESTATIC,P+"TimedMarks","stamp","(Ljava/lang/Object;)V",false));m.instructions.insertBefore(n,x);hooks++;}
 if(c.name.equals(P+"ImpactMarks")&&m.name.equals("tick")){m.instructions.insert(new MethodInsnNode(INVOKESTATIC,P+"TimedMarks","tick","()V",false));hooks++;}
 if(c.name.equals(P+"ImpactMarks")&&m.name.equals("configure")){m.instructions.clear();m.tryCatchBlocks.clear();m.localVariables=null;m.instructions.add(new VarInsnNode(ALOAD,0));m.instructions.add(new MethodInsnNode(INVOKESTATIC,P+"TimedMarks","configure","(Ljava/lang/Object;)V",false));m.instructions.add(new InsnNode(RETURN));hooks++;}
 }
 if(before==hooks)return b;ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode check=new ClassNode();new ClassReader(w.toByteArray()).accept(check,0);for(MethodNode m:check.methods)if((m.access&(ACC_NATIVE|ACC_ABSTRACT))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(check.name,m);return w.toByteArray();}
 public static void main(String[]a)throws Exception{Map<String,byte[]>entries=new TreeMap<>();try(ZipFile z=new ZipFile(a[0])){for(ZipEntry e:Collections.list(z.entries())){byte[]b=z.getInputStream(e).readAllBytes();entries.put(e.getName(),e.getName().endsWith(".class")?patch(b):b);}}if(hooks!=3)throw new IllegalStateException("hooks "+hooks);try(var files=Files.walk(Path.of(a[2]))){for(Path p:files.filter(p->p.toString().endsWith(".class")).toList())entries.put(Path.of(a[2]).relativize(p).toString(),Files.readAllBytes(p));}try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){for(var e:entries.entrySet()){ZipEntry n=new ZipEntry(e.getKey());n.setTime(0);z.putNextEntry(n);z.write(e.getValue());z.closeEntry();}}System.out.println("Three expiry hooks verified");}
}
