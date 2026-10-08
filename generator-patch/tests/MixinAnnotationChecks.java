import java.util.List;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

/** Mixin 0.8.7 reads @Mixin from RuntimeInvisibleAnnotations (RetentionPolicy.CLASS). */
public final class MixinAnnotationChecks {
    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;";
    private static final String REDIRECT = "Lorg/spongepowered/asm/mixin/injection/Redirect;";
    private static boolean contains(List<AnnotationNode> annotations,String name) {
        return annotations != null && annotations.stream().anyMatch(a -> a.desc.equals(name));
    }
    public static void main(String[] args) throws Exception {
        int count=0,handlers=0;
        try(ZipFile jar=new ZipFile(args[0])) {
            for(String name:new String[]{"FactoryMixin","FurnaceMixin","ItemEntityMixin","StoveMixin",
                    "FoodMenuMergeMixin","FoodSlotMergeMixin","FoodInventoryMergeMixin","FoodDroppedMergeMixin"}) {
                ClassNode node=new ClassNode();
                var entry=jar.getEntry("dev/zomboid/survival/mixin/"+name+".class");
                if(entry==null) throw new AssertionError("Missing mixin class: "+name);
                new ClassReader(jar.getInputStream(entry).readAllBytes()).accept(node,0);
                if(!contains(node.invisibleAnnotations,MIXIN) || contains(node.visibleAnnotations,MIXIN)) {
                    throw new AssertionError(name+": @Mixin must be a runtime-invisible class annotation, as expected by Mixin 0.8.7");
                }
                count++;
                if(name.startsWith("Food")) for(MethodNode method:node.methods) {
                    if(method.name.startsWith("survival$")) {
                        if(!contains(method.visibleAnnotations,INJECT) && !contains(method.visibleAnnotations,REDIRECT)) {
                            throw new AssertionError(name+"."+method.name+": missing runtime-visible injection annotation");
                        }
                        handlers++;
                    }
                }
            }
        }
        if(handlers!=6) throw new AssertionError("Expected all six food-merge injection handlers; got "+handlers);
        System.out.println("PASS: "+count+" mixins have the annotation format required by Mixin 0.8.7; all "+handlers+" food injection annotations are present.");
    }
}
