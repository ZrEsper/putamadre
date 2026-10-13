package com.locknar.taczammocheck.network;

import com.locknar.taczammocheck.client.AmmoCheckThirdPersonClient;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import com.locknar.taczammocheck.port.DistExecutor;
import com.locknar.taczammocheck.port.NetworkEvent;

public record AmmoCheckPosePacket(int entity, UUID uuid, UUID token, boolean active, long started, long duration, String gunId) {
    public static void encode(AmmoCheckPosePacket p, FriendlyByteBuf b) {
        b.writeVarInt(p.entity);
        b.writeUUID(p.uuid);
        b.writeUUID(p.token);
        b.writeBoolean(p.active);
        b.writeLong(p.started);
        b.writeLong(p.duration);
        b.writeUtf(p.gunId, 256);
    }

    public static AmmoCheckPosePacket decode(FriendlyByteBuf b) {
        return new AmmoCheckPosePacket(b.readVarInt(), b.readUUID(), b.readUUID(), b.readBoolean(), b.readLong(), b.readLong(), b.readUtf(256));
    }

    public static void handle(AmmoCheckPosePacket p, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> AmmoCheckThirdPersonClient.accept(p)));
        supplier.get().setPacketHandled(true);
    }
}
