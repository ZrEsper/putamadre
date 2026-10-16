package dev.zomboid.isotacz;
/** One immutable ear position shared by the listener and optional acoustic ray tracing. */
public final class PlayerAudio31 {
 static volatile Frame frame;
 record Frame(Object world,Object eyes,Object forward,Object up){}
 static Frame capture(Object camera){Object mc=Bridge.mc(),player=R.get(mc,"player"),world=R.get(mc,"level");if(!Bridge.active()||player==null||world==null||!R.yes(R.call(camera,"isInitialized"))){frame=null;return null;}float partial=((Number)R.call(camera,"getPartialTickTime")).floatValue();float pitch=((Number)R.call(player,"getViewXRot",partial)).floatValue(),yaw=((Number)R.call(player,"getViewYRot",partial)).floatValue();Frame next=new Frame(world,R.call(player,"getEyePosition",partial),R.call(R.type("net.minecraft.world.phys.Vec3"),"directionFromRotation",pitch,yaw),R.call(R.type("net.minecraft.world.phys.Vec3"),"directionFromRotation",pitch-90f,yaw));frame=next;return next;}
 public static Object point(Object camera){Object mc=Bridge.mc(),p=R.get(mc,"player");if(!Bridge.active()||p==null)return R.call(camera,"getPosition");Frame f=frame;if(f!=null&&f.world==R.get(mc,"level"))return f.eyes;return R.call(p,"getEyePosition",((Number)R.call(camera,"getPartialTickTime")).floatValue());}
 public static void update(Object camera,boolean loaded,Object listener,Object executor,Object ci){Frame f=capture(camera);if(f==null||!loaded)return;Object transform=R.make("com.mojang.blaze3d.audio.ListenerTransform",f.eyes,f.forward,f.up);R.call(executor,"execute",(Runnable)()->R.call(listener,"setTransform",transform));R.call(ci,"cancel");}
 public static Object acoustics(Object camera,Object original){if(!Bridge.active()||R.get(Bridge.mc(),"player")==null)return R.call(original,"call",(Object)new Object[]{camera});return point(camera);}
}
