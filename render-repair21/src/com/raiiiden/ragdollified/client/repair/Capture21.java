package com.raiiiden.ragdollified.client.repair;
import dev.zomboid.gore.R;
/** Preserve compatible humanoid model and texture caches without clearing all corpses. */
public final class Capture21 {
 static boolean warned;
 public static void model(Object entity,Object renderer,Object modelType){
  try{R.call(R.type("com.raiiiden.ragdollified.client.compat.MobModelCompatibility"),"capture",entity,renderer,modelType);}catch(RuntimeException optional){}
  try{Object model=R.call(renderer,"getModel");if(!R.type("net.minecraft.client.model.HumanoidModel").isInstance(model))return;Object key=R.call(R.type("net.minecraft.world.entity.EntityType"),"getKey",R.call(entity,"getType"));R.call(R.type("com.raiiiden.ragdollified.client.ClientMobModelCache"),"cacheModel",String.valueOf(key),model);}catch(RuntimeException unsupported){if(!warned){warned=true;System.err.println("[Ragdoll21] Optional humanoid model capture unavailable: "+unsupported);}}
 }
 public static void trim(java.util.Map<?,?>cache){if(cache.size()<=200)return;var it=cache.keySet().iterator();while(cache.size()>200&&it.hasNext()){it.next();it.remove();}}
}
