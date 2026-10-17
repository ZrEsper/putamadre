package dev.zomboidmelee.boxing;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;
/** Client requests a target only; server owns range, aim, damage, stamina and cooldown. */
public record Heavy18(int entityId,int action) implements CustomPacketPayload {
 public static final Type<Heavy18> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("zomboid_global_melee","heavy_fist"));
 public static final StreamCodec<RegistryFriendlyByteBuf,Heavy18> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.entityId());b.writeVarInt(p.action());},b->new Heavy18(b.readVarInt(),b.readVarInt()));
 public Type<? extends CustomPacketPayload> type(){return TYPE;}
}
