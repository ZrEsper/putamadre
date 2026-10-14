package dev.zomboid.taczfeedback;
public final class GlareRules {
    public static boolean visible(double alignment,double distance,boolean aiming,boolean sniper,boolean scope,boolean unobstructed) {
        return aiming&&sniper&&scope&&unobstructed&&Double.isFinite(distance)&&Double.isFinite(alignment)&&distance>=.5&&distance<=160&&alignment>Math.cos(Math.toRadians(distance<=32?35:18));
    }
    public static float size(double distance) {return (float)Math.min(1.5,.38+Math.max(0,distance)*.008);}
    public static int alpha(double alignment,double distance) {
        double edge=Math.cos(Math.toRadians(distance<=32?35:18));
        double facing=Math.max(0,Math.min(1,(alignment-edge)/(1-edge)));
        return (int)Math.round(255*(.85+.15*facing));
    }
}
