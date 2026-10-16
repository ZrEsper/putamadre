package mod.cdv.tnpc.tactical;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;
public record TalkPayload(String text) implements CustomPacketPayload{
 public static final Type<TalkPayload> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("tacznpcs","conversation20"));
 public static final StreamCodec<RegistryFriendlyByteBuf,TalkPayload> CODEC=new StreamCodec<>(){public TalkPayload decode(RegistryFriendlyByteBuf b){return new TalkPayload(b.readUtf(8192));}public void encode(RegistryFriendlyByteBuf b,TalkPayload v){SocialR.call(b,"writeUtf",v.text,8192);}};public Type<TalkPayload> type(){return TYPE;}
}
