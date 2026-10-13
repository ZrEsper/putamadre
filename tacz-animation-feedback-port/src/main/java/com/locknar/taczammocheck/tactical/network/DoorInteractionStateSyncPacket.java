package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import com.locknar.taczammocheck.port.NetworkEvent;

public record DoorInteractionStateSyncPacket(boolean enabled) {
    public static void encode(DoorInteractionStateSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.enabled);
    }

    public static DoorInteractionStateSyncPacket decode(FriendlyByteBuf buf) {
        return new DoorInteractionStateSyncPacket(buf.readBoolean());
    }

    public static void handle(DoorInteractionStateSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("doorInteractionState", packet));
        context.setPacketHandled(true);
    }
}
