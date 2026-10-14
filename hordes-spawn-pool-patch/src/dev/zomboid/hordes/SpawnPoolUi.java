package dev.zomboid.hordes;
import java.util.*;
import java.lang.reflect.*;
/** Screen extensions use the existing draft and explicit Apply action. */
public final class SpawnPoolUi {
 static final Map<Object,Integer> PRESET=new WeakHashMap<>();
 public static final String[] ROWS={"pool_tontos","pool_brutos","pool_astutos","ia_inteligencia","ia_agresividad","ia_oido","frecuencia","densidad","agresivos","vision"};
 public static String[][] rows(String[][] old,String which){
  String[] extra=which.equals("KEYS")?ROWS:which.equals("LABELS")?new String[]{"Tontos: peso","Brutos: peso","Astutos: peso","Inteligencia / reaccion (x)","Agresividad de la IA (x)","Oido (x)","Velocidad de aparicion (x)","Cantidad de zombis (x)","Zombis enfurecidos (%)","Distancia de vision"}:new String[]{
   "0 excluye este tipo. Se normalizan los tres pesos; 80/15/5 = 80% tontos. Solo nuevos zombis.",
   "0 excluye este tipo. Brutos: reaccion intermedia. No es la especie mutant_brute.",
   "0 excluye este tipo. Astutos: deteccion rapida y comportamiento inteligente existente.",
   "0.25-3x: velocidad de reaccion y memoria. El tipo de personalidad se elige con los pesos.",
   "0.25-3x: mayor valor = ataques mas frecuentes y persecucion mas persistente. No cambia el dano.",
   "0-3x: alcance de audicion individual; 0 = sordos. Incluye ruido de disparos y acciones.",
   "0.1-10x: reposicion y oleadas. Menor = aparicion mas lenta.",
   "0.1-10x: poblacion objetivo. Conserva el limite de zona y las reservas.",
   "-1 = original; 0-100% activa el perfil adicional rapido/fuerte. Es distinto de la agresividad IA.",
   "-1 = original; 0 = sin vision; hasta 128 bloques."};
  String[][] result=Arrays.copyOf(old,old.length+2);result[old.length]=extra;
  result[old.length+1]=which.equals("KEYS")?new String[]{"velocidad_tontos","velocidad_brutos","velocidad_astutos","dano_tontos","dano_brutos","dano_astutos"}:which.equals("LABELS")?new String[]{"Velocidad: tontos (x)","Velocidad: brutos (x)","Velocidad: astutos (x)","Dano: tontos (x)","Dano: brutos (x)","Dano: astutos (x)"}:new String[]{"0-3x la velocidad original; 0 = inmovil.","0-3x. Independiente de los pesos de aparicion.","0-3x. Mantiene el ritmo aleatorio original de cada zombi.","0-5x el dano original; 0 = sin dano cuerpo a cuerpo. No cambia ataques de proyectiles externos.","0-5x. Conserva los modificadores originales del zombi.","0-5x. Se aplica a zombis existentes y nuevos."};
  return result;
 }
 public static String[] tabs(String[] old){String[] a=Arrays.copyOf(old,old.length+2);a[old.length]="Spawn e IA";a[old.length+1]="Velocidad y dano";return a;}
 public static String buttonText(String original,Object screen){return ((Number)ZoneReflection.get(screen,"page")).intValue()>=7?"Cambiar preset":original;}
 @SuppressWarnings("unchecked") public static boolean preset(Object screen){
  if(((Number)ZoneReflection.get(screen,"page")).intValue()<7)return false;
  try{
   ZoneReflection.call(screen,"remember");
   int p=PRESET.getOrDefault(screen,-1)+1;p%=3;PRESET.put(screen,p);
   Map<String,Double> draft=(Map<String,Double>)ZoneReflection.get(screen,"draft");
   double[][] presets={{80,15,5,.65,.65,.65,.7,.7,0,-1},{45,40,15,1,1,1,1,1,-1,-1},{15,35,50,1.6,1.6,1.5,1.2,1.2,20,-1}};
   if(((Number)ZoneReflection.get(screen,"page")).intValue()==7){for(int i=0;i<ROWS.length;i++)draft.put(ROWS[i],presets[p][i]);}
   else {double[][] physical={{.75,.85,.95,.65,.8,.9},{1,1,1,1,1,1},{1.1,1,1.25,1.2,1.6,1.35}};for(int i=0;i<6;i++)draft.put(SpawnPool.KEYS[i+6],physical[p][i]);}
   ZoneReflection.call(screen,"rebuildWidgets");
   set(screen,"status",new String[]{"Facil","Equilibrado","Dificil"}[p]+" preparado. Pulsa Aplicar para guardar.");
  }catch(RuntimeException e){set(screen,"status",e.getMessage());}
  return true;
 }
 public static String summary(Map<String,Double> draft){double[] p=SpawnPool.percentages(draft);return String.format(Locale.ROOT,"Mezcla: %.1f%% tontos / %.1f%% brutos / %.1f%% astutos. Solo nuevos zombis.",p[0],p[1],p[2]);}
 @SuppressWarnings("unchecked") public static String status(String text,Object screen){
  if(((Number)ZoneReflection.get(screen,"page")).intValue()!=7)return text;
  Map<String,Double> values=new HashMap<>((Map<String,Double>)ZoneReflection.get(screen,"draft"));
  try{Map<String,Object> boxes=(Map<String,Object>)ZoneReflection.get(screen,"boxes");for(int i=0;i<3;i++){Object box=boxes.get(SpawnPool.KEYS[i]);if(box!=null)values.put(SpawnPool.KEYS[i],Double.parseDouble(String.valueOf(ZoneReflection.call(box,"getValue"))));}
   SpawnPool.validate(values);return text+"\n"+summary(values);
  }catch(RuntimeException e){return "Corrige los pesos: al menos uno debe ser mayor que 0. "+text;}
 }
 public static double scroll(String key,double value){
  for(int i=3;i<6;i++)if(key.equals(SpawnPool.KEYS[i]))return Math.max(i==5?0:.25,Math.min(3,value));
  if(key.startsWith("velocidad_"))return Math.max(0,Math.min(3,value));
  if(key.startsWith("dano_"))return Math.max(0,Math.min(5,value));
  return value;
 }
 static void set(Object screen,String key,Object value){
  try{Field f=screen.getClass().getDeclaredField(key);f.setAccessible(true);f.set(screen,value);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}
 }
}
