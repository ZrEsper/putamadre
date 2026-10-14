import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import java.nio.file.*;import java.util.*;import java.util.zip.*;
/** Executes selected exact patched methods against small API doubles, never ships them. */
public class Fixture implements Opcodes{
 public static void main(String[] args)throws Exception{
  try(ZipFile zip=new ZipFile(args[0])){
   for(String[] spec:new String[][]{{"dev/zomboid/hordes/ZombieIndividual","FixtureZI","stat"},{"dev/zomboid/reactive/Reactive","FixtureSpeed","speed","cap"}}){
    ClassNode c=new ClassNode();new ClassReader(zip.getInputStream(zip.getEntry(spec[0]+".class")).readAllBytes()).accept(c,0);
    c.name=spec[1];c.superName="java/lang/Object";c.interfaces.clear();c.fields.clear();c.visibleAnnotations=null;c.invisibleAnnotations=null;c.innerClasses.clear();c.nestHostClass=null;c.nestMembers=null;
    Set<String> names=new HashSet<>(Arrays.asList(spec).subList(2,spec.length));c.methods.removeIf(m->!names.contains(m.name));
    for(MethodNode m:c.methods){m.access=ACC_PUBLIC|ACC_STATIC;m.visibleAnnotations=null;m.invisibleAnnotations=null;
     for(AbstractInsnNode n:m.instructions.toArray())if(n instanceof MethodInsnNode mc){
      if(mc.owner.equals(spec[0]))mc.owner=spec[1];
      if(mc.owner.equals("dev/zomboid/hordes/GlobalZombie")||mc.owner.equals("dev/zomboid/hordes/SiegeZombie"))mc.owner="TestPolicy";
     }
    }
    ClassWriter w=new ClassWriter(0);c.accept(w);Files.write(Path.of(args[1],spec[1]+".class"),w.toByteArray());
   }
  }
 }
}
