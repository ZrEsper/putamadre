package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ModNetwork;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.locknar.taczammocheck.port.NetworkEvent;

public record GunRunBrakeRequestPacket(int mode) {
    public static final int MODE_START_LEFT = 0;
    public static final int MODE_START_RIGHT = 1;
    public static final int MODE_CANCEL = 2;

    public static void encode(GunRunBrakeRequestPacket packet, FriendlyByteBuf buf) {
        buf.writeByte(packet.mode);
    }

    public static GunRunBrakeRequestPacket decode(FriendlyByteBuf buf) {
        return new GunRunBrakeRequestPacket(buf.readUnsignedByte());
    }

    public static void handle(GunRunBrakeRequestPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || packet.mode < 0 || packet.mode > 2) {
                return;
            }
            ModNetwork.sendGunRunBrakeSync(player, packet.mode);
        });
        context.setPacketHandled(true);
    }
}
