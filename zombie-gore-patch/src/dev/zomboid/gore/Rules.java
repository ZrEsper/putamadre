package dev.zomboid.gore;
public final class Rules {
 public enum Weapon {PISTOL,SHOTGUN,SNIPER,OTHER}
 public static final int HEAD=0,JAW=1,LEFT_ARM=2,RIGHT_ARM=3,LEFT_LEG=4,RIGHT_LEG=5,LEFT_BODY=6,RIGHT_BODY=7;
 public static Weapon weapon(String type){String t=type.toLowerCase(java.util.Locale.ROOT);return t.contains("shotgun")?Weapon.SHOTGUN:t.contains("sniper")?Weapon.SNIPER:t.contains("pistol")?Weapon.PISTOL:Weapon.OTHER;}
 public static int zone(double height,double sideways,boolean headshot){if(height>=.9)return HEAD;if(height>=.79)return JAW;if(height<.43)return sideways>=0?LEFT_LEG:RIGHT_LEG;if(Math.abs(sideways)>.24)return sideways>0?LEFT_ARM:RIGHT_ARM;return sideways>=0?LEFT_BODY:RIGHT_BODY;}
 public static int update(int state,int zone,float damage,float accumulated,float max,Weapon gun){int wounds=(1<<zone)<<8;boolean tear=gun==Weapon.SHOTGUN||gun==Weapon.SNIPER;double threshold=(zone<2?.22:zone<6?.34:.55)*Math.max(20,max);int loss=((tear&&accumulated>=threshold)||(!tear&&accumulated>=threshold*2.5))?1<<zone:0;int exit=gun==Weapon.SNIPER?(1<<zone)<<18:0;return state|wounds|loss|exit;}
 public static double speed(int state){int losses=state&255;double s=1;if((losses&1)!=0)s*=.7;if((losses&48)!=0)s*=.25;if((losses&192)!=0)s*=.8;return s;}
 public static double attack(int state){int arms=Integer.bitCount((state&255)&12);return Math.max(.2,1-arms*.35)*((state&2)!=0?.65:1);}
 public static double push(Weapon gun){return gun==Weapon.SHOTGUN?.12:gun==Weapon.SNIPER?.10:.065;}
 public static boolean crawling(int state){return (state&48)!=0;}
 public static int charStage(int ticks){return ticks>=120?3:ticks>=60?2:ticks>=20?1:0;}
}
