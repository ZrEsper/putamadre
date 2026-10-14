package dev.zomboid.hordes;
import java.util.*;
/** Server-authoritative weighted personalities. Existing entities keep their personality. */
public final class SpawnPool {
 public static final String[] KEYS={"pool_tontos","pool_brutos","pool_astutos","ia_inteligencia","ia_agresividad","ia_oido","velocidad_tontos","velocidad_brutos","velocidad_astutos","dano_tontos","dano_brutos","dano_astutos"};
 public static final double[] DEFAULTS={45,40,15,1,1,1,1,1,1,1,1,1};
 public static void install(Map<String,Double> defs,Map<String,Double> min,Map<String,Double> max,Map<String,Double> values){
  for(int i=0;i<KEYS.length;i++){defs.put(KEYS[i],DEFAULTS[i]);min.put(KEYS[i],i<3||i>=5?0.:.25);max.put(KEYS[i],i<3?100.:i>=9?5.:3.);values.putIfAbsent(KEYS[i],DEFAULTS[i]);}
 }
 public static void validate(Map<String,Double> values){
  for(int i=0;i<KEYS.length;i++){double v=values.getOrDefault(KEYS[i],DEFAULTS[i]);if(!Double.isFinite(v)||v<(i<3||i>=5?0:.25)||v>(i<3?100:i>=9?5:3))throw new IllegalArgumentException("Valor fuera de rango: "+KEYS[i]);}
  if(values.getOrDefault(KEYS[0],45.)+values.getOrDefault(KEYS[1],40.)+values.getOrDefault(KEYS[2],15.)<=0)throw new IllegalArgumentException("Deja al menos un tipo de zombi con peso mayor que 0.");
 }
 public static void sanitize(Map<String,Double> values){
  double total=values.getOrDefault(KEYS[0],45.)+values.getOrDefault(KEYS[1],40.)+values.getOrDefault(KEYS[2],15.);
  if(!Double.isFinite(total)||total<=0){for(int i=0;i<3;i++)values.put(KEYS[i],DEFAULTS[i]);System.err.println("[Zomboid Hordes] Pool guardado vacio; restaurando mezcla 45/40/15.");}
 }
 public static double[] percentages(Map<String,Double> values){
  double[] p=new double[3];double total=0;for(int i=0;i<3;i++){p[i]=values.getOrDefault(KEYS[i],DEFAULTS[i]);total+=p[i];}
  if(!Double.isFinite(total)||total<=0)return new double[]{45,40,15};
  for(int i=0;i<3;i++)p[i]=100*p[i]/total;return p;
 }
 public static int choose(double random,double a,double b,double c){
  double total=a+b+c;if(!Double.isFinite(total)||total<=0||a<0||b<0||c<0)throw new IllegalArgumentException("Pool de zombis vacio o invalido");
  double r=Math.max(0,Math.min(Math.nextDown(1.),Double.isFinite(random)?random:0))*total;
  if(r<a)return 0;if(r<a+b)return 1;return 2;
 }
 public static int scaledDelay(int ticks,double intelligence){return Math.max(0,Math.min(200,(int)Math.round(ticks/Math.max(.25,Math.min(3,intelligence)))));}
 public static int memory(int type){return memory(type,WorldTuning.get("ia_inteligencia"),WorldTuning.get("ia_agresividad"));}
 public static int memory(int type,double intelligence,double aggression){return (int)Math.round((type==0?120:type==1?180:240)*Math.sqrt(intelligence)*aggression);}
 public static int windup(int type){return attackInterval(type==0?12:type==1?10:8,WorldTuning.get("ia_agresividad"));}
 public static int cooldown(int type){return attackInterval(type==0?28:type==1?22:18,WorldTuning.get("ia_agresividad"));}
 public static int attackInterval(int ticks,double aggression){return Math.max(4,(int)Math.round(ticks/Math.max(.25,Math.min(3,aggression))));}
 public static double hearing(double base,double factor){return Math.max(0,Math.min(8,base*factor));}
 public static void prepare(Object tag){
  if(!has(tag,"zpool_version"))put(tag,"putBoolean","zpool_new",!has(tag,"zi_profile_version"));
 }
 public static void apply(Object mob,Object tag){
  if(!has(tag,"zpool_version")){
   if(Boolean.TRUE.equals(ZoneReflection.call(tag,"getBoolean","zpool_new"))){
    Object rng=ZoneReflection.call(mob,"getRandom");
    int type=choose(ZoneReflection.num(ZoneReflection.call(rng,"nextDouble")),WorldTuning.get(KEYS[0]),WorldTuning.get(KEYS[1]),WorldTuning.get(KEYS[2]));
    put(tag,"putInt","zi_type",type);
    int delay=type==0?6+((Number)ZoneReflection.call(rng,"nextInt",10)).intValue():type==1?2+((Number)ZoneReflection.call(rng,"nextInt",5)).intValue():((Number)ZoneReflection.call(rng,"nextInt",4)).intValue();
    put(tag,"putInt","zi_delay",delay);
   }
   put(tag,"putInt","zpool_base_delay",((Number)ZoneReflection.call(tag,"getInt","zi_delay")).intValue());
   put(tag,"putDouble","zpool_base_hearing",ZoneReflection.num(ZoneReflection.call(tag,"getDouble","zi_hearing")));
   put(tag,"putInt","zpool_version",1);ZoneReflection.call(tag,"remove","zpool_new");
  }
  int revision=WorldTuning.revision();
  // Read two scalars so world reload with the same revision cannot leave stale cached values.
  double intelligence=WorldTuning.get(KEYS[3]),ears=WorldTuning.get(KEYS[5]);
  if(has(tag,"zpool_applied")&&ZoneReflection.num(ZoneReflection.call(tag,"getInt","zpool_applied"))==revision&&ZoneReflection.num(ZoneReflection.call(tag,"getDouble","zpool_intelligence"))==intelligence&&ZoneReflection.num(ZoneReflection.call(tag,"getDouble","zpool_ears"))==ears)return;
  put(tag,"putInt","zi_delay",scaledDelay(((Number)ZoneReflection.call(tag,"getInt","zpool_base_delay")).intValue(),intelligence));
  put(tag,"putDouble","zi_hearing",hearing(ZoneReflection.num(ZoneReflection.call(tag,"getDouble","zpool_base_hearing")),ears));
  put(tag,"putInt","zpool_applied",revision);put(tag,"putDouble","zpool_intelligence",intelligence);put(tag,"putDouble","zpool_ears",ears);
 }
 public static double speed(double base,Object mob){return base*factor(mob,"velocidad_");}
 public static double damageModifier(double original,Object mob){return (1+original)*factor(mob,"dano_")-1;}
 public static double damageModifier(double original,double factor){return (1+original)*factor-1;}
 static double factor(Object mob,String prefix){Object tag=ZoneReflection.call(mob,"getPersistentData");int type=((Number)ZoneReflection.call(tag,"getInt","zi_type")).intValue();return WorldTuning.get(prefix+(type==0?"tontos":type==1?"brutos":"astutos"));}
 public static String wheelKey(String key){return key.startsWith("ia_")||key.startsWith("velocidad_")||key.startsWith("dano_")?"densidad":key;}
 static boolean has(Object tag,String key){return Boolean.TRUE.equals(ZoneReflection.call(tag,"contains",key));}
 static void put(Object tag,String method,String key,Object value){ZoneReflection.call(tag,method,key,value);}
}
