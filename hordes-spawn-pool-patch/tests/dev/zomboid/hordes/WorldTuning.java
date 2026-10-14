package dev.zomboid.hordes;
import java.util.*;
/** Test-only world-control store; never packaged in the mod. */
public class WorldTuning {
 public static final Map<String,Double> values=new HashMap<>();
 public static int revision;
 public static double get(String key){return values.get(key);}
 public static int revision(){return revision;}
 public static void reset(){values.clear();for(int i=0;i<SpawnPool.KEYS.length;i++)values.put(SpawnPool.KEYS[i],SpawnPool.DEFAULTS[i]);revision++;}
}
