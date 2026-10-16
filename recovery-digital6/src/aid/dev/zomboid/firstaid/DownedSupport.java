package dev.zomboid.firstaid;
/** Common-side inventory lookup, also used by the existing server-authoritative revival path. */
public final class DownedSupport{
 public static boolean down(Object p){if(p==null||!R.yes(R.call(p,"isAlive")))return false;Object m=Assist.model(p);return m!=null&&(Revival.active(m)||((Number)R.call(m,"getUnconsciousTicks")).intValue()>0);}
 static boolean isDefib(Object s){return s!=null&&R.yes(R.call(R.type("ichttt.mods.firstaid.common.EventHandler"),"isDefibrillator",s));}
 public static Object defib(Object p){
  for(String hand:new String[]{"MAIN_HAND","OFF_HAND"}){Object s=R.call(p,"getItemInHand",R.get(R.type("net.minecraft.world.InteractionHand"),hand));if(isDefib(s))return s;}
  Object model=Assist.model(p);if(model==null||!Revival.active(model))return null;
  Object inventory=R.call(p,"getInventory");int count=((Number)R.call(inventory,"getContainerSize")).intValue();
  for(int slot=0;slot<count;slot++){Object s=R.call(inventory,"getItem",slot);if(isDefib(s))return s;}
  return null;
 }
 public static boolean canSelf(Object p){Object m=p==null?null:Assist.model(p);return m!=null&&Revival.active(m)&&R.yes(R.call(p,"isAlive"))&&defib(p)!=null;}
 public static void message(Object player,String text){String key=text.startsWith("Reanimando")||text.startsWith("Desfibrilador")?"firstaid.blackout.reviving":text.startsWith("Reanimaci")&&text.contains("completada")?"firstaid.blackout.complete":text.startsWith("Reanimaci")?"firstaid.blackout.interrupted":"";Object c=key.isEmpty()?R.call(R.type("net.minecraft.network.chat.Component"),"literal",text):R.call(R.type("net.minecraft.network.chat.Component"),"translatable",key,(Object)new Object[0]);R.call(player,"displayClientMessage",c,true);}
 public static int seconds(Object p){Object m=Assist.model(p);return m==null?0:Math.max(0,(((Number)R.call(m,"getUnconsciousTicks")).intValue()+19)/20);}
}
