package blackoutInteractive.ema_08_.simpleNet;
import java.util.*;
import java.lang.reflect.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public abstract class AbstractChannelHandler {
 static final Map<Class<?>,CustomPacketPayload.Type<APacket>> TYPES=new HashMap<>();
 protected final List<Class<? extends APacket>> packets=new ArrayList<>();
 protected AbstractChannelHandler(String id,String name,String version) {}
 protected void register(Class<? extends APacket> c) {packets.add(c);TYPES.put(c,new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("brimm", c.getSimpleName().toLowerCase(Locale.ROOT))));}
 public void registerPayloads(RegisterPayloadHandlersEvent event) {
 var registrar=event.registrar("1");
 for(var c:packets) {
 try {Method enc=Arrays.stream(c.getDeclaredMethods()).filter(m->m.isAnnotationPresent(PacketEncoder.class)).findFirst().orElseThrow(); Method dec=Arrays.stream(c.getDeclaredMethods()).filter(m->m.isAnnotationPresent(PacketDecoder.class)).findFirst().orElseThrow();enc.setAccessible(true);dec.setAccessible(true);
 StreamCodec<RegistryFriendlyByteBuf,APacket> codec=new StreamCodec<>() {
 public APacket decode(RegistryFriendlyByteBuf b) {try{return (APacket)dec.invoke(null,b);}catch(ReflectiveOperationException e){throw new IllegalArgumentException(e);}}
 public void encode(RegistryFriendlyByteBuf b,APacket p) {try{enc.invoke(null,p,b);}catch(ReflectiveOperationException e){throw new IllegalArgumentException(e);}}
 };
 if(APacket.AC2SPacket.class.isAssignableFrom(c)) registrar.playToServer(TYPES.get(c),codec,APacket::handle);
 else if(APacket.AS2CPacket.class.isAssignableFrom(c)) registrar.playToClient(TYPES.get(c),codec,APacket::handle);
 else registrar.playBidirectional(TYPES.get(c),codec,APacket::handle);
 }catch(Exception e){throw new IllegalStateException(e);}
 }
 }
 public void sendTo(APacket p,ServerPlayer player){PacketDistributor.sendToPlayer(player,p);}
 public void sendToAll(APacket p){PacketDistributor.sendToAllPlayers(p);}
 public void sendToServer(APacket p){PacketDistributor.sendToServer(p);}
}
