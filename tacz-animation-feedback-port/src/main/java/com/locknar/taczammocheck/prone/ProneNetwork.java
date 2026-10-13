package com.locknar.taczammocheck.prone;

import com.locknar.taczammocheck.prone.ProneClient;
import com.locknar.taczammocheck.prone.ProneServer;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import com.locknar.taczammocheck.port.NetworkDirection;
import com.locknar.taczammocheck.port.NetworkEvent;
import com.locknar.taczammocheck.port.NetworkRegistry;
import com.locknar.taczammocheck.port.PacketDistributor;
import com.locknar.taczammocheck.port.SimpleChannel;

public final class ProneNetwork {
    private static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named((ResourceLocation)ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "prone")).networkProtocolVersion(() -> "2").clientAcceptedVersions("2"::equals).serverAcceptedVersions("2"::equals).simpleChannel();

    private ProneNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(Request.class, 0, NetworkDirection.PLAY_TO_SERVER).encoder(Request::encode).decoder(Request::decode).consumerMainThread(Request::handle).add();
        CHANNEL.messageBuilder(Sync.class, 1, NetworkDirection.PLAY_TO_CLIENT).encoder(Sync::encode).decoder(Sync::decode).consumerMainThread(Sync::handle).add();
        CHANNEL.messageBuilder(Move.class, 2, NetworkDirection.PLAY_TO_SERVER).encoder(Move::encode).decoder(Move::decode).consumerMainThread(Move::handle).add();
    }

    public static void request(long sequence, boolean down) {
        CHANNEL.sendToServer((Object)new Request(sequence, down));
    }

    public static void movement(long sequence, boolean moving, float yaw) {
        CHANNEL.sendToServer((Object)new Move(sequence, moving, yaw));
    }

    private static Sync packet(ServerPlayer player, ProneServer.State state) {
        return new Sync(player.getId(), player.getUUID(), state.phase(), state.start(), state.anchor(), state.revision(), state.duration(), state.target());
    }

    public static void broadcast(ServerPlayer player, ProneServer.State state) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), (Object)ProneNetwork.packet(player, state));
    }

    public static void send(ServerPlayer viewer, ServerPlayer subject, ProneServer.State state) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> viewer), (Object)ProneNetwork.packet(subject, state));
    }

    public record Request(long sequence, boolean down) {
        static void encode(Request p, FriendlyByteBuf b) {
            b.writeVarLong(p.sequence);
            b.writeBoolean(p.down);
        }

        static Request decode(FriendlyByteBuf b) {
            return new Request(b.readVarLong(), b.readBoolean());
        }

        static void handle(Request p, Supplier<NetworkEvent.Context> c) {
            ProneServer.request(c.get().getSender(), p.sequence, p.down);
            c.get().setPacketHandled(true);
        }
    }

    public record Sync(int entity, UUID uuid, int phase, long started, float anchor, long revision, int duration, float target) {
        static void encode(Sync p, FriendlyByteBuf b) {
            b.writeVarInt(p.entity);
            b.writeUUID(p.uuid);
            b.writeByte(p.phase);
            b.writeLong(p.started);
            b.writeFloat(p.anchor);
            b.writeVarLong(p.revision);
            b.writeVarInt(p.duration);
            b.writeFloat(p.target);
        }

        static Sync decode(FriendlyByteBuf b) {
            return new Sync(b.readVarInt(), b.readUUID(), b.readUnsignedByte(), b.readLong(), b.readFloat(), b.readVarLong(), b.readVarInt(), b.readFloat());
        }

        static void handle(Sync p, Supplier<NetworkEvent.Context> c) {
            ProneClient.accept(p);
            c.get().setPacketHandled(true);
        }
    }

    public record Move(long sequence, boolean moving, float yaw) {
        static void encode(Move p, FriendlyByteBuf b) {
            b.writeVarLong(p.sequence);
            b.writeBoolean(p.moving);
            b.writeFloat(p.yaw);
        }

        static Move decode(FriendlyByteBuf b) {
            return new Move(b.readVarLong(), b.readBoolean(), b.readFloat());
        }

        static void handle(Move p, Supplier<NetworkEvent.Context> c) {
            ProneServer.movement(c.get().getSender(), p.sequence, p.moving, p.yaw);
            c.get().setPacketHandled(true);
        }
    }
}
