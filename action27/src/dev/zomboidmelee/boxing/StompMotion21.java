package dev.zomboidmelee.boxing;
/** A high-knee heel stomp. Angles describe a real thigh/shin/ankle chain. */
public final class StompMotion21{
 public static final double PLANT=.52,HOLD=.485;public static final long DURATION=900_000_000L;
 static final double[]T={0,.12,.28,.38,.52,.58,.68,.84,1};
 static double track(double p,double...v){if(!Double.isFinite(p))return v[0];p=Math.max(0,Math.min(1,p));for(int i=1;i<T.length;i++)if(p<=T[i]){double f=Motion20.smooth((p-T[i-1])/(T[i]-T[i-1]));return v[i-1]+(v[i]-v[i-1])*f;}return v[v.length-1];}
 public record Pose(double hipX,double hipY,double hipZ,double thigh,double knee,double ankle,double yaw,double roll){}
 public static Pose pose(double p,double reach,double height,double side,double eye){double lift=lift(p),press=stamp(p),enter=Motion20.smooth(Math.min(p/.12,(1-p)/.16));reach=Math.max(.32,Math.min(.55,reach*.60));height=Math.max(0,Math.min(.5,height));double hipY=-eye+.88,hipZ=-.20,ankleY=-eye+height+3.7/16d+.38*lift,ankleZ=-reach;double down=hipY-ankleY,forward=hipZ-ankleZ,l1=7/16d,l2=5/16d,r=Math.min(l1+l2-.005,Math.hypot(down,forward));double knee=-Math.acos(Math.max(-1,Math.min(1,(r*r-l1*l1-l2*l2)/(2*l1*l2))));double thigh=Math.atan2(forward,down)-Math.atan2(l2*Math.sin(knee),l1+l2*Math.cos(knee));double flat=track(p,0,-5,-10,-8,0,0,-5,-3,0);return new Pose(.28+(side-.20)*.28*press,hipY,hipZ, -35+(Math.toDegrees(thigh)+35)*enter,Math.toDegrees(knee)*enter,flat,0,-2*lift);}
 public static double lift(double p){return track(p,0,.30,1,1,0,.08,.35,.45,0);}
 public static double stamp(double p){return track(p,0,0,0,.02,1,.96,.48,.03,0);}
 public static double contactWave(double seconds){if(seconds<=0||seconds>=.30)return 0;return (1-Math.exp(-seconds*100))*Math.exp(-seconds*19)*Math.sin(seconds*24);}
 public static double[]sole(double p,double reach,double height,double side,double eye){Pose a=pose(p,reach,height,side,eye);double t=Math.toRadians(a.thigh),s=Math.toRadians(a.thigh+a.knee),f=Math.toRadians(a.ankle);return new double[]{a.hipX,a.hipY-Math.cos(t)*7/16.0-Math.cos(s)*5/16.0-Math.cos(f)*3.7/16.0,a.hipZ-Math.sin(t)*7/16.0-Math.sin(s)*5/16.0-Math.sin(f)*3.7/16.0};}
}
