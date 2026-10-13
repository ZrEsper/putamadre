package me.xjqsh.lesraisinsarmor.mixin;

import me.xjqsh.lesraisinsarmor.effect.SuitEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(value=Dist.CLIENT)
@Mixin(value={MobEffectInstance.class})
public abstract class EffectInstanceMixin {
    @Shadow
    public abstract net.minecraft.core.Holder<MobEffect> getEffect();

    @Inject(method={"isInfiniteDuration"}, at={@At(value="HEAD")}, cancellable=true)
    public void isNoCounter(CallbackInfoReturnable<Boolean> cir) {
        if (this.getEffect().value() instanceof SuitEffect) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
