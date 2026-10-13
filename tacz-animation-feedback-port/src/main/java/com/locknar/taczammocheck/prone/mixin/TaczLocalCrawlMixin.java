package com.locknar.taczammocheck.prone.mixin;

import com.locknar.taczammocheck.prone.ProneClient;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"com.tacz.guns.client.gameplay.LocalPlayerCrawl"}, remap=false)
public abstract class TaczLocalCrawlMixin {
    @Shadow
    @Final
    private LocalPlayer player;

    @Inject(method={"crawl"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void locknar$replaceRequest(boolean crawl, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method={"tickCrawl"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void locknar$replaceTick(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method={"isCrawling"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void locknar$visualCompat(CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(ProneClient.active((AbstractClientPlayer)this.player));
    }
}
