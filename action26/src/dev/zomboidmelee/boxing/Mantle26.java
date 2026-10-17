package dev.zomboidmelee.boxing;
import net.minecraft.network.RegistryFriendlyByteBuf;import net.minecraft.network.codec.StreamCodec;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;import java.lang.reflect.*;
/** Server-approved landing point. The client must not rediscover a ledge after network latency. */
public record Mantle26(int entityId,double x,double y,double z) implements CustomPacketPayload {
 public static final Type<Mantle26> TYPE=new Type<>(ResourceLocation.fromNamespaceAndPath("zomboid_global_melee","mantle_target26"));
 public static final StreamCodec<RegistryFriendlyByteBuf,Mantle26> CODEC=StreamCodec.of((b,p)->{b.writeVarInt(p.entityId);write(b,p.x);write(b,p.y);write(b,p.z);},b->new Mantle26(b.readVarInt(),read(b),read(b),read(b)));
 static void write(RegistryFriendlyByteBuf b,double value){long n=Double.doubleToRawLongBits(value);b.writeVarInt((int)(n>>>32));b.writeVarInt((int)n);}static double read(RegistryFriendlyByteBuf b){long hi=b.readVarInt(),lo=b.readVarInt()&0xffffffffL;return Double.longBitsToDouble((hi<<32)|lo);}
 public Type<? extends CustomPacketPayload> type(){return TYPE;}
 public static void register(Object registrar){Class<?> handler=R.type("net.neoforged.neoforge.network.handling.IPayloadHandler");Object proxy=Proxy.newProxyInstance(handler.getClassLoader(),new Class<?>[]{handler},(p,m,a)->{switch(m.getName()){case "handle":Mantle26 packet=(Mantle26)a[0];Object ctx=a[1];R.call(ctx,"enqueueWork",(Runnable)()->receive(packet));return null;case "toString":return "Mantle26Handler";case "hashCode":return System.identityHashCode(p);case "equals":return p==a[0];default:return null;}});R.call(registrar,"playToClient",TYPE,CODEC,proxy);}
 public static void receive(Mantle26 packet){Object mc=BoxingClient16.mc(),level=R.get(mc,"level");if(level==null||!Double.isFinite(packet.x)||!Double.isFinite(packet.y)||!Double.isFinite(packet.z))return;Object p=R.call(level,"getEntity",packet.entityId);if(p!=null)Parkour20.target(p,packet.x,packet.y,packet.z);}
 public static void send(Object p,double x,double y,double z){if(!Move25.server(p))return;R.call(R.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToPlayersTrackingEntityAndSelf",p,new Mantle26(((Number)R.call(p,"getId")).intValue(),x,y,z),Array.newInstance(CustomPacketPayload.class,0));}
}
