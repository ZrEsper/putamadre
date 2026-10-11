package dev.zomboid.scavenging;
public final class Rules {
 public static int level(int n){return Math.max(0,Math.min(10,n));}
 public static int ticks(int level){return 30-2*level(level);}
 public static double luck(double luck,int level){return Math.max(-2,Math.min(8,luck))+level(level)*.35;}
 public static int rarity(double roll,double luck,int level){double x=Math.max(-.03,Math.min(.12,luck(luck,level)*.012));return roll<.002+x*.04?4:roll<.02+x*.20?3:roll<.10+x*.55?2:roll<.30+x?1:0;}
 public static String name(int tier){return new String[]{"Común","Poco común","Raro","Épico","Legendario"}[Math.max(0,Math.min(4,tier))];}
 public static double xp(int tier,boolean corpse){return (corpse?5:8)+Math.max(0,Math.min(4,tier))*6;}
 public static int bonusRolls(int tier,int level){return 1+tier+level(level)/3;}
 public static boolean upgrade(int tier,double roll){return tier==4||tier==3&&roll<.20||tier==2&&roll<.05;}
 public static boolean valid(long elapsed,int total,boolean inReach,boolean sameTarget,boolean sameWorld,boolean freeMenu){return elapsed>=0&&total>0&&inReach&&sameTarget&&sameWorld&&freeMenu;}
}
