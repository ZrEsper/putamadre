package blackoutInteractive.brimmArmors.common.packets;

import net.minecraft.server.level.ServerPlayer;
import blackoutInteractive.brimmArmors.BrimmArmors;
import blackoutInteractive.brimmArmors.common.configurations.ConfigsManager;
import blackoutInteractive.brimmArmors.server.ConfigsMatcher;
import blackoutInteractive.ema_08_.simpleNet.APacket;
import blackoutInteractive.ema_08_.simpleNet.PacketDecoder;
import blackoutInteractive.ema_08_.simpleNet.PacketEncoder;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ConfigCheckPacket
extends APacket {
    private static final String TRIGGER_HASH = "__";
    private final String hash;

    private ConfigCheckPacket(String hash) {
        this.hash = Objects.requireNonNull(hash);
    }

    public static ConfigCheckPacket trigger() {
        return new ConfigCheckPacket(TRIGGER_HASH);
    }

    @PacketEncoder(implClassName="blackoutInteractive.brimmArmors.common.packets.ConfigCheckPacket")
    private static void encode(ConfigCheckPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.hash);
    }

    @PacketDecoder(implClassName="blackoutInteractive.brimmArmors.common.packets.ConfigCheckPacket")
    private static ConfigCheckPacket decode(FriendlyByteBuf buf) {
        return new ConfigCheckPacket(buf.readUtf());
    }

    @Override
    protected void handleServer(IPayloadContext ctx) {
        ConfigsMatcher.verifyPlayer(((ServerPlayer)ctx.player()), this.hash);
    }

    @Override
    protected void handleClient(IPayloadContext ctx) {
        if (!TRIGGER_HASH.equals(this.hash)) {
            throw new UnsupportedOperationException("Wrong side?");
        }
        BrimmArmors.network.sendToServer(new ConfigCheckPacket(ConfigsManager.configHash()));
    }
}

