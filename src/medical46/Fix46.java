import java.nio.file.*;import java.util.*;import java.util.zip.*;import java.io.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Fix46 implements Opcodes {
 public static void main(String[]a)throws Exception{
  Map<String,byte[]> data=new LinkedHashMap<>();try(ZipFile z=new ZipFile(a[0])){for(var it=z.entries();it.hasMoreElements();){var e=it.nextElement();data.put(e.getName(),z.getInputStream(e).readAllBytes());}}
  String name="dev/zomboid/gore/Bullet13.class";ClassNode c=new ClassNode();new ClassReader(data.get(name)).accept(c,0);int fixes=0;
  for(MethodNode m:c.methods)if(m.name.equals("tick"))for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof LdcInsnNode l&&l.cst.equals("scale")){
   // Match the boxed float argument of this particular reflected Vec3.scale call.
   for(AbstractInsnNode p=n.getNext();p!=null;p=p.getNext())if(p instanceof MethodInsnNode call){if(call.owner.equals("java/lang/Float")&&call.name.equals("valueOf")&&call.desc.equals("(F)Ljava/lang/Float;")){m.instructions.insertBefore(p,new InsnNode(F2D));call.owner="java/lang/Double";call.desc="(D)Ljava/lang/Double;";fixes++;}break;}
  }
  if(fixes!=1)throw new AssertionError("Expected exactly one invalid float scale argument, found "+fixes);
  ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);byte[] patched=w.toByteArray();ClassNode check=new ClassNode();new ClassReader(patched).accept(check,0);for(MethodNode m:check.methods)if((m.access&(ACC_ABSTRACT|ACC_NATIVE))==0)new Analyzer<>(new BasicVerifier()).analyze(check.name,m);data.put(name,patched);
  String meta="META-INF/neoforge.mods.toml";data.put(meta,new String(data.get(meta),java.nio.charset.StandardCharsets.UTF_8).replace("version=\"1.0.20.45\"","version=\"1.0.20.46\"").replace("Gore .30","Gore .31").getBytes(java.nio.charset.StandardCharsets.UTF_8));
  try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){for(var e:data.entrySet()){z.putNextEntry(new ZipEntry(e.getKey()));z.write(e.getValue());z.closeEntry();}}
  System.out.println("EXACT_BOXED_FLOAT_TO_DOUBLE_FIXES="+fixes);
 }
}
