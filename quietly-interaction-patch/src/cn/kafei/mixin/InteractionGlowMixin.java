package cn.kafei.mixin;
import cn.kafei.interact.Interaction;import net.minecraft.world.entity.Entity;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(targets="net.minecraft.client.Minecraft",remap=false)
public abstract class InteractionGlowMixin{
 @Inject(method="shouldEntityAppearGlowing",at=@At("RETURN"),cancellable=true)
 private void quietly$glow(Entity e,CallbackInfoReturnable<Boolean> ci){if(Interaction.entity(e))ci.setReturnValue(true);}
}
