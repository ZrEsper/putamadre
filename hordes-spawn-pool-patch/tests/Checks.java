import dev.zomboid.hordes.*;
import java.util.*;
public class Checks {
 static int checks;
 static void check(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 static void eq(double a,double b,String s){check(Math.abs(a-b)<1e-8,s+" actual="+a);}
 static void bad(Runnable r,String s){boolean rejected=false;try{r.run();}catch(IllegalArgumentException e){rejected=true;}check(rejected,s);}
 public static class Tag{
  public final Map<String,Object> data=new HashMap<>();
  public boolean contains(String k){return data.containsKey(k);}
  public int getInt(String k){Object v=data.get(k);return v instanceof Number?((Number)v).intValue():0;}
  public double getDouble(String k){Object v=data.get(k);return v instanceof Number?((Number)v).doubleValue():0;}
  public boolean getBoolean(String k){return Boolean.TRUE.equals(data.get(k));}
  public void putInt(String k,int v){data.put(k,v);}public void putDouble(String k,double v){data.put(k,v);}public void putBoolean(String k,boolean v){data.put(k,v);}public void remove(String k){data.remove(k);}
 }
 public static class Mob{
  public final Tag tag=new Tag();public final Random random=new Random(90);
  public Tag getPersistentData(){return tag;}public Random getRandom(){return random;}
 }
 public static class Box{private final String value;public Box(String value){this.value=value;}public String getValue(){return value;}}
 public static class Screen{
  private int page=7;private Map<String,Double> draft=new HashMap<>();private String status="";private Map<String,Object> boxes=new HashMap<>();public int rebuilds;
  public Screen(){draft.putAll(WorldTuning.values);draft.put("cofres_armas",77.);}
  public void remember(){}public void rebuildWidgets(){rebuilds++;}
 }
 static Mob fresh(){Mob m=new Mob();SpawnPool.prepare(m.tag);m.tag.putInt("zi_profile_version",2);m.tag.putInt("zi_type",0);m.tag.putInt("zi_delay",10);m.tag.putDouble("zi_hearing",1.2);return m;}
 public static void main(String[] args){
  WorldTuning.reset();
  Map<String,Double> defs=new HashMap<>(),min=new HashMap<>(),max=new HashMap<>(),values=new HashMap<>();
  SpawnPool.install(defs,min,max,values);
  check(defs.size()==12&&values.size()==12,"all keys installed for persistence and batch");
  eq(min.get("ia_inteligencia"),.25,"reaction minimum");eq(min.get("ia_oido"),0,"deaf allowed");eq(max.get("dano_brutos"),5,"damage limit");
  SpawnPool.validate(values);
  Map<String,Double> invalid=new HashMap<>(values);for(int i=0;i<3;i++)invalid.put(SpawnPool.KEYS[i],0.);
  bad(()->SpawnPool.validate(invalid),"empty pool rejected before save");
  invalid.put("pool_tontos",20.);invalid.put("ia_oido",Double.NaN);bad(()->SpawnPool.validate(invalid),"nonfinite rejected");
  invalid.put("ia_oido",4.);bad(()->SpawnPool.validate(invalid),"hearing limit enforced");
  for(int t=0;t<3;t++){double[] w={0,0,0};w[t]=100;for(double r:new double[]{0,.1,.5,.999999,1})check(SpawnPool.choose(r,w[0],w[1],w[2])==t,"exclusive type even on boundaries");}
  int[] tally=new int[3];for(int i=0;i<10000;i++)tally[SpawnPool.choose((i+.5)/10000,80,15,5)]++;
  check(Arrays.equals(tally,new int[]{8000,1500,500}),"80/15/5 normalized pool distribution");
  tally=new int[3];for(int i=0;i<10000;i++)tally[SpawnPool.choose((i+.5)/10000,8,1.5,.5)]++;
  check(Arrays.equals(tally,new int[]{8000,1500,500}),"weights need not sum to 100");
  eq(SpawnPool.percentages(Map.of("pool_tontos",8.,"pool_brutos",1.5,"pool_astutos",.5))[0],80,"preview uses actual distribution");
  bad(()->SpawnPool.choose(.5,0,0,0),"invalid sampling rejected");
  eq(SpawnPool.scaledDelay(10,.5),20,"low intelligence slower reaction");eq(SpawnPool.scaledDelay(10,2),5,"high intelligence faster reaction");
  eq(SpawnPool.memory(1,1,1),180,"default pursuit unchanged");check(SpawnPool.memory(1,.5,.5)<180,"easy pursuit shorter");check(SpawnPool.memory(1,2,2)>180,"hard pursuit longer");
  eq(SpawnPool.attackInterval(28,1),28,"original attack cadence");eq(SpawnPool.attackInterval(28,.5),56,"passive slower attacks");eq(SpawnPool.attackInterval(28,2),14,"aggressive faster attacks");
  eq(SpawnPool.hearing(1.2,0),0,"deaf");eq(SpawnPool.hearing(1.2,.5),.6,"quiet hearing");eq(SpawnPool.hearing(1.2,2),2.4,"enhanced hearing");
  eq(SpawnPool.damageModifier(.15,.5),-.425,"damage reduction keeps native smart bonus");eq(SpawnPool.damageModifier(.15,0),-1,"zero damage is negative full reduction");eq(SpawnPool.damageModifier(.15,2),1.3,"damage increases keep native bonus");
  WorldTuning.values.put("pool_tontos",0.);WorldTuning.values.put("pool_brutos",100.);WorldTuning.values.put("pool_astutos",0.);
  Mob newborn=fresh();SpawnPool.apply(newborn,newborn.tag);check(newborn.tag.getInt("zi_type")==1,"newborn follows final pool after native assignments");
  check(newborn.tag.getInt("zpool_version")==1&&!newborn.tag.contains("zpool_new"),"stable persistent marker");
  double baseH=newborn.tag.getDouble("zi_hearing");int baseD=newborn.tag.getInt("zi_delay");
  for(int i=0;i<100;i++)SpawnPool.apply(newborn,newborn.tag);
  eq(newborn.tag.getDouble("zi_hearing"),baseH,"hearing never compounds");eq(newborn.tag.getInt("zi_delay"),baseD,"delay never compounds");
  WorldTuning.values.put("ia_oido",2.);WorldTuning.values.put("ia_inteligencia",.5);WorldTuning.revision++;
  SpawnPool.apply(newborn,newborn.tag);eq(newborn.tag.getDouble("zi_hearing"),baseH*2,"live hearing update");eq(newborn.tag.getInt("zi_delay"),baseD*2,"live intelligence update");
  WorldTuning.values.put("ia_oido",0.);WorldTuning.revision++;SpawnPool.apply(newborn,newborn.tag);eq(newborn.tag.getDouble("zi_hearing"),0,"live deafness");
  WorldTuning.values.put("ia_oido",1.);SpawnPool.apply(newborn,newborn.tag);eq(newborn.tag.getDouble("zi_hearing"),baseH,"reload with same revision updates ears");
  WorldTuning.values.put("pool_brutos",0.);WorldTuning.values.put("pool_astutos",100.);WorldTuning.revision++;SpawnPool.apply(newborn,newborn.tag);check(newborn.tag.getInt("zi_type")==1,"new weights do not reroll existing personality");
  Mob next=fresh();SpawnPool.apply(next,next.tag);check(next.tag.getInt("zi_type")==2,"new weights affect new zombies");
  Mob old=new Mob();old.tag.putInt("zi_profile_version",2);old.tag.putInt("zi_type",0);old.tag.putDouble("zi_hearing",1.1);old.tag.putInt("zi_delay",8);SpawnPool.prepare(old.tag);SpawnPool.apply(old,old.tag);check(old.tag.getInt("zi_type")==0,"old saved zombie personality preserved");
  WorldTuning.values.put("velocidad_tontos",.5);eq(SpawnPool.speed(.28,old),.14,"tonto speed independent");eq(SpawnPool.speed(.28,next),.28,"astuto speed unaffected");
  WorldTuning.values.put("velocidad_astutos",0.);eq(SpawnPool.speed(.28,next),0,"zero speed allowed");WorldTuning.values.put("dano_brutos",2.);eq(SpawnPool.damageModifier(0.,newborn),1,"bruto damage independent");
  eq(SpawnPoolUi.scroll("ia_agresividad",9),3,"wheel maximum");eq(SpawnPoolUi.scroll("ia_inteligencia",0),.25,"wheel minimum");eq(SpawnPoolUi.scroll("dano_tontos",8),5,"damage wheel maximum");
  check(SpawnPool.wheelKey("velocidad_brutos").equals("densidad"),"fine wheel speed");
  String[] tabs=SpawnPoolUi.tabs(new String[]{"0","1","2","3","4","5","6"});check(tabs.length==9&&tabs[8].equals("Velocidad y dano"),"both tabs present");
  for(String kind:new String[]{"KEYS","LABELS","HINTS"}){String[][] a=SpawnPoolUi.rows(new String[7][],kind);check(a.length==9&&a[7].length==10&&a[8].length==6,"matching grid "+kind);}
  WorldTuning.reset();Screen screen=new Screen();check(SpawnPoolUi.preset(screen),"easy preset consumed");eq(screen.draft.get("pool_tontos"),80,"easy majority");eq(screen.draft.get("cofres_armas"),77,"preset preserves loot");check(screen.status.contains("Aplicar"),"preset stages before save");
  SpawnPoolUi.preset(screen);eq(screen.draft.get("pool_astutos"),15,"balanced preset");SpawnPoolUi.preset(screen);eq(screen.draft.get("pool_astutos"),50,"hard preset");SpawnPoolUi.preset(screen);eq(screen.draft.get("pool_tontos"),80,"preset cycles");
  screen.page=8;SpawnPoolUi.preset(screen);check(screen.draft.containsKey("velocidad_brutos"),"physical preset fields");eq(screen.draft.get("pool_tontos"),80,"physical preset preserves pool");screen.page=4;check(!SpawnPoolUi.preset(screen),"loot preview remains original");
  screen.page=7;screen.boxes.put("pool_tontos",new Box("50"));screen.boxes.put("pool_brutos",new Box("50"));screen.boxes.put("pool_astutos",new Box("0"));
  String preview=SpawnPoolUi.status("Cambios aplicados",screen);check(preview.contains("50.0% tontos")&&preview.contains("50.0% brutos"),"live boxes normalized");check(preview.contains("Cambios aplicados"),"live preview preserves server result");
  for(String key:new String[]{"pool_tontos","pool_brutos","pool_astutos"})screen.boxes.put(key,new Box("0"));check(SpawnPoolUi.status("Error al guardar",screen).contains("Error al guardar"),"invalid draft keeps error");
  Map<String,Double> corrupt=new HashMap<>(values);for(int i=0;i<3;i++)corrupt.put(SpawnPool.KEYS[i],0.);SpawnPool.sanitize(corrupt);eq(corrupt.get("pool_tontos"),45,"invalid saved pool recovers");SpawnPool.sanitize(values);eq(values.get("pool_brutos"),40,"valid saved pool unchanged");
  System.out.println("Passed "+checks+" behavior checks using test-only world/entity/screen doubles; no Minecraft runtime.");
 }
}
