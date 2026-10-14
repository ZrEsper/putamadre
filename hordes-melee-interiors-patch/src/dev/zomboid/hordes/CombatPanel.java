package dev.zomboid.hordes;
import java.util.*;
public final class CombatPanel {
 public static final String[] MELEE={"melee_normal","melee_pesado","melee_empujon","melee_espadas","melee_hachas","melee_sartenes","melee_otros"};
 public static final String ENABLE="interior_spawn_todos",CHANCE="interior_spawn_probabilidad";
 public static void install(Map<String,Double> defs,Map<String,Double> min,Map<String,Double> max,Map<String,Double> values){
  for(String key:MELEE){defs.put(key,1.);min.put(key,.25);max.put(key,4.);values.putIfAbsent(key,1.);}
  defs.put(ENABLE,0.);min.put(ENABLE,0.);max.put(ENABLE,1.);values.putIfAbsent(ENABLE,0.);
  defs.put(CHANCE,20.);min.put(CHANCE,0.);max.put(CHANCE,100.);values.putIfAbsent(CHANCE,20.);
 }
 public static String[][] rows(String[][] old,String which){
  String[][] out=Arrays.copyOf(old,old.length+1);
  String[] extra=which.equals("KEYS")?MELEE:which.equals("LABELS")?new String[]{"Cooldown ataque normal (x)","Cooldown ataque pesado (x)","Cooldown empujon (x)","Cooldown espadas (x)","Cooldown hachas (x)","Cooldown sartenes (x)","Cooldown otras armas (x)"}:new String[]{"0.25-4x: 0.5 = mitad de espera; 2 = doble. Conserva el ajuste original del mod.","Se combina con el multiplicador de la familia del arma.","Cliente y servidor sincronizados. No cambia aturdimiento ni stamina.","Espadas vanilla y armas identificadas como sword.","Hachas, incluida su version melee; no incluye picos.","Sartenes skillet/frying pan.","Bates, cuchillos, herramientas y otros melee. Incluye LR Tactical."};
  out[old.length]=extra;
  if(which.equals("LABELS"))for(int p=4;p<=6;p++){out[p]=Arrays.copyOf(out[p],out[p].length);for(int i=2;i<out[p].length;i++)out[p][i]=out[p][i].replace("(%)","(peso)");}
  String[] interior=which.equals("KEYS")?new String[]{ENABLE,CHANCE}:which.equals("LABELS")?new String[]{"Spawn dentro de casas","Reemplazo interior (%)"}:new String[]{"Opcional: traslada un spawn nuevo cercano a otro cuarto conectado por puertas. Si no hay lugar seguro, conserva el exterior.","0-100% por spawn cercano valido. Nunca el mismo cuarto; usa ruidos reales, sin objetivo asignado."};
  out[2]=Arrays.copyOf(old[2],old[2].length+2);System.arraycopy(interior,0,out[2],old[2].length,2);return out;
 }
 public static String[] tabs(String[] old){String[] out=Arrays.copyOf(old,old.length+1);out[old.length]="Cooldowns melee";return out;}
 public static String switchText(String text,String key){return key.equals(ENABLE)?text.equals("Ejecutar al aplicar")?"Activado":"Desactivado":text;}
 public static String wheel(String key){return key.startsWith("melee_")?"densidad":key;}
 public static double scroll(String key,double value){return key.startsWith("melee_")?Math.max(.25,Math.min(4,value)):SpawnPoolUi.scroll(key,value);}
 static final Map<Object,Integer> presets=new WeakHashMap<>();
 @SuppressWarnings("unchecked") public static boolean preset(Object screen){
  if(((Number)ZoneReflection.get(screen,"page")).intValue()!=9)return SpawnPoolUi.preset(screen);
  try{ZoneReflection.call(screen,"remember");int n=(presets.getOrDefault(screen,-1)+1)%3;presets.put(screen,n);
   Map<String,Double> draft=(Map<String,Double>)ZoneReflection.get(screen,"draft");for(int i=0;i<MELEE.length;i++)draft.put(MELEE[i],i<3?new double[]{.75,1,1.5}[n]:1.);
   ZoneReflection.call(screen,"rebuildWidgets");var f=screen.getClass().getDeclaredField("status");f.setAccessible(true);f.set(screen,new String[]{"Rapido","Original","Lento"}[n]+" preparado. Pulsa Aplicar para guardar.");
  }catch(ReflectiveOperationException e){throw new IllegalStateException(e);}return true;
 }
}
