package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.server.MirrorGunHandler;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import com.locknar.taczammocheck.port.NetworkEvent;

public record MirrorCancelPacket() {
    public static void encode(MirrorCancelPacket packet, FriendlyByteBuf buf) {
    }

    public static MirrorCancelPacket decode(FriendlyByteBuf buf) {
        return new MirrorCancelPacket();
    }

    public static void handle(MirrorCancelPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                MirrorGunHandler.onMirrorCancel(player);
            }
        });
        context.setPacketHandled(true);
    }
}
