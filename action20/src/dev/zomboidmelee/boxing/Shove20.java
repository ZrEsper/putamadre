package dev.zomboidmelee.boxing;
public final class Shove20{
 public static boolean nativeGun(Object stack){try{return R.call(R.type("com.tacz.guns.api.item.IGun"),"getIGunOrNull",stack)!=null;}catch(IllegalStateException missing){return false;}}
 public static boolean hurt(Object v,Object source,float amount){Server20.prepare(v);return R.yes(R.call(v,"hurt",source,amount));}
 public static void recoil(Object v,double strength,double x,double z){double n=Math.hypot(x,z);if(n>.001)R.call(v,"push",-x/n*strength,0d,-z/n*strength);R.set(v,"hurtMarked",true);}
}
