package dev.zomboid.hordes;
import java.util.*;
public final class ApocalypseSettings {
 public static final String[] MUTANTS={"crawler","blister_zombie","split_head_zombie","rotten_mutant","spitter","zombie_brute","mutant_brute"};
 public static final double[] MUTANT_WEIGHTS={18,25,23,18,10,5,1};
 public static final String[] GLOBAL={"profesion_cadaveres_todos","profesion_spawn_probabilidad","profesion_por_aspecto_todos","ropa_cadaveres_todos","cura_nivel_todos","cura_inyeccion_loot","profesion_loot_cantidad"};
 public static final String[] PROFILES={"Desolado","Medio","Normal","Sobrado","Inicios del apocalipsis"};
 static void put(String k,double v,double low,double high,Map<String,Double>d,Map<String,Double>min,Map<String,Double>max,Map<String,Double>values){d.put(k,v);min.put(k,low);max.put(k,high);values.putIfAbsent(k,v);}
 public static void install(Map<String,Double>d,Map<String,Double>min,Map<String,Double>max,Map<String,Double>values){
  double[] def={1,50,1,1,0,.5,1};double[] hi={1,100,1,1,1,100,10};
  for(int i=0;i<GLOBAL.length;i++)put(GLOBAL[i],def[i],0,hi[i],d,min,max,values);
  for(int i=0;i<ProfessionCatalog.IDS.length;i++)put("cadaver_pool_"+ProfessionCatalog.IDS[i],ProfessionCatalog.WEIGHTS[i],0,100,d,min,max,values);
  for(int i=0;i<MUTANTS.length;i++)put("mutante_pool_"+MUTANTS[i],MUTANT_WEIGHTS[i],0,100,d,min,max,values);
 }
 public static int weighted(double[] w,double roll){double sum=0;for(double x:w)if(Double.isFinite(x)&&x>0)sum+=x;if(sum<=0)return -1;double r=Math.max(0,Math.min(Math.nextDown(1.),roll))*sum;int last=-1;for(int i=0;i<w.length;i++)if(w[i]>0&&Double.isFinite(w[i])){last=i;if((r-=w[i])<0)return i;}return last;}
 public static double[] weights(Map<String,Double>d,String prefix,String[]ids){double[]w=new double[ids.length];for(int i=0;i<w.length;i++)w[i]=d.getOrDefault(prefix+ids[i],0.);return w;}
 public static int count(double base,double factor,double roll,int cap){double n=Math.max(0,base*factor);return Math.min(cap,(int)Math.floor(n)+(roll<n-Math.floor(n)?1:0));}
 public static void preset(Map<String,Double>d,int p){
  if(p<0||p>=5)throw new IllegalArgumentException("perfil");
  // Reset all persistent controls; never execute despawn/cleanup/reset actions.
  @SuppressWarnings("unchecked") Map<String,Double> defs=(Map<String,Double>)ZoneReflection.get(WorldTuning.class,"DEFAULT");d.clear();d.putAll(defs);
  for(String k:new ArrayList<>(d.keySet()))if(Arrays.asList(ZoneSettings.ACTIONS).contains(k)||k.equals("limpiar_objetos_todos"))d.put(k,0.);
  double loot=new double[]{.08,.45,1,2.5,5}[p];
  for(String prefix:LootProfiles.PREFIX){d.put(prefix+"_loot_todos",1.);d.put(prefix+"_cantidad",loot);}
  d.put("profesion_loot_cantidad",1.);d.put("cura_inyeccion_loot",new double[]{.03,.2,.5,2,5}[p]);
  d.put("densidad",new double[]{1.6,1.25,1,.75,.4}[p]);d.put("frecuencia",new double[]{1.4,1.2,1,.8,.5}[p]);
  d.put("mutantes",new double[]{4,2,1,.5,.1}[p]);d.put("limite_mutantes",new double[]{5,3,2,2,1}[p]);
  d.put("pool_tontos",new double[]{20,35,45,60,75}[p]);d.put("pool_brutos",new double[]{40,40,40,30,20}[p]);d.put("pool_astutos",new double[]{40,25,15,10,5}[p]);
  d.put("ia_inteligencia",new double[]{1.5,1.2,1,.8,.6}[p]);d.put("ia_agresividad",new double[]{1.5,1.2,1,.85,.65}[p]);d.put("ia_oido",new double[]{1.5,1.2,1,.85,.7}[p]);
  for(String type:new String[]{"tontos","brutos","astutos"}){d.put("velocidad_"+type,new double[]{1.15,1.05,1,.9,.8}[p]);d.put("dano_"+type,new double[]{1.5,1.2,1,.8,.6}[p]);}
  for(String k:new String[]{"epicos","legendarios","miticos","vision_nocturna","ropa_militar","armas_cargadas","accesorios_armas"})d.put(k,Math.min(100,defs.get(k)*new double[]{.1,.5,1,2,4}[p]));
  d.put("profesion_cadaveres_todos",1.);d.put("profesion_por_aspecto_todos",1.);d.put("ropa_cadaveres_todos",1.);d.put("cura_nivel_todos",0.);
 }
 public static void levelUp(Object player){if(!WorldTuning.flag("cura_nivel_todos"))return;try{ZoneReflection.call(ZoneReflection.type("dev.zomboid.firstaid.ApocalypseMedicine"),"healAll",player);}catch(RuntimeException e){System.getLogger("Apocalypse31").log(System.Logger.Level.ERROR,"Curacion por nivel: FirstAid actualizado requerido",e);}}
}
