package com.locknar.taczammocheck.client;

import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.locknar.taczammocheck.prone.ProneClient;
import net.minecraft.client.Minecraft;

public final class KeyInterceptor {
    private KeyInterceptor() {
    }

    public static boolean handleRawKey(long windowPointer, int key, int scanCode, int action, int modifiers) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null || windowPointer != mc.getWindow().getWindow()) {
            return false;
        }
        if (ProneClient.handleKey(key, scanCode, action)) {
            return true;
        }
        if (AmmoCheckClient.handleRawReloadKey(windowPointer, key, scanCode, action, modifiers)) {
            return true;
        }
        return com.locknar.taczammocheck.tactical.client.KeyInterceptor.handleRawKey(windowPointer, key, scanCode, action, modifiers);
    }
}
