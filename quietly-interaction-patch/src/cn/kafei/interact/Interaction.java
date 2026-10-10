package cn.kafei.interact;
import java.lang.reflect.*;import java.util.*;
public final class Interaction{
 static boolean is(String name,Object object){return object!=null&&R.type(name).isInstance(object);}
 static boolean corpse(Object o){return o!=null&&o.getClass().getName().equals("de.maxhenkel.corpse.entities.CorpseEntity");}
 public static boolean door(Object o){return o!=null&&(is("net.minecraft.world.level.block.DoorBlock",o)||is("net.minecraft.world.level.block.TrapDoorBlock",o)||is("net.minecraft.world.level.block.FenceGateBlock",o));}
 public static String action(Object o){return door(o)?"Abrir / cerrar":"Abrir / interactuar";}
 public static boolean supported(Object o){if(o==null)return false;return door(o)||corpse(o)||is("net.minecraft.world.Container",o)||is("net.minecraft.world.MenuProvider",o)||o.getClass().getSimpleName().equals("EnderChestBlockEntity");}
 public static Object target(){Object mc=R.mc();if(R.field(mc,"screen")!=null||R.field(mc,"player")==null)return null;Object hit=R.field(mc,"hitResult");if(hit==null)return null;String kind=R.call(hit,"getType").toString();if(kind.equals("ENTITY"))return R.call(hit,"getEntity");if(kind.equals("BLOCK")){Object level=R.field(mc,"level");if(level==null)return null;Object p=R.call(hit,"getBlockPos"),be=R.call(level,"getBlockEntity",p);return be!=null?be:R.call(R.call(level,"getBlockState",p),"getBlock");}return null;}
 public static boolean block(){Object hit=R.field(R.mc(),"hitResult");return hit!=null&&R.call(hit,"getType").toString().equals("BLOCK")&&supported(target());}
 public static boolean entity(Object entity){return supported(entity)&&target()==entity;}
 public static String key(){Object opts=R.field(R.mc(),"options");Object use=R.field(opts,"keyUse");return R.call(R.call(use,"getTranslatedKeyMessage"),"getString").toString();}
 public static String category(String path){String s=path.toLowerCase(Locale.ROOT);if(s.matches(".*(bandage|plaster|painkiller|medicine|splint|ice_pack|amoxy|ibuprofen|blood_pack|firstaid|salts).*"))return "Medicina";if(s.matches(".*(bread|apple|carrot|potato|food|stew|sandwich|cookie|cocoa|milk|drink|rice|tomato|cabbage|beef|pork|fish).*"))return "Alimentos / bebidas";if(s.matches(".*(ammo|gun|bullet|attachment|weapon).*"))return "Armas / munición";if(s.matches(".*(pickaxe|shovel|axe|hoe|crowbar|rod|binocular|compass).*"))return "Herramientas";if(s.matches(".*(helmet|chestplate|leggings|boots|shirt|pants|vest|hat|scarf|shoes).*"))return "Ropa / equipo";if(s.matches(".*(redstone|copper|repeater|comparator|clock|light).*"))return "Electrónica";return "Materiales / provisiones";}
 static boolean allowed(Object target,Object player){
  if(corpse(target)){
   try{Object conf=R.field(R.type("de.maxhenkel.corpse.Main"),"SERVER_CONFIG");boolean owners=(Boolean)R.call(R.field(conf,"onlyOwnerAccess"),"get");if(!owners)return true;
    Optional<?> uuid=(Optional<?>)R.call(target,"getCorpseUUID");return uuid.isEmpty()||uuid.get().equals(R.call(player,"getUUID"))||(Boolean)R.call(player,"hasPermissions",2)||((Boolean)R.call(R.field(conf,"skeletonAccess"),"get")&&(Boolean)R.call(target,"isSkeleton"));
   }catch(Exception e){return false;}
  }
  if(is("net.minecraft.world.level.block.entity.BaseContainerBlockEntity",target)){
   try{Object lock=R.field(target,"lockKey");if(lock!=R.field(R.type("net.minecraft.world.LockCode"),"NO_LOCK"))return false;}catch(Exception e){return false;}
  }return true;
 }
 public static String pending(Object table){String s=table.toString();if(s.contains("crate_pharmacy"))return "Posibles suministros médicos";if(s.contains("crate_office"))return "Papelería / suministros variados";if(s.contains("crate_kitchen")||s.contains("crate_fridge"))return "Posibles alimentos / bebidas";if(s.contains("crate_workshop")||s.contains("crate_mechanic"))return "Posibles herramientas / materiales";if(s.contains("crate_military")||s.contains("crate_police"))return "Posible equipo / munición";return "Contenido por revisar";}
 public static String preview(Object target,Object player){
  if(!supported(target)||door(target))return "";if(!allowed(target,player))return "Contenido protegido";
  if(target.getClass().getSimpleName().equals("EnderChestBlockEntity"))return "Almacenamiento personal";
  if(is("net.minecraft.world.RandomizableContainer",target)&&R.call(target,"getLootTable")!=null)return pending(R.call(target,"getLootTable"));
  LinkedHashSet<String> cats=new LinkedHashSet<>();List<?> equipment=null;
  if(corpse(target))try{equipment=(List<?>)R.call(target,"getEquipment");}catch(Exception e){return "Cadáver por revisar";}
  if(equipment==null&&!is("net.minecraft.world.Container",target))return "Interactuar para revisar";
  int size=equipment!=null?equipment.size():((Number)R.call(target,"getContainerSize")).intValue();
  for(int i=0;i<Math.min(size,54);i++){Object stack=equipment!=null?equipment.get(i):R.call(target,"getItem",i);if((Boolean)R.call(stack,"isEmpty"))continue;Object reg=R.field(R.type("net.minecraft.core.registries.BuiltInRegistries"),"ITEM");cats.add(category(R.call(reg,"getKey",R.call(stack,"getItem")).toString()));if(cats.size()==3)break;}
  return cats.isEmpty()?"Sin objetos visibles":String.join(" · ",cats);
 }
}
