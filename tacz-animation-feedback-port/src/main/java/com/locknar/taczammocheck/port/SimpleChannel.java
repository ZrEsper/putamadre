package com.locknar.taczammocheck.port;
import java.util.*;
import java.util.function.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
public final class SimpleChannel {
 final ResourceLocation name;
 final Map<Class<?>,Builder<?>> messages=new HashMap<>();
 static final List<SimpleChannel> CHANNELS=new ArrayList<>();
 public SimpleChannel(ResourceLocation name){this.name=name;CHANNELS.add(this);}
 public <T> Builder<T> messageBuilder(Class<T> cls,int id,NetworkDirection direction){return new Builder<>(cls,id,direction);}
 public static int registeredMessages(){return CHANNELS.stream().mapToInt(c->c.messages.size()).sum();}
 public static Object codecRoundTrip(Object message,net.minecraft.core.RegistryAccess registries){
  for(var channel:CHANNELS){var b=(Builder<Object>)channel.messages.get(message.getClass());if(b==null)continue;
   var buf=new RegistryFriendlyByteBuf(io.netty.buffer.Unpooled.buffer(),registries);try{b.encoder.accept(message,buf);return b.decoder.apply(buf);}finally{buf.release();}
  }throw new IllegalArgumentException("Unregistered payload");
 }
 public static void registerAll(RegisterPayloadHandlersEvent event){for(var channel:CHANNELS)for(var b:channel.messages.values())b.register(event);}
 public final class Builder<T> {
  final Class<T> cls;final CustomPacketPayload.Type<Envelope> type;final NetworkDirection direction;
  BiConsumer<T,RegistryFriendlyByteBuf> encoder;Function<RegistryFriendlyByteBuf,T> decoder;BiConsumer<T,Supplier<NetworkEvent.Context>> handler;
  Builder(Class<T> cls,int id,NetworkDirection d){this.cls=cls;direction=d;type=new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(name.getNamespace(),name.getPath()+"/"+id));}
  public Builder<T> encoder(BiConsumer<T,RegistryFriendlyByteBuf> f){encoder=f;return this;}
  public Builder<T> decoder(Function<RegistryFriendlyByteBuf,T> f){decoder=f;return this;}
  public Builder<T> consumerMainThread(BiConsumer<T,Supplier<NetworkEvent.Context>> f){handler=f;return this;}
  public void add(){messages.put(cls,this);}
  void register(RegisterPayloadHandlersEvent event){
   var codec=new StreamCodec<RegistryFriendlyByteBuf,Envelope>(){
    public Envelope decode(RegistryFriendlyByteBuf b){return new Envelope(type,decoder.apply(b));}
    public void encode(RegistryFriendlyByteBuf b,Envelope p){encoder.accept(cls.cast(p.data),b);}
   };var registrar=event.registrar("neoforge-port-1");
   net.neoforged.neoforge.network.handling.IPayloadHandler<Envelope> receiver=(p,c)->handler.accept(cls.cast(p.data),()->new NetworkEvent.Context(c));
   if(direction==NetworkDirection.PLAY_TO_CLIENT)registrar.playToClient(type,codec,receiver);else registrar.playToServer(type,codec,receiver);
  }
 }
 public record Envelope(CustomPacketPayload.Type<Envelope> type,Object data) implements CustomPacketPayload {}
 Envelope wrap(Object data){var b=messages.get(data.getClass());if(b==null)throw new IllegalArgumentException("Unregistered packet "+data.getClass());return new Envelope(b.type,data);}
 public void sendToServer(Object message){var b=messages.get(message.getClass());if(b.direction!=NetworkDirection.PLAY_TO_SERVER)throw new IllegalArgumentException("Incorrect packet direction");net.neoforged.neoforge.network.PacketDistributor.sendToServer(wrap(message));}
 public void send(PacketDistributor.Destination dest,Object message){var b=messages.get(message.getClass());if(b.direction!=NetworkDirection.PLAY_TO_CLIENT)throw new IllegalArgumentException("Incorrect packet direction");var p=wrap(message);switch(dest.kind()){
 case 0->net.neoforged.neoforge.network.PacketDistributor.sendToPlayer((ServerPlayer)dest.target(),p);
 case 1->net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingEntityAndSelf((Entity)dest.target(),p);
 case 2->net.neoforged.neoforge.network.PacketDistributor.sendToPlayersTrackingEntity((Entity)dest.target(),p);
 case 3->net.neoforged.neoforge.network.PacketDistributor.sendToAllPlayers(p);
 default->throw new IllegalArgumentException("Destination");}}
}
