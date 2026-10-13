package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.client.KickAnimationClient;
import com.locknar.taczammocheck.tactical.client.PlayerAnimatorBridge;

public final class ClientKickAnimationHandler {
    private ClientKickAnimationHandler() {
    }

    public static void handleKickAnimation(int entityId, int mode) {
        KickAnimationClient.startForEntity(entityId, mode);
        if (mode == 0) {
            PlayerAnimatorBridge.playKickForEntity(entityId);
        }
    }
}
