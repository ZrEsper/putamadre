package com.locknar.taczammocheck.tactical;

import com.locknar.taczammocheck.tactical.network.ModNetwork;
import com.locknar.taczammocheck.tactical.server.MirrorGunHandler;
import com.locknar.taczammocheck.tactical.server.ServerKickHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;

public final class DoorInteractionGlobalState {
    private static final String DATA_NAME = "locknar_tacz_ammo_check_global_state";
    private static final String ENABLED_KEY = "doorInteractionEnabled";
    private static volatile boolean clientEnabled = true;

    private DoorInteractionGlobalState() {
    }

    public static boolean isClientEnabled() {
        return clientEnabled;
    }

    public static void setClientEnabled(boolean enabled) {
        clientEnabled = enabled;
    }

    public static void resetClientState() {
        clientEnabled = true;
    }

    public static boolean isServerEnabled(MinecraftServer server) {
        return server == null || DoorInteractionGlobalState.data((MinecraftServer)server).enabled;
    }

    public static void setServerEnabled(MinecraftServer server, boolean enabled) {
        if (server == null) {
            return;
        }
        GlobalData data = DoorInteractionGlobalState.data(server);
        if (data.enabled == enabled) {
            ModNetwork.sendDoorInteractionStateToAll(server, enabled);
            return;
        }
        data.enabled = enabled;
        data.setDirty();
        if (!enabled) {
            ServerKickHandler.clearAllPending();
            MirrorGunHandler.clearAllServerState();
        }
        ModNetwork.sendDoorInteractionStateToAll(server, enabled);
    }

    public static void toggle(MinecraftServer server) {
        DoorInteractionGlobalState.setServerEnabled(server, !DoorInteractionGlobalState.isServerEnabled(server));
    }

    public static void syncTo(ServerPlayer player) {
        if (player == null || player.getServer() == null) {
            return;
        }
        ModNetwork.sendDoorInteractionState(player, DoorInteractionGlobalState.isServerEnabled(player.getServer()));
    }

    private static GlobalData data(MinecraftServer server) {
        return (GlobalData)server.overworld().getDataStorage().computeIfAbsent(new SavedData.Factory<>(GlobalData::new, (tag, registries)->GlobalData.load(tag)), DATA_NAME);
    }

    private static final class GlobalData
    extends SavedData {
        private boolean enabled = true;

        private GlobalData() {
        }

        private static GlobalData load(CompoundTag tag) {
            GlobalData data = new GlobalData();
            if (tag.contains(DoorInteractionGlobalState.ENABLED_KEY)) {
                data.enabled = tag.getBoolean(DoorInteractionGlobalState.ENABLED_KEY);
            }
            return data;
        }

        public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
            tag.putBoolean(DoorInteractionGlobalState.ENABLED_KEY, this.enabled);
            return tag;
        }
    }
}
