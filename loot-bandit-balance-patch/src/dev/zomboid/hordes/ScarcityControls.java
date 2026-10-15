package dev.zomboid.hordes;
import java.util.*;import java.nio.file.*;import java.io.*;
public final class ScarcityControls {
 public static final String[]KEYS={"loot_balance_todos","loot_arma_rara","loot_armadura_rara","loot_municion_rara","loot_suministros_cantidad","bandidos_armados","bandidos_armadura","bandidos_suministros","bandidos_progresion_todos","bandidos_recarga_segundos"};
 public static final double[]DEFAULTS={1,2,2,10,1,2,2,15,1,2};
 public static final Map<String,Double>LOOT=Map.ofEntries(Map.entry("epicos",.6),Map.entry("legendarios",.1),Map.entry("miticos",.01),Map.entry("vision_nocturna",.2),Map.entry("ropa_militar",.5),Map.entry("accesorios_armas",5.),Map.entry("armas_cargadas",10.));
 public static void install(Map<String,Double>d,Map<String,Double>min,Map<String,Double>max,Map<String,Double>v){for(int i=0;i<KEYS.length;i++){String k=KEYS[i];d.put(k,DEFAULTS[i]);min.put(k,i==9?.5:0.);max.put(k,k.endsWith("_todos")?1.:i==4?3.:i==9?10.:100.);v.putIfAbsent(k,DEFAULTS[i]);}
  for(var e:LOOT.entrySet()){d.put(e.getKey(),e.getValue());v.put(e.getKey(),e.getValue());}
  for(String p:LootProfiles.PREFIX){d.put(p+"_armas",1.);v.put(p+"_armas",1.);d.put(p+"_municion",2.);v.put(p+"_municion",2.);d.put(p+"_accesorios",1.);v.put(p+"_accesorios",1.);}
 }
 public static void prepare(Map<String,Double>d){d.putAll(LOOT);for(int i=0;i<KEYS.length;i++)d.put(KEYS[i],DEFAULTS[i]);for(String p:LootProfiles.PREFIX){d.put(p+"_armas",1.);d.put(p+"_municion",2.);d.put(p+"_accesorios",1.);}}
 static boolean migrating;static final Set<Path>attempted=new HashSet<>();
 public static void migrate(){Path file=(Path)ZoneReflection.get(WorldTuning.class,"file");if(file==null||migrating)return;Path mark=file.resolveSibling("zomboid-scarcity33-applied.marker");if(Files.exists(mark)||!attempted.add(file))return;
  migrating=true;try{Map<String,Double>d=new HashMap<>(WorldTuning.snapshot());prepare(d);WorldTuning.batch(d,WorldTuning.revision());Files.writeString(mark,"Loot scarcity applied once. Zombie population, AI, speed, damage and military spawns retained.\n",StandardOpenOption.CREATE_NEW);System.getLogger("Scarcity33").log(System.Logger.Level.INFO,"Loot inicial reducido; ajustes de zombis conservados.");}catch(Exception e){System.getLogger("Scarcity33").log(System.Logger.Level.ERROR,"No se pudo migrar loot: usa Restaurar escasez en el panel.",e);}finally{migrating=false;}
 }
}
