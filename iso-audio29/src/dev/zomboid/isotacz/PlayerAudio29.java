package dev.zomboid.isotacz;
/** Snapshot on render thread; only the listener update runs on the audio executor. */
public final class PlayerAudio29 {
 public static Object point(Object camera){Object p=R.get(Bridge.mc(),"player");return p==null?R.call(camera,"getPosition"):R.call(p,"getEyePosition",((Number)R.call(camera,"getPartialTickTime")).floatValue());}
 public static void update(Object camera,boolean loaded,Object listener,Object executor,Object ci){
  Object mc=Bridge.mc(),p=R.get(mc,"player");
  if(!Bridge.active()||!loaded||!R.yes(R.call(camera,"isInitialized"))||p==null)return;
  float partial=((Number)R.call(camera,"getPartialTickTime")).floatValue();
  float pitch=((Number)R.call(p,"getViewXRot",partial)).floatValue(),yaw=((Number)R.call(p,"getViewYRot",partial)).floatValue();
  Object forward=R.call(R.type("net.minecraft.world.phys.Vec3"),"directionFromRotation",pitch,yaw),up=R.call(R.type("net.minecraft.world.phys.Vec3"),"directionFromRotation",pitch-90f,yaw);
  Object transform=R.make("com.mojang.blaze3d.audio.ListenerTransform",point(camera),forward,up);
  R.call(executor,"execute",(Runnable)()->R.call(listener,"setTransform",transform));R.call(ci,"cancel");
 }
}
