package dev.zomboidmelee.boxing;
/** A short camera focus during the committed stomp; never translates the player. */
public final class Look21{
 static double wrap(double v){return ((v+180)%360+360)%360-180;}
 public static void apply(Object event,Object p,Client20.State s,double phase){if(s.target<0)return;Object v=R.call(R.call(p,"level"),"getEntity",s.target);if(v==null||!Stomp18.down(v))return;Object point=Stomp18.head(v),eye=R.call(p,"getEyePosition");double dx=R.num(R.get(point,"x"))-R.num(R.get(eye,"x")),dy=R.num(R.get(point,"y"))-R.num(R.get(eye,"y")),dz=R.num(R.get(point,"z"))-R.num(R.get(eye,"z"));double yaw=Math.toDegrees(Math.atan2(-dx,dz)),pitch=-Math.toDegrees(Math.atan2(dy,Math.hypot(dx,dz)));double weight=Motion20.smooth(Math.min(phase/.14,(1-phase)/.18));float oldYaw=((Number)R.call(event,"getYaw")).floatValue(),oldPitch=((Number)R.call(event,"getPitch")).floatValue();R.call(event,"setYaw",oldYaw+(float)(wrap(yaw-oldYaw)*weight));R.call(event,"setPitch",oldPitch+(float)((pitch-oldPitch)*weight));}
}
