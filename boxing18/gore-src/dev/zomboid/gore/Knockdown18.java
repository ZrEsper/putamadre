package dev.zomboid.gore;
/** Temporary floor state reuses Gore's existing synced integer and collision refresh. */
public final class Knockdown18 {
 public static void tick(Object event){Object v=R.call(event,"getEntity");if(!Gore.zombie(v)||R.yes(R.call(R.call(v,"level"),"isClientSide")))return;int state=Gore.state(v);if((state&(1<<30))==0)return;long now=((Number)R.call(R.call(v,"level"),"getGameTime")).longValue(),until=((Number)R.call(R.call(v,"getPersistentData"),"getLong","boxing18_down_until")).longValue();if(now>=until){Gore.set(v,state&~(1<<30));R.call(R.call(v,"getPersistentData"),"remove","boxing18_down_until");}else {R.call(R.call(v,"getNavigation"),"stop");R.call(v,"setJumping",false);}}
}
