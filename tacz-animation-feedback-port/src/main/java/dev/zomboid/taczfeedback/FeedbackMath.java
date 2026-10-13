package dev.zomboid.taczfeedback;
public final class FeedbackMath {
 public static double bearing(double dx,double dz,double cameraYaw){double angle=Math.toDegrees(Math.atan2(-dx,dz))-cameraYaw;return Math.toRadians(angle-Math.floor((angle+180)/360)*360);}
 public static boolean glare(double alignment,double distance,boolean aiming,boolean sniper,boolean scope,boolean visible){return aiming&&sniper&&scope&&visible&&distance>=2&&distance<=160&&alignment>Math.cos(Math.toRadians(12));}
 public static float fade(long elapsedMillis){return Math.max(0,1-elapsedMillis/1150f);}
}
