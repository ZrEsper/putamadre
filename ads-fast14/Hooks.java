import java.nio.file.*;import org.objectweb.asm.*;
public class Hooks implements Opcodes {
 static void make(Path out,boolean common)throws Exception{
  String name="dev/zomboid/ads14/mixin/"+(common?"PlayerAim14":"ClientAim14");ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);w.visit(V21,ACC_PUBLIC|ACC_ABSTRACT,name,null,"java/lang/Object",null);w.visitAnnotation("Lorg/spongepowered/asm/mixin/Pseudo;",false).visitEnd();
  var a=w.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;",false);var ar=a.visitArray("targets");ar.visit(null,common?"com.tacz.guns.entity.shooter.LivingEntityAim":"com.tacz.guns.client.gameplay.LocalPlayerAim");ar.visitEnd();a.visit("remap",false);a.visitEnd();
  if(common){var f=w.visitField(ACC_PRIVATE,"shooter","Lnet/minecraft/world/entity/LivingEntity;",null,null);f.visitAnnotation("Lorg/spongepowered/asm/mixin/Shadow;",true).visitEnd();f.visitAnnotation("Lorg/spongepowered/asm/mixin/Final;",true).visitEnd();f.visitEnd();}
  var m=w.visitMethod(ACC_PRIVATE,"ads14$quickPlayerAim","(F)F",null,null);a=m.visitAnnotation("Lorg/spongepowered/asm/mixin/injection/ModifyArg;",true);ar=a.visitArray("method");ar.visit(null,common?"tickAimingProgress()V":"getAlphaProgress(Lcom/tacz/guns/resource/pojo/data/gun/GunData;)F");ar.visitEnd();a.visit("index",1);var at=a.visitAnnotation("at","Lorg/spongepowered/asm/mixin/injection/At;");at.visit("value","INVOKE");at.visit("target","Ljava/lang/Math;max(FF)F");at.visitEnd();a.visit("require",1);a.visit("remap",false);a.visitEnd();m.visitCode();
  Label skip=new Label();if(common){m.visitVarInsn(ALOAD,0);m.visitFieldInsn(GETFIELD,name,"shooter","Lnet/minecraft/world/entity/LivingEntity;");m.visitTypeInsn(INSTANCEOF,"net/minecraft/world/entity/player/Player");m.visitJumpInsn(IFEQ,skip);}
  m.visitVarInsn(FLOAD,1);m.visitLdcInsn(.05f);m.visitMethodInsn(INVOKESTATIC,"java/lang/Math","min","(FF)F",false);m.visitInsn(FRETURN);
  if(common){m.visitLabel(skip);m.visitFrame(F_SAME,0,null,0,null);m.visitVarInsn(FLOAD,1);m.visitInsn(FRETURN);}
  m.visitMaxs(0,0);m.visitEnd();w.visitEnd();Path p=out.resolve(name+".class");Files.createDirectories(p.getParent());Files.write(p,w.toByteArray());
 }
 public static void main(String[]args)throws Exception{make(Path.of(args[0]),true);make(Path.of(args[0]),false);}
}
