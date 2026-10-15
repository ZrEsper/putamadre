package dev.zomboid.mutants.horror;
import java.util.*;
/** Positional rally call, not an attack order against the caller. */
public final class Rally {
 static Object c(Object o,String n,Object...a){return R.call(o,n,a);}
 static double n(Object o,String key){return ((Number)c(o,key)).doubleValue();}
 public static void alert(Object level,Object caller,double radius){
  Object box=c(c(caller,"getBoundingBox"),"inflate",radius);
  List<Object> nearby=new ArrayList<>();
  for(Object mob:(Iterable<?>)c(level,"getEntitiesOfClass",R.type("net.minecraft.world.entity.Mob"),box)){
   if(mob==caller||mob.getClass().getName().equals("dev.zomboid.mutants.Screamers$Screamer")||!Boolean.TRUE.equals(c(mob,"isAlive"))||!Boolean.TRUE.equals(c(R.type("dev.zomboid.mutants.Combat"),"infected",mob))||((Number)c(mob,"distanceToSqr",caller)).doubleValue()>radius*radius)continue;
   // A zombie already fighting a player keeps fighting. Idle zombies investigate the cry.
   if(c(mob,"getTarget")!=null)continue;
   nearby.add(mob);
  }
  nearby.sort(Comparator.comparingDouble(m->((Number)c(m,"distanceToSqr",caller)).doubleValue()));
  long until=((Number)c(level,"getGameTime")).longValue()+480;
  Object pos=c(caller,"blockPosition");
  for(Object mob:nearby.subList(0,Math.min(128,nearby.size()))){
   Object tag=c(mob,"getPersistentData");c(tag,"putLong","zh_pos",c(pos,"asLong"));c(tag,"putLong","zh_until",until);c(tag,"remove","zh_player");
   // Native Hordes noise goal keeps following this point after this initial path request.
   double dx=n(caller,"getX")-n(mob,"getX"),dz=n(caller,"getZ")-n(mob,"getZ"),distance=Math.hypot(dx,dz),fraction=distance>16?16/distance:1;
   c(c(mob,"getNavigation"),"moveTo",n(mob,"getX")+dx*fraction,distance>16?n(mob,"getY"):n(caller,"getY"),n(mob,"getZ")+dz*fraction,1.15);
  }
 }
}
