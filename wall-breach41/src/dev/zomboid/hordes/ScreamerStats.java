package dev.zomboid.hordes;
/** Keep this support mutant out of the common-zombie pace/health/siege lottery. */
public final class ScreamerStats {
 static Object c(Object o,String n,Object...a){return ChunkReflection.call(o,n,a);}
 public static boolean apply(Object entity){
  if(!entity.getClass().getName().equals("dev.zomboid.mutants.Screamers$Screamer"))return false;
  Object tag=c(entity,"getPersistentData");c(tag,"putBoolean","zh_siege_checked",true);c(tag,"putBoolean","zh_siege",false);
  Object speed=c(entity,"getAttribute",ChunkReflection.constant("net.minecraft.world.entity.ai.attributes.Attributes","MOVEMENT_SPEED"));
  Object health=c(entity,"getAttribute",ChunkReflection.constant("net.minecraft.world.entity.ai.attributes.Attributes","MAX_HEALTH"));
  if(speed!=null){remove(speed,"individual_pace");remove(speed,"world_aggression7");c(speed,"setBaseValue",.50);}
  if(health!=null){remove(health,"individual_health");c(health,"setBaseValue",40.0);if(((Number)c(entity,"getHealth")).floatValue()>40)c(entity,"setHealth",40.0f);}
  return true;
 }
 static void remove(Object attr,String id){Object key=ChunkReflection.stat("net.minecraft.resources.ResourceLocation","fromNamespaceAndPath","zomboid_hordes",id);if(c(attr,"getModifier",key)!=null)c(attr,"removeModifier",key);}
}
