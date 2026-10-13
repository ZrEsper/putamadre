package com.locknar.taczammocheck.prone.mixin;

import com.locknar.taczammocheck.prone.ProneClient;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"com.tacz.guns.compat.playeranimator.animation.AnimationManager"}, remap=false)
public abstract class TaczProneAnimatorMixin {
    @Inject(method={"isPlayerLie"}, at={@At(value="HEAD")}, cancellable=true, require=1)
    private static void locknar$upperBodyReference(AbstractClientPlayer player, CallbackInfoReturnable<Boolean> ci) {
        if (ProneClient.active(player) || ProneClient.suppressVanillaRecovery((LivingEntity)player)) {
            ci.setReturnValue(false);
        }
    }
}
