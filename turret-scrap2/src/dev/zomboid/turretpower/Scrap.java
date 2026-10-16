package dev.zomboid.turretpower;
public final class Scrap {
 public static Object ITEM;
 public static void register(Object e){Object key=R.get(R.type("net.minecraft.core.registries.Registries"),"ITEM");if(!key.equals(R.call(e,"getRegistryKey")))return;Object id=R.call(R.type("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","tacz_turrets","electronic_scrap");Object props=R.call(R.make("net.minecraft.world.item.Item$Properties"),"stacksTo",64);ITEM=R.make("net.minecraft.world.item.Item",props);R.call(e,"register",key,id,(java.util.function.Supplier<Object>)()->ITEM);}
 public static void creative(Object e){if(ITEM!=null&&R.call(e,"getTabKey").equals(R.call(R.get(R.type("com.tacz.guns.init.ModCreativeTabs"),"OTHER_TAB"),"getKey")))R.call(e,"accept",ITEM);}
 public static Object stack(int count){return ITEM==null?null:R.make("net.minecraft.world.item.ItemStack",ITEM,count);}
}
