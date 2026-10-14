package dev.zomboid.npcprobe;
import java.util.*;import java.util.function.Supplier;
public final class Probe {
 static final String NPC="mod.cdv.tnpc.entity.AutoGunOperator";
 public static Object tag(Object npc){return R.call(npc,"getPersistentData");}
 public static boolean marked(Object npc){if(npc==null||!R.type(NPC).isInstance(npc))return false;try{if(R.yes(R.call(tag(npc),"getBoolean","np15_test")))return true;Object skin=R.call(R.call(npc,"getEntityData"),"get",R.get(R.type(NPC),"SYNCED_SKIN"));return R.yes(R.call(skin,"getBoolean","np15_test"));}catch(RuntimeException e){return false;}}
 public static boolean down(Object npc){return R.yes(R.call(tag(npc),"getBoolean","np15_down"));}
 static long now(Object npc){return R.lng(R.call(R.call(npc,"level"),"getGameTime"));}
 static Object owner(Object npc){String s=String.valueOf(R.call(tag(npc),"getString","np15_owner"));if(s.isEmpty())return null;return R.call(R.call(npc,"level"),"getPlayerByUUID",UUID.fromString(s));}
 public static String mode(Object npc){return String.valueOf(R.call(tag(npc),"getString","np15_mode"));}
 public static void name(Object npc){R.call(npc,"setCustomName",R.call(R.type("net.minecraft.network.chat.Component"),"literal","NPC pruebas · "+(down(npc)?"DERRIBADO":mode(npc))));R.call(npc,"setCustomNameVisible",true);}
 static void message(Object p,String text){R.call(p,"displayClientMessage",R.call(R.type("net.minecraft.network.chat.Component"),"literal",text),true);}
 public static void collapse(Object npc){R.call(tag(npc),"putBoolean","np15_down",true);R.call(tag(npc),"putLong","np15_deadline",now(npc)+1200);R.call(tag(npc),"putInt","np15_finish",0);R.call(npc,"cancelReload");R.call(npc,"aim",false);R.call(npc,"setTarget",(Object)null);R.call(R.call(npc,"getNavigation"),"stop");Parts.sync(npc);}
 public static boolean blockDeath(Object npc,Object source){if(!marked(npc)||R.yes(R.call(R.call(npc,"level"),"isClientSide"))||R.yes(R.call(tag(npc),"getBoolean","np15_final")))return false;
  String id=String.valueOf(R.call(source,"getMsgId"));if(id.equals("outOfWorld")||id.equals("genericKill"))return false;
  // Damage post listener decides whether a critical part was actually destroyed.
  R.call(npc,"setHealth",1f);return true;
 }
 public static Boolean hurt(Object npc,Object source,float amount){if(!marked(npc)||!down(npc))return null;if(amount<=0)return false;
  R.call(tag(npc),"remove","np15_helper");Object a=R.call(source,"getEntity"),direct=R.call(source,"getDirectEntity");String id=String.valueOf(R.call(source,"getMsgId"));
  if(id.equals("outOfWorld")||id.equals("genericKill")){finish(npc,source);return false;}
  if(a!=null||direct!=null){int hits=((Number)R.call(tag(npc),"getInt","np15_finish")).intValue()+1;R.call(tag(npc),"putInt","np15_finish",hits);if(hits>=3)finish(npc,source);}return false;
 }
 public static void finish(Object npc,Object source){if(R.yes(R.call(tag(npc),"getBoolean","np15_final")))return;R.call(tag(npc),"putBoolean","np15_final",true);R.call(npc,"setHealth",0f);R.call(npc,"die",source);}
 public static Boolean allied(Object npc,Object other){if(!marked(npc))return null;Object o=owner(npc);if(other==o)return mode(npc).equals("aliado");return !mode(npc).equals("quieto")&&R.type("net.minecraft.world.entity.player.Player").isInstance(other);}
 public static void prepare(Object npc){if(!marked(npc)||R.yes(R.call(R.call(npc,"level"),"isClientSide")))return;
  R.call(npc,"setNoAi",down(npc));for(String key:List.of("goalSelector","targetSelector")){Object selector=R.get(npc,key);for(Object wrapped:new ArrayList<>((Collection<?>)R.call(selector,"getAvailableGoals"))){Object goal=R.call(wrapped,"getGoal");if(!goal.getClass().getSimpleName().equals("FloatGoal"))R.call(selector,"removeGoal",goal);}}
 }
 public static void dispatch(Object npc){if(marked(npc)){tick(npc);return;}R.call(R.type("mod.cdv.tnpc.tactical.BanditBrain"),"tick",npc);}
 public static void tick(Object npc){if(R.yes(R.call(R.call(npc,"level"),"isClientSide"))||!R.yes(R.call(npc,"isAlive")))return;long time=now(npc);Object t=tag(npc);
  if(down(npc)){
   if(time>=R.lng(R.call(t,"getLong","np15_deadline"))){finish(npc,R.call(R.call(npc,"damageSources"),"genericKill"));return;}
   String helper=String.valueOf(R.call(t,"getString","np15_helper"));if(!helper.isEmpty()){
    Object p=R.call(R.call(npc,"level"),"getPlayerByUUID",UUID.fromString(helper));boolean valid=p!=null&&R.yes(R.call(p,"isAlive"))&&R.num(R.call(p,"distanceToSqr",npc))<=9&&R.num(R.get(p,"hurtTime"))==0;
    if(!valid){R.call(t,"remove","np15_helper");if(p!=null)message(p,"Reanimación interrumpida.");}
    else {long elapsed=time-R.lng(R.call(t,"getLong","np15_revive_start"));if(elapsed<0)R.call(t,"remove","np15_helper");else if(elapsed>=160){Parts.revive(npc);message(p,"NPC reanimado. Cura sus partes heridas con vendas.");}else if(elapsed%10==0)message(p,"Reanimando NPC: "+elapsed*100/160+"% · permanece a 3 bloques");}
   }return;
  }
  Object target=null,leader=owner(npc);
  if(mode(npc).equals("aliado")){
   List<?> monsters=(List<?>)R.call(R.call(npc,"level"),"getEntitiesOfClass",R.type("net.minecraft.world.entity.monster.Monster"),R.call(R.call(npc,"getBoundingBox"),"inflate",24d),(java.util.function.Predicate<Object>)m->R.yes(R.call(m,"isAlive")));
   target=monsters.stream().min(Comparator.comparingDouble(m->R.num(R.call(npc,"distanceToSqr",m)))).orElse(null);
   if(target==null&&leader!=null&&R.num(R.call(npc,"distanceToSqr",leader))>9)R.call(R.call(npc,"getNavigation"),"moveTo",leader,1d);
  }else if(mode(npc).equals("enemigo"))target=leader;
  else {R.call(npc,"aim",false);R.call(npc,"setTarget",(Object)null);R.call(R.call(npc,"getNavigation"),"stop");return;}
  if(target==null){R.call(npc,"aim",false);R.call(npc,"setTarget",(Object)null);return;}
 if(target==null||!R.yes(R.call(target,"isAlive"))||(target==leader&&(R.yes(R.call(target,"isCreative"))||R.yes(R.call(target,"isSpectator"))))||R.num(R.call(target,"distanceToSqr",npc))>1024)return;
  R.call(npc,"setTarget",target);if(R.num(R.call(npc,"distanceToSqr",target))>4&&R.call(R.type("com.tacz.guns.api.item.IGun"),"getIGunOrNull",R.call(npc,"getMainHandItem"))==null)R.call(R.call(npc,"getNavigation"),"moveTo",target,1d);if(time%4==0)fire(npc,target);if(time%20==0&&R.num(R.call(target,"distanceToSqr",npc))<6.25)melee(npc,target);
 }
 public static void fire(Object npc,Object target){if(down(npc)||!R.yes(R.call(npc,"hasLineOfSight",target)))return;Object stack=R.call(npc,"getMainHandItem"),gun=R.call(R.type("com.tacz.guns.api.item.IGun"),"getIGunOrNull",stack);if(gun==null)return;
  double dx=R.coord(target,"X")-R.coord(npc,"X"),dz=R.coord(target,"Z")-R.coord(npc,"Z"),dy=R.num(R.call(target,"getEyeY"))-R.num(R.call(npc,"getEyeY"));float yaw=(float)(Math.toDegrees(Math.atan2(dz,dx))-90),pitch=(float)-Math.toDegrees(Math.atan2(dy,Math.hypot(dx,dz)));
  R.call(npc,"setYRot",yaw);R.call(npc,"setYHeadRot",yaw);R.call(npc,"setXRot",pitch);R.call(npc,"aim",true);Object result=R.call(npc,"shoot",(Supplier<Float>)()->pitch,(Supplier<Float>)()->yaw);if(String.valueOf(result).contains("NO_AMMO"))R.call(npc,"reload");
 }
 public static void melee(Object npc,Object target){if(down(npc)||R.num(R.call(target,"distanceToSqr",npc))>6.25||!R.yes(R.call(npc,"hasLineOfSight",target)))return;Object gun=R.call(R.type("com.tacz.guns.api.item.IGun"),"getIGunOrNull",R.call(npc,"getMainHandItem"));if(gun!=null)R.call(npc,"melee");else {R.call(npc,"swing",R.get(R.type("net.minecraft.world.InteractionHand"),"MAIN_HAND"));R.call(npc,"doHurtTarget",target);}}
 public static Object interact(Object npc,Object player,Object hand){if(!marked(npc))return null;Object consume=R.get(R.type("net.minecraft.world.InteractionResult"),"SUCCESS");if(R.yes(R.call(R.call(npc,"level"),"isClientSide")))return consume;if(R.num(R.call(player,"distanceToSqr",npc))>9)return consume;
  Object held=R.call(player,"getItemInHand",hand);
  if(down(npc)){if(!R.yes(R.call(held,"isEmpty"))){message(player,"Para reanimar: mano vacía y clic derecho. Después usa vendas.");return consume;}
   if(String.valueOf(R.call(tag(npc),"getString","np15_helper")).isEmpty()){R.call(tag(npc),"putString","np15_helper",String.valueOf(R.call(player,"getUUID")));R.call(tag(npc),"putLong","np15_revive_start",now(npc));}message(player,"Reanimación: 8 segundos. Quédate cerca sin recibir golpes.");return consume;}
  String id=String.valueOf(R.call(R.get(R.type("net.minecraft.core.registries.BuiltInRegistries"),"ITEM"),"getKey",R.call(held,"getItem")));
  if(id.equals("firstaid:bandage")||id.equals("firstaid:plaster")){int part=Parts.aim(npc,player);double[] h=Parts.health(npc),max=Parts.maxima(npc);if(h[part]<max[part]){Parts.heal(npc,part,id.endsWith("bandage")?6:3);if(!R.yes(R.get(R.call(player,"getAbilities"),"instabuild")))R.call(held,"shrink",1);message(player,"Curado: "+Parts.NAMES[part]);}else message(player,"Esa parte ya está sana.");}else message(player,Parts.summary(npc));return consume;
 }
}
