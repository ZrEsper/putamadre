package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.ActionAnimationSampler;
import com.locknar.taczammocheck.tactical.client.BackGunClientCache;
import com.locknar.taczammocheck.tactical.client.DirectionalLegAnimationClient;
import com.locknar.taczammocheck.tactical.client.DoorHudFeedbackClient;
import com.locknar.taczammocheck.tactical.client.DoorOpenHandAnimationClient;
import com.locknar.taczammocheck.tactical.client.GunRunAnimationClient;
import com.locknar.taczammocheck.tactical.client.KickAnimationClient;
import com.locknar.taczammocheck.tactical.client.MirrorViewClient;
import com.locknar.taczammocheck.tactical.client.ProneTacticalAnimationClient;
import com.locknar.taczammocheck.tactical.client.TaczHighReadyClient;
import com.locknar.taczammocheck.tactical.client.TaczKickRaiseClient;
import com.locknar.taczammocheck.tactical.client.TaczWallObstructionClient;
import com.locknar.taczammocheck.tactical.client.TaczWallObstructionThirdPersonClient;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

public final class ClientCleanup {
    private ClientCleanup() {
    }

    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientCleanup.clearAllClientState();
    }

    public static void clearAllClientState() {
        TaczHighReadyClient.clearClientState();
        TaczKickRaiseClient.clearAll();
        KickAnimationClient.clearAll();
        DoorOpenHandAnimationClient.clear();
        ActionAnimationSampler.clear();
        DoorHudFeedbackClient.clear();
        TaczWallObstructionClient.clearClientState();
        TaczWallObstructionThirdPersonClient.clearClientState();
        BackGunClientCache.clear();
        ThirdPersonWeaponSwitchClient.clear();
        DirectionalLegAnimationClient.clear();
        GunRunAnimationClient.clear();
        ProneTacticalAnimationClient.clear();
        MirrorViewClient.stop();
        TaczGunCompat.clearCaches();
        DoorInteractionGlobalState.resetClientState();
    }
}
