package dev.zomboidmelee.boxing;
import java.util.*;
public final class BoxingClient16 {
 static java.util.function.LongSupplier CLOCK=System::nanoTime;static double lastSwing;static boolean heavy,stomping;static long nextPunch;static Object owner;static boolean held,renderingHands,warned,blockSwingSent;static long pressed,started;static int combo=-1;static final Map<Object,Remote> REMOTE=new WeakHashMap<>();
 static Object mc(){return R.call(R.type("net.minecraft.client.Minecraft"),"getInstance");}
 static Object optional(Object o,String field){try{return R.get(o,field);}catch(IllegalStateException missing){return null;}}
 public static boolean usable(Object p){return Boxing16.empty(p)&&R.yes(R.call(p,"isAlive"))&&!R.yes(R.call(p,"isSpectator"))&&!R.yes(R.call(p,"isSleeping"))&&!R.yes(R.call(p,"isUsingItem"))&&!visorBusy(p);}
 static boolean visorBusy(Object p){try{return R.num(R.call(R.call(p,"getPersistentData"),"getInt","NVG_ANIM_TICK"))>0;}catch(IllegalStateException noData){return false;}}
 public static double phase(long now,long start){return start==0?1:(now-start)/320_000_000.0;}
 public static boolean holdingReady(long now,long press){return press!=0&&now-press>=350_000_000L;}
 static void reset(){heavy=false;stomping=false;nextPunch=0;held=false;blockSwingSent=false;pressed=started=0;combo=-1;}
 static void update(Object m,Object p,long now){if(p!=owner){owner=p;reset();REMOTE.clear();}if(p==null||!usable(p)||R.get(m,"screen")!=null||!R.yes(R.call(m,"isWindowActive"))){reset();return;}boolean down=R.yes(R.call(R.get(R.get(m,"options"),"keyAttack"),"isDown"));if(down&&!held){pressed=now;blockSwingSent=false;begin(now);}else if(!down)pressed=0;held=down;}
 static void begin(long now){if(now<nextPunch)return;heavy=false;stomping=false;combo++;started=now;}
 public static void tick(Object event){Object m=mc();update(m,R.get(m,"player"),CLOCK.getAsLong());}
 static Object target(Object m){Object hit=R.get(m,"hitResult");try{Class<?>iso=R.type("com.cleannrooster.dungeons_iso.mod.Mod");if(R.yes(R.get(iso,"enabled")))hit=R.get(iso,"crosshairTarget");}catch(IllegalStateException absent){}return hit;}
 public static void input(Object event){Object m=mc(),p=R.get(m,"player");if(p==null||!usable(p)||R.get(m,"screen")!=null)return;if(!R.yes(R.call(event,"isAttack"))){use(event,m,p);return;}long now=CLOCK.getAsLong();update(m,p,now);Object hit=target(m);if(hit!=null)R.set(m,"hitResult",hit);boolean block=hit!=null&&R.call(hit,"getType").toString().equals("BLOCK");
  if(block&&!holdingReady(now,pressed)){R.call(event,"setCanceled",true);R.call(event,"setSwingHand",false);if(!blockSwingSent){blockSwingSent=true;R.call(p,"swing",R.get(R.type("net.minecraft.world.InteractionHand"),"MAIN_HAND"));}return;}
  if(!block){if(now<nextPunch){R.call(event,"setCanceled",true);R.call(event,"setSwingHand",false);return;}begin(now);nextPunch=now+400_000_000L;}
 }
 public static void mouse(Object event){Object m=mc(),p=R.get(m,"player");if(p==null||!usable(p)||R.get(m,"screen")!=null||!R.yes(R.call(m,"isWindowActive")))return;int button=((Number)R.call(event,"getButton")).intValue();if(!R.yes(R.call(R.get(R.get(m,"options"),"keyAttack"),"matchesMouse",button)))return;long now=CLOCK.getAsLong();update(m,p,now);int action=((Number)R.call(event,"getAction")).intValue();if(action==1){pressed=now;held=true;blockSwingSent=false;begin(now);}else if(action==0){pressed=0;held=false;}}
 public static boolean bareClient(){Object m=mc();return Boxing16.empty(R.get(m,"player"));}
 public static void hand(Object event){Object m=mc(),p=R.get(m,"player");if(p==null||!usable(p)||R.get(m,"screen")!=null)return;if(!R.call(event,"getHand").toString().equals("MAIN_HAND")){R.call(event,"setCanceled",true);return;}if(renderFirstPerson(((Number)R.call(event,"getPartialTick")).floatValue(),R.call(event,"getPoseStack"),R.call(event,"getMultiBufferSource"),p,((Number)R.call(event,"getPackedLight")).intValue()))R.call(event,"setCanceled",true);}
 /** Runs before item/weapon cached renderers and their camera transforms. */
 public static boolean renderFirstPerson(float partial,Object pose,Object buffers,Object p,int light){Object m=mc();if(p==null||!usable(p)||R.get(m,"screen")!=null)return false;long now=CLOCK.getAsLong();Object hit=target(m);boolean mining=held&&holdingReady(now,pressed)&&hit!=null&&R.call(hit,"getType").toString().equals("BLOCK");double progress=mining?((now-pressed-350_000_000L)%420_000_000L)/420_000_000.0:(heavy?(started==0?1:(now-started)/460_000_000.0):phase(now,started));double swing=R.num(R.call(p,"getAttackAnim",partial));
  if(!mining&&progress>=1&&swing>0){if(lastSwing<=0||swing<lastSwing-.2)combo++;progress=swing;}lastSwing=swing;
  try{renderingHands=true;Object renderer=R.call(R.call(m,"getEntityRenderDispatcher"),"getRenderer",p);for(int side=0;side<2;side++){
   double extension=Motion16.value(Math.max(0,combo),side,progress)*(heavy?1.18:1);BoxingArms17.draw(renderer,pose,buffers,light,p,side,stomping?0:extension);}

   // The outer vanilla method is canceled; its final endBatch must not be skipped.
   if(stomping&&progress<1)BoxingArms17.leg(renderer,pose,buffers,light,p,progress);BoxingArms17.flush(buffers);return true;
  }catch(RuntimeException failure){if(!warned){warned=true;System.err.println("[Boxing18] Native skin-arm render failed: "+failure);}return false;}finally{renderingHands=false;}
 }

 static void rotate(Object pose,String axis,double degrees){R.call(pose,"mulPose",R.call(R.get(R.type("com.mojang.math.Axis"),axis),"rotationDegrees",(float)degrees));}
 public static void body(Object model,Object player){if(!renderingHands&&!(R.type("net.minecraft.world.entity.player.Player").isInstance(player))){flinch(model,player);return;}if(renderingHands||!R.type("net.minecraft.world.entity.player.Player").isInstance(player)||!usable(player))return;double swing=R.num(R.call(player,"getAttackAnim",0f));Remote state=REMOTE.computeIfAbsent(player,k->new Remote());if(swing>0&&(state.last==0||swing<state.last))state.combo++;state.last=swing;
  for(int side=0;side<2;side++){int sign=side==0?1:-1;double extension=Motion16.value(Math.max(0,state.combo),side,swing);BoxingArms17.pose(model,side,(float)(-1.10-.48*extension),(float)(-sign*(.20-.12*extension)),(float)(sign*(.12+.07*extension)));}

 }

 static void use(Object event,Object m,Object p){if(!R.yes(R.call(event,"isUseItem"))||!R.call(event,"getHand").toString().equals("MAIN_HAND"))return;Object hit=target(m);int id=-1;if(hit!=null){String kind=R.call(hit,"getType").toString();if(kind.equals("BLOCK"))return;if(kind.equals("ENTITY")){Object entity=R.call(hit,"getEntity");if(!Combat18.zombie(entity))return;id=((Number)R.call(entity,"getId")).intValue();}}
  R.call(event,"setCanceled",true);R.call(event,"setSwingHand",false);long now=CLOCK.getAsLong();if(now<nextPunch)return;heavy=true;stomping=hit!=null&&R.call(hit,"getType").toString().equals("ENTITY")&&Stomp18.headAim(p,R.call(hit,"getEntity"));combo++;started=now;nextPunch=now+(stomping?1_000_000_000L:900_000_000L);Combat18.send(id,stomping?1:0);R.call(p,"swing",R.get(R.type("net.minecraft.world.InteractionHand"),"MAIN_HAND"));
 }
 public static double cameraWave(long now,long start,boolean strong){if(start==0)return 0;double p=(now-start)/(strong?460_000_000.0:320_000_000.0);return p<=0||p>=1?0:Math.sin(p*Math.PI*2)*Math.sin(p*Math.PI);}
 public static void camera(Object event){Object m=mc(),p=R.get(m,"player");if(p==null||!usable(p)||R.get(m,"screen")!=null)return;double wave=cameraWave(CLOCK.getAsLong(),started,heavy);if(wave==0)return;float pitch=((Number)R.call(event,"getPitch")).floatValue(),roll=((Number)R.call(event,"getRoll")).floatValue();R.call(event,"setPitch",pitch+(float)(wave*(heavy?1.1:.45)));R.call(event,"setRoll",roll+(float)(wave*(heavy?.6:.2)*(combo%2==0?1:-1)));}
 static void flinch(Object model,Object entity){if(Stomp18.GORE!=null)return;if(!Combat18.zombie(entity))return;int hurt=((Number)R.get(entity,"hurtTime")).intValue();if(hurt>0)BoxingArms17.flinch(model,(float)Math.sin(Math.min(1,hurt/10.0)*Math.PI/2));}
 public static void prone(Object entity,Object pose){if(!Stomp18.temporary(entity))return;double scale=Stomp18.fallenScale(entity);R.call(pose,"translate",0.0,.22*scale,-.8*scale);rotate(pose,"XP",90);}
 static final class Remote{double last;int combo=-1;}
}
