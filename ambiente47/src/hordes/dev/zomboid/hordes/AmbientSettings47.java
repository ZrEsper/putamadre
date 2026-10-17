package dev.zomboid.hordes;
import java.util.*;
public final class AmbientSettings47 {
 public static final String TOTAL="ambiente_limite_total",GROUP="ambiente_limite_grupo",HEAT="ambiente_ruido_todos";
 public static void install(Map<String,Double>d,Map<String,Double>min,Map<String,Double>max,Map<String,Double>v){add(d,min,max,v,TOTAL,80,1,80);add(d,min,max,v,GROUP,50,1,80);add(d,min,max,v,HEAT,1,0,1);}
 static void add(Map<String,Double>d,Map<String,Double>min,Map<String,Double>max,Map<String,Double>v,String k,double n,double lo,double hi){d.put(k,n);min.put(k,lo);max.put(k,hi);v.putIfAbsent(k,n);}
 public static void validate(String k,double n){if((k.equals(TOTAL)||k.equals(GROUP)||k.equals(HEAT))&&(!Double.isFinite(n)||n!=Math.rint(n)))throw new IllegalArgumentException("El límite debe ser un número entero.");}
 public static void validateBatch(Map<String,Double>d){if(d.getOrDefault(GROUP,50.)>d.getOrDefault(TOTAL,80.))throw new IllegalArgumentException("El grupo no puede superar el total de los 9 chunks.");}
 public static void applied(){ZoneReserve.limit=256;}
 public static boolean enabled(){return WorldTuning.snapshot().containsKey(TOTAL);}
 public static int total(){return (int)WorldTuning.get(TOTAL);}public static int group(){return Math.min(total(),(int)WorldTuning.get(GROUP));}
 public static int desired(double density,int cap,double heat){return Math.max(0,Math.min(cap,(int)Math.round(cap*(Math.min(1,Math.max(0,density)*.65)+(1-Math.min(1,Math.max(0,density)*.65))*Math.max(0,Math.min(100,heat))/100))));}
 public static void preset(Map<String,Double>d,int profile){d.put("poblacion_pausada_todos",0.);d.put("limite_zona",256.);d.put("ambiente_ruido_todos",1.);}
 public static boolean window(int cx,int cz,int px,int pz){return Math.abs((long)cx-px)<=1&&Math.abs((long)cz-pz)<=1;}
}
