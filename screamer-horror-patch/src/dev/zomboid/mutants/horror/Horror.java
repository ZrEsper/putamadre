package dev.zomboid.mutants.horror;
import java.util.*;import java.util.function.Supplier;
public final class Horror {
 static final String ID="mutants_l4d2";public static Object items,sounds,egg,scream;
 static final Map<Object,Object>targets=Collections.synchronizedMap(new WeakHashMap<>());static final Map<Object,Integer>cries=Collections.synchronizedMap(new WeakHashMap<>());
 static Object c(Object o,String n,Object...a){return R.call(o,n,a);}static Class<?>t(String n){return R.type(n);}static Object key(String path){return c(t("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath",ID,path);}
 public static void register(Object bus){items=c(t("net.neoforged.neoforge.registries.DeferredRegister"),"create",R.get(t("net.minecraft.core.registries.Registries"),"ITEM"),ID);sounds=c(t("net.neoforged.neoforge.registries.DeferredRegister"),"create",R.get(t("net.minecraft.core.registries.Registries"),"SOUND_EVENT"),ID);
  egg=c(items,"register","screamer_spawn_egg",(Supplier<Object>)()->R.make("net.neoforged.neoforge.common.DeferredSpawnEggItem",(Supplier<Object>)()->c(R.get(t("dev.zomboid.mutants.Screamers"),"TYPE"),"get"),0x302429,0xd4c6a1,R.make("net.minecraft.world.item.Item$Properties")));
  scream=c(sounds,"register","screamer_detect",(Supplier<Object>)()->c(t("net.minecraft.sounds.SoundEvent"),"createVariableRangeEvent",key("screamer_detect")));c(items,"register",bus);c(sounds,"register",bus);
 }
 public static void creative(Object event){if(Objects.equals(c(event,"getTabKey"),R.get(t("net.minecraft.world.item.CreativeModeTabs"),"SPAWN_EGGS")))c(event,"accept",c(egg,"get"));}
 public static void observe(Object entity){Object target=c(entity,"getTarget"),id=target==null?null:c(target,"getUUID"),previous=targets.get(entity);if(id==null){targets.remove(entity);return;}if(!id.equals(previous)){targets.put(entity,id);R.set(entity,"nextCry",0L);}}
 public static void cry(Object entity){c(entity,"playSound",c(scream,"get"),6.0f,1.0f);Object level=c(entity,"level");c(level,"broadcastEntityEvent",entity,(byte)61);}
 public static void markCry(Object entity){cries.put(entity,((Number)R.get(entity,"tickCount")).intValue());}
 public static boolean screaming(Object entity){Integer start=cries.get(entity);if(start==null)return false;int elapsed=((Number)R.get(entity,"tickCount")).intValue()-start;if(elapsed<0||elapsed>=202){cries.remove(entity);return false;}return true;}
 static float wave(float x){return (float)Math.sin(x);}static void rot(Object part,float x,float y,float z){R.set(part,"xRot",x);R.set(part,"yRot",y);R.set(part,"zRot",z);}
 public static void animate(Object model,Object entity,float stride,float movement,float age){Object head=R.get(model,"head"),body=R.get(model,"body"),right=R.get(model,"rightArm"),left=R.get(model,"leftArm"),jaw=R.get(model,"jaw");boolean hunting=Boolean.TRUE.equals(c(entity,"isAggressive")),crying=screaming(entity);float amount=Math.min(1,Math.abs(movement));
  float twitch=(float)Math.pow(Math.max(0,wave(age*.73f)),12)*wave(age*2.7f),snap=hunting?.27f:.08f;
  float yaw=((Number)R.get(head,"yRot")).floatValue(),pitch=((Number)R.get(head,"xRot")).floatValue();
  rot(body,.24f+amount*.2f+wave(age*.17f)*.035f,wave(age*.13f)*.10f,wave(age*.19f)*.09f+twitch*.09f);
  rot(head,pitch+(crying?-.65f:.12f)+wave(age*.31f)*.06f,yaw+twitch*snap,(crying?.38f:.16f)+wave(age*.29f)*.13f-twitch*snap);
  rot(right,(crying?-2.1f:hunting?-1.35f:-.18f)+wave(age*.37f)*.17f,-.35f+wave(age*.23f)*.15f,-.45f-twitch*.3f);
  rot(left,(crying?-1.25f:hunting?-.70f:.16f)+wave(age*.43f+1.7f)*.23f,.42f,-.15f+wave(age*.27f)*.25f);
  rot(R.get(model,"rightLeg"),(float)Math.cos(stride*1.12f)*1.1f*amount,.08f,.06f);rot(R.get(model,"leftLeg"),-(float)Math.cos(stride*1.12f+.18f)*1.2f*amount,-.1f,-.07f);
  rot(jaw,crying?1.05f+wave(age*.91f)*.09f:hunting?.22f+wave(age*.4f)*.06f:.035f,0,crying?wave(age*.63f)*.04f:0);
 }
}
