package dev.zomboid.daylight;
public class Daylight {
 static boolean warned;
 public static void apply(Object world,Object entity){
  try {if(entity==null||R.yes(R.call(world,"isClientSide"))||!R.yes(R.call(world,"isDay"))||!R.type("net.minecraft.world.entity.LivingEntity").isInstance(entity))return;
   if(!R.yes(R.call(R.call(entity,"getType"),"is",R.get(R.type("net.minecraft.tags.EntityTypeTags"),"UNDEAD"))))return;
   if(Math.floorMod(((Number)R.get(entity,"tickCount")).intValue()+((Number)R.call(entity,"getId")).intValue(),20)!=0)return;
   Object vars=R.call(R.type("net.mcreator.weakerdayzombieneoforge.network.WeakerDayZombieNeoforgeModVariables$WorldVariables"),"get",world);
   if(R.num(R.get(vars,"wdz_toggle"))!=0)return;
   // No velocity reset. Gentle temporary daylight slowness expires normally after sunset.
   Object slow=R.get(R.type("net.minecraft.world.effect.MobEffects"),"MOVEMENT_SLOWDOWN");
   R.call(entity,"addEffect",R.make("net.minecraft.world.effect.MobEffectInstance",slow,40,0,false,false));
  }catch(Throwable e){if(!warned){warned=true;System.err.println("[Weaker day gentle pacing] "+e);e.printStackTrace();}}
 }
}
