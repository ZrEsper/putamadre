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

            }
        }
        System.out.println("ASM verified "+count+" methods; preserved mixin entries: "+args[0]);
    }
}
