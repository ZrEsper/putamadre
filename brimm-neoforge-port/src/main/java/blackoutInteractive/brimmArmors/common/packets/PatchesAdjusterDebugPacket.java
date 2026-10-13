package blackoutInteractive.brimmArmors.common.packets;

import blackoutInteractive.brimmArmors.client.screens.PatchesAdjusterScreen;
import blackoutInteractive.ema_08_.rendering.overlay.OverlayPos;
import blackoutInteractive.ema_08_.simpleNet.APacket;
import blackoutInteractive.ema_08_.simpleNet.PacketDecoder;
import blackoutInteractive.ema_08_.simpleNet.PacketEncoder;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PatchesAdjusterDebugPacket
extends APacket.AS2CPacket {
    private final String armorName;
    private final OverlayPos op;

    public PatchesAdjusterDebugPacket(String armorName, OverlayPos op) {
        this.armorName = Objects.requireNonNull(armorName);
        this.op = Objects.requireNonNull(op);
    }

    private PatchesAdjusterDebugPacket(FriendlyByteBuf buf) {
        this(buf.readUtf(), OverlayPos.values()[buf.readInt()]);
    }

    @PacketEncoder(implClassName="blackoutInteractive.brimmArmors.common.packets.PatchesAdjusterDebugPacket")
    private static void encode(PatchesAdjusterDebugPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.armorName);
        buf.writeInt(msg.op.ordinal());
    }

    @PacketDecoder(implClassName="blackoutInteractive.brimmArmors.common.packets.PatchesAdjusterDebugPacket")
    private static PatchesAdjusterDebugPacket decode(FriendlyByteBuf buf) {
        return new PatchesAdjusterDebugPacket(buf);
    }

    @Override
    protected void handleClient(IPayloadContext ctx) {
        PatchesAdjusterDebugPacket.openScreen(this.armorName, this.op);
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void openScreen(String armorName, OverlayPos op) {
        Minecraft.getInstance().setScreen((Screen)new PatchesAdjusterScreen(armorName, op));
    }
}

