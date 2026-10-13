package com.locknar.taczammocheck.tactical.network;

import com.locknar.taczammocheck.tactical.network.ClientPacketDispatch;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import com.locknar.taczammocheck.port.NetworkEvent;

public record WeaponSwitchSyncPacket(int entityId, int sequence, int type, int oldSlot, int newSlot, ItemStack outgoing, ItemStack incoming) {
    public static final int TYPE_HOLSTER_ONLY = 0;
    public static final int TYPE_DRAW_ONLY = 1;
    public static final int TYPE_HOLSTER_DRAW = 2;

    public static void encode(WeaponSwitchSyncPacket packet, RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(packet.entityId);
        buf.writeVarInt(packet.sequence);
        buf.writeVarInt(packet.type);
        buf.writeVarInt(packet.oldSlot);
        buf.writeVarInt(packet.newSlot);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf,packet.outgoing);
        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf,packet.incoming);
    }

    public static WeaponSwitchSyncPacket decode(RegistryFriendlyByteBuf buf) {
        return new WeaponSwitchSyncPacket(buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    public static void handle(WeaponSwitchSyncPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> ClientPacketDispatch.dispatch("weaponSwitch", packet));
        context.setPacketHandled(true);
    }
}
