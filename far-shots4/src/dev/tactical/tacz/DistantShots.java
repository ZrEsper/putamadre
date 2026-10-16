package dev.tactical.tacz;
/** Sound radius only: no changes to bullet range, damage or zombie hearing. */
public final class DistantShots {
 public static int range(int configured,String name,int original){
  if(configured<=0||name==null)return original;
  if(name.startsWith("pzpos;")){int at=name.lastIndexOf(';');name=name.substring(at+1);}
  return switch(name){case "shoot","shoot_3p"->768;case "silence","silence_3p"->128;default->original;};
 }
}
