package mod.cdv.tnpc.tactical;
import java.util.EnumSet;import net.minecraft.world.entity.ai.goal.Goal;
public final class SocialGoal extends Goal {final Object npc;public SocialGoal(Object npc){this.npc=npc;setFlags(EnumSet.of(Goal.Flag.MOVE,Goal.Flag.LOOK,Goal.Flag.TARGET));}public boolean canUse(){return Social.occupied(npc);}public boolean canContinueToUse(){return canUse();}public boolean requiresUpdateEveryTick(){return true;}public void tick(){}public void stop(){}}
