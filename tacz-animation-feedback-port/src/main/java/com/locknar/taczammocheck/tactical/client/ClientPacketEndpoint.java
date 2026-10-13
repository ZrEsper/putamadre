package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.locknar.taczammocheck.tactical.TaczWallObstructionCommon;
import com.locknar.taczammocheck.tactical.client.BackGunClientCache;
import com.locknar.taczammocheck.tactical.client.ClientKickAnimationHandler;
import com.locknar.taczammocheck.tactical.client.DoorHudFeedbackClient;
import com.locknar.taczammocheck.tactical.client.DoorOpenHandAnimationClient;
import com.locknar.taczammocheck.tactical.client.GunRunAnimationClient;
import com.locknar.taczammocheck.tactical.client.KickAnimationClient;
import com.locknar.taczammocheck.tactical.client.MirrorViewClient;
import com.locknar.taczammocheck.tactical.client.TaczHighReadyClient;
import com.locknar.taczammocheck.tactical.client.TaczKickRaiseClient;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import com.locknar.taczammocheck.tactical.network.BackGunSyncPacket;
import com.locknar.taczammocheck.tactical.network.DoorInteractionStateSyncPacket;
import com.locknar.taczammocheck.tactical.network.GunRunBrakeSyncPacket;
import com.locknar.taczammocheck.tactical.network.HighReadySyncPacket;
import com.locknar.taczammocheck.tactical.network.KickAnimationPacket;
import com.locknar.taczammocheck.tactical.network.KickRaisePacket;
import com.locknar.taczammocheck.tactical.network.MirrorProgressPacket;
import com.locknar.taczammocheck.tactical.network.MirrorViewPacket;
import com.locknar.taczammocheck.tactical.network.WeaponSwitchSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;

public final class ClientPacketEndpoint {
    private ClientPacketEndpoint() {
    }

    public static void backGun(Object raw) {
        BackGunSyncPacket packet = (BackGunSyncPacket)raw;
        BackGunClientCache.update(packet.entityId(), packet.firstSlot(), packet.selectedSlot());
    }

    public static void gunRunBrake(Object raw) {
        GunRunBrakeSyncPacket packet = (GunRunBrakeSyncPacket)raw;
        GunRunAnimationClient.handleBrakeSync(packet.entityId(), packet.mode());
    }

    public static void highReady(Object raw) {
        HighReadySyncPacket packet = (HighReadySyncPacket)raw;
        TaczHighReadyClient.setEntityHighReady(packet.entityId(), packet.active());
    }

    public static void kickAnimation(Object raw) {
        KickAnimationPacket packet = (KickAnimationPacket)raw;
        ClientKickAnimationHandler.handleKickAnimation(packet.entityId(), packet.mode());
    }

    public static void kickRaise(Object raw) {
        KickRaisePacket packet = (KickRaisePacket)raw;
        TaczKickRaiseClient.triggerKickRaiseForEntity(packet.entityId(), packet.durationMs());
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.player != null && mc.player.getId() == packet.entityId()) {
            TaczWallObstructionCommon.startKickRaiseBlock((LivingEntity)mc.player, packet.durationMs());
        }
    }

    public static void mirrorProgress(Object raw) {
        MirrorProgressPacket packet = (MirrorProgressPacket)raw;
        MirrorViewClient.startUseProgress(packet.totalTicks());
    }

    public static void mirrorView(Object raw) {
        MirrorViewPacket packet = (MirrorViewPacket)raw;
        MirrorViewClient.start(packet.x(), packet.y(), packet.z(), packet.yaw(), packet.pitch(), packet.durationTicks());
    }

    public static void weaponSwitch(Object raw) {
        WeaponSwitchSyncPacket packet = (WeaponSwitchSyncPacket)raw;
        ThirdPersonWeaponSwitchClient.handleSwitch(packet.entityId(), packet.sequence(), packet.type(), packet.oldSlot(), packet.newSlot(), packet.outgoing(), packet.incoming());
    }

    public static void doorInteractionState(Object raw) {
        DoorInteractionStateSyncPacket packet = (DoorInteractionStateSyncPacket)raw;
        DoorInteractionGlobalState.setClientEnabled(packet.enabled());
        if (!packet.enabled()) {
            DoorHudFeedbackClient.clear();
            DoorOpenHandAnimationClient.clear();
            KickAnimationClient.clearAll();
            MirrorViewClient.stop();
        }
    }
}
