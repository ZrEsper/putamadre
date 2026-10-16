import java.util.*;import java.util.zip.*;import java.nio.file.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Patch implements Opcodes{
 static int hooks;
 static byte[] patch(byte[] b)throws Exception{ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);int before=hooks;
  if(c.name.equals("ichttt/mods/firstaid/client/PainVisualEffectsController"))for(MethodNode m:c.methods)if(m.name.equals("updateEffect")){
   InsnList x=new InsnList();LabelNode resume=new LabelNode();x.add(new MethodInsnNode(INVOKESTATIC,"ichttt/mods/firstaid/client/HelmetFilterPriority","defer","()Z",false));x.add(new JumpInsnNode(IFEQ,resume));x.add(new VarInsnNode(ALOAD,0));x.add(new VarInsnNode(ALOAD,1));x.add(new MethodInsnNode(INVOKEVIRTUAL,c.name,"forceShutdown","(Lnet/minecraft/client/Minecraft;)V",false));x.add(new InsnNode(RETURN));x.add(resume);x.add(new FrameNode(F_SAME,0,null,0,null));m.instructions.insert(x);hooks++;
  }
  if(c.name.equals("dev/tactical/tacz/ImpactMarks")){
   for(FieldNode f:c.fields)if(f.name.equals("LIMIT")){if(!Integer.valueOf(2048).equals(f.value))throw new IllegalStateException("limit field");f.value=200;hooks++;}
   for(MethodNode m:c.methods)if(m.name.equals("remember"))for(AbstractInsnNode n:m.instructions.toArray()){
    if(n instanceof IntInsnNode i&&i.operand==2048){i.operand=200;hooks++;}
    if(n instanceof MethodInsnNode i&&i.owner.equals("java/util/Map")&&i.name.equals("put")){i.setOpcode(INVOKESTATIC);i.owner="dev/tactical/tacz/BoundedMarks";i.name="orderedPut";i.desc="(Ljava/util/Map;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;";i.itf=false;hooks++;}
   }
  }
  if(before==hooks)return b;ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);ClassNode check=new ClassNode();new ClassReader(w.toByteArray()).accept(check,0);for(MethodNode m:check.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<BasicValue>(new BasicVerifier()).analyze(check.name,m);return w.toByteArray();
 }
 public static void main(String[]a)throws Exception{Map<String,byte[]> entries=new TreeMap<>();try(ZipFile z=new ZipFile(a[0])){for(ZipEntry e:Collections.list(z.entries())){byte[] b=z.getInputStream(e).readAllBytes();entries.put(e.getName(),e.getName().endsWith(".class")?patch(b):b);}}int expected=Integer.parseInt(a[3]);if(hooks!=expected)throw new IllegalStateException("Expected hooks="+expected+" got="+hooks);try(var files=Files.walk(Path.of(a[2]))){for(Path p:files.filter(p->p.toString().endsWith(".class")).toList())entries.put(Path.of(a[2]).relativize(p).toString(),Files.readAllBytes(p));}try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){for(var e:entries.entrySet()){ZipEntry n=new ZipEntry(e.getKey());n.setTime(0);z.putNextEntry(n);z.write(e.getValue());z.closeEntry();}}System.out.println("Verified hooks="+hooks+" in "+a[1]);}
}
