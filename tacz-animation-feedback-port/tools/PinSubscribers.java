import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import java.util.*;
import java.nio.file.*;
import java.util.zip.*;
public class PinSubscribers {
 public static void main(String[] args)throws Exception{
  int patched=0;Path target=Path.of(args[1]);
  try(ZipFile input=new ZipFile(args[0]);ZipOutputStream out=new ZipOutputStream(Files.newOutputStream(target))){
   var entries=input.entries();while(entries.hasMoreElements()){
    var e=entries.nextElement();byte[] bytes=input.getInputStream(e).readAllBytes();boolean changed=false;
    if(e.getName().startsWith("com/tacz/")&&e.getName().endsWith(".class")){
     ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);
     for(var list:Arrays.asList(c.visibleAnnotations,c.invisibleAnnotations))if(list!=null)for(AnnotationNode a:list)if(a.desc.equals("Lnet/neoforged/fml/common/EventBusSubscriber;")){
      if(a.values==null)a.values=new ArrayList<>();boolean has=false;for(int i=0;i<a.values.size();i+=2)if(a.values.get(i).equals("modid"))has=true;
      if(!has){a.values.add("modid");a.values.add("tacz");changed=true;}
     }
     if(changed){ClassWriter w=new ClassWriter(0);c.accept(w);bytes=w.toByteArray();patched++;System.out.println("Pinned subscriber "+e.getName());}
    }
    ZipEntry n=new ZipEntry(e.getName());n.setTime(315532800000L);out.putNextEntry(n);out.write(bytes);out.closeEntry();
   }
  }System.out.println("Pinned "+patched+" TACZ subscribers to prevent duplicate registration in merged JAR");
 }
}
