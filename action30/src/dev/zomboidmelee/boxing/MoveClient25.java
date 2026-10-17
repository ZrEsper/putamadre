package dev.zomboidmelee.boxing;
import java.util.*;import net.minecraft.client.model.*;import net.minecraft.client.model.geom.ModelPart;
public final class MoveClient25{
 static Object owner,world;static boolean crouch,jump;static long jumpAt,lastSlide,sprintAt;static final Map<Object,long[]> SLIDES=new WeakHashMap<>();
 static long runAt,jumpHeldAt,lastMantle;static boolean automaticMantle;
 public static void tick(){Object m=BoxingClient16.mc(),p=R.get(m,"player"),level=R.get(m,"level");long now=BoxingClient16.CLOCK.getAsLong();if(p!=owner||level!=world){if(owner!=null){Slide20.clientStop(owner);Parkour20.ACTIVE.remove(owner);}owner=p;world=level;SideMap25.clearClient();SLIDES.clear();jumpAt=lastSlide=sprintAt=runAt=jumpHeldAt=lastMantle=0;automaticMantle=false;crouch=jump=false;}Bodycam26.tick(p,level);Run29.tick(p,level);Lean30.tick(m,p,level);if(p==null)return;
  // Measure this frame before testing the keys. Keep a short real running history across crouch deceleration.
  Slide20.clientTick(p);if(R.yes(R.call(p,"isSprinting")))sprintAt=now;double[] measured=Slide20.MOTION.get(p);boolean moving=measured!=null&&Math.hypot(measured[2],measured[3])>.12;if(moving&&now-sprintAt<300_000_000L)runAt=now;
  boolean down=Keys26.down()||R.yes(R.call(R.get(R.get(m,"options"),"keyShift"),"isDown")),up=R.yes(R.call(R.get(R.get(m,"options"),"keyJump"),"isDown"));boolean ready=R.get(m,"screen")==null&&R.yes(R.call(m,"isWindowActive"))&&Move25.allowed(p);
  if(ready&&down&&!crouch)trySlide(p,now);
  Parkour20.tick(p,false);
  if(ready&&up&&!jump){boolean second=jumpAt!=0&&now-jumpAt<650_000_000L&&!R.yes(R.call(p,"onGround"));jumpHeldAt=now;automaticMantle=true;if(second){Combat18.send(-1,3);lastMantle=now;jumpAt=0;}else jumpAt=now;}
  // Holding jump catches the ledge after the normal jump, without frame-perfect double tapping.
  if(ready&&up&&automaticMantle&&!R.yes(R.call(p,"onGround"))&&now-jumpHeldAt<1_000_000_000L&&now-lastMantle>200_000_000L&&!Parkour20.active(p)&&Parkour20.find(p)!=null){Combat18.send(-1,3);lastMantle=now;automaticMantle=false;}
  if(!ready){jumpAt=0;automaticMantle=false;}if(!up)automaticMantle=false;crouch=down;jump=up;
 }
 public static boolean trySlide(Object p,long now){if(!Move25.allowed(p)||!R.yes(R.call(p,"onGround"))||R.call(p,"getForcedPose")!=null||runAt==0||now-runAt>=300_000_000L)return false;if(now-lastSlide>350_000_000L){Combat18.send(-1,2);lastSlide=now;}return true;}
 public static void confirmSlide(Object p,int duration,double x,double z,double speed){Slide28.LOW.put(p,true);if(p==R.get(BoxingClient16.mc(),"player"))Slide20.clientTarget(p,x,z,speed,duration);SLIDES.put(p,new long[]{BoxingClient16.CLOCK.getAsLong(),duration});}
 public static boolean receive(int id,int action){if(action!=99&&(action<108||action>122)&&action!=200&&action!=201)return false;Object level=R.get(BoxingClient16.mc(),"level");if(level==null)return true;Object p=R.call(level,"getEntity",id);if(p==null)return true;if(action==200||action==201){Parkour20.accept(p,action==200);return true;}int length=action==99?0:action-100;if(p==R.get(BoxingClient16.mc(),"player"))Slide20.clientStart(p,length);if(length==0)SLIDES.remove(p);else SLIDES.put(p,new long[]{BoxingClient16.CLOCK.getAsLong(),length});return true;}
 static double slide(Object p){long[]a=SLIDES.get(p);if(a==null)return 0;double phase=(BoxingClient16.CLOCK.getAsLong()-a[0])/(a[1]*50_000_000d);if(phase>=1){SLIDES.remove(p);return 0;}return Motion20.smooth(Math.min(phase/.12,(1-phase)/.20));}
 static double climb(Object p){if(Parkour20.active(p))return 1;Long at=Parkour20.VISUAL.get(p);if(at==null)return 0;double t=(BoxingClient16.CLOCK.getAsLong()-at)/1_400_000_000d;return t>=1?0:Motion20.smooth(Math.min(t/.10,(1-t)/.16));}
 public static void part(Object model,String name,String field,double target,double weight){if(!(model instanceof HumanoidModel<?>m))return;ModelPart p=switch(name){case "rightArm"->m.rightArm;case "leftArm"->m.leftArm;case "rightLeg"->m.rightLeg;case "leftLeg"->m.leftLeg;case "body"->m.body;default->null;};if(p==null)return;float old=p.xRot;p.xRot=(float)(old+(target-old)*weight);}
 public static boolean body(Object model,Object p){if(!R.type("net.minecraft.world.entity.player.Player").isInstance(p))return false;if(Parkour20.body(model,p)){layers(model);return true;}double w=slide(p);if(Slide28.lowVisual(p)){long[] phase=SLIDES.get(p);if(phase==null||BoxingClient16.CLOCK.getAsLong()-phase[0]>phase[1]*25_000_000L&&!Slide28.fits(p,1.8))w=1;}return SlidePose28.apply(model,p,w);}
 static void layers(Object model){if(model instanceof PlayerModel<?>m){if(m.rightSleeve!=null&&m.rightArm!=null)m.rightSleeve.copyFrom(m.rightArm);if(m.leftSleeve!=null&&m.leftArm!=null)m.leftSleeve.copyFrom(m.leftArm);if(m.rightPants!=null&&m.rightLeg!=null)m.rightPants.copyFrom(m.rightLeg);if(m.leftPants!=null&&m.leftLeg!=null)m.leftPants.copyFrom(m.leftLeg);if(m.jacket!=null&&m.body!=null)m.jacket.copyFrom(m.body);}}
 public static First24.Frame first(Object p,int side){double climb=climb(p),slide=slide(p);if(climb<=0&&slide<=0)return null;int sign=side==0?1:-1;return new First24.Frame(sign*(.28+.09*slide),-.50+.28*climb-.16*slide,-.32-.20*climb+.05*slide,-145+60*climb-12*slide,sign*(8+12*slide),sign*(7+6*climb+10*slide),sign*(15*climb+12*slide));}
 public static void camera(Object e){Bodycam26.camera(e);}
}
