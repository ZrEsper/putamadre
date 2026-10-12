package dev.zomboid.tacz;
public final class ScopeZoom {
 public static double fov(double base,double zoom,double progress){if(!Double.isFinite(base)||base<=0||base>=180)return base;if(!Double.isFinite(zoom)||zoom<1)zoom=1;if(!Double.isFinite(progress))progress=0;double magnification=1+(zoom-1)*Math.max(0,Math.min(1,progress));return Math.toDegrees(2*Math.atan(Math.tan(Math.toRadians(base)*.5)/magnification));}
 public static void apply(Object event){try{
  if(Bridge.isoActive()||!R.yes(R.call(event,"usedConfiguredFov")))return;
  Object mc=Bridge.mc(),player=R.get(mc,"player");if(player==null)return;
  if(R.call(R.call(event,"getCamera"),"getEntity")!=player)return;
  Object stack=R.call(player,"getMainHandItem"),gun=R.call(R.type("com.tacz.guns.api.item.IGun"),"getIGunOrNull",stack);if(gun==null)return;
  Object operator=R.call(R.type("com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator"),"fromLocalPlayer",player);
  double progress=R.num(R.call(operator,"getClientAimingProgress",(float)R.num(R.call(event,"getPartialTick"))));
  double zoom=R.num(R.call(gun,"getAimingZoom",stack));
  R.call(event,"setFOV",fov(R.num(R.call(event,"getFOV")),zoom,progress));
 }catch(Throwable e){Bridge.warn("scope magnification",e);}}
}
