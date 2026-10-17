package dev.zomboidmelee.boxing;
import dev.zomboidmelee.combat.PanelCooldown;
/** Shared server rules. Does not depend on any Epic Fight class. */
public final class Boxing16 {
 public static boolean empty(Object player){return player!=null&&R.yes(R.call(R.call(player,"getMainHandItem"),"isEmpty"))&&R.yes(R.call(R.call(player,"getOffhandItem"),"isEmpty"));}
 public static float cooldown(float original,Object player){return empty(player)?PanelCooldown.vanilla(7f,player):PanelCooldown.vanilla(original,player);}
 public static void hit(Object event){Object victim=R.call(event,"getEntity");if(R.yes(R.call(R.call(victim,"level"),"isClientSide"))||R.num(R.call(event,"getNewDamage"))<=0)return;Object source=R.call(event,"getSource"),attacker=R.call(source,"getEntity");if(attacker==null||!R.type("net.minecraft.server.level.ServerPlayer").isInstance(attacker)||attacker!=R.call(source,"getDirectEntity")||!"player".equals(R.call(source,"getMsgId"))||!empty(attacker))return;
  if(!R.yes(R.call(R.type("dev.zomboidmelee.combat.ShoveCombat"),"zombie",victim)))return;
  Object data=R.call(victim,"getPersistentData");long now=R.lng(R.call(R.call(victim,"level"),"getGameTime")),until=R.lng(R.call(data,"getLong","zpush_until"));R.call(data,"putLong","zpush_until",Math.max(until,now+8));R.call(R.call(victim,"getNavigation"),"stop");
 }
}
