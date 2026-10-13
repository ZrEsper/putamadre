package com.locknar.taczammocheck.prone.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.tacz.guns.entity.shooter.LivingEntityCrawl"}, remap=false)
public abstract class TaczServerCrawlMixin {
    @Shadow
    @Final
    private LivingEntity shooter;

    @Inject(method={"crawl"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void locknar$replaceRequest(boolean crawl, CallbackInfo ci) {
        if (this.shooter instanceof Player) {
            ci.cancel();
        }
    }

    @Inject(method={"tickCrawling"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void locknar$replaceTick(CallbackInfo ci) {
        if (this.shooter instanceof Player) {
            ci.cancel();
        }
    }
}
