package dev.zomboid.firstaid;
import java.util.*;import java.util.function.Function;
public final class ApocalypseMedicine {
 public static Object ITEM;
 public static void register(Object register){Function<Object,Object>factory=p->R.make("dev.zomboid.firstaid.CureInjector",R.call(p,"stacksTo",1));ITEM=R.call(register,"registerItem","zombie_cure_injector",factory);}
 public static void creative(Object event){if(ITEM!=null)R.call(event,"accept",R.call(ITEM,"get"));}
 public static void cure(Object player){if(R.yes(R.call(R.call(player,"level"),"isClientSide")))return;
  try{Class<?> infection=Class.forName("net.smileycorp.hordes.infection.HordesInfection");Object effect=R.get(infection,"INFECTED");if(R.yes(R.call(player,"removeEffect",effect))){Object packet=R.make("net.smileycorp.hordes.infection.network.CureEntityMessage",player);R.call(R.type("net.smileycorp.hordes.infection.network.InfectionPacketHandler"),"sendTracking",packet,player);R.call(R.type("net.smileycorp.hordes.infection.network.InfectionPacketHandler"),"sendTo",packet,player);}}
  catch(ClassNotFoundException e){System.getLogger("Apocalypse31").log(System.Logger.Level.WARNING,"The Hordes no instalado: no hay infeccion zombi que retirar");}
  R.call(player,"displayClientMessage",R.call(R.type("net.minecraft.network.chat.Component"),"translatable","item.firstaid.zombie_cure_injector.used",new Object[0]),true);
 }
 public static void healAll(Object player){if(R.yes(R.call(R.call(player,"level"),"isClientSide"))||!R.yes(R.call(player,"isAlive")))return;
  Object model=R.call(R.type("ichttt.mods.firstaid.common.util.CommonUtils"),"getDamageModel",player);
  if(model!=null){R.call(model,"clearStatusEffects");R.call(model,"setAddictionValue",0f);R.call(model,"clearUnconsciousState");R.call(model,"clearUnconsciousPenalties",player);
   R.call(R.type("ichttt.mods.firstaid.survival.ClinicalData"),"replace",model,R.make("ichttt.mods.firstaid.survival.ClinicalState"));
   for(Object part:ZeroParts.parts(model)){R.set(part,"currentHealth",((Number)R.call(part,"getMaxHealth")).floatValue());R.set(part,"activeHealer",null);}
   R.call(model,"syncVanillaHealth",player);R.call(model,"scheduleResync");}
  cure(player);R.call(player,"removeAllEffects");Object wet=R.call(R.type("ichttt.mods.firstaid.survival.WetGameplay"),"state",player);Arrays.fill((int[])R.get(wet,"wet"),0);for(String k:new String[]{"flu","sneeze","exposure"})R.set(wet,k,0);R.set(wet,"immune",false);
  Object tag=R.call(player,"getPersistentData");for(String k:new String[]{"FAFinishHits","FADownDeadline","FADownedMask"})R.call(tag,"remove",k);
  R.call(R.type("ichttt.mods.firstaid.fracture.Fractures"),"updateSlow",player);R.call(player,"setHealth",((Number)R.call(player,"getMaxHealth")).floatValue());R.call(player,"refreshDimensions");R.call(R.type("ichttt.mods.firstaid.common.util.CommonUtils"),"syncDamageModel",player);
  R.call(player,"displayClientMessage",R.call(R.type("net.minecraft.network.chat.Component"),"translatable","firstaid.apocalypse.level_heal",new Object[0]),true);
 }
}
