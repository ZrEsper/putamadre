package dev.zomboidmelee.boxing;
import java.lang.reflect.*;import java.util.*;
/** Server-authoritative bare-hand rules. No forced player velocity or position. */
public final class Combat18{
 static final Map<Object,Long> NEXT=Collections.synchronizedMap(new WeakHashMap<>());
 static final ThreadLocal<Boolean> HEAVY=ThreadLocal.withInitial(()->false);
 static long time(Object p){return R.lng(R.call(R.call(p,"level"),"getGameTime"));}
 static boolean zombie(Object p){return R.yes(R.call(R.type("dev.zomboidmelee.combat.ShoveCombat"),"zombie",p));}
 public static boolean gate(Object p,Object target){if(!Boxing16.empty(p)||R.yes(R.call(R.call(p,"level"),"isClientSide")))return true;long now=time(p);if(now<NEXT.getOrDefault(p,Long.MIN_VALUE))return false;NEXT.put(p,now+8);mark(target,now);return true;}
 static void mark(Object target,long now){if(target!=null&&zombie(target))R.call(R.call(target,"getPersistentData"),"putLong","boxing18_no_launch",now+2);}
 public static void knockback(Object event){Object v=R.call(event,"getEntity");if(!zombie(v))return;long until=R.lng(R.call(R.call(v,"getPersistentData"),"getLong","boxing18_no_launch"));if(until>time(v))R.call(event,"setCanceled",true);}
 public static int stunTicks(){return HEAVY.get()?14:8;}
 public static void register(Object event){Object registrar=R.call(event,"registrar","boxing18");Class<?> handler=R.type("net.neoforged.neoforge.network.handling.IPayloadHandler");Object proxy=Proxy.newProxyInstance(handler.getClassLoader(),new Class<?>[]{handler},(o,m,a)->{if(m.getName().equals("handle")){Heavy18 packet=(Heavy18)a[0];Object context=a[1];R.call(context,"enqueueWork",(Runnable)()->request(R.call(context,"player"),packet.entityId(),packet.action()));}if(m.getName().equals("toString"))return "HeavyFist18Handler";if(m.getName().equals("hashCode"))return System.identityHashCode(o);if(m.getName().equals("equals"))return o==a[0];return null;});R.call(registrar,"playToServer",Heavy18.TYPE,Heavy18.CODEC,proxy);}
 public static void send(int id){send(id,0);}public static void send(int id,int action){Class<?> payload=R.type("net.minecraft.network.protocol.common.custom.CustomPacketPayload");R.call(R.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToServer",new Heavy18(id,action),Array.newInstance(payload,0));}
 static void sound(Object p,String name,float volume,float pitch){R.call(R.call(p,"level"),"playSound",null,R.call(p,"blockPosition"),R.get(R.type("net.minecraft.sounds.SoundEvents"),name),R.get(R.type("net.minecraft.sounds.SoundSource"),"PLAYERS"),volume,pitch);}
 static boolean clearRay(Object p,Object end){Object context=R.make("net.minecraft.world.level.ClipContext",R.call(p,"getEyePosition"),end,R.get(R.type("net.minecraft.world.level.ClipContext$Block"),"COLLIDER"),R.get(R.type("net.minecraft.world.level.ClipContext$Fluid"),"NONE"),p);return R.call(R.call(R.call(p,"level"),"clip",context),"getType").toString().equals("MISS");}
 public static void request(Object p,int id,int action){if(action==1){Stomp18.stomp(p,id);return;}if(action==0)heavy(p,id);}
 public static void heavy(Object p,int id){if(!R.type("net.minecraft.server.level.ServerPlayer").isInstance(p)||!Boxing16.empty(p)||!R.yes(R.call(p,"isAlive"))||R.yes(R.call(p,"isSpectator"))||R.yes(R.call(p,"isSleeping"))||R.yes(R.call(p,"isUsingItem")))return;long now=time(p);if(now<NEXT.getOrDefault(p,Long.MIN_VALUE))return;
  Object v=id<0?null:R.call(R.call(p,"level"),"getEntity",id);
  if(v!=null){if(v==p||!R.type("net.minecraft.world.entity.LivingEntity").isInstance(v)||!R.yes(R.call(v,"isAlive"))||R.num(R.call(p,"distanceToSqr",v))>16||!R.yes(R.call(p,"hasLineOfSight",v)))return;Object start=R.call(p,"getEyePosition"),end=R.call(start,"add",R.call(R.call(p,"getLookAngle"),"scale",3.0));Object box=R.call(R.call(v,"getBoundingBox"),"inflate",.1);Optional<?> intersection=(Optional<?>)R.call(box,"clip",start,end);if(!R.yes(R.call(box,"contains",start))&&!intersection.isPresent())return;if(!clearRay(p,intersection.orElse(null)==null?start:intersection.get()))return;}
  if(!R.yes(R.call(R.type("com.armilp.ezweight.stamina.StaminaCommon"),"consume",p,100.0)))return;NEXT.put(p,now+18);R.call(p,"resetAttackStrengthTicker");sound(p,"PLAYER_ATTACK_SWEEP",.55f,.65f);
  if(v!=null){mark(v,now);HEAVY.set(true);try{Object source=R.call(R.call(p,"damageSources"),"playerAttack",p);if(R.yes(R.call(v,"hurt",source,3f)))sound(v,"PLAYER_ATTACK_STRONG",.8f,.7f);}finally{HEAVY.remove();}}
 }
}
