package com.locknar.taczammocheck.tactical.mixin;

import com.locknar.taczammocheck.tactical.client.TaczWallObstructionClient;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={MouseHandler.class})
public abstract class MouseHandlerMixin {
    @Inject(method={"onPress"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void locknar_doorkick$onPressNamed(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        if (TaczWallObstructionClient.handleRawMouse(windowPointer, button, action, modifiers)) {
            ci.cancel();
        }
    }

}
