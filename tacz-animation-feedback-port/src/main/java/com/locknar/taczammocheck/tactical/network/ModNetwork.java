package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.BackGunSyncPacket;
import com.locknar.taczammocheck.tactical.network.DoorInteractionStateSyncPacket;
import com.locknar.taczammocheck.tactical.network.GunRunBrakeRequestPacket;
import com.locknar.taczammocheck.tactical.network.GunRunBrakeSyncPacket;
import com.locknar.taczammocheck.tactical.network.HighReadyRequestPacket;
import com.locknar.taczammocheck.tactical.network.HighReadySyncPacket;
import com.locknar.taczammocheck.tactical.network.KickAnimationPacket;
import com.locknar.taczammocheck.tactical.network.KickDoorPacket;
import com.locknar.taczammocheck.tactical.network.KickRaisePacket;
import com.locknar.taczammocheck.tactical.network.MirrorCancelPacket;
import com.locknar.taczammocheck.tactical.network.MirrorProgressPacket;
import com.locknar.taczammocheck.tactical.network.MirrorViewPacket;
import com.locknar.taczammocheck.tactical.network.WeaponSwitchSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import com.locknar.taczammocheck.port.NetworkDirection;
import com.locknar.taczammocheck.port.NetworkRegistry;
import com.locknar.taczammocheck.port.PacketDistributor;
import com.locknar.taczammocheck.port.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "4";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named((ResourceLocation)ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "tactical")).networkProtocolVersion(() -> "4").clientAcceptedVersions("4"::equals).serverAcceptedVersions("4"::equals).simpleChannel();

    private ModNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(KickDoorPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(KickDoorPacket::encode).decoder(KickDoorPacket::decode).consumerMainThread(KickDoorPacket::handle).add();
        CHANNEL.messageBuilder(KickAnimationPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(KickAnimationPacket::encode).decoder(KickAnimationPacket::decode).consumerMainThread(KickAnimationPacket::handle).add();
        CHANNEL.messageBuilder(KickRaisePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(KickRaisePacket::encode).decoder(KickRaisePacket::decode).consumerMainThread(KickRaisePacket::handle).add();
        CHANNEL.messageBuilder(MirrorViewPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(MirrorViewPacket::encode).decoder(MirrorViewPacket::decode).consumerMainThread(MirrorViewPacket::handle).add();
        CHANNEL.messageBuilder(MirrorProgressPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(MirrorProgressPacket::encode).decoder(MirrorProgressPacket::decode).consumerMainThread(MirrorProgressPacket::handle).add();
        CHANNEL.messageBuilder(MirrorCancelPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(MirrorCancelPacket::encode).decoder(MirrorCancelPacket::decode).consumerMainThread(MirrorCancelPacket::handle).add();
        CHANNEL.messageBuilder(HighReadyRequestPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(HighReadyRequestPacket::encode).decoder(HighReadyRequestPacket::decode).consumerMainThread(HighReadyRequestPacket::handle).add();
        CHANNEL.messageBuilder(HighReadySyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(HighReadySyncPacket::encode).decoder(HighReadySyncPacket::decode).consumerMainThread(HighReadySyncPacket::handle).add();
        CHANNEL.messageBuilder(BackGunSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(BackGunSyncPacket::encode).decoder(BackGunSyncPacket::decode).consumerMainThread(BackGunSyncPacket::handle).add();
        CHANNEL.messageBuilder(WeaponSwitchSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(WeaponSwitchSyncPacket::encode).decoder(WeaponSwitchSyncPacket::decode).consumerMainThread(WeaponSwitchSyncPacket::handle).add();
        CHANNEL.messageBuilder(GunRunBrakeRequestPacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(GunRunBrakeRequestPacket::encode).decoder(GunRunBrakeRequestPacket::decode).consumerMainThread(GunRunBrakeRequestPacket::handle).add();
        CHANNEL.messageBuilder(GunRunBrakeSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(GunRunBrakeSyncPacket::encode).decoder(GunRunBrakeSyncPacket::decode).consumerMainThread(GunRunBrakeSyncPacket::handle).add();
        CHANNEL.messageBuilder(DoorInteractionStateSyncPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(DoorInteractionStateSyncPacket::encode).decoder(DoorInteractionStateSyncPacket::decode).consumerMainThread(DoorInteractionStateSyncPacket::handle).add();
    }

    public static void sendKickDoor(BlockPos lowerDoorPos) {
        CHANNEL.sendToServer((Object)new KickDoorPacket(lowerDoorPos));
    }

    public static void sendKickAnimation(ServerPlayer player) {
        ModNetwork.sendKickAnimation(player, 0);
    }

    public static void sendKickAnimation(ServerPlayer player, int mode) {
        if (player == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), (Object)new KickAnimationPacket(player.getId(), mode));
    }

    public static void sendKickRaise(ServerPlayer player, long durationMs) {
        if (player == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), (Object)new KickRaisePacket(player.getId(), durationMs));
    }

    public static void sendMirrorView(ServerPlayer player, double x, double y, double z, float yaw, float pitch, int durationTicks) {
        if (player == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), (Object)new MirrorViewPacket(x, y, z, yaw, pitch, durationTicks));
    }

    public static void sendMirrorProgress(ServerPlayer player, int totalTicks) {
        if (player == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), (Object)new MirrorProgressPacket(totalTicks));
    }

    public static void sendMirrorCancel() {
        CHANNEL.sendToServer((Object)new MirrorCancelPacket());
    }

    public static void sendBackGunSync(ServerPlayer subject) {
        if (subject == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> subject), (Object)new BackGunSyncPacket(subject.getId(), subject.getInventory().getItem(0).copy(), subject.getInventory().selected));
    }

    public static void sendBackGunSyncTo(ServerPlayer viewer, ServerPlayer subject) {
        if (viewer == null || subject == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), (Object)new BackGunSyncPacket(subject.getId(), subject.getInventory().getItem(0).copy(), subject.getInventory().selected));
    }

    public static void sendWeaponSwitch(ServerPlayer subject, WeaponSwitchSyncPacket packet) {
        if (subject == null || packet == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> subject), (Object)packet);
    }

    public static void sendHighReadyRequest(boolean active) {
        CHANNEL.sendToServer((Object)new HighReadyRequestPacket(active));
    }

    public static void sendHighReadySync(ServerPlayer player, boolean active) {
        if (player == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), (Object)new HighReadySyncPacket(player.getId(), active));
    }

    public static void sendGunRunBrakeRequest(int mode) {
        if (mode < 0 || mode > 2) {
            return;
        }
        CHANNEL.sendToServer((Object)new GunRunBrakeRequestPacket(mode));
    }

    public static void sendGunRunBrakeSync(ServerPlayer subject, int mode) {
        if (subject == null || mode < 0 || mode > 2) {
            return;
        }
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> subject), (Object)new GunRunBrakeSyncPacket(subject.getId(), mode));
    }

    public static void sendDoorInteractionState(ServerPlayer player, boolean enabled) {
        if (player == null) {
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), (Object)new DoorInteractionStateSyncPacket(enabled));
    }

    public static void sendDoorInteractionStateToAll(MinecraftServer server, boolean enabled) {
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ModNetwork.sendDoorInteractionState(player, enabled);
        }
    }
}
