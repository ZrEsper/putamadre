package dev.zomboid.turretpower;
import java.util.*;
/** Server-authoritative generator link. The turret's own battery never authorizes fire. */
public final class Link {
 public static final int SHOT_FE=100;
 static boolean reported;
 static final Map<Object,Burst> BURSTS=new WeakHashMap<>();
 public static final class Burst {Object target;long ready,last=-100;}
 static Burst burst(Object t){return BURSTS.computeIfAbsent(t,k->new Burst());}
 static long now(Object t){return R.lng(R.call(R.call(t,"level"),"getGameTime"));}
 static boolean server(Object w){return !R.yes(R.call(w,"isClientSide"));}
 static Object generator(Object t){return generator(t,true);}
 static Object generator(Object t,boolean requirePower){
  Object w=R.call(t,"level");if(!server(w))return null;
  try {Object map=R.get(R.type("dev.zomboid.survival.MachineEntity"),"GENERATORS");Object positions=((Map<?,?>)map).get(w);if(!(positions instanceof Set<?> ps))return null;
   Object origin=R.call(t,"blockPosition"),best=null;double nearest=Double.MAX_VALUE;
   for(Object p:ps){double d=R.num(R.call(origin,"distSqr",p));if(d>625||d>=nearest||!R.yes(R.call(w,"hasChunkAt",p)))continue;
    Object g=R.call(w,"getBlockEntity",p);if(g==null||!R.yes(R.call(g,"generator")))continue;
    Object key=R.call(R.get(R.type("net.minecraft.core.registries.BuiltInRegistries"),"BLOCK"),"getKey",R.call(R.call(w,"getBlockState",p),"getBlock"));
    if(!key.toString().startsWith("doomsday_decoration:"))continue;
    if(requirePower&&R.num(R.call(R.get(g,"energy"),"extractEnergy",SHOT_FE,true))!=SHOT_FE)continue;
    best=g;nearest=d;
   }return best;
  }catch(IllegalStateException e){if(!reported){reported=true;System.err.println("[Doomsday turret] Generator link unavailable: "+e.getMessage());}return null;}
 }
 static Object monitor(Object t){Object g=generator(t);return g==null?generator(t,false):g;}
 public static int stored(Object t){Object g=monitor(t);return g==null?0:((Number)R.call(R.get(g,"energy"),"getEnergyStored")).intValue();}
 public static int capacity(Object t){Object g=monitor(t);return g==null?128000:((Number)R.call(R.get(g,"energy"),"getMaxEnergyStored")).intValue();}
 public static boolean power(Object t){return generator(t)!=null;}
 static boolean track(Object t){
  Object w=R.call(t,"level");if(!server(w))return false;Burst b=burst(t);
  Object target=R.call(R.type("net.tslat.smartbrainlib.util.BrainUtils"),"getTargetOfEntity",t);
  boolean valid=R.yes(R.call(t,"isEnabled"))&&R.yes(R.call(t,"hasGun"))&&R.yes(R.call(t,"gunHasAmmo"))&&target!=null&&R.yes(R.call(target,"isAlive"))&&R.num(R.call(t,"distanceToSqr",target))<=400&&R.yes(R.call(t,"isValidTarget",target))&&R.yes(R.call(t,"hasLineOfSight",target))&&power(t);
  if(!valid){b.target=null;return false;}
  if(b.target!=target){b.target=target;b.ready=Math.max(b.last+10,now(t)+16);}
  return true;
 }
 public static boolean allowed(Object t){return track(t)&&now(t)>=burst(t).ready;}
 public static void fire(Object event){Object t=R.call(event,"getShooter");if(!R.type("com.entropy.tacz_turrets.turret.TurretEntity").isInstance(t))return;if(!server(R.call(t,"level"))||R.yes(R.call(event,"isCanceled")))return;if(!allowed(t)||!R.yes(R.call(t,"gunHasAmmo"))){R.call(event,"setCanceled",true);return;}success(t);}
 public static void success(Object t){Object g=generator(t);if(g==null)throw new IllegalStateException("Generator disappeared during synchronous turret shot");int taken=((Number)R.call(R.get(g,"energy"),"extractEnergy",SHOT_FE,false)).intValue();if(taken!=SHOT_FE)throw new IllegalStateException("Generator energy changed during synchronous turret shot");R.call(g,"setChanged");Burst b=burst(t);b.last=now(t);b.ready=b.last+10;}
 public static boolean zombie(Object mob){String id=R.call(R.get(R.type("net.minecraft.core.registries.BuiltInRegistries"),"ENTITY_TYPE"),"getKey",R.call(mob,"getType")).toString().toLowerCase(Locale.ROOT);return id.equals("minecraft:husk")||id.equals("minecraft:drowned")||id.contains("zombie")||id.startsWith("mutantszombies:")||id.startsWith("mutants_l4d2:");}
 public static boolean zombieDamage(Object source){Object e=R.call(source,"getEntity");return e!=null&&zombie(e);}
 public static void tick(Object turret){track(turret);Sounds.tick(turret);Object w=R.call(turret,"level");if(!server(w)||now(turret)%20!=0)return;
  Object box=R.call(R.call(turret,"getBoundingBox"),"inflate",12.0);
  for(Object mob:(List<?>)R.call(w,"getEntitiesOfClass",R.type("net.minecraft.world.entity.Mob"),box)){
   if(!zombie(mob)||!R.yes(R.call(mob,"isAlive")))continue;Object current=R.call(mob,"getTarget");
   if(current==null||!R.yes(R.call(current,"isAlive")))R.call(mob,"setTarget",turret);
   if(R.call(mob,"getTarget")==turret&&R.num(R.call(mob,"distanceToSqr",turret))<=4&&R.yes(R.call(R.call(mob,"getSensing"),"hasLineOfSight",turret))&&R.num(R.get(turret,"invulnerableTime"))<=0)R.call(mob,"doHurtTarget",turret);
  }
 }
}
