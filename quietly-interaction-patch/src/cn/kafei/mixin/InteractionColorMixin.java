package cn.kafei.mixin;
import cn.kafei.interact.Interaction;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(targets="net.minecraft.world.entity.Entity",remap=false)
public abstract class InteractionColorMixin{
 @Inject(method="getTeamColor",at=@At("RETURN"),cancellable=true)
 private void quietly$color(CallbackInfoReturnable<Integer> ci){if(Interaction.entity(this))ci.setReturnValue(0xffd114);}
}
