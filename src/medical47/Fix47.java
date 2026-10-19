import java.nio.file.*;import java.util.*;import java.util.zip.*;import java.io.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Fix47 {
 public static void main(String[]a)throws Exception{
  Map<String,byte[]>data=new LinkedHashMap<>();try(ZipFile z=new ZipFile(a[0])){for(var it=z.entries();it.hasMoreElements();){var e=it.nextElement();data.put(e.getName(),z.getInputStream(e).readAllBytes());}}
  String entry="dev/zomboid/firstaid/Wounds45.class";ClassNode c=new ClassNode();new ClassReader(data.get(entry)).accept(c,0);int n=0;
  for(MethodNode m:c.methods)for(AbstractInsnNode x:m.instructions)if(x instanceof LdcInsnNode l&&l.cst.equals("ichttt.mods.firstaid.survival.TreatmentV33")){l.cst="ichttt.mods.firstaid.compat.lrtactical.TreatmentV33";n++;}else if(x instanceof LdcInsnNode l&&l.cst.equals("ru.ranazy.tacz_firstaid_compat.compat.firstaid.BodypartSlot")){l.cst="ru.ranazy.tacz_firstaid_compat.compat.firstaid.BodypartHitbox$BodypartSlot";n++;}
  if(n!=2)throw new AssertionError("expected two invalid class references, got "+n);
  ClassWriter w=new ClassWriter(0);c.accept(w);data.put(entry,w.toByteArray());for(MethodNode m:c.methods)if((m.access&(Opcodes.ACC_ABSTRACT|Opcodes.ACC_NATIVE))==0)new Analyzer<>(new BasicVerifier()).analyze(c.name,m);
  String meta="META-INF/neoforge.mods.toml";data.put(meta,new String(data.get(meta),java.nio.charset.StandardCharsets.UTF_8).replace("v3.6.15-procedural-visible-wounds","v3.6.16-bite-class-fix").replace("v3.6.15 · Heridas visibles","v3.6.16 · Heridas visibles").getBytes(java.nio.charset.StandardCharsets.UTF_8));
  try(ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(Path.of(a[1])))){for(var e:data.entrySet()){z.putNextEntry(new ZipEntry(e.getKey()));z.write(e.getValue());z.closeEntry();}}System.out.println("CORRECTED_CLASS_REFERENCES="+n);
 }
}
