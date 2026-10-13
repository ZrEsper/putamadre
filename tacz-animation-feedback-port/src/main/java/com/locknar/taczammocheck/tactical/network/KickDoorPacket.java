package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.server.ServerKickHandler;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.locknar.taczammocheck.port.NetworkEvent;

public record KickDoorPacket(BlockPos lowerDoorPos) {
    public static void encode(KickDoorPacket packet, FriendlyByteBuf buf) {
        buf.writeBlockPos(packet.lowerDoorPos);
    }

    public static KickDoorPacket decode(FriendlyByteBuf buf) {
        return new KickDoorPacket(buf.readBlockPos());
    }

    public static void handle(KickDoorPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ServerKickHandler.tryKickDoor(player, packet.lowerDoorPos);
            }
        });
        context.setPacketHandled(true);
    }
}
