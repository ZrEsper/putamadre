package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import com.locknar.taczammocheck.port.NetworkEvent;

public record GunRunBrakeSyncPacket(int entityId, int mode) {
    public static void encode(GunRunBrakeSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
        buf.writeByte(packet.mode);
    }

    public static GunRunBrakeSyncPacket decode(FriendlyByteBuf buf) {
        return new GunRunBrakeSyncPacket(buf.readVarInt(), buf.readUnsignedByte());
    }

    public static void handle(GunRunBrakeSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("gunRunBrake", packet));
        context.setPacketHandled(true);
    }
}
