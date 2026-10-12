package dev.zomboid.clans;
import net.minecraft.world.entity.ai.goal.Goal;
public final class NexusGoal extends Goal {private final Object mob;public NexusGoal(Object mob){this.mob=mob;Nexus.configureGoal(this);}public boolean canUse(){return Nexus.shouldUseGoal(mob);}public boolean canContinueToUse(){return canUse();}public void tick(){Nexus.mobTick(mob);}}
