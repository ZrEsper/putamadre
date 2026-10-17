package dev.zomboidmelee.boxing;
/** A high-knee heel stomp. Angles describe a real thigh/shin/ankle chain. */
public final class StompMotion21{
 public static final double PLANT=.52,HOLD=.485;public static final long DURATION=900_000_000L;
 static final double[]T={0,.12,.28,.38,.52,.58,.68,.84,1};
 static double track(double p,double...v){if(!Double.isFinite(p))return v[0];p=Math.max(0,Math.min(1,p));for(int i=1;i<T.length;i++)if(p<=T[i]){double f=Motion20.smooth((p-T[i-1])/(T[i]-T[i-1]));return v[i-1]+(v[i]-v[i-1])*f;}return v[v.length-1];}
 public record Pose(double hipX,double hipY,double hipZ,double thigh,double knee,double ankle,double yaw,double roll){}
 public static Pose pose(double p,double reach,double height,double side,double eye){double load=lift(p),press=stamp(p);return new Pose(track(p,.30,.29,.26,.24,side,side,.16,.25,.32),-eye+.944+height*press+track(p,0,.015,.04,.02,0,.025,.06,.02,0),track(p,.08,-reach+.33,-reach+.20,-reach+.20,-reach+.2305,-reach+.22,-reach+.20,-.12,.10),track(p,-35,25,105,109,20,22,42,65,-35),track(p,0,-55,-155,-155,-5,-10,-65,-115,0),track(p,0,-35,-85,-82,-5,1,-18,-45,0),track(p,0,-5,-9,-8,2,2,-3,-5,0),track(p,0,-3,-5,-5,1,0,-2,-3,0));}
 public static double lift(double p){return track(p,0,.30,1,1,0,.08,.35,.45,0);}
 public static double stamp(double p){return track(p,0,0,0,.02,1,.96,.48,.03,0);}
 public static double contactWave(double seconds){if(seconds<=0||seconds>=.30)return 0;return (1-Math.exp(-seconds*100))*Math.exp(-seconds*19)*Math.sin(seconds*24);}
 public static double[]sole(double p,double reach,double height,double side,double eye){Pose a=pose(p,reach,height,side,eye);double t=Math.toRadians(a.thigh),s=Math.toRadians(a.thigh+a.knee),f=Math.toRadians(a.ankle);return new double[]{a.hipX,a.hipY-Math.cos(t)*7/16.0-Math.cos(s)*5/16.0-Math.cos(f)*3.7/16.0,a.hipZ-Math.sin(t)*7/16.0-Math.sin(s)*5/16.0-Math.sin(f)*3.7/16.0};}
}
