package mod.cdv.tnpc.tactical;
import java.util.*;
/** Keep aim independent of navigation yaw, with one angular step per server tick. */
public final class Aim29 {
 static final Map<Object,State> STATES=new WeakHashMap<>();static final class State{long tick=-1000;float yaw,pitch;}
 static Object c(Object n,String m,Object...a)throws Exception{return Reflect.call(n,m,a);}
 public static double face(Object n,Object target){try{long now=Social.time(n);State s=STATES.computeIfAbsent(n,k->new State());double dx=Social.num(c(target,"getX"))-Social.num(c(n,"getX")),dz=Social.num(c(target,"getZ"))-Social.num(c(n,"getZ")),y=Social.num(c(target,"getY"))+Social.num(c(target,"getBbHeight"))*.52,dy=y-Social.num(c(n,"getEyeY"));double yaw=Math.toDegrees(Math.atan2(dz,dx))-90,pitch=-Math.toDegrees(Math.atan2(dy,Math.max(.01,Math.hypot(dx,dz))));if(now-s.tick>20){s.yaw=((Number)c(n,"getYHeadRot")).floatValue();s.pitch=((Number)c(n,"getXRot")).floatValue();}if(now!=s.tick){s.yaw+=(float)Math.max(-12,Math.min(12,Sense28.delta(s.yaw,yaw)));s.pitch+=(float)Math.max(-8,Math.min(8,pitch-s.pitch));s.tick=now;}c(n,"setYRot",s.yaw);c(n,"setYHeadRot",s.yaw);c(n,"setYBodyRot",s.yaw);c(n,"setXRot",s.pitch);c(c(n,"getLookControl"),"setLookAt",Social.num(c(target,"getX")),y,Social.num(c(target,"getZ")),12f,8f);return Math.abs(Sense28.delta(s.yaw,yaw));}catch(Exception e){CalmBrain.error("aim29 tracking",e);return 180;}}
}
