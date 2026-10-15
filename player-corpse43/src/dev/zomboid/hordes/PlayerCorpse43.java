package dev.zomboid.hordes;
import java.lang.reflect.*;import java.util.*;
/** Real inventory is authoritative; equipment snapshots are appearance only. */
public final class PlayerCorpse43 {
 static final String META="zp43_player_death",DONE="zp43_corpse_done";
 static final String DEATH="de.maxhenkel.corpse.corelib.death.Death",ITEMS="de.maxhenkel.corpse.corelib.item.ItemUtils",COS="lain.mods.cos.api.CosArmorAPI";
 static final Map<Object,Pending> PENDING=new LinkedHashMap<>();static long clock;
 record Pending(Object zombie,long until){}
 static Object c(Object o,String n,Object...a){return ZoneReflection.call(o,n,a);}static Class<?>t(String s){return ZoneReflection.type(s);}static Object g(Object o,String n){return ZoneReflection.get(o,n);}static boolean yes(Object b){return Boolean.TRUE.equals(b);}static boolean empty(Object s){return s==null||yes(c(s,"isEmpty"));}static Object copy(Object s){return c(s,"copy");}static Object tag(Object e){return c(e,"getPersistentData");}static Object level(Object e){return c(e,"level");}static Object provider(Object e){return c(level(e),"registryAccess");}static Object air(){return g(t("net.minecraft.world.item.ItemStack"),"EMPTY");}
 static boolean available(String name){try{Class.forName(name);return true;}catch(ClassNotFoundException absent){return false;}}
 static Object make(String type,Object...a)throws Exception{for(Constructor<?>ctor:t(type).getDeclaredConstructors()){Class<?>[]p=ctor.getParameterTypes();if(p.length!=a.length)continue;boolean valid=true;for(int i=0;i<p.length;i++){Class<?>k=p[i];if(k.isPrimitive())k=k==int.class?Integer.class:k==double.class?Double.class:k==long.class?Long.class:k==boolean.class?Boolean.class:k;if(a[i]==null?p[i].isPrimitive():!k.isInstance(a[i]))valid=false;}if(valid){ctor.setAccessible(true);return ctor.newInstance(a);}}throw new NoSuchMethodException(type);}
 static void set(Object o,String field,Object value)throws Exception{for(Class<?>k=o.getClass();k!=null;k=k.getSuperclass())try{Field f=k.getDeclaredField(field);f.setAccessible(true);f.set(o,value);return;}catch(NoSuchFieldException absent){}throw new NoSuchFieldException(field);}
 static List<Object> list(int size){return (List<Object>)c(t("net.minecraft.core.NonNullList"),"withSize",size,air());}
 static boolean keep(Object p){return yes(c(c(level(p),"getGameRules"),"getBoolean",g(t("net.minecraft.world.level.GameRules"),"RULE_KEEPINVENTORY")));}
 static Object cosmetics(Object p){return available(COS)?c(t(COS),"getCAStacks",c(p,"getUUID")):null;}
 static Object meta(Object z){Object data=tag(z);return yes(c(data,"contains",META))?c(data,"getCompound",META):null;}
 static void warn(String message,Exception e){System.getLogger("PlayerCorpse43").log(System.Logger.Level.WARNING,message,e);}
 public static void capture(Object event){try{
  Object p=c(event,"getEntity");if(!t("net.minecraft.server.level.ServerPlayer").isInstance(p)||!available(DEATH)||!available("net.smileycorp.hordes.common.capability.HordesCapabilities"))return;
  Object cap=c(p,"getCapability",g(t("net.smileycorp.hordes.common.capability.HordesCapabilities"),"ZOMBIFY_PLAYER"),null);if(cap==null)return;Object z=c(cap,"getZombie");if(z==null||!PlayerGrave.entity(z))return;
  Object death=c(t(DEATH),"fromPlayer",p),m=c(death,"toNBT",provider(p),false);
  c(t(ITEMS),"saveInventory",provider(p),m,"ZP43Equipment",c(death,"getEquipment"));
  List<Object> preview=list(4);Object cos=cosmetics(p);if(cos!=null)for(int i=0;i<Math.min(4,((Number)c(cos,"getSlots")).intValue());i++){preview.set(i,copy(c(cos,"getStackInSlot",i)));c(m,"putBoolean","ZP43Hidden"+i,yes(c(cos,"isSkinArmor",i)));}
  c(t(ITEMS),"saveInventory",provider(p),m,"ZP43Cosmetic",preview);c(m,"putBoolean","ZP43Keep",keep(p)||(available("lain.mods.cos.impl.ModConfigs")&&yes(c(t("lain.mods.cos.impl.ModConfigs"),"getCosArmorKeepThroughDeath"))));
  c(tag(z),"put",META,m);PENDING.put(p,new Pending(z,clock+40));
 }catch(Exception e){warn("No se pudo registrar la identidad del jugador infectado; inventarios sin modificar",e);}}
 static List<Object> read(Object z,Object m,String name,int size){List<Object>l=list(size);c(t(ITEMS),"readInventory",provider(z),m,name,l);return l;}
 public static void join(Object z){try{if(!PlayerGrave.entity(z)||yes(c(level(z),"isClientSide")))return;Object m=meta(z);Object[]slots=t("net.minecraft.world.entity.EquipmentSlot").getEnumConstants();
  List<Object>equipment=m==null?null:read(z,m,"ZP43Equipment",slots.length),cos=m==null?null:read(z,m,"ZP43Cosmetic",4);
  for(int i=0;i<slots.length;i++){Object slot=slots[i];c(z,"setDropChance",slot,0f);if(equipment==null)continue;Object stack=equipment.get(i);if("HUMANOID_ARMOR".equals(String.valueOf(c(slot,"getType")))){int index=((Number)c(slot,"getIndex")).intValue();if(index<4){if(yes(c(m,"getBoolean","ZP43Hidden"+index)))stack=air();else if(!empty(cos.get(index)))stack=cos.get(index);}}c(z,"setItemSlot",slot,copy(stack));}
  c(z,"setCanPickUpLoot",false);c(z,"setPersistenceRequired");
 }catch(Exception e){warn("No se pudo restaurar el aspecto del zombi del jugador",e);}}
 static List<Object> transfer(Object p,Object z){List<Object>moved=new ArrayList<>();Object m=meta(z);if(m==null||yes(c(m,"getBoolean","ZP43Keep")))return moved;Object cos=cosmetics(p);if(cos==null)return moved;
  List<Object>before=read(z,m,"ZP43Cosmetic",4),cargo=(List<Object>)c(z,"getInventory");
  for(int i=0;i<4;i++){Object source=c(cos,"getStackInSlot",i),snapshot=before.get(i);if(empty(source)||empty(snapshot)||!yes(c(t("net.minecraft.world.item.ItemStack"),"isSameItemSameComponents",source,snapshot)))continue;
   // Only the original stack, never new gear equipped after respawning.
   int count=((Number)c(source,"getCount")).intValue(),original=((Number)c(snapshot,"getCount")).intValue();if(count!=original)continue;
   Object owned=copy(source);cargo.add(owned);try{c(cos,"setStackInSlot",i,air());}catch(RuntimeException failure){if(!empty(c(cos,"getStackInSlot",i))){cargo.remove(owned);throw failure;}warn("La cosmética se trasladó, pero un listener externo falló al notificarlo",failure);}moved.add(owned);
  }return moved;
 }
 static boolean present(Object z){return c(level(z),"getEntity",c(z,"getUUID"))==z;}
 public static void tick(){clock++;for(var it=PENDING.entrySet().iterator();it.hasNext();){var e=it.next();Object z=e.getValue().zombie();try{if(present(z)){transfer(e.getKey(),z);join(z);it.remove();}else if(clock>=e.getValue().until())it.remove();}catch(Exception ex){warn("No se pudo completar el traslado cosmético; se conservó la fuente disponible",ex);it.remove();}}}
 static Object fallback(Object z)throws Exception{Object m=make("net.minecraft.nbt.CompoundTag");Optional<?>owner=(Optional<?>)c(z,"getPlayerUUID");c(m,"putUUID","PlayerUuid",owner.orElse(null)==null?c(z,"getUUID"):owner.get());c(m,"putUUID","Id",UUID.randomUUID());c(m,"putString","PlayerName",String.valueOf(c(c(z,"getName"),"getString")));c(m,"putLong","Timestamp",System.currentTimeMillis());c(m,"putDouble","PosX",c(z,"getX"));c(m,"putDouble","PosY",c(z,"getY"));c(m,"putDouble","PosZ",c(z,"getZ"));return m;}
 public static void drops(Object event){Object z=c(event,"getEntity");if(!PlayerGrave.entity(z)||yes(c(level(z),"isClientSide")))return;
  Collection<Object>drops=(Collection<Object>)c(event,"getDrops");if(yes(c(tag(z),"getBoolean",DONE))){drops.clear();c(event,"setCanceled",true);return;}
  try{
   if(!available(DEATH))return;
   // A zombie may be killed in the same tick it appears, before deferred cosmetics move.
   for(var it=PENDING.entrySet().iterator();it.hasNext();){var e=it.next();if(e.getValue().zombie()!=z)continue;for(Object extra:transfer(e.getKey(),z))drops.add(make("net.minecraft.world.entity.item.ItemEntity",level(z),c(z,"getX"),c(z,"getY"),c(z,"getZ"),copy(extra)));it.remove();}
   Object m=meta(z);m=m==null?fallback(z):c(m,"copy");c(m,"putDouble","PosX",c(z,"getX"));c(m,"putDouble","PosY",c(z,"getY"));c(m,"putDouble","PosZ",c(z,"getZ"));
   Object death=c(t(DEATH),"fromNBT",provider(z),m);List<Object>owned=(List<Object>)c(z,"getInventory"),items=(List<Object>)c(death,"getAdditionalItems"),equipment=(List<Object>)c(death,"getEquipment");
   // Flat authoritative cargo includes real armor and cosmetics; display copies are never loot.
   for(Object item:owned)if(!empty(item))items.add(copy(item));Object[]slots=t("net.minecraft.world.entity.EquipmentSlot").getEnumConstants();for(int i=0;i<slots.length;i++)equipment.set(i,copy(c(z,"getItemBySlot",slots[i])));
   Object corpse=make("de.maxhenkel.corpse.entities.CorpseEntity",level(z));set(corpse,"death",death);c(corpse,"setCorpseUUID",c(death,"getPlayerUUID"));c(corpse,"setCorpseName",c(death,"getPlayerName"));corpse.getClass().getMethod("setCorpseModel",byte.class).invoke(corpse,((Number)c(death,"getModel")).byteValue());c(corpse,"setEquipment",equipment);c(corpse,"setPos",c(z,"getX"),c(z,"getY"),c(z,"getZ"));c(corpse,"setYRot",c(z,"getYRot"));
   if(!yes(c(level(z),"addFreshEntity",corpse))){System.getLogger("PlayerCorpse43").log(System.Logger.Level.WARNING,"Corpse rechazado por el mundo; se mantienen los drops nativos");return;}
   c(tag(z),"putBoolean",DONE,true);owned.clear();drops.clear();c(event,"setCanceled",true);
  }catch(Exception e){warn("Falló la creación del Corpse; no se suprimieron los drops nativos",e);}
 }
}
