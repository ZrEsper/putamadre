package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.server.TaczHighReadyServer;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.locknar.taczammocheck.port.NetworkEvent;

public record HighReadyRequestPacket(boolean active) {
    public static void encode(HighReadyRequestPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.active);
    }

    public static HighReadyRequestPacket decode(FriendlyByteBuf buf) {
        return new HighReadyRequestPacket(buf.readBoolean());
    }

    public static void handle(HighReadyRequestPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                TaczHighReadyServer.handleHighReadyRequest(player, packet.active);
            }
        });
        context.setPacketHandled(true);
    }
}
