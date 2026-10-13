package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import com.locknar.taczammocheck.port.NetworkEvent;

public record MirrorProgressPacket(int totalTicks) {
    public static void encode(MirrorProgressPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.totalTicks);
    }

    public static MirrorProgressPacket decode(FriendlyByteBuf buf) {
        return new MirrorProgressPacket(buf.readVarInt());
    }

    public static void handle(MirrorProgressPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("mirrorProgress", packet));
        context.setPacketHandled(true);
    }
}
