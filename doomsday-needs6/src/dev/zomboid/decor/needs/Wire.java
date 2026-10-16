package dev.zomboid.decor.needs;
import dev.zomboid.decor.*;import java.util.*;
public final class Wire{
 static final Set<String> IDS=Set.of("wiremesh","wiremesh_2","wiremesh_3","cornerwiremesh");static final Map<Object,Long> LAST=new WeakHashMap<>();static boolean warned;
 public static void touch(Object state,Object world,Object pos,Object entity){if(!IDS.contains(Storage.id(state))||R.yes(R.call(world,"isClientSide"))||!R.type("net.minecraft.world.entity.LivingEntity").isInstance(entity)||!R.yes(R.call(entity,"isAlive")))return;
  Object bounds=R.call(entity,"getBoundingBox");boolean intersects=false;for(Object box:(List<?>)R.call(R.call(state,"getShape",world,pos),"toAabbs")){Object moved=R.call(box,"move",R.coord(pos,"X"),R.coord(pos,"Y"),R.coord(pos,"Z"));if(R.yes(R.call(bounds,"intersects",moved))){intersects=true;break;}}if(!intersects)return;
  long now=R.lng(R.call(world,"getGameTime"));Long last=LAST.get(entity);if(last!=null&&now-last>=0&&now-last<20)return;LAST.put(entity,now);boolean hurt=R.yes(R.call(entity,"hurt",R.call(R.call(entity,"damageSources"),"sweetBerryBush"),2f));if(!hurt)return;
  if(R.type("net.minecraft.server.level.ServerPlayer").isInstance(entity))try{Object leg=R.get(R.type("ichttt.mods.firstaid.api.enums.EnumPlayerPart"),R.yes(R.call(R.call(entity,"getRandom"),"nextBoolean"))?"LEFT_LEG":"RIGHT_LEG");R.call(R.type("ichttt.mods.firstaid.fracture.Bleeding"),"start",entity,leg);}catch(IllegalStateException absent){if(!warned){warned=true;R.warn("FirstAid lower-leg bleeding unavailable",absent);}}
  Content.sound(world,pos,"wire_cut",.45f);Object option=R.make("net.minecraft.core.particles.BlockParticleOption",R.get(R.type("net.minecraft.core.particles.ParticleTypes"),"BLOCK"),R.call(R.get(R.type("net.minecraft.world.level.block.Blocks"),"REDSTONE_BLOCK"),"defaultBlockState"));R.call(world,"sendParticles",option,R.coord(entity,"X"),R.coord(entity,"Y")+.25,R.coord(entity,"Z"),5,.15,.1,.15,.04);
 }
}
