package dev.zomboid.hordes;
import java.nio.file.*;import java.io.*;import java.util.*;
public final class CorpseProfessions {
 static final String ROLE="zh31_profession",CHECK="zh31_profession_checked";static Path catalogPath;static long stamp=-1,lastCheck;static Properties catalog=new Properties();static final Set<String>warnings=new HashSet<>();
 static Object c(Object o,String n,Object...a){return ZoneReflection.call(o,n,a);}static boolean yes(Object x){return Boolean.TRUE.equals(x);}static Object tag(Object e){return c(e,"getPersistentData");}static double roll(Object level){return ((Number)c(ZoneReflection.get(level,"random"),"nextDouble")).doubleValue();}
 static int index(String id){return Arrays.asList(ProfessionCatalog.IDS).indexOf(id);}
 static Properties defaults(){Properties p=new Properties();for(int i=0;i<ProfessionCatalog.IDS.length;i++){String id=ProfessionCatalog.IDS[i];p.setProperty(id+".loot",ProfessionCatalog.LOOT[i]);p.setProperty(id+".clothes",ProfessionCatalog.CLOTHES[i]);p.setProperty(id+".names",id+","+ProfessionCatalog.LABELS[i].toLowerCase(Locale.ROOT));p.setProperty(id+".chance","100");p.setProperty(id+".amount","1");}p.setProperty("military.names","military,militar,soldier,soldado,army");p.setProperty("doctor.names","doctor,medico,médico");p.setProperty("mechanic.names","mechanic,mecanico,mecánico");p.setProperty("construction_worker.names","construction,constructor,obrero");p.setProperty("nude.names","nude,naked,desnudo");return p;}
 static synchronized Properties settings(){
  Path f=(Path)ZoneReflection.get(WorldTuning.class,"file");if(f==null)return defaults();Path path=f.resolveSibling("zomboid-apocalypse-corpse-catalog.properties");long now=System.nanoTime();if(path.equals(catalogPath)&&now-lastCheck<2_000_000_000L)return catalog;lastCheck=now;
  try{long modified=Files.exists(path)?Files.getLastModifiedTime(path).toMillis():-1;if(path.equals(catalogPath)&&modified==stamp)return catalog;
   Properties p=defaults();Files.createDirectories(path.getParent());if(Files.exists(path)){try(Reader reader=Files.newBufferedReader(path)){p.load(reader);}}else{try(Writer writer=Files.newBufferedWriter(path,StandardOpenOption.CREATE_NEW)){p.store(writer,"Loot professionnel: id@cantidad separados por comas; names=palabras de equipo/nombre; chance=0..100; amount=0..10. UTF-8.");}}
   for(String id:ProfessionCatalog.IDS){double chance=Double.parseDouble(p.getProperty(id+".chance")),amount=Double.parseDouble(p.getProperty(id+".amount"));if(!Double.isFinite(chance)||chance<0||chance>100||!Double.isFinite(amount)||amount<0||amount>10)throw new IOException("Rango invalido: "+id);}
   catalog=p;catalogPath=path;stamp=Files.getLastModifiedTime(path).toMillis();System.getLogger("Apocalypse31").log(System.Logger.Level.INFO,"Catalogo de cadaveres cargado: "+path);
  }catch(Exception e){warn("catalogo",e);if(!path.equals(catalogPath)){catalog=defaults();catalogPath=path;stamp=-1;}}return catalog;
 }
 static void warn(String key,Throwable e){if(warnings.add(key))System.getLogger("Apocalypse31").log(System.Logger.Level.WARNING,key,e);}
 static String itemId(Object stack){return String.valueOf(c(ZoneReflection.get(ZoneReflection.type("net.minecraft.core.registries.BuiltInRegistries"),"ITEM"),"getKey",c(stack,"getItem")));}
 static Object stack(String spec){String[]s=spec.strip().split("@");String id=s[0].contains(":")?s[0]:"minecraft:"+s[0];int count=s.length>1?Integer.parseInt(s[1]):1;if(count<1||count>64)throw new IllegalArgumentException("Cantidad: "+spec);Object reg=ZoneReflection.get(ZoneReflection.type("net.minecraft.core.registries.BuiltInRegistries"),"ITEM");Object key=c(ZoneReflection.type("net.minecraft.resources.ResourceLocation"),"parse",id);if(!yes(c(reg,"containsKey",key)))return null;Object item=c(reg,"get",key);if(id.equals("minecraft:air"))return null;try{return ZoneReflection.type("net.minecraft.world.item.ItemStack").getConstructor(ZoneReflection.type("net.minecraft.world.level.ItemLike"),int.class).newInstance(item,Math.min(count,((Number)c(item,"getDefaultMaxStackSize")).intValue()));}catch(Exception e){throw new IllegalStateException(e);}}
 static List<Object> armor(Object entity){List<Object>list=new ArrayList<>();for(Object s:(Iterable<?>)c(entity,"getArmorSlots"))if(!yes(c(s,"isEmpty")))list.add(c(s,"copy"));return list;}
 static int recognize(Object entity){
  Object t=tag(entity);String explicit=String.valueOf(c(t,"getString",ROLE));
  StringBuilder hints=new StringBuilder();Object name=c(entity,"getCustomName");if(name!=null)hints.append(c(name,"getString")).append(' ');
  for(String k:new String[]{"profession","Profession","skin","Skin","outfit","Outfit"})hints.append(c(t,"getString",k)).append(' ');
  for(Object s:armor(entity))hints.append(itemId(s)).append(' ');
  try{if(entity.getClass().getName().contains("ZombieVillager")){Object data=c(entity,"getVillagerData");Object profession=c(data,"getProfession");Object reg=ZoneReflection.get(ZoneReflection.type("net.minecraft.core.registries.BuiltInRegistries"),"VILLAGER_PROFESSION");String id=String.valueOf(c(reg,"getKey",profession));String p=id.substring(id.indexOf(':')+1);String map=switch(p){case "armorer","toolsmith","weaponsmith"->"blacksmith";case "mason"->"construction_worker";case "leatherworker"->"tailor";case "cleric"->"doctor";default->p;};if(index(map)>=0)return index(map);}}catch(RuntimeException e){warn("profesion de aldeano",e);}
  String text=hints.toString().toLowerCase(Locale.ROOT);Properties p=settings();for(String preferred:new String[]{"nude","military","doctor","police_officer"})if(matches(text,p.getProperty(preferred+".names")))return index(preferred);
  for(int i=0;i<ProfessionCatalog.IDS.length;i++)if(matches(text,p.getProperty(ProfessionCatalog.IDS[i]+".names")))return i;return index(explicit);
 }
 static boolean matches(String text,String rules){for(String token:rules.split(","))if(!token.isBlank()&&text.contains(token.strip().toLowerCase(Locale.ROOT)))return true;return false;}
 static int choose(Object level){return ApocalypseSettings.weighted(ApocalypseSettings.weights(WorldTuning.snapshot(),"cadaver_pool_",ProfessionCatalog.IDS),roll(level));}
 public static void assign(Object entity){try{
  Object level=c(entity,"level");if(yes(c(level,"isClientSide")))return;Object t=tag(entity);if(yes(c(t,"getBoolean",CHECK)))return;c(t,"putBoolean",CHECK,true);
  if(!WorldTuning.flag("profesion_cadaveres_todos"))return;int i=WorldTuning.flag("profesion_por_aspecto_todos")?recognize(entity):-1;if(i<0&&roll(level)*100<WorldTuning.get("profesion_spawn_probabilidad"))i=choose(level);if(i<0)return;
  String id=ProfessionCatalog.IDS[i];c(t,"putString",ROLE,id);if(id.equals("nude")||!WorldTuning.flag("ropa_cadaveres_todos"))return;
  // A random profession never replaces the visible texture or equips invented armor.

 }catch(RuntimeException e){warn("asignar profesion",e);}}
 static void add(Object inv,Object stack){if(stack==null||yes(c(stack,"isEmpty")))return;int n=((Number)c(inv,"getContainerSize")).intValue();for(int i=0;i<n;i++)if(yes(c(c(inv,"getItem",i),"isEmpty"))){c(inv,"setItem",i,stack);return;}}
 public static void corpse(Object corpse,Object level,Object event){
  try{Object entity=c(event,"getEntity");c(ZoneReflection.type("dev.zomboid.hordes.LootProfiles"),"markConverted",event);
   String type=String.valueOf(c(c(entity,"getType"),"getDescriptionId"));
   // Cargo is owned inventory, not procedural supply: never replace it with a rolled profile.
   if(type.contains("tacznpcs")||type.contains("bandit"))return;
   Object inv=ZoneReflection.get(corpse,"inventory");int i=WorldTuning.flag("profesion_por_aspecto_todos")?recognize(entity):index(String.valueOf(c(tag(entity),"getString",ROLE)));
   if(i<0||!WorldTuning.flag("profesion_cadaveres_todos")){LootProfiles.corpse(corpse,level,c(entity,"position"));ensureClothes(inv,entity,i);limitTools(inv);return;}
   Properties p=settings();String id=ProfessionCatalog.IDS[i];ZoneReflection.set(corpse,"profession31",id);List<Object>items=new ArrayList<>();double amount=WorldTuning.get("cadaveres_cantidad")*WorldTuning.get("profesion_loot_cantidad")*Double.parseDouble(p.getProperty(id+".amount"));
   int n=roll(level)*100<Double.parseDouble(p.getProperty(id+".chance"))?ApocalypseSettings.count(3,amount,roll(level),20):0;
   List<Object>pool=new ArrayList<>();for(String spec:p.getProperty(id+".loot").split(",")){Object s=stack(spec);if(s!=null)pool.add(s);}
   if(n>0&&Set.of("military","veteran","police_officer","security_guard","hunter").contains(id)){
    try{Object gun=stack("tacz:modern_kinetic_gun");if(gun!=null){Object profile=c(ZoneReflection.type("dev.zomboid.remains.WeaponLoot"),"choose",((Number)ZoneReflection.get(corpse,"quality")).intValue(),ZoneReflection.get(level,"random"));if(profile!=null)pool.add(c(ZoneReflection.type("dev.zomboid.remains.GunAccess"),"create",profile,0));}}
    catch(RuntimeException ex){warn("catalogo de armas TaCZ",ex);}
   }
   for(int j=0;j<n&&!pool.isEmpty();j++)items.add(c(pool.get((int)(roll(level)*pool.size())),"copy"));
   // Construct first; failed catalog entries keep original inventory rather than clearing it.
   c(inv,"clearContent");for(Object s:items)add(inv,s);ensureClothes(inv,entity,i);injection(inv,level,"cadaveres");limitTools(inv);
  }catch(RuntimeException e){warn("loot profesional",e);}
 }
 static void ensureClothes(Object inv,Object entity,int role){if(!WorldTuning.flag("ropa_cadaveres_todos")||role==index("nude"))return;List<Object>equipped=armor(entity);for(Object s:equipped){boolean found=false;int n=((Number)c(inv,"getContainerSize")).intValue();for(int j=0;j<n;j++)if(!yes(c(c(inv,"getItem",j),"isEmpty"))&&yes(c(ZoneReflection.type("net.minecraft.world.item.ItemStack"),"isSameItemSameComponents",s,c(inv,"getItem",j)))){found=true;break;}if(!found)add(inv,s);}
 if(equipped.isEmpty()){int p=role>=0?role:index("unemployed");boolean added=false;for(String spec:settings().getProperty(ProfessionCatalog.IDS[p]+".clothes","").split(",")){if(!spec.isBlank()){Object clothing=stack(spec);if(clothing!=null){add(inv,clothing);added=true;}}}if(!added){add(inv,stack("leather_chestplate"));add(inv,stack("leather_leggings"));}}
}
 public static void injection(Object inv,Object level,String prefix){double chance=WorldTuning.get("cura_inyeccion_loot")/100*Math.min(1,WorldTuning.get(prefix+"_cantidad"));if(roll(level)<chance)add(inv,stack("firstaid:zombie_cure_injector"));}
 static boolean tool(Object stack){if(yes(c(stack,"isEmpty")))return false;Object item=c(stack,"getItem");for(Class<?>type=item.getClass();type!=null;type=type.getSuperclass()){String name=type.getSimpleName();if(Set.of("DiggerItem","ShearsItem","FishingRodItem","FlintAndSteelItem","BrushItem").contains(name))return true;}String id=itemId(stack).toLowerCase(Locale.ROOT);String path=id.substring(id.indexOf(':')+1);return path.endsWith("_axe")||path.endsWith("_pickaxe")||path.endsWith("_shovel")||path.endsWith("_hoe")||path.equals("shears")||path.equals("fishing_rod")||path.equals("flint_and_steel")||path.equals("brush")||path.contains("wrench")||path.contains("screwdriver")||path.contains("crowbar");}
 public static void limitTools(Object inv){boolean kept=false;int n=((Number)c(inv,"getContainerSize")).intValue();for(int i=0;i<n;i++){Object stack=c(inv,"getItem",i);if(!tool(stack))continue;if(kept)c(inv,"setItem",i,ZoneReflection.get(ZoneReflection.type("net.minecraft.world.item.ItemStack"),"EMPTY"));else{c(stack,"setCount",1);kept=true;}}}
 public static void afterGeneric(Object corpse,Object level){Object inv=ZoneReflection.get(corpse,"inventory");injection(inv,level,"cadaveres");limitTools(inv);}
 public static Object container(Object list,Object block,Object params){if(!java.util.List.class.isInstance(list))return list;boolean supported=false;for(Class<?>t=block.getClass();t!=null;t=t.getSuperclass()){String name=t.getName().toLowerCase(Locale.ROOT);if(name.contains("chest")||name.contains("barrel")){supported=true;break;}}if(!supported)return list;Object level=c(params,"getLevel");double chance=WorldTuning.get("cura_inyeccion_loot")/100*Math.min(1,WorldTuning.get("cofres_cantidad"));if(roll(level)<chance){Object item=stack("firstaid:zombie_cure_injector");@SuppressWarnings("unchecked") List<Object>items=(List<Object>)list;int free=0;for(int i=0;i<((Number)c(block,"getContainerSize")).intValue();i++)if(yes(c(c(block,"getItem",i),"isEmpty")))free++;if(item!=null&&items.size()<free)items.add(item);}return list;}
}
