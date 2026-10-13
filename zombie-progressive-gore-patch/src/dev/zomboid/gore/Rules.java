package dev.zomboid.gore;
public final class Rules {
 public enum Weapon {PISTOL,SHOTGUN,SNIPER,OTHER}
 public static final int HEAD=0,JAW=1,LEFT_ARM=2,RIGHT_ARM=3,LEFT_LEG=4,RIGHT_LEG=5,LEFT_BODY=6,RIGHT_BODY=7;
 public static Weapon weapon(String type){String t=type.toLowerCase(java.util.Locale.ROOT);return t.contains("shotgun")?Weapon.SHOTGUN:t.contains("sniper")?Weapon.SNIPER:t.contains("pistol")?Weapon.PISTOL:Weapon.OTHER;}
 public static int zone(double height,double sideways,boolean headshot){if(headshot||height>=.84)return HEAD;if(height>=.74)return JAW;if(height<.43)return sideways>=0?LEFT_LEG:RIGHT_LEG;if(Math.abs(sideways)>.24)return sideways>0?LEFT_ARM:RIGHT_ARM;return sideways>=0?LEFT_BODY:RIGHT_BODY;}
 public static int update(int state,int zone,float damage,float accumulated,float max,Weapon gun){if(damage<=0)return state;int wounds=(1<<zone)<<8;boolean strong=gun==Weapon.SHOTGUN||gun==Weapon.SNIPER;float threshold=zone<2?3:zone<6?(strong?5:9):(strong?9:18);int loss=accumulated>=threshold?1<<zone:0;int broken=zone>=2&&zone<6&&accumulated>=3?1<<(26+zone-2):0;int exit=gun==Weapon.SNIPER?(1<<zone)<<18:0;return state|wounds|loss|broken|exit;}
 public static int disabled(int state){return (state&60)|(((state>>>26)&15)<<2);}
 public static double speed(int state){int limbs=disabled(state),legs=Integer.bitCount(limbs&48);double s=legs==2?.16:legs==1?.5:1;if((state&48)!=0)s=Math.min(s,.22);if((state&1)!=0)s*=.75;if((state&192)!=0)s*=.8;return s;}
 public static double attack(int state){int arms=Integer.bitCount(disabled(state)&12);if(arms==2)return (state&2)!=0?0:.45;return (arms==1?.6:1)*((state&2)!=0?.65:1);}
 public static double push(Weapon gun){return gun==Weapon.SHOTGUN?.12:gun==Weapon.SNIPER?.10:.065;}
 public static boolean crawling(int state){return (state&48)!=0||(disabled(state)&48)==48;}
 public static int charStage(int ticks){return ticks>=120?3:ticks>=60?2:ticks>=20?1:0;}
}
