package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import com.locknar.taczammocheck.port.NetworkEvent;

public record KickRaisePacket(int entityId, long durationMs) {
    public static void encode(KickRaisePacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
        buf.writeVarLong(packet.durationMs);
    }

    public static KickRaisePacket decode(FriendlyByteBuf buf) {
        return new KickRaisePacket(buf.readVarInt(), buf.readVarLong());
    }

    public static void handle(KickRaisePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("kickRaise", packet));
        context.setPacketHandled(true);
    }
}
