import java.nio.file.*;import java.util.*;import java.util.zip.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;
/** Keeps the exact patched input-method bytecode; unrelated rendering methods are not loaded in API-double tests. */
public class Fixtures {
 public static void main(String[]a)throws Exception {
  Path root=Path.of(a[2]);Files.createDirectories(root);
  Map<String,Set<String>> keep=a[0].equals("tacz")?Map.of("com/tacz/guns/client/event/ClientPreventGunClick",Set.of("onClickInput"),"com/tacz/guns/client/input/AimKey",Set.of("<clinit>","onAimPress","onAimHoldingPreInput")):Map.of("me/xjqsh/lrtactical/client/ClientEventsHandler",Set.of("onClickInput"),"me/xjqsh/lrtactical/client/input/AttackKeys",Set.of("<clinit>","onSpAttack"));
  try(ZipFile z=new ZipFile(a[1])){for(var entry:keep.entrySet()){
   ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(entry.getKey()+".class")).readAllBytes()).accept(c,0);
   c.methods.removeIf(m->!entry.getValue().contains(m.name));
   if(!entry.getValue().contains("<clinit>"))c.fields.clear();
   ClassWriter w=new ClassWriter(0);c.accept(w);Path output=root.resolve(c.name+".class");Files.createDirectories(output.getParent());Files.write(output,w.toByteArray());
  }}
 }
}
