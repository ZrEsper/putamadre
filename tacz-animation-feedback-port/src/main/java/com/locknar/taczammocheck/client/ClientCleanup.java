package com.locknar.taczammocheck.client;

import com.locknar.taczammocheck.TaczGunCompat;
import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.locknar.taczammocheck.prone.ProneClient;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public final class ClientCleanup {
    private ClientCleanup() {
    }

    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientCleanup.clearAllClientState();
    }

    public static void clearAllClientState() {
        AmmoCheckClient.clearClientState();
        ProneClient.clear();
        TaczGunCompat.clearCaches();
    }
}
