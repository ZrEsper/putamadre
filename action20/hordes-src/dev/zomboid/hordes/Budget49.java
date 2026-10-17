package dev.zomboid.hordes;
import java.util.*;
/** Uses the existing active/reserve indexes, not world entity scans. */
public final class Budget49{
 public static final String KEY="ambiente_limite_global";
 static final ThreadLocal<Set<Object>>SEEN=ThreadLocal.withInitial(()->Collections.newSetFromMap(new IdentityHashMap<>()));
 static Object c(Object o,String n,Object...a){return ZoneReflection.call(o,n,a);}
 public static void install(Map<String,Double>d,Map<String,Double>min,Map<String,Double>max,Map<String,Double>values){d.put(KEY,100d);min.put(KEY,1d);max.put(KEY,500d);values.putIfAbsent(KEY,100d);}
 public static void validate(String key,double value){if(KEY.equals(key)&&(!Double.isFinite(value)||value!=Math.rint(value)))throw new IllegalArgumentException("El limite global debe ser un entero de 1 a 500.");}
 public static int cap(){return (int)Math.round(WorldTuning.get(KEY));}
 public static int count(){int n=0;Set<Object>seen=SEEN.get();seen.clear();for(Object s:((Map<?,?>)ZoneReflection.get(ZoneReserve.class,"LEVELS")).values())if(seen.add(s))n+=((Map<?,?>)ZoneReflection.get(s,"active")).size();return n;}
 public static boolean full(){return count()>=cap();}
 public static boolean room(Object state,boolean zoneRoom){return zoneRoom&&count()<cap();}
 public static boolean join(Object e){Object level=c(e,"getLevel");if(Boolean.TRUE.equals(c(level,"isClientSide"))||Boolean.TRUE.equals(c(e,"loadedFromDisk")))return true;Object entity=c(e,"getEntity");if(!Boolean.TRUE.equals(c(ZoneReserve.class,"zombie",entity)))return true;c(ZoneReserve.class,"state",level);if(count()<cap())return true;c(e,"setCanceled",true);return false;}
 public static boolean pre(Object e){Object entity=c(e,"getEntity"),level=c(entity,"level");if(Boolean.TRUE.equals(c(level,"isClientSide"))||!Boolean.TRUE.equals(c(ZoneReserve.class,"zombie",entity))||count()<=cap())return false;Object s=c(ZoneReserve.class,"state",level);if(!((Map<?,?>)ZoneReflection.get(s,"active")).containsKey(c(entity,"getUUID"))||Boolean.TRUE.equals(c(entity,"isPassenger"))||Boolean.TRUE.equals(c(entity,"isVehicle"))||Boolean.TRUE.equals(c(ZoneReserve.class,"protectedNow",s,entity,level)))return false;c(ZoneReserve.class,"queue",s,entity);c(e,"setCanceled",true);return true;}
 public static String[]keys(String[] a){return append(a,KEY);}public static String[]labels(String[]a){return append(a,"Maximo global de zombis activos");}public static String[]icons(String[]a){return append(a,"minecraft:zombie_spawn_egg");}static String[]append(String[]a,String s){String[]b=Arrays.copyOf(a,a.length+1);b[a.length]=s;return b;}
}
