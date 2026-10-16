import org.objectweb.asm.*;import org.objectweb.asm.tree.*;import java.nio.file.*;import java.util.*;import java.util.zip.*;
public class NativeAudioChecks{
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 static ClassNode node(byte[] b){ClassNode c=new ClassNode();new ClassReader(b).accept(c,0);return c;}
 static Map<String,Object> values(AnnotationNode a){Map<String,Object> m=new HashMap<>();for(int i=0;i<a.values.size();i+=2)m.put((String)a.values.get(i),a.values.get(i+1));return m;}
 public static void main(String[] args)throws Exception{
  ClassNode sp=node(Files.readAllBytes(Path.of(args[0])));int calls=0;
  String selector="evaluateEnvironment(IDDDLnet/minecraft/sounds/SoundSource;Lnet/minecraft/resources/ResourceLocation;Z)Lnet/minecraft/world/phys/Vec3;";
  for(MethodNode m:sp.methods)if((m.name+m.desc).equals(selector))for(AbstractInsnNode i:m.instructions)if(i instanceof MethodInsnNode x&&x.owner.equals("net/minecraft/client/Camera")&&x.name.equals("getPosition"))calls++;
  ok(calls==1,"exact supplied SP1.5.1 has one camera ray-tracing origin at exact descriptor");
  try(ZipFile z=new ZipFile(args[1])){ClassNode mix=node(z.getInputStream(z.getEntry("com/cleannrooster/dungeons_iso/mixin/compat/sound_physics_remastered/PlayerAcoustics31Mixin.class")).readAllBytes());MethodNode hook=mix.methods.getFirst();Map<String,Object> a=values(hook.visibleAnnotations.getFirst());ok(((List<?>)a.get("method")).equals(List.of(selector)),"exact target selector");ok(a.get("at") instanceof List<?>,"MixinExtras requires array-valued @At");ok(((List<?>)a.get("at")).size()==1&&a.get("require").equals(1),"one required acoustic hook");ok(values((AnnotationNode)((List<?>)a.get("at")).getFirst()).get("target").equals("Lnet/minecraft/client/Camera;getPosition()Lnet/minecraft/world/phys/Vec3;"),"only substitute camera position in acoustics");}
  Object plugin=Class.forName("com.cleannrooster.dungeons_iso.MixinPlugin").getConstructor().newInstance();var should=plugin.getClass().getMethod("shouldApplyMixin",String.class,String.class);var loaded=Class.forName("com.cleannrooster.dungeons_iso.ModCompat").getField("loaded");String name="com.cleannrooster.dungeons_iso.mixin.compat.sound_physics_remastered.PlayerAcoustics31Mixin";loaded.setBoolean(null,false);ok(!(Boolean)should.invoke(plugin,"com.sonicether.soundphysics.SoundPhysics",name),"optional acoustic mod absent skips hook");loaded.setBoolean(null,true);ok((Boolean)should.invoke(plugin,"com.sonicether.soundphysics.SoundPhysics",name),"actual native plugin enables hook for provided mod ID");
  System.out.println("NATIVE_ACOUSTIC_TARGET_CHECKS="+checks);
 }
}
