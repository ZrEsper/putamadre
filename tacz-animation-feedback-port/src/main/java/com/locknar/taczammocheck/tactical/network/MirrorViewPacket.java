package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import com.locknar.taczammocheck.port.NetworkEvent;

public record MirrorViewPacket(double x, double y, double z, float yaw, float pitch, int durationTicks) {
    public static void encode(MirrorViewPacket packet, FriendlyByteBuf buf) {
        buf.writeDouble(packet.x);
        buf.writeDouble(packet.y);
        buf.writeDouble(packet.z);
        buf.writeFloat(packet.yaw);
        buf.writeFloat(packet.pitch);
        buf.writeVarInt(packet.durationTicks);
    }

    public static MirrorViewPacket decode(FriendlyByteBuf buf) {
        return new MirrorViewPacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat(), buf.readFloat(), buf.readVarInt());
    }

    public static void handle(MirrorViewPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("mirrorView", packet));
        context.setPacketHandled(true);
    }
}
