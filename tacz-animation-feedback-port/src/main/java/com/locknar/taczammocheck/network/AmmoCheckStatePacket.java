package com.locknar.taczammocheck.network;

import com.locknar.taczammocheck.AmmoCheckServer;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import com.locknar.taczammocheck.port.NetworkEvent;

public record AmmoCheckStatePacket(UUID token, boolean active, long durationMs, String ordinary) {
    public static void encode(AmmoCheckStatePacket p, FriendlyByteBuf b) {
        b.writeUUID(p.token);
        b.writeBoolean(p.active);
        b.writeLong(p.durationMs);
        b.writeUtf(p.ordinary, 64);
    }

    public static AmmoCheckStatePacket decode(FriendlyByteBuf b) {
        return new AmmoCheckStatePacket(b.readUUID(), b.readBoolean(), b.readLong(), b.readUtf(64));
    }

    public static void handle(AmmoCheckStatePacket p, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                AmmoCheckServer.state(context.getSender(), p.token, p.active, p.durationMs, p.ordinary);
            }
        });
        context.setPacketHandled(true);
    }
}
