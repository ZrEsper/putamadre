package blackoutInteractive.brimmArmors.server;

import blackoutInteractive.brimmArmors.BrimmArmors;
import blackoutInteractive.brimmArmors.common.configurations.ConfigsManager;
import blackoutInteractive.brimmArmors.common.packets.ConfigCheckPacket;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class ConfigsMatcher {
    private static final Map<String, Long> pendingVerifications = new HashMap<String, Long>();
    private static final long TIMEOUT_MS = 30000L;

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        pendingVerifications.put(event.getEntity().getName().getString(), System.currentTimeMillis());
        BrimmArmors.network.sendTo(ConfigCheckPacket.trigger(), (ServerPlayer)event.getEntity());
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        long now = System.currentTimeMillis();
        pendingVerifications.entrySet().removeIf(entry -> {
            String name = (String)entry.getKey();
            long joinTime = (Long)entry.getValue();
            if (now - joinTime > 30000L) {
                ServerPlayer player = ConfigsMatcher.getPlayerByName(name);
                if (player != null) {
                    player.connection.disconnect((Component)Component.literal((String)(String.valueOf(ChatFormatting.RED) + "brimm: Config verification timed out.")));
                    BrimmArmors.LOGGER.warn("Config verification timed out for {}.", (Object)name);
                }
                return true;
            }
            return false;
        });
    }

    public static void verifyPlayer(ServerPlayer player, String clientHash) {
        String name = player.getName().getString();
        Long joinTime = pendingVerifications.get(name);
        if (joinTime == null) {
            BrimmArmors.LOGGER.warn("Attempted to verify {}'s past the required time.", (Object)name);
            return;
        }
        String serverHash = ConfigsManager.configHash();
        if (!serverHash.equals(clientHash)) {
            player.connection.disconnect((Component)Component.literal((String)(String.valueOf(ChatFormatting.RED) + "brimm: Config verification failed.")));
            BrimmArmors.LOGGER.warn("Config verification failed for {}. Expected hash '{}', got '{}'.", (Object)name, (Object)serverHash, (Object)clientHash);
        } else {
            BrimmArmors.LOGGER.info("Verified config for {}.", (Object)name);
        }
        pendingVerifications.remove(name);
    }

    private static ServerPlayer getPlayerByName(String name) {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(name);
    }
}

