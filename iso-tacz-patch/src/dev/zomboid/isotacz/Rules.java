package dev.zomboid.isotacz;
public final class Rules {
 public static double wrap(double a){return ((a%360)+540)%360-180;}
 public static double step(double current,double target,double seconds){double delta=wrap(target-current);return current+Math.max(-70*seconds,Math.min(70*seconds,delta));}
 public static boolean sprint(double forward,double side){return Math.hypot(forward,side)>=0.79;}
 public static int choose(double[] scores){int best=0;for(int i=1;i<scores.length;i++)if(scores[i]-Math.abs(wrap(i*45))*0.0005>scores[best]-Math.abs(wrap(best*45))*0.0005)best=i;return scores[0]<0.82&&scores[best]>scores[0]+0.18?best:0;}
 public static double[] angles(double x,double y,double z){double horizontal=Math.hypot(x,z);return new double[]{Math.toDegrees(Math.atan2(z,x))-90,-Math.toDegrees(Math.atan2(y,horizontal))};}
 public static double[] project(double x,double y,double w,int width,int height){if(w<=0.001)return null;double nx=x/w,ny=y/w;if(Math.abs(nx)>1||Math.abs(ny)>1)return null;return new double[]{(nx+1)*width/2,(1-ny)*height/2};}
 public static double[] behind(double yaw,double pitch,double distance){double y=Math.toRadians(yaw),p=Math.toRadians(pitch);return new double[]{Math.sin(y)*Math.cos(p)*distance,Math.sin(p)*distance,-Math.cos(y)*Math.cos(p)*distance};}
}
