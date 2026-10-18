import java.nio.file.*;import java.util.*;import java.util.zip.*;import java.lang.reflect.*;import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import org.objectweb.asm.tree.analysis.*;
public class Checks implements Opcodes{
 static int checks;static void ok(boolean x){if(!x)throw new AssertionError();checks++;}
 static class Loader extends ClassLoader{Class<?> make(String n,byte[]b){return defineClass(n,b,0,b.length);}}
 static byte[]stub(String n,String parent){ClassWriter w=new ClassWriter(0);w.visit(V21,ACC_PUBLIC,n,null,parent,null);var m=w.visitMethod(ACC_PUBLIC,"<init>","()V",null,null);m.visitCode();m.visitVarInsn(ALOAD,0);m.visitMethodInsn(INVOKESPECIAL,parent,"<init>","()V",false);m.visitInsn(RETURN);m.visitMaxs(1,1);m.visitEnd();w.visitEnd();return w.toByteArray();}
 static Class<?> fixture(Loader l,byte[]bytes)throws Exception{ClassNode c=new ClassNode();new ClassReader(bytes).accept(c,0);String old=c.name;c.name+="$Fixture";c.access&=~ACC_ABSTRACT;c.visibleAnnotations=c.invisibleAnnotations=null;for(var f:c.fields){f.visibleAnnotations=f.invisibleAnnotations=null;}for(var m:c.methods){m.visibleAnnotations=m.invisibleAnnotations=null;for(var i:m.instructions)if(i instanceof FieldInsnNode f&&f.owner.equals(old))f.owner=c.name;new Analyzer<BasicValue>(new BasicVerifier()).analyze(c.name,m);}MethodNode ctor=new MethodNode(ACC_PUBLIC,"<init>","()V",null,null);ctor.instructions.add(new VarInsnNode(ALOAD,0));ctor.instructions.add(new MethodInsnNode(INVOKESPECIAL,"java/lang/Object","<init>","()V",false));ctor.instructions.add(new InsnNode(RETURN));c.methods.add(ctor);ClassWriter w=new ClassWriter(ClassWriter.COMPUTE_MAXS);c.accept(w);return l.make(c.name.replace('/','.'),w.toByteArray());}
 static int mathCalls(ZipFile z,String path,String name)throws Exception{ClassNode c=new ClassNode();new ClassReader(z.getInputStream(z.getEntry(path)).readAllBytes()).accept(c,0);int n=0;for(var m:c.methods)if(m.name.equals(name))for(var i:m.instructions)if(i instanceof MethodInsnNode call&&call.owner.equals("java/lang/Math")&&call.name.equals("max")&&call.desc.equals("(FF)F"))n++;return n;}
 public static void main(String[]args)throws Exception{
  try(ZipFile t=new ZipFile(args[1])){ok(mathCalls(t,"com/tacz/guns/client/gameplay/LocalPlayerAim.class","getAlphaProgress")==1);ok(mathCalls(t,"com/tacz/guns/entity/shooter/LivingEntityAim.class","tickAimingProgress")==1);}
  Loader l=new Loader();Class<?> living=l.make("net.minecraft.world.entity.LivingEntity",stub("net/minecraft/world/entity/LivingEntity","java/lang/Object"));Class<?> player=l.make("net.minecraft.world.entity.player.Player",stub("net/minecraft/world/entity/player/Player","net/minecraft/world/entity/LivingEntity"));
  try(ZipFile z=new ZipFile(args[0])){for(String name:List.of("ClientAim14","PlayerAim14")){
   Class<?> cls=fixture(l,z.getInputStream(z.getEntry("dev/zomboid/ads14/mixin/"+name+".class")).readAllBytes());Object instance=cls.getConstructor().newInstance();Method cap=cls.getDeclaredMethod("ads14$quickPlayerAim",float.class);cap.setAccessible(true);Field shooter=null;
   if(name.equals("PlayerAim14")){shooter=cls.getDeclaredField("shooter");shooter.setAccessible(true);shooter.set(instance,player.getConstructor().newInstance());}
   for(float seconds:new float[]{-.2f,0f,.02f,.05f,.1f,.3f,1f,Float.POSITIVE_INFINITY}){float got=(Float)cap.invoke(instance,seconds);ok(Float.compare(got,Math.min(seconds,.05f))==0);ok(Math.max(0,got)<=.05f);}
   float fast=(Float)cap.invoke(instance,.3f);ok(Math.min(1f,.051f/fast)==1f);ok(.051f/.3f<.2f);
   if(shooter!=null){shooter.set(instance,living.getConstructor().newInstance());ok((Float)cap.invoke(instance,.3f)==.3f);}
  }}System.out.println("REAL_GENERATED_ADS_HOOK_50MS_PLAYER_ONLY_NATIVE_TARGET_CHECKS="+checks);
 }
}
