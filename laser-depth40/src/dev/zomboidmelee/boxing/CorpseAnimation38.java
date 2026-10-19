package dev.zomboidmelee.boxing;
import java.util.*;
/** Corpse dummy players never tick: do not accumulate living-player fade chains on them. */
public final class CorpseAnimation38 {
 static final Map<Object,Boolean> cleaned=new WeakHashMap<>();static boolean warned;
 public static boolean block(Object player){if(player==null||!player.getClass().getName().equals("de.maxhenkel.corpse.entities.DummyPlayer"))return false;
 if(!cleaned.containsKey(player)){try{Object data=R.call(R.type("dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess"),"getPlayerAssociatedData",player),compat=R.type("com.tacz.guns.compat.playeranimator.PlayerAnimatorCompat");for(String name:new String[]{"LOWER_ANIMATION","LOOP_UPPER_ANIMATION","ONCE_UPPER_ANIMATION","ROTATION_ANIMATION"}){Object layer=R.call(data,"get",R.get(compat,name));if(layer==null)continue;R.call(layer,"setAnimation",(Object)null);Object modifiers=R.get(layer,"modifiers");if(modifiers instanceof List<?> list)while(!list.isEmpty())R.call(layer,"removeModifier",list.size()-1);}cleaned.put(player,true);}catch(RuntimeException e){if(!warned){warned=true;System.err.println("[Melee38] Corpse animation cleanup: "+e);}}}return true;
 }
}
