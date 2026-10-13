package com.locknar.taczammocheck.port;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
public final class NetworkEvent {
 public static class Context {
  final IPayloadContext context;
  public Context(IPayloadContext c){context=c;}
  public ServerPlayer getSender(){return context.player() instanceof ServerPlayer p?p:null;}
  public void enqueueWork(Runnable r){context.enqueueWork(r);}
  public void setPacketHandled(boolean handled){}
 }
}
