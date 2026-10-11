package dev.zomboid.firstaid;
public final class Revival {
 static float raw(Object m){return ZeroParts.rawHealth(m,null);}
 public static boolean active(Object m){return R.yes(R.get(m,"criticalConditionActive"))&&raw(m)<=0;}
 public static double chance(int fitness){return .25+.02*Math.max(0,Math.min(10,fitness));}
 public static void bodyFatal(Object player,Object source){Object model=R.call(R.type("ichttt.mods.firstaid.common.util.CommonUtils"),"getDamageModel",player);if(model==null||raw(model)>0||active(model))return;post(model,player,source);if(!active(model))R.call(R.type("ichttt.mods.firstaid.common.util.CommonUtils"),"killPlayerDirectly",player,source);}
 static int fitness(Object p){try{Object skills=R.type("dev.survivorcreator.skills.Skills");return ((Number)R.call(skills,"level",p,R.get(skills,"FITNESS"))).intValue();}catch(Exception e){return 0;}}
 static double random(Object p){return R.num(R.call(R.call(p,"getRandom"),"nextDouble"));}
 public static void post(Object m,Object p,Object source){if(R.yes(R.call(R.call(p,"level"),"isClientSide")))return;if(raw(m)>0){R.call(R.call(p,"getPersistentData"),"putBoolean","FAFatalRollMade",false);return;}if(active(m)||!R.yes(R.call(p,"isAlive")))return;Object persistent=R.call(p,"getPersistentData");if(R.yes(R.call(persistent,"getBoolean","FAFatalRollMade")))return;
 // Commands and unrecoverable out-of-world deaths keep their normal behavior.
 if(source!=null){try{Object types=R.type("net.minecraft.world.damagesource.DamageTypes");if(R.yes(R.call(source,"is",R.get(types,"GENERIC_KILL")))||R.yes(R.call(source,"is",R.get(types,"FELL_OUT_OF_WORLD"))))return;}catch(Exception ignored){}}
 R.call(persistent,"putBoolean","FAFatalRollMade",true);if(random(p)>=.95)return;double chance=chance(fitness(p));Object tag=R.call(p,"getPersistentData");R.call(tag,"putBoolean","FARevivalAttempted",false);R.call(tag,"putBoolean","FARevivalSuccess",random(p)<chance);R.set(m,"criticalConditionActive",true);R.call(m,"setUnconsciousState",2400,true,true,"firstaid.gui.downed_"+(int)Math.round(chance*100));R.call(m,"scheduleResync");}
 public static void tick(Object m,Object p){if(raw(m)>0){R.call(R.call(p,"getPersistentData"),"putBoolean","FAFatalRollMade",false);return;}if(!active(m))post(m,p,null);if(!active(m))return;Object tag=R.call(p,"getPersistentData");int ticks=((Number)R.call(m,"getUnconsciousTicks")).intValue();if(ticks<=2200&&!R.yes(R.call(tag,"getBoolean","FARevivalAttempted"))){R.call(tag,"putBoolean","FARevivalAttempted",true);if(R.yes(R.call(tag,"getBoolean","FARevivalSuccess")))R.call(m,"rescueFromCriticalState",p,null,false);else R.call(p,"displayClientMessage",R.call(R.type("net.minecraft.network.chat.Component"),"literal","No pudiste levantarte. Todavía pueden reanimarte tus compañeros."),true);}}
}
