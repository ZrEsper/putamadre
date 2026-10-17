package dev.zomboidmelee.boxing;
public final class Move25{
 public static boolean server(Object p){return p!=null&&R.type("net.minecraft.server.level.ServerPlayer").isInstance(p)&&!R.yes(R.call(R.call(p,"level"),"isClientSide"));}
 public static boolean allowed(Object p){return p!=null&&R.yes(R.call(p,"isAlive"))&&!R.yes(R.call(p,"isSpectator"))&&!R.yes(R.call(p,"isSleeping"))&&!R.yes(R.call(p,"isUsingItem"))&&!R.yes(R.call(p,"isPassenger"))&&!R.yes(R.call(p,"isInWater"))&&!R.yes(R.get(R.call(p,"getAbilities"),"flying"));}
 public static void tick(Object event){Object p=R.call(event,"getEntity");if(!server(p))return;if(!allowed(p)||Stomp21.PENDING.containsKey(p)){Slide20.stop(p);if(Parkour20.active(p))Parkour20.stop(p,true);return;}Slide20.tick(p);Parkour20.tick(p,true);}
 public static boolean request(Object p,int action){if(action!=2&&action!=3)return false;if(!server(p)||!allowed(p)||Stomp21.PENDING.containsKey(p))return true;if(action==2)Slide20.request(p);else Parkour20.request(p);return true;}
}
