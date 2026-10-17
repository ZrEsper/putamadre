package dev.zomboidmelee.boxing;
import java.util.*;
public final class BoxingClient16 {
 static Object owner;static boolean held,renderingHands,warned,blockSwingSent;static long pressed,started;static int combo=-1;static final Map<Object,Remote> REMOTE=new WeakHashMap<>();
 static Object mc(){return R.call(R.type("net.minecraft.client.Minecraft"),"getInstance");}
 static Object optional(Object o,String field){try{return R.get(o,field);}catch(IllegalStateException missing){return null;}}
 public static boolean usable(Object p){return Boxing16.empty(p)&&R.yes(R.call(p,"isAlive"))&&!R.yes(R.call(p,"isSpectator"))&&!R.yes(R.call(p,"isSleeping"))&&!R.yes(R.call(p,"isUsingItem"))&&!visorBusy(p);}
 static boolean visorBusy(Object p){try{return R.num(R.call(R.call(p,"getPersistentData"),"getInt","NVG_ANIM_TICK"))>0;}catch(IllegalStateException noData){return false;}}
 public static double phase(long now,long start){return start==0?1:(now-start)/320_000_000.0;}
 public static boolean holdingReady(long now,long press){return press!=0&&now-press>=350_000_000L;}
 static void reset(){held=false;blockSwingSent=false;pressed=started=0;combo=-1;}
 static void update(Object m,Object p,long now){if(p!=owner){owner=p;reset();REMOTE.clear();}if(p==null||!usable(p)||R.get(m,"screen")!=null||!R.yes(R.call(m,"isWindowActive"))){reset();return;}boolean down=R.yes(R.call(R.get(R.get(m,"options"),"keyAttack"),"isDown"));if(down&&!held){pressed=now;blockSwingSent=false;begin(now);}else if(!down)pressed=0;held=down;}
 static void begin(long now){if(started!=0&&now-started<230_000_000L)return;combo++;started=now;}
 public static void tick(Object event){Object m=mc();update(m,R.get(m,"player"),System.nanoTime());}
 static Object target(Object m){Object hit=R.get(m,"hitResult");try{Class<?>iso=R.type("com.cleannrooster.dungeons_iso.mod.Mod");if(R.yes(R.get(iso,"enabled")))hit=R.get(iso,"crosshairTarget");}catch(IllegalStateException absent){}return hit;}
 public static void input(Object event){Object m=mc(),p=R.get(m,"player");if(p==null||!usable(p)||R.get(m,"screen")!=null||!R.yes(R.call(event,"isAttack")))return;long now=System.nanoTime();update(m,p,now);Object hit=target(m);if(hit!=null)R.set(m,"hitResult",hit);boolean block=hit!=null&&R.call(hit,"getType").toString().equals("BLOCK");
  if(block&&!holdingReady(now,pressed)){R.call(event,"setCanceled",true);R.call(event,"setSwingHand",false);if(!blockSwingSent){blockSwingSent=true;R.call(p,"swing",R.get(R.type("net.minecraft.world.InteractionHand"),"MAIN_HAND"));}return;}
  if(!block&&(!held||now-started>=230_000_000L))begin(now);
 }
 public static void hand(Object event){Object m=mc(),p=R.get(m,"player");if(p==null||!usable(p)||R.get(m,"screen")!=null)return;
  if(!R.call(event,"getHand").toString().equals("MAIN_HAND")){R.call(event,"setCanceled",true);return;}
  Object pose=R.call(event,"getPoseStack"),buffers=R.call(event,"getMultiBufferSource");int light=((Number)R.call(event,"getPackedLight")).intValue();long now=System.nanoTime();boolean mining=held&&holdingReady(now,pressed)&&target(m)!=null&&R.call(target(m),"getType").toString().equals("BLOCK");double progress=mining?((now-pressed-350_000_000L)%420_000_000L)/420_000_000.0:phase(now,started);
  try{renderingHands=true;Object renderer=R.call(R.call(m,"getEntityRenderDispatcher"),"getRenderer",p);for(int side=0;side<2;side++){
    int sign=side==0?1:-1;double extension=Motion16.value(Math.max(0,combo),side,progress);
    R.call(pose,"pushPose");try{R.call(pose,"translate",sign*(.48-.10*extension),-.65+.12*extension,-.10-.34*extension);
     rotate(pose,"YP",sign*(20-12*extension));rotate(pose,"XP",-55-9*extension);rotate(pose,"ZP",sign*3*extension);
     R.call(pose,"translate",sign*(5.0/16),-2.0/16,0.0);R.call(renderer,side==0?"renderRightHand":"renderLeftHand",pose,buffers,light,p);
    }finally{R.call(pose,"popPose");}
   }R.call(event,"setCanceled",true);
  }catch(RuntimeException failure){if(!warned){warned=true;System.err.println("[Boxing16] Native skin-arm render failed; vanilla hands retained: "+failure);}}finally{renderingHands=false;}
 }
 static void rotate(Object pose,String axis,double degrees){R.call(pose,"mulPose",R.call(R.get(R.type("com.mojang.math.Axis"),axis),"rotationDegrees",(float)degrees));}
 public static void body(Object model,Object player){if(renderingHands||!R.type("net.minecraft.world.entity.player.Player").isInstance(player)||!usable(player))return;double swing=R.num(R.call(player,"getAttackAnim",0f));Remote state=REMOTE.computeIfAbsent(player,k->new Remote());if(swing>0&&(state.last==0||swing<state.last))state.combo++;state.last=swing;
  for(int side=0;side<2;side++){Object arm=R.get(model,side==0?"rightArm":"leftArm");int sign=side==0?1:-1;double extension=Motion16.value(Math.max(0,state.combo),side,swing);R.set(arm,"xRot",(float)(-1.10-.48*extension));R.set(arm,"yRot",(float)(-sign*(.20-.12*extension)));R.set(arm,"zRot",(float)(sign*(.12+.07*extension)));Object sleeve=optional(model,side==0?"rightSleeve":"leftSleeve");if(sleeve!=null)R.call(sleeve,"copyFrom",arm);}
 }
 static final class Remote{double last;int combo=-1;}
}
