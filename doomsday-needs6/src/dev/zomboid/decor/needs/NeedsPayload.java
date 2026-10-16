package dev.zomboid.decor.needs;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;
public record NeedsPayload(int kind,int ticks)implements CustomPacketPayload{
 public static final Type<NeedsPayload> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("doomsday_decoration","needs_state"));
 public static final StreamCodec<RegistryFriendlyByteBuf,NeedsPayload> CODEC=new StreamCodec<>(){public NeedsPayload decode(RegistryFriendlyByteBuf b){return new NeedsPayload(b.readInt(),b.readInt());}public void encode(RegistryFriendlyByteBuf b,NeedsPayload v){dev.zomboid.decor.R.call(b,"writeInt",v.kind());dev.zomboid.decor.R.call(b,"writeInt",v.ticks());}};
 public Type<NeedsPayload> type(){return TYPE;}
}
