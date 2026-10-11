package dev.zomboid.scavenging;
public final class Rules {
 public static int level(int n){return Math.max(0,Math.min(10,n));}
 public static int ticks(int level){return 30-2*level(level);}
 public static double luck(double luck,int level){return Math.max(-2,Math.min(8,luck))+level(level)*.75;}
 public static int rarity(double roll,double luck,int level){double x=Math.max(-.03,Math.min(.12,luck(luck,level)*.012));int n=level(level);return roll<.002+x*.04+n*.001?4:roll<.02+x*.20+n*.006?3:roll<.10+x*.55+n*.018?2:roll<.30+x+n*.03?1:0;}
 public static String name(int tier){return new String[]{"Común","Poco común","Raro","Épico","Legendario"}[Math.max(0,Math.min(4,tier))];}
 public static double xp(int tier,boolean corpse){return (corpse?5:8)+Math.max(0,Math.min(4,tier))*6;}
 public static int bonusRolls(int tier,int level){return 1+tier+level(level)/2;}
 public static int quantityBonus(int level){return level(level)/3;}
 public static double wear(int tier,int level){return Math.max(0,.45-.1*tier-.025*level(level));}
 public static boolean upgrade(int tier,double roll){return tier>0;}
 public static int slots(int tier){return 27+27*Math.max(0,Math.min(4,tier));}
 public static boolean valid(long elapsed,int total,boolean inReach,boolean sameTarget,boolean sameWorld,boolean freeMenu){return elapsed>=0&&total>0&&inReach&&sameTarget&&sameWorld&&freeMenu;}
}
