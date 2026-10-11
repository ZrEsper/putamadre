package dev.zomboid.reactive;
public final class Rules {
 public static int kind(double roll){return roll<.45?0:roll<.85?1:2;}
 public static int pace(double roll){return roll<.55?0:roll<.75?1:roll<.85?2:3;}
 public static double speed(int pace){return switch(pace){case 1->.22;case 2->.18;case 3->.34;default->.28;};}
 public static double vision(int kind,boolean crouching,boolean slippery){return (kind==0?12:kind==1?18:26)*(crouching?.70:1)*(slippery?.90:1);}
 public static double viewCos(int kind){return Math.cos(Math.toRadians(kind==0?50:kind==1?60:75));}
 public static double hearing(int kind,double roll){return (kind==0?.85:kind==1?1.05:1.2)+Math.max(0,Math.min(1,roll))*.25;}
 public static double steps(boolean sprint,boolean crouch,boolean water,boolean slippery,boolean clumsy){return (sprint?18:crouch?2.2:6)*(water?1.35:1)*(slippery?.55:1)*(clumsy?1.2:1);}
 public static double hearingRange(double radius,double hearing,boolean blocked){return Math.max(Math.min(radius,2),radius*hearing*(blocked?.70:1));}
 public static int memory(int kind){return kind==0?120:kind==1?180:240;}
 public static int windup(int kind){return kind==0?12:kind==1?10:8;}
 public static int cooldown(int kind){return kind==0?28:kind==1?22:18;}
 public static boolean dodge(long jump,long now,long cooldown){return jump>=now-4&&jump<=now&&cooldown<=now;}
 public static double ambient(String id,float volume){
  String s=id.toLowerCase(java.util.Locale.ROOT);if(!Float.isFinite(volume)||volume<=0)return 0;
  if(s.contains("zombie")||s.contains("step")||s.contains("music")||s.contains("ambient")||s.contains("weather")||s.contains("rain"))return 0;
  double base=s.contains("explod")?72:s.contains("gun")||s.contains("shoot")||s.contains("shot")?56:s.contains("sneez")||s.contains("cough")?8:s.contains("break")?18:s.contains("door")||s.contains("trapdoor")?12:s.contains("chest")||s.contains("barrel")?8:s.contains("place")||s.contains("hit")||s.contains("anvil")||s.contains("piston")?14:0;
  return Math.min(96,base*Math.min(2,Math.max(.4,volume)));
 }
}
