package com.locknar.taczammocheck.prone.mixin;

import com.locknar.taczammocheck.prone.ProneClient;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public abstract class ProneSwimRecoveryMixin {
    @Inject(method={"getSwimAmount"}, at={@At(value="HEAD")}, cancellable=true, require=1)
    private void locknar$completedStand(float partial, CallbackInfoReturnable<Float> ci) {
        if (ProneClient.suppressVanillaRecovery((LivingEntity)(Object)this)) {
            ci.setReturnValue(Float.valueOf(0.0f));
        }
    }
}
