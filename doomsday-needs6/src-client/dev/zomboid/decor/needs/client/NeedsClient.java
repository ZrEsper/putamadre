package dev.zomboid.decor.needs.client;
import dev.zomboid.decor.R;
public final class NeedsClient{
 static Object key;static int kind,ticks,watchdog;static boolean forced;static Object previous;
 static Object mc(){return R.call(R.type("net.minecraft.client.Minecraft"),"getInstance");}
 public static void keys(Object event){key=R.make("net.minecraft.client.KeyMapping","key.doomsday.needs",88,"key.categories.doomsday");R.call(event,"register",key);}
 public static void state(int k,int t){kind=k;ticks=t;watchdog=t>0?100:0;}
 static void release(Object p){if(forced&&p!=null&&R.call(p,"getForcedPose")==R.get(R.type("net.minecraft.world.entity.Pose"),"CROUCHING"))R.call(p,"setForcedPose",previous);forced=false;previous=null;}
 public static void tick(Object event){Object m=mc(),p=R.get(m,"player");if(p==null||R.get(m,"level")==null){release(p);kind=ticks=0;return;}if(!R.yes(R.call(p,"isAlive"))){release(p);kind=ticks=0;return;}
  if(kind==2&&ticks>0){if(!forced)previous=R.call(p,"getForcedPose");R.call(p,"setForcedPose",R.get(R.type("net.minecraft.world.entity.Pose"),"CROUCHING"));forced=true;}else release(p);
  if(watchdog>0&&--watchdog==0)ticks=0;if(key==null)return;while(R.yes(R.call(key,"consumeClick")))if(kind>0&&ticks==0&&R.get(m,"screen")==null)R.call(R.get(p,"connection"),"sendCommand","necesidades aqui");
 }
 public static void input(Object event){if(kind==2&&ticks>0){Object input=R.call(event,"getInput");R.set(input,"shiftKeyDown",true);R.set(input,"jumping",false);}}
}
