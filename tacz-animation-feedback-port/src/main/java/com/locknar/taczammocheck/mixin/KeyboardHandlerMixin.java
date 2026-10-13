package com.locknar.taczammocheck.mixin;

import com.locknar.taczammocheck.client.KeyInterceptor;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={KeyboardHandler.class})
public abstract class KeyboardHandlerMixin {
    @Inject(method={"keyPress"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void locknar$taczAmmoCheck$keyPressNamed(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (KeyInterceptor.handleRawKey(windowPointer, key, scanCode, action, modifiers)) {
            ci.cancel();
        }
    }

}
