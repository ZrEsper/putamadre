package dev.zomboidmelee.boxing;
/** SWIMMING is used only for its 0.6 m collision box, never as the visual swimming animation. */
public final class Slide28 {
 static final SideMap25<Boolean> LOW=new SideMap25<>();static final SideMap25<Long> SWIM_GUARD=new SideMap25<>();
 static void guard(Object p){SWIM_GUARD.put(p,Combat18.time(p)+24);}
 public static boolean suppressSwim(Object p){if(lowVisual(p))return true;Long until=SWIM_GUARD.get(p);if(until==null)return false;if(Combat18.time(p)>until){SWIM_GUARD.remove(p);return false;}return R.call(p,"getForcedPose")==null;}
 public static boolean lowVisual(Object p){if(!LOW.containsKey(p))return false;if(R.call(p,"getPose")==pose("SWIMMING")||MoveClient25.SLIDES.containsKey(p))return true;LOW.remove(p);guard(p);return false;}
 static void exit(Object p){if(!LOW.containsKey(p)||Slide20.STATES.containsKey(p)||Slide20.CLIENT.containsKey(p))return;if(R.call(p,"getForcedPose")!=null||R.call(p,"getPose")!=pose("SWIMMING")){LOW.remove(p);guard(p);return;}if(fits(p,1.8)){R.call(p,"setPose",pose("STANDING"));R.call(p,"refreshDimensions");LOW.remove(p);guard(p);}}
 static Object pose(String name){return R.get(R.type("net.minecraft.world.entity.Pose"),name);}
 static boolean fits(Object p,double height){double x=R.coord(p,"X"),y=R.coord(p,"Y"),z=R.coord(p,"Z");Object box=R.make("net.minecraft.world.phys.AABB",x-.3,y,z-.3,x+.3,y+height,z+.3);return R.yes(R.call(R.call(p,"level"),"noCollision",p,box));}
 static void lower(Object p){LOW.put(p,true);R.call(p,"setForcedPose",pose("SWIMMING"));R.call(p,"setPose",pose("SWIMMING"));R.call(p,"refreshDimensions");R.set(p,"horizontalCollision",false);}
 static void release(Object p,Object original){guard(p);R.call(p,"setForcedPose",original);if(!R.yes(R.call(p,"isAlive"))){LOW.remove(p);return;}Object next=original!=null?original:fits(p,1.8)?pose("STANDING"):fits(p,1.5)?pose("CROUCHING"):pose("SWIMMING");R.call(p,"setPose",next);R.call(p,"refreshDimensions");}
}
