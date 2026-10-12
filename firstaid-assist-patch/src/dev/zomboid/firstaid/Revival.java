package dev.zomboid.firstaid;
public final class Revival {
 static float raw(Object m){return ZeroParts.rawHealth(m,null);}
 public static boolean active(Object m){return R.yes(R.get(m,"criticalConditionActive"));}
 public static boolean bypass(Object source){if(source==null)return false;try{Object types=R.type("net.minecraft.world.damagesource.DamageTypes");return R.yes(R.call(source,"is",R.get(types,"GENERIC_KILL")))||R.yes(R.call(source,"is",R.get(types,"FELL_OUT_OF_WORLD")));}catch(Exception e){return false;}}
 public static boolean preventDeath(Object m,Object p,Object source){if(bypass(source))return false;if(raw(m)>0)return true;post(m,p,source);return active(m);}
 public static void bodyFatal(Object p,Object source){Object m=Assist.model(p);if(m!=null&&!preventDeath(m,p,source))R.call(R.type("ichttt.mods.firstaid.common.util.CommonUtils"),"killPlayerDirectly",p,source);}
 public static void post(Object m,Object p,Object source){if(R.yes(R.call(R.call(p,"level"),"isClientSide"))||active(m)||raw(m)>0||!R.yes(R.call(p,"isAlive"))||bypass(source))return;R.set(m,"criticalConditionActive",true);Object tag=R.call(p,"getPersistentData");R.call(tag,"putInt","FAFinishHits",0);R.call(m,"setUnconsciousState",2000,false,true,"firstaid.gui.downed_assist");R.call(m,"scheduleResync");}
 public static void tick(Object m,Object p){Assist.hearts(p);if(!active(m)&&raw(m)<=0)post(m,p,null);}
}
