package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import com.locknar.taczammocheck.port.NetworkEvent;

public record BackGunSyncPacket(int entityId, ItemStack firstSlot, int selectedSlot) {
    public static void encode(BackGunSyncPacket packet, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf,packet.firstSlot);
        buf.writeVarInt(packet.selectedSlot);
    }

    public static BackGunSyncPacket decode(RegistryFriendlyByteBuf buf) {
        return new BackGunSyncPacket(buf.readVarInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readVarInt());
    }

    public static void handle(BackGunSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("backGun", packet));
        context.setPacketHandled(true);
    }
}
