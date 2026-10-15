package dev.zomboid.remains;
public final class ScarcityCompat33{
 public static void body(Object corpse,Object level,Object event){ProfileCompat.corpse(corpse,level,event);try{Class.forName("dev.zomboid.hordes.LootNerf").getMethod("body",Object.class,Object.class,Object.class).invoke(null,corpse,level,event);}catch(ClassNotFoundException e){}catch(ReflectiveOperationException e){System.getLogger("Scarcity33").log(System.Logger.Level.ERROR,"No se pudo reducir loot del cuerpo",e);}}
 public static Object synthetic(Object target,String method,Object[]args){return null;}
 public static double gunChance(int q){return new double[]{.005,.01,.02,.03,.04,.05}[Math.max(0,Math.min(5,q))];}
 public static int ammoMin(int q){return new int[]{1,2,3,4,5,6}[Math.max(0,Math.min(5,q))];}
 public static int ammoMax(int q){return new int[]{2,3,4,6,8,12}[Math.max(0,Math.min(5,q))];}
}
