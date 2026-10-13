package blackoutInteractive.ema_08_.simpleNet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.neoforge.network.handling.IPayloadContext;
public abstract class APacket implements CustomPacketPayload {
 public Type<? extends CustomPacketPayload> type() { return AbstractChannelHandler.TYPES.get(getClass()); }
 public final void handle(IPayloadContext ctx) { if(ctx.flow()==PacketFlow.SERVERBOUND) handleServer(ctx); else handleClient(ctx); }
 protected abstract void handleClient(IPayloadContext ctx);
 protected abstract void handleServer(IPayloadContext ctx);
 public static abstract class AS2CPacket extends APacket { protected final void handleServer(IPayloadContext ctx) { throw new IllegalStateException("Wrong packet direction"); } }
 public static abstract class AC2SPacket extends APacket { protected final void handleClient(IPayloadContext ctx) { throw new IllegalStateException("Wrong packet direction"); } }
}
