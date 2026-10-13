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
                if(c.name.equals("dev/zomboid/storage/StorageEvents")) {
                    AnnotationNode subscriber=c.visibleAnnotations==null?null:c.visibleAnnotations.stream().filter(a->a.desc.equals("Lnet/neoforged/fml/common/EventBusSubscriber;")).findFirst().orElse(null);
                    if(subscriber==null || !subscriber.values.contains("quietly"))throw new AssertionError("Missing Quietly event subscriber");
                    for(String descriptor:List.of("(Lnet/neoforged/neoforge/event/entity/player/PlayerContainerEvent$Open;)V","(Lnet/neoforged/neoforge/event/level/BlockDropsEvent;)V")) {
                        if(c.methods.stream().noneMatch(m->m.desc.equals(descriptor)&&(m.access&Opcodes.ACC_STATIC)!=0&&m.visibleAnnotations!=null&&m.visibleAnnotations.stream().anyMatch(a->a.desc.equals("Lnet/neoforged/bus/api/SubscribeEvent;"))))throw new AssertionError("Missing supported event callback: "+descriptor);
                    }
                }
            }
        }
        System.out.println("ASM verified "+count+" methods; mixin retention verified: "+args[0]);
    }
}
