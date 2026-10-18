import java.nio.file.*;import org.objectweb.asm.*;
public class Hooks implements Opcodes{public static void main(String[]args)throws Exception{
 ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);String name="com/raiiiden/warborn/mixin/client/NvgFinalPass13";w.visit(V21,ACC_PUBLIC|ACC_ABSTRACT,name,null,"java/lang/Object",null);
 var a=w.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;",false);var ar=a.visitArray("targets");ar.visit(null,"net.minecraft.client.renderer.GameRenderer");ar.visitEnd();a.visit("remap",false);a.visitEnd();
 var m=w.visitMethod(ACC_PRIVATE,"nvg13$afterHands","(Lnet/minecraft/client/DeltaTracker;Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V",null,null);
 a=m.visitAnnotation("Lorg/spongepowered/asm/mixin/injection/Inject;",true);ar=a.visitArray("method");ar.visit(null,"renderLevel(Lnet/minecraft/client/DeltaTracker;)V");ar.visitEnd();ar=a.visitArray("at");var at=ar.visitAnnotation(null,"Lorg/spongepowered/asm/mixin/injection/At;");at.visit("value","TAIL");at.visitEnd();ar.visitEnd();a.visit("require",1);a.visit("remap",false);a.visitEnd();m.visitCode();m.visitVarInsn(ALOAD,1);m.visitMethodInsn(INVOKESTATIC,"com/raiiiden/warborn/hybrid/Vision12","finish","(Ljava/lang/Object;)V",false);m.visitInsn(RETURN);m.visitMaxs(0,0);m.visitEnd();w.visitEnd();Files.write(Path.of(args[0]),w.toByteArray());
}}
