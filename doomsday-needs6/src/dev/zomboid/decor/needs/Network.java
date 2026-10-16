package dev.zomboid.decor.needs;
import dev.zomboid.decor.R;import java.lang.reflect.*;
public final class Network{
 public static void register(Object event){Object registrar=R.call(event,"registrar","6");Class<?> handler=R.type("net.neoforged.neoforge.network.handling.IPayloadHandler");Object proxy=Proxy.newProxyInstance(handler.getClassLoader(),new Class<?>[]{handler},(p,m,args)->{if(m.getName().equals("handle")){NeedsPayload value=(NeedsPayload)args[0];R.call(args[1],"enqueueWork",(Runnable)()->R.call(R.type("dev.zomboid.decor.needs.client.NeedsClient"),"state",value.kind(),value.ticks()));}return null;});R.call(registrar,"playToClient",NeedsPayload.TYPE,NeedsPayload.CODEC,proxy);}
 public static void send(Object player,int kind,int ticks){Object empty=Array.newInstance(R.type("net.minecraft.network.protocol.common.custom.CustomPacketPayload"),0);R.call(R.type("net.neoforged.neoforge.network.PacketDistributor"),"sendToPlayer",player,new NeedsPayload(kind,ticks),empty);}
}
