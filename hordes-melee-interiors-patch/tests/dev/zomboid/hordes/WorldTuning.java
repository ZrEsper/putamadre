package dev.zomboid.hordes;import java.util.*;
public class WorldTuning {public static Map<String,Double> values=new HashMap<>();public static double get(String k){return values.get(k);}public static boolean flag(String k){return get(k)>0;}public static int revision=1;public static int revision(){return revision;}public static Map<String,Double> snapshot(){return Map.copyOf(values);}}
