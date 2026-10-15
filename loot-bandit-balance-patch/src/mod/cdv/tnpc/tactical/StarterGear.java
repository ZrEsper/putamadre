package mod.cdv.tnpc.tactical;
import java.util.*;
public final class StarterGear {
 static final String MARK="Scarcity33NewSurvivor";static final Set<String>WARN=new HashSet<>();
 static Object c(Object o,String n,Object...a)throws Exception{return Reflect.call(o,n,a);}static boolean yes(Object o){return Boolean.TRUE.equals(o);}static Object tag(Object npc)throws Exception{return c(npc,"getPersistentData");}
 static double setting(String key,double def){try{return ((Number)Class.forName("dev.zomboid.hordes.WorldTuning").getMethod("get",String.class).invoke(null,key)).doubleValue();}catch(Exception e){return def;}}
 static void warn(String feature,Exception e){if(WARN.add(feature))CalmBrain.error("Scarcity33 "+feature,e);}
 static boolean probe(Object t)throws Exception{return yes(c(t,"getBoolean","np15_test"));}
 static boolean managed(Object npc)throws Exception{return yes(c(tag(npc),"getBoolean",MARK))&&!probe(tag(npc));}
 static String id(Object s)throws Exception{return CalmLoot.id(s);}
 static boolean armor(Object s)throws Exception{if(CalmLoot.empty(s))return false;String id=id(s);return !id.startsWith("selfexpression:")||id.contains("armor")||id.contains("stalker")||id.contains("zombie_survivor");}
 public static void outfit(Object npc,Random random)throws Exception{
  Object t=tag(npc);if(probe(t))return;boolean fresh=!probe(t)&&!yes(c(t,"getBoolean","TacZNpcLoadoutV11"))&&!yes(c(t,"getBoolean","TacZNpcCivilianV5"));
  c(SurvivalGear.class,"originalOutfit33",npc,random);if(!fresh)return;
  boolean armed=random.nextDouble()*100<setting("bandidos_armados",2),protectedArmor=random.nextDouble()*100<setting("bandidos_armadura",2);
  Object main=c(npc,"getMainHandItem");if(!armed)c(npc,"setItemSlot",CalmLoot.slot("MAINHAND"),CalmLoot.emptyStack());else if(CalmLoot.empty(main))c(npc,"setItemSlot",CalmLoot.slot("MAINHAND"),CalmLoot.stack("minecraft:wooden_sword"));
  c(npc,"setItemSlot",CalmLoot.slot("OFFHAND"),CalmLoot.emptyStack());
  if(!protectedArmor)for(String slot:CalmLoot.SLOTS){Object stack=c(npc,"getItemBySlot",CalmLoot.slot(slot));if(!CalmLoot.empty(stack)&&armor(stack))c(npc,"setItemSlot",CalmLoot.slot(slot),CalmLoot.emptyStack());}
  List<Object>bag=new ArrayList<>();if(random.nextDouble()*100<setting("bandidos_suministros",15))for(Object s:CalmLoot.bag(npc)){String id=id(s);if(id.contains("ammo")||id.contains("gun")||id.contains("sword")||id.contains("armor")||id.contains("plate")||id.contains("backpack"))continue;Object copy=c(s,"copy");c(copy,"setCount",1);bag.add(copy);break;}
  CalmLoot.saveBag(npc,bag);c(t,"putBoolean",MARK,true);c(t,"putBoolean","TacZNpcCombatV12",true);c(t,"putInt","TacZNpcScavengingXP",0);
  Object held=c(npc,"getMainHandItem");if(!CalmLoot.empty(held)){Object item=c(held,"getItem");if(Reflect.type("com.tacz.guns.api.item.IGun").isInstance(item)){int current=((Number)c(item,"getCurrentAmmoCount",held)).intValue();c(item,"setCurrentAmmoCount",held,Math.min(current,4));c(item,"setBulletInBarrel",held,false);}}
 }
 public static Object armorSlot(Object stack){try{Object item=c(stack,"getItem");return Reflect.type("net.minecraft.world.item.ArmorItem").isInstance(item)?c(item,"getEquipmentSlot"):null;}catch(Exception e){warn("armorSlot",e);return null;}}
 public static boolean allowed(Object npc,Object stack){return true;}
 public static boolean reload(Object npc){try{if(!managed(npc)||setting("bandidos_progresion_todos",1)<=0)return false;Object t=tag(npc);if(((Number)c(t,"getLong","Scarcity33ReloadAt")).longValue()>0)return true;Object held=c(npc,"getMainHandItem");if(CalmLoot.empty(held)||!Reflect.type("com.tacz.guns.api.item.IGun").isInstance(c(held,"getItem")))return true;Object gun=c(held,"getItem"),data=NightCombat.data(held);if(data==null)return true;int capacity=((Number)c(data,"getAmmoAmount")).intValue(),current=((Number)c(gun,"getCurrentAmmoCount",held)).intValue();if(current>=capacity)return true;Object ammo=NightCombat.ammoId(held);boolean available=false;for(Object stack:CalmLoot.bag(npc))if(compatible(stack,ammo)){available=true;break;}if(!available)return true;
   c(t,"putLong","Scarcity33ReloadAt",now(npc)+Math.max(10,(long)(setting("bandidos_recarga_segundos",2)*20)));c(t,"putString","Scarcity33ReloadGun",String.valueOf(c(gun,"getGunId",held)));return true;
  }catch(Exception e){warn("reload",e);return true;}}
 static long now(Object npc)throws Exception{return ((Number)c(c(npc,"level"),"getGameTime")).longValue();}
 static boolean compatible(Object stack,Object ammo)throws Exception{if(CalmLoot.empty(stack)||ammo==null)return false;Object item=c(stack,"getItem");if(!Reflect.type("com.tacz.guns.api.item.IAmmo").isInstance(item))return false;return String.valueOf(c(item,"getAmmoId",stack)).equals(String.valueOf(ammo));}
 public static int ammunition(int original,Object npc){try{if(!managed(npc)||setting("bandidos_progresion_todos",1)<=0)return original;Object held=c(npc,"getMainHandItem");if(CalmLoot.empty(held)||!Reflect.type("com.tacz.guns.api.item.IGun").isInstance(c(held,"getItem")))return 0;int total=((Number)c(c(held,"getItem"),"getCurrentAmmoCount",held)).intValue();Object ammo=NightCombat.ammoId(held);for(Object s:CalmLoot.bag(npc))if(compatible(s,ammo))total+=((Number)c(s,"getCount")).intValue();return Math.max(0,total);}catch(Exception e){warn("ammunition count",e);return 0;}}
 public static void tick(Object npc){try{if(!managed(npc))return;Object t=tag(npc);long deadline=((Number)c(t,"getLong","Scarcity33ReloadAt")).longValue();if(deadline<=0||now(npc)<deadline)return;c(t,"putLong","Scarcity33ReloadAt",0L);if(!yes(c(npc,"isAlive")))return;Object held=c(npc,"getMainHandItem"),gun=c(held,"getItem");if(!Reflect.type("com.tacz.guns.api.item.IGun").isInstance(gun)||!String.valueOf(c(gun,"getGunId",held)).equals(String.valueOf(c(t,"getString","Scarcity33ReloadGun"))))return;Object data=NightCombat.data(held);if(data==null)return;int current=((Number)c(gun,"getCurrentAmmoCount",held)).intValue(),capacity=((Number)c(data,"getAmmoAmount")).intValue(),need=Math.max(0,capacity-current),loaded=0;Object ammo=NightCombat.ammoId(held);List<Object>old=CalmLoot.bag(npc),bag=new ArrayList<>();for(Object original:old){Object s=c(original,"copy");if(need>0&&compatible(s,ammo)){int count=((Number)c(s,"getCount")).intValue(),take=Math.min(need,count);c(s,"shrink",take);need-=take;loaded+=take;}if(!CalmLoot.empty(s))bag.add(s);}if(loaded<=0)return;
   CalmLoot.saveBag(npc,bag);try{Object copy=c(held,"copy");c(gun,"setCurrentAmmoCount",copy,current+loaded);c(gun,"setBulletInBarrel",copy,false);c(npc,"setItemSlot",CalmLoot.slot("MAINHAND"),copy);}catch(Exception e){CalmLoot.saveBag(npc,old);throw e;}
  }catch(Exception e){warn("finite ammunition",e);}}
}
