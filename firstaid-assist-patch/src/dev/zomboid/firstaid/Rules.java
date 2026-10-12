package dev.zomboid.firstaid;
public final class Rules {
 public static float health(float[] current,float[] max,float playerMax){float sum=0,total=0;for(int i=0;i<current.length;i++){float m=max[i];if(!Float.isFinite(m)||m<=0)continue;total+=m;if(Float.isFinite(current[i]))sum+=Math.max(0,Math.min(m,current[i]));}return total>0?sum/total*playerMax:0;}
 public static int mask(int old,float[] health){int next=old;for(int i=0;i<health.length;i++){if(health[i]<=0)next|=1<<i;else if(health[i]>=1)next&=~(1<<i);}return next;}
 static boolean bit(int mask,int i){return (mask&(1<<i))!=0;}
 public static double speed(int m){return -Math.min(.70,(bit(m,4)?.20:0)+(bit(m,2)?.25:0)+(bit(m,6)?.25:0)+(bit(m,3)?.15:0)+(bit(m,7)?.15:0));}
 public static double damage(int m){return -(bit(m,1)?.25:0)-(bit(m,5)?.25:0);}
 public static double attackSpeed(int m){return -(bit(m,0)?.35:0)-(bit(m,4)?.15:0);}
}
