package dev.zomboid.turretpower;
import java.util.*;
public final class Sounds {
 static final Map<String,Object> SOUNDS=new HashMap<>();static final Map<Object,State> STATES=new WeakHashMap<>();static final class State{Object target;long alert=-100,move=-100,idle=-100;double yaw=Double.NaN,pitch;}
 public static void register(Object e){Object key=R.get(R.type("net.minecraft.core.registries.Registries"),"SOUND_EVENT");if(!key.equals(R.call(e,"getRegistryKey")))return;for(String name:new String[]{"servo_idle","servo_move","target_alert"}){Object id=R.call(R.type("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","tacz_turrets",name);Object sound=R.call(R.type("net.minecraft.sounds.SoundEvent"),"createVariableRangeEvent",id);SOUNDS.put(name,sound);R.call(e,"register",key,id,(java.util.function.Supplier<Object>)()->sound);}}
 static void play(Object t,String name,float volume){Object sound=SOUNDS.get(name);if(sound!=null)R.call(R.call(t,"level"),"playSound",null,R.call(t,"blockPosition"),sound,R.get(R.type("net.minecraft.sounds.SoundSource"),"BLOCKS"),volume,1f);}
 static double angle(double v){return Math.abs(((v+180)%360+360)%360-180);}
 public static void tick(Object t){Object w=R.call(t,"level");if(!Link.server(w))return;State s=STATES.computeIfAbsent(t,k->new State());if(!R.yes(R.call(t,"isEnabled"))||!R.yes(R.call(t,"hasGun"))||!Link.power(t)){s.target=null;s.yaw=Double.NaN;return;}long n=Link.now(t);double yaw=R.num(R.call(t,"getYHeadRot")),pitch=R.num(R.call(t,"getXRot"));boolean moved=!Double.isNaN(s.yaw)&&(angle(yaw-s.yaw)>=.8||Math.abs(pitch-s.pitch)>=.8);s.yaw=yaw;s.pitch=pitch;
  if(moved&&n-s.move>=8){play(t,"servo_move",.7f);s.move=n;}else if(n-s.idle>=80&&n-s.move>=20){play(t,"servo_idle",.35f);s.idle=n;}
  Object target=R.call(R.type("net.tslat.smartbrainlib.util.BrainUtils"),"getTargetOfEntity",t);Object id=target!=null&&R.yes(R.call(target,"isAlive"))&&R.yes(R.call(t,"isValidTarget",target))&&R.yes(R.call(t,"hasLineOfSight",target))?R.call(target,"getUUID"):null;if(id!=null&&!id.equals(s.target)&&n-s.alert>=60){play(t,"target_alert",1.1f);s.alert=n;s.target=id;}if(id==null)s.target=null;
 }
}
