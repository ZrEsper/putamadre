package dev.zomboid.hordes;
import java.util.LinkedHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

/** Find the first obstruction across the mob's walking envelope, never a block behind it. */
public final class BreachAI {
 public static boolean failedStep(Mob mob, CompoundTag tag, long now) {
  if (!tag.contains("zh_step_at") || Math.hypot(mob.getX()-tag.getDouble("zh_step_x"),mob.getZ()-tag.getDouble("zh_step_z"))>.75) {
   tag.putLong("zh_step_at",now);tag.putDouble("zh_step_x",mob.getX());tag.putDouble("zh_step_z",mob.getZ());return false;
  }
  return now-tag.getLong("zh_step_at")>=40;
 }
 public static boolean blocked(Mob mob) {
  boolean stuck=failedStep(mob,mob.getPersistentData(),mob.level().getGameTime());
  return mob.horizontalCollision || mob.getNavigation().isDone() || stuck;
 }
 public static boolean permitted(Mob mob,ServerLevel level) {
  return EventHooks.canEntityGrief(level,mob);
 }
 public static BlockPos find(Mob mob, ServerLevel level, CompoundTag tag, double dx,double dz) {
  double length=Math.hypot(dx,dz);if(!Double.isFinite(length)||length<.001)return null;
  dx/=length;dz/=length;
  double half=Math.max(.1,mob.getBbWidth()*.5-.025);
  double height=Math.max(.2,mob.getBbHeight());
  LinkedHashSet<BlockPos> hits=new LinkedHashSet<>();
  // Include feet and crown: opening only the chest block does not produce a walkable doorway.
  for(double y:new double[]{.1,Math.min(.9,height*.5),height-.1})
   for(double side:new double[]{0,-half,half}) {
    Vec3 from=mob.position().add(-dz*side,y,dx*side);
    Vec3 to=from.add(dx*(half+1.05),0,dz*(half+1.05));
    BlockHitResult hit=level.clip(new ClipContext(from,to,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,mob));
    if(hit.getType()==HitResult.Type.BLOCK && level.hasChunkAt(hit.getBlockPos()) && !level.getBlockState(hit.getBlockPos()).getCollisionShape(level,hit.getBlockPos()).isEmpty())hits.add(hit.getBlockPos().immutable());
   }
  // Preserve work only while the same wall remains physically reachable and in the route.
  if(tag.contains("zh_siege_bx")) {
   BlockPos old=new BlockPos(tag.getInt("zh_siege_bx"),tag.getInt("zh_siege_by"),tag.getInt("zh_siege_bz"));
   if(hits.contains(old))return old;
  }
  // Prefer a reachable breakable portion; a protected block is never used to see through walls.
  for(BlockPos p:hits) {
   var state=level.getBlockState(p);
   if(SiegeRules.breakable(state.getDestroySpeed(level,p),state.hasBlockEntity(),!state.getFluidState().isEmpty()))return p;
  }
  return hits.isEmpty()?null:hits.iterator().next();
 }
}
