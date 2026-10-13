package dev.zomboid.taczfeedback;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
public record HitDirection(double x,double y,double z) implements CustomPacketPayload {
 public static final Type<HitDirection> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("tacz","incoming_direction"));
 public static final StreamCodec<RegistryFriendlyByteBuf,HitDirection> CODEC=new StreamCodec<>() {
  public HitDirection decode(RegistryFriendlyByteBuf b){return new HitDirection(b.readDouble(),b.readDouble(),b.readDouble());}
  public void encode(RegistryFriendlyByteBuf b,HitDirection p){b.writeDouble(p.x);b.writeDouble(p.y);b.writeDouble(p.z);}
 };
 public Type<HitDirection> type(){return TYPE;}
}
