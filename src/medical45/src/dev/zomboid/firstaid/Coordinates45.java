package dev.zomboid.firstaid;
/** Shared forward/inverse transform for the accepted TaCZ hit and its visual mark. */
public final class Coordinates45 {
 static double n(Object o,String f){return ((Number)R.get(o,f)).doubleValue();}
 static double call(Object o,String s){return ((Number)R.call(o,s)).doubleValue();}
 static boolean crawl(Object p){String pose=String.valueOf(R.call(p,"getPose"));if(pose.equals("SWIMMING")||pose.equals("FALL_FLYING"))return true;try{Object op=R.call(R.type("com.tacz.guns.api.entity.IGunOperator"),"fromLivingEntity",p);Object data=op==null?null:R.call(op,"getDataHolder");return data!=null&&R.yes(R.get(data,"isCrawling"));}catch(IllegalStateException e){if(e.getCause()instanceof ClassNotFoundException)return false;throw e;}}
 public static double[] toLocal(double x,double y,double z,double yaw,boolean crawl,boolean crouch){double c=Math.cos(yaw),s=Math.sin(yaw),a=x*c+z*s,b=-x*s+z*c;return crawl?new double[]{a,.9+b,.3-y}:new double[]{a,y+(crouch?.125:0),b};}
 public static double[] toWorld(double x,double y,double z,double yaw,boolean crawl,boolean crouch){if(crawl){double t=y;y=.3-z;z=t-.9;}else if(crouch)y-=.125;double c=Math.cos(yaw),s=Math.sin(yaw);return new double[]{x*c-z*s,y,x*s+z*c};}
 public static Object local(Object point,Object p){double[]a=toLocal(n(point,"x")-call(p,"getX"),n(point,"y")-call(p,"getY"),n(point,"z")-call(p,"getZ"),Math.toRadians(n(p,"yBodyRot")),crawl(p),String.valueOf(R.call(p,"getPose")).equals("CROUCHING"));return R.make("net.minecraft.world.phys.Vec3",a[0],a[1],a[2]);}
 public static Object world(Object point,Object p){double[]a=toWorld(n(point,"x"),n(point,"y"),n(point,"z"),Math.toRadians(n(p,"yBodyRot")),crawl(p),String.valueOf(R.call(p,"getPose")).equals("CROUCHING"));return R.make("net.minecraft.world.phys.Vec3",a[0]+call(p,"getX"),a[1]+call(p,"getY"),a[2]+call(p,"getZ"));}
}
