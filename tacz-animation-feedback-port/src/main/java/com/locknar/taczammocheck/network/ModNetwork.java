package com.locknar.taczammocheck.network;

import com.locknar.taczammocheck.network.AmmoCheckPosePacket;
import com.locknar.taczammocheck.network.AmmoCheckStatePacket;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import com.locknar.taczammocheck.port.NetworkDirection;
import com.locknar.taczammocheck.port.NetworkRegistry;
import com.locknar.taczammocheck.port.PacketDistributor;
import com.locknar.taczammocheck.port.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "3";
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named((ResourceLocation)ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "main")).networkProtocolVersion(() -> "3").clientAcceptedVersions("3"::equals).serverAcceptedVersions("3"::equals).simpleChannel();

    private ModNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.messageBuilder(AmmoCheckStatePacket.class, id++, NetworkDirection.PLAY_TO_SERVER).encoder(AmmoCheckStatePacket::encode).decoder(AmmoCheckStatePacket::decode).consumerMainThread(AmmoCheckStatePacket::handle).add();
        CHANNEL.messageBuilder(AmmoCheckPosePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT).encoder(AmmoCheckPosePacket::encode).decoder(AmmoCheckPosePacket::decode).consumerMainThread(AmmoCheckPosePacket::handle).add();
    }

    public static void sendAmmoCheckState(UUID token, boolean active, long durationMs, String ordinary) {
        CHANNEL.sendToServer((Object)new AmmoCheckStatePacket(token, active, durationMs, ordinary));
    }

    public static void broadcast(ServerPlayer player, AmmoCheckPosePacket packet) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), (Object)packet);
    }

    public static void send(ServerPlayer player, AmmoCheckPosePacket packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }
}
