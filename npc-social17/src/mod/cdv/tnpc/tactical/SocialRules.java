package mod.cdv.tnpc.tactical;
public final class SocialRules {
 public static int clamp(int n){return Math.max(-100,Math.min(100,n));}
 public static int disposition(int roll,boolean armed){return roll<25&&armed?0:roll<75?1:2;}
 public static boolean attack(int kind,boolean armed,boolean recruited,boolean retaliation,int trust,int reputation,int seenTicks){if(recruited||kind==2||!armed||reputation<=-60)return false;return retaliation||(kind==0||kind==1&&trust<=-35)&&seenTicks>=100;}
 public static boolean recruit(int kind,int trust,int reputation,boolean armed){return kind==0&&armed&&reputation<=-60||trust>=(reputation<=-30?75:kind==2?35:45);}
 public static int personal(int trust,int reputation,int heard){return clamp(trust+(heard==0?0:reputation/3));}
 public static boolean aid(int kind,boolean faction,int trust,int reputation){return faction||kind!=0&&trust>=0&&reputation>-50;}
 public static String mood(int threats,int crowd,boolean raining,boolean night,float hp){return hp<.45?"wounded":threats>=4?"horde":crowd>=5?"crowd":night?"night":raining?"rain":"calm";}
}
