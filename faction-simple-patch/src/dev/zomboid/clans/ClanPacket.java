package dev.zomboid.clans;
import net.minecraft.network.FriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;import net.neoforged.neoforge.network.handling.IPayloadContext;
public record ClanPacket(int action,String a,String b,int value) implements CustomPacketPayload {
 public static final int STATUS=0,DECLARE=1,PROFILE=2,SURRENDER=3,CLAIM=4,SYNC=5;
 public static final Type<ClanPacket> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("factionfriction","zomboid_clans_v1"));public static final StreamCodec<FriendlyByteBuf,ClanPacket> CODEC=StreamCodec.of((buf,p)->{buf.writeVarInt(p.action);buf.writeUtf(p.a,32767);buf.writeUtf(p.b,256);buf.writeVarInt(p.value);},buf->new ClanPacket(buf.readVarInt(),buf.readUtf(32767),buf.readUtf(256),buf.readVarInt()));
 public Type<ClanPacket> type(){return TYPE;}
 public static void register(RegisterPayloadHandlersEvent e){e.registrar("zomboid-clans-2").playBidirectional(TYPE,CODEC,ClanPacket::handle);}
 public static void handle(ClanPacket p,IPayloadContext c){if(R.type("net.minecraft.server.level.ServerPlayer").isInstance(c.player())){if(p.action<STATUS||p.action==SYNC||p.action>9||p.a.length()>128||p.b.length()>256)return;c.enqueueWork(()->Clans.action(c.player(),p));}else if(p.action==SYNC)c.enqueueWork(()->R.call(R.type("dev.zomboid.clans.ClientClans"),"accept",p.a));}
 public static void send(int action,String a,String b,int value){R.call(R.type("me.danialisntcool.factionfriction.network.FFNetwork"),"sendToServer",new ClanPacket(action,a,b,value));}
}
