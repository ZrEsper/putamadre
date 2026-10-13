package dev.zomboid.hordes;
import java.util.*;
public final class ZoneSettings {
 public static Map<String,Double> mutable(Map<String,Double> map){return new HashMap<>(map);}
 public static final String PAUSE="poblacion_pausada_todos";
 public static final String LIMIT="limite_zona";
 public static final String[] ACTIONS={"despawn_zombis_todos","regenerar_zombis_todos","limpiar_restos_todos","reiniciar_loot_todos"};
 public static void install(Map<String,Double> defaults,Map<String,Double> min,Map<String,Double> max,Map<String,Double> values){ItemCleanup.install(defaults,min,max,values);LootProfiles.install(defaults,min,max,values);defaults.put(LIMIT,32d);min.put(LIMIT,1d);max.put(LIMIT,256d);values.putIfAbsent(LIMIT,32d);defaults.put(PAUSE,0d);min.put(PAUSE,0d);max.put(PAUSE,1d);values.putIfAbsent(PAUSE,0d);for(String k:ACTIONS){defaults.put(k,0d);min.put(k,0d);max.put(k,1d);values.putIfAbsent(k,0d);}}
 public static Map<String,Double> persistence(Map<String,Double> values){ItemCleanup.validate(values);LootProfiles.validate(values,ZoneReflection.get(WorldTuning.class,"server"));Map<String,Double> copy=new HashMap<>(values);copy.put(ItemCleanup.KEYS[6],0d);if(values.getOrDefault(ACTIONS[1],0d)>0)copy.put(PAUSE,0d);else if(values.getOrDefault(ACTIONS[0],0d)>0)copy.put(PAUSE,1d);for(String k:ACTIONS)copy.put(k,0d);return copy;}
 public static double checked(String key,double value){if(key.equals(LIMIT)&&value!=Math.rint(value))throw new IllegalArgumentException("El límite de zona debe ser un número entero.");return value;}
 public static void applied(Map<String,Double> values,Object server){ItemCleanup.applied(values,server);boolean[] actions=new boolean[ACTIONS.length];for(int i=0;i<ACTIONS.length;i++){actions[i]=values.getOrDefault(ACTIONS[i],0d)>0;values.put(ACTIONS[i],0d);}if(actions[1])values.put(PAUSE,0d);else if(actions[0])values.put(PAUSE,1d);ZoneReserve.limit=(int)Math.rint(values.getOrDefault(LIMIT,32d));if(server!=null)ZoneReserve.pauseAll(server,values.getOrDefault(PAUSE,0d)>0);if(server!=null&&Arrays.toString(actions).contains("true"))ZoneReserve.maintenance(server,actions);}
}
