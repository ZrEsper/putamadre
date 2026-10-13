package com.locknar.taczammocheck.prone.mixin;

import net.neoforged.neoforge.client.event.InputEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"com.tacz.guns.client.input.CrawlKey"}, remap=false)
public abstract class TaczCrawlKeyMixin {
    @Inject(method={"onCrawlPress"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void locknar$nativeKey(InputEvent.Key event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method={"onCrawlControllerPress"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void locknar$nativeController(boolean pressed, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(false);
    }
}
