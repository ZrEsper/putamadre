package com.raiiiden.ragdollified.client.repair;
import java.util.*;import dev.zomboid.gore.R;
/** Much weaker impacts, with a total per-body budget so pellets do not launch the corpse. */
public final class Impulse23 {
 static final Map<Object,Budget> BUDGETS=new WeakHashMap<>();static final class Budget{long time;double used;}
 public static double[] reduce(double x,double y,double z,double remaining){if(!Double.isFinite(x)||!Double.isFinite(y)||!Double.isFinite(z))return new double[3];x*=.12;y=Math.max(-2.5,Math.min(2.5,y*.12));z*=.12;double n=Math.sqrt(x*x+y*y+z*z),scale=n>remaining?Math.max(0,remaining)/Math.max(.000001,n):1;return new double[]{x*scale,y*scale,z*scale};}
 public static Object apply(Object rag,Object vector){double[] v;long now=System.nanoTime();synchronized(BUDGETS){Budget b=BUDGETS.computeIfAbsent(rag,k->new Budget());if(now-b.time>=200_000_000L){b.time=now;b.used=0;}v=reduce(R.num(R.get(vector,"x")),R.num(R.get(vector,"y")),R.num(R.get(vector,"z")),12-b.used);b.used+=Math.sqrt(v[0]*v[0]+v[1]*v[1]+v[2]*v[2]);}return R.make("javax.vecmath.Vector3f",(float)v[0],(float)v[1],(float)v[2]);}
}
