import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;
public final class Verify {
    public static void main(String[] args)throws Exception {
        int count=0;
        try(ZipFile z=new ZipFile(args[0])) {
            for(var entries=z.entries();entries.hasMoreElements();) {
                var entry=entries.nextElement();if(!entry.getName().endsWith(".class"))continue;
                ClassNode c=new ClassNode();new ClassReader(z.getInputStream(entry).readAllBytes()).accept(c,0);
                for(MethodNode m:c.methods)if(m.instructions.size()>0) { new Analyzer<>(new BasicVerifier()).analyze(c.name,m);count++; }
                if(c.name.startsWith("cn/kafei/mixin/")) {
                    if(c.visibleAnnotations!=null && c.visibleAnnotations.stream().anyMatch(a->a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")))throw new AssertionError("@Mixin has wrong retention: "+c.name);
                    if(c.invisibleAnnotations==null || c.invisibleAnnotations.stream().noneMatch(a->a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;"))) throw new AssertionError("Missing @Mixin: "+c.name);
                }
            }
        }
        System.out.println("ASM verified "+count+" methods; mixin retention verified: "+args[0]);
    }
}
