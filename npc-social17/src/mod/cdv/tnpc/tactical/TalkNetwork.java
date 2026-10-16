package mod.cdv.tnpc.tactical;
import java.lang.reflect.*;
public final class TalkNetwork {
 public static void register(Object event){Object registrar=Social.c(event,"registrar","20");Class<?> handler=Social.t("net.neoforged.neoforge.network.handling.IPayloadHandler");Object proxy=Proxy.newProxyInstance(handler.getClassLoader(),new Class<?>[]{handler},(p,m,args)->{if(m.getName().equals("handle")){TalkPayload value=(TalkPayload)args[0];Social.c(args[1],"enqueueWork",(Runnable)()->{if(value.text().startsWith("M20\n"))Social.c(Social.t("dev.zomboid.firstaid.BodyTreatmentScreen20"),"receiveNpc",value.text().substring(4));else Social.c(Social.t("mod.cdv.tnpc.social.client.ConversationScreen"),"open",value.text());});}return null;});Social.c(registrar,"playToClient",TalkPayload.TYPE,TalkPayload.CODEC,proxy);}
 public static void send(Object player,String data){Object empty=Array.newInstance(Social.t("net.minecraft.network.protocol.common.custom.CustomPacketPayload"),0);Social.c(Social.t("net.neoforged.neoforge.network.PacketDistributor"),"sendToPlayer",player,new TalkPayload(data),empty);}
}
