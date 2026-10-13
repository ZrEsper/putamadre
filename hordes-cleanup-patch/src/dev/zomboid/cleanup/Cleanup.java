package dev.zomboid.cleanup;
import java.util.*;import java.nio.file.*;import java.io.*;import dev.zomboid.hordes.fast.FastReflection;
public final class Cleanup {
 static final Class<?> ITEM=FastReflection.type("net.minecraft.world.entity.item.ItemEntity"),ZOMBIE=FastReflection.type("net.minecraft.world.entity.monster.Zombie");
 static final Map<Object,Budget> budgets=new WeakHashMap<>();static final Settings settings=Settings.load();
 static final Map<Object,Active> active=new WeakHashMap<>();static final class Active{long tick=Long.MIN_VALUE;Set<Object> ids=new HashSet<>();}
 static boolean activeTicket(Object level,Object tag,long now){if(!yes(call(tag,"hasUUID","zh_ticket")))return false;Active a=active.computeIfAbsent(level,k->new Active());if(a.tick==Long.MIN_VALUE||now<a.tick||now-a.tick>=100){a.tick=now;a.ids.clear();Object data=call(FastReflection.type("dev.zomboid.hordes.HordeData"),"get",level);for(Object ticket:(Iterable<?>)FastReflection.get(data,"tickets"))if(num(FastReflection.get(ticket,"remaining"))>0)a.ids.add(FastReflection.get(ticket,"id"));}return a.ids.contains(call(tag,"getUUID","zh_ticket"));}
 static final class Budget {long second=Long.MIN_VALUE;int count;}
 public record Settings(boolean items,boolean hordes,int itemAge,int idleAge,double itemDistance,double hordeDistance,int limit){
  static Settings load(){Properties p=new Properties();Path path=Path.of("config/zomboid-hordes-cleanup.properties");try{if(Files.exists(path))try(var in=Files.newInputStream(path)){p.load(in);}else{Files.createDirectories(path.getParent());Files.writeString(path,"items=true\nhordes=true\nitem-age-seconds=120\nhorde-idle-seconds=300\nitem-player-distance=16\nhorde-player-distance=64\nmax-removals-per-second=8\n");}}catch(IOException ex){System.err.println("[Zomboid cleanup] Config unavailable; using defaults: "+ex.getClass().getSimpleName());}return new Settings(Boolean.parseBoolean(p.getProperty("items","true")),Boolean.parseBoolean(p.getProperty("hordes","true")),number(p,"item-age-seconds",120,30,3600)*20,number(p,"horde-idle-seconds",300,60,7200)*20,number(p,"item-player-distance",16,8,256),number(p,"horde-player-distance",64,32,256),number(p,"max-removals-per-second",8,1,64));}
  static int number(Properties p,String key,int fallback,int min,int max){try{return Math.max(min,Math.min(max,Integer.parseInt(p.getProperty(key,""+fallback))));}catch(NumberFormatException ex){return fallback;}}
 }
 static Object call(Object o,String n,Object...args){return FastReflection.call(o,n,args);}static boolean yes(Object o){return Boolean.TRUE.equals(o);}static long num(Object o){return ((Number)o).longValue();}
 public static void tick(Object event){Object e=call(event,"getEntity");boolean item=ITEM.isInstance(e);if(!(item?settings.items:settings.hordes&&ZOMBIE.isInstance(e)))return;if(Math.floorMod(num(FastReflection.get(e,"tickCount"))+num(call(e,"getId")),100)!=0)return;Object level=call(e,"level");if(yes(FastReflection.get(level,"isClientSide"))||yes(call(e,"isRemoved")))return;long now=num(call(level,"getGameTime"));if(eligible(e,level,now,item,settings)&&permit(level,now,settings.limit))call(e,"discard");}
 static boolean permit(Object level,long now,int limit){Budget b=budgets.computeIfAbsent(level,k->new Budget());long second=now/20;if(b.second!=second){b.second=second;b.count=0;}if(b.count>=limit)return false;b.count++;return true;}
 public static boolean eligible(Object e,Object level,long now,boolean item,Settings s){if(yes(call(e,"hasCustomName")))return false;
  if(item){if(num(call(e,"getAge"))<s.itemAge)return false;Object stack=call(e,"getItem"),component=FastReflection.get(FastReflection.type("net.minecraft.core.component.DataComponents"),"CUSTOM_NAME");if(yes(call(stack,"has",component)))return false;return !near(e,level,s.itemDistance);}
  Object tag=call(e,"getPersistentData");if(!yes(call(tag,"getBoolean","zh_spawned"))||yes(call(tag,"getBoolean","zh_population"))||yes(call(tag,"contains","FactionPveWave"))||yes(call(e,"isPersistenceRequired"))||yes(call(e,"isPassenger"))||yes(call(e,"isVehicle")))return false;
  Object target=call(e,"getTarget");boolean engaged=target!=null&&yes(call(target,"isAlive"))&&((Number)call(e,"distanceToSqr",target)).doubleValue()<=96*96;
  boolean memory=num(call(tag,"getLong","zi_seen_until"))>now||num(call(tag,"getLong","zh_until"))>now||num(call(tag,"getLong","zha_until"))>now;
  if(engaged||memory||activeTicket(level,tag,now)||near(e,level,s.hordeDistance)){call(tag,"putLong","zh_cleanup_idle_since",now);return false;}
  if(!yes(call(tag,"contains","zh_cleanup_idle_since"))){call(tag,"putLong","zh_cleanup_idle_since",now);return false;}
  long since=num(call(tag,"getLong","zh_cleanup_idle_since"));if(since>now){call(tag,"putLong","zh_cleanup_idle_since",now);return false;}return now-since>=s.idleAge;
 }
 static boolean near(Object e,Object level,double distance){for(Object p:(Iterable<?>)call(level,"players")){if(yes(call(p,"isAlive"))&&!yes(call(p,"isSpectator"))&&((Number)call(e,"distanceToSqr",p)).doubleValue()<=distance*distance)return true;}return false;}
}
