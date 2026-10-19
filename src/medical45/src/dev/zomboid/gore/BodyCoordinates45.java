package dev.zomboid.gore;
public final class BodyCoordinates45 {
 public static double height(Object e){double h=R.num(R.call(R.call(e,"getType"),"getHeight"));return h*(R.yes(R.call(e,"isBaby"))?.5:1);}
 public static double[] local(Object e,Object point,double yaw){double scale=height(e)/1.95;double[] a=Hit28.local(R.num(R.get(point,"x"))-R.num(R.call(e,"getX")),R.num(R.get(point,"y"))-R.num(R.call(e,"getY")),R.num(R.get(point,"z"))-R.num(R.call(e,"getZ")),yaw,scale);
  String pose=String.valueOf(R.call(e,"getPose"));if(pose.equals("SWIMMING")||pose.equals("FALL_FLYING")){double y=a[1],z=a[2];a[1]=12-z;a[2]=y-4;}else if(pose.equals("CROUCHING"))a[1]+=2;
  return a;
 }
}
