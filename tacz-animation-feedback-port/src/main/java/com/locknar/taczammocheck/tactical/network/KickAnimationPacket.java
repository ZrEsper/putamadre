package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import com.locknar.taczammocheck.port.NetworkEvent;

public record KickAnimationPacket(int entityId, int mode) {
    public static final int MODE_NORMAL = 0;
    public static final int MODE_BACK_KICK_FROM_LEFT = 1;
    public static final int MODE_BACK_KICK_FROM_RIGHT = 2;

    public static void encode(KickAnimationPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
        buf.writeVarInt(packet.mode);
    }

    public static KickAnimationPacket decode(FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();
        int mode = 0;
        if (buf.isReadable()) {
            mode = buf.readVarInt();
        }
        return new KickAnimationPacket(entityId, mode);
    }

    public static void handle(KickAnimationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("kickAnimation", packet));
        context.setPacketHandled(true);
    }
}
