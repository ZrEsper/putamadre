package dev.zomboid.turretpower;
import java.util.*;import java.util.function.Supplier;
public final class Tier4{
 public static final String[] IDS={"turret","turret_reinforced","turret_advanced"};public static final double[] RANGES={7,13,20},HEALTH={50,100,160};public static final int[] INTERVALS={60,32,16};static final Object[] ITEMS=new Object[3];
 static Class<?> entityClass(){return R.type("com.entropy.tacz_turrets.turret.TurretEntity");}static Object accessor(){return R.get(entityClass(),"ZH_TIER4");}
 public static int tier(Object t){return Math.max(0,Math.min(2,((Number)R.call(R.call(t,"getEntityData"),"get",accessor())).intValue()));}
 public static double range(Object t){return RANGES[tier(t)];}public static int rangeInt(Object t){return (int)range(t);}public static int interval(Object t){return INTERVALS[tier(t)];}
 public static void define(Object builder){R.call(builder,"define",accessor(),0);}
 public static void save(Object t,Object tag){R.call(tag,"putInt","ZHTurretTier",tier(t));}
 public static void load(Object t,Object tag){int tier=Math.max(0,Math.min(2,((Number)R.call(tag,"getInt","ZHTurretTier")).intValue()));R.call(R.call(t,"getEntityData"),"set",accessor(),tier);stats(t);}
 public static void stats(Object t){if(R.yes(R.call(R.call(t,"level"),"isClientSide")))return;Object attrs=R.type("net.minecraft.world.entity.ai.attributes.Attributes");Object max=R.call(t,"getAttribute",R.get(attrs,"MAX_HEALTH")),follow=R.call(t,"getAttribute",R.get(attrs,"FOLLOW_RANGE"));double old=R.num(R.call(t,"getMaxHealth")),wanted=HEALTH[tier(t)];if(R.num(R.call(max,"getBaseValue"))!=wanted){double fraction=old<=0?1:Math.max(0,Math.min(1,R.num(R.call(t,"getHealth"))/old));R.call(max,"setBaseValue",wanted);R.call(t,"setHealth",(float)(R.num(R.call(t,"getMaxHealth"))*fraction));}R.call(follow,"setBaseValue",range(t));Object armor=R.call(t,"getAttribute",R.get(attrs,"ARMOR"));R.call(armor,"setBaseValue",R.num(R.call(R.get(R.type("com.entropy.tacz_turrets.config.TACZTurretsConfig"),"TURRET_ARMOR"),"get")));}
 public static void place(Object t,Object context){String id=R.call(R.get(R.type("net.minecraft.core.registries.BuiltInRegistries"),"ITEM"),"getKey",R.call(R.call(context,"getItemInHand"),"getItem")).toString();int tier=id.endsWith(IDS[2])?2:id.endsWith(IDS[1])?1:0;R.call(R.call(t,"getEntityData"),"set",accessor(),tier);stats(t);R.call(t,"setHealth",(float)HEALTH[tier]);}
 public static Object item(Object t){int tier=tier(t);Object item=tier==0?R.call(R.get(R.type("com.entropy.tacz_turrets.registry.ItemRegistry"),"TURRET"),"get"):ITEMS[tier];if(item==null)throw new IllegalStateException("Unregistered turret tier "+tier);return item;}
 public static Object stack(Object t){return R.make("net.minecraft.world.item.ItemStack",item(t));}
 public static void register(Object event){Object key=R.get(R.type("net.minecraft.core.registries.Registries"),"ITEM");if(!key.equals(R.call(event,"getRegistryKey")))return;for(int i=1;i<=2;i++){Object item=R.make("com.entropy.tacz_turrets.item.TurretItem");ITEMS[i]=item;Object id=R.call(R.type("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","tacz_turrets",IDS[i]);R.call(event,"register",key,id,(Supplier<Object>)()->item);}}
 public static void creative(Object event){if(!R.call(event,"getTabKey").equals(R.call(R.get(R.type("com.tacz.guns.init.ModCreativeTabs"),"OTHER_TAB"),"getKey")))return;for(int i=1;i<=2;i++)if(ITEMS[i]!=null)R.call(event,"accept",ITEMS[i]);}
 static Object appearance(Object t){return R.get(R.type("com.entropy.tacz_turrets.turret.TurretModelType"),tier(t)==1?"INDUSTRIAL":"TACTICAL");}
 public static Object model(Object original,Object t){return tier(t)==0?original:R.call(appearance(t),"model");}
 public static Object texture(Object original,Object t){return tier(t)==0?original:R.call(appearance(t),"texture",R.call(R.type("com.entropy.tacz_turrets.turret.TurretState"),"getState",t));}
}
