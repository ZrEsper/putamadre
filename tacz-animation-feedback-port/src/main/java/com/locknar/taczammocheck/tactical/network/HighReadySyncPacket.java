package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import com.locknar.taczammocheck.port.NetworkEvent;

public record HighReadySyncPacket(int entityId, boolean active) {
    public static void encode(HighReadySyncPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
        buf.writeBoolean(packet.active);
    }

    public static HighReadySyncPacket decode(FriendlyByteBuf buf) {
        return new HighReadySyncPacket(buf.readVarInt(), buf.readBoolean());
    }

    public static void handle(HighReadySyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("highReady", packet));
        context.setPacketHandled(true);
    }
}
