package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.locknar.taczammocheck.prone.ProneClient;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class TaczHighReadyClient {
    public static final KeyMapping HIGH_READY_KEY = new KeyMapping("key.locknar_tacz_ammo_check.high_ready", InputConstants.Type.KEYSYM, 342, "key.categories.locknar_doorkick");
    private static final Map<Integer, Boolean> REMOTE_HIGH_READY = new ConcurrentHashMap<Integer, Boolean>();
    private static boolean lastRequestedActive;
    private static float currentProgress;
    private static float previousProgress;

    private TaczHighReadyClient() {
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(HIGH_READY_KEY);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        boolean activeNow;
        if (false) {
            return;
        }
        previousProgress = currentProgress;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null) {
            TaczHighReadyClient.clearClientState();
            return;
        }
        if (mc.player.tickCount % 40 == 0) {
            TaczHighReadyClient.cleanupRemoteHighReady(mc);
        }
        currentProgress = (activeNow = TaczHighReadyClient.shouldRequestHighReady()) ? Math.min(1.0f, currentProgress + 0.3f) : Math.max(0.0f, currentProgress - 0.35f);
        if (activeNow != lastRequestedActive) {
            lastRequestedActive = activeNow;
            ModNetwork.sendHighReadyRequest(activeNow);
        }
    }

    private static boolean shouldRequestHighReady() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null || mc.gameMode == null || mc.screen != null) {
            return false;
        }
        if (!HIGH_READY_KEY.isDown() || ProneClient.active((AbstractClientPlayer)mc.player) || AmmoCheckClient.isActive()) {
            return false;
        }
        GameType mode = mc.gameMode.getPlayerMode();
        if (mode != GameType.SURVIVAL && mode != GameType.ADVENTURE && mode != GameType.CREATIVE) {
            return false;
        }
        if (mc.player.isSpectator()) {
            return false;
        }
        if (mc.player.isShiftKeyDown() || mc.player.isSprinting()) {
            return false;
        }
        return TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) != null;
    }

    public static float getLocalHighReadyProgress(float partialTick) {
        return Mth.clamp((float)Mth.lerp((float)partialTick, (float)previousProgress, (float)currentProgress), (float)0.0f, (float)1.0f);
    }

    public static boolean isLocalHighReadyActive() {
        return currentProgress > 0.05f || lastRequestedActive;
    }

    public static boolean isLocalHighReadyForShooting() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return false;
        }
        return TaczHighReadyClient.isLocalHighReadyActive() && TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) != null;
    }

    public static void setEntityHighReady(int entityId, boolean active) {
        if (entityId < 0) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.player != null && mc.player.getId() == entityId) {
            if (!active) {
                lastRequestedActive = false;
            }
            return;
        }
        if (active) {
            REMOTE_HIGH_READY.put(entityId, Boolean.TRUE);
        } else {
            REMOTE_HIGH_READY.remove(entityId);
        }
    }

    public static float getHighReadyProgressForEntity(int entityId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.player != null && mc.player.getId() == entityId) {
            return currentProgress;
        }
        return REMOTE_HIGH_READY.containsKey(entityId) ? 1.0f : 0.0f;
    }

    public static boolean isHighReadyActiveForEntity(int entityId) {
        return TaczHighReadyClient.getHighReadyProgressForEntity(entityId) > 0.05f;
    }

    private static void cleanupRemoteHighReady(Minecraft mc) {
        if (mc == null || mc.level == null || REMOTE_HIGH_READY.isEmpty()) {
            return;
        }
        REMOTE_HIGH_READY.keySet().removeIf(entityId -> mc.level.getEntity(entityId.intValue()) == null);
    }

    public static void clearClientState() {
        REMOTE_HIGH_READY.clear();
        lastRequestedActive = false;
        currentProgress = 0.0f;
        previousProgress = 0.0f;
    }
}
