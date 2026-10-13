package com.locknar.taczammocheck.tactical.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public final class TaczKickRaiseClient {
    public static final long DEFAULT_RAISE_MS = 1000L;
    private static final Map<Integer, RaiseState> RAISE_STATES = new HashMap<Integer, RaiseState>();

    private TaczKickRaiseClient() {
    }

    public static void triggerKickRaiseForEntity(int entityId) {
        TaczKickRaiseClient.triggerKickRaiseForEntity(entityId, 1000L);
    }

    public static void triggerKickRaiseForEntity(int entityId, long durationMs) {
        if (entityId < 0) {
            return;
        }
        long safeDuration = Math.max(50L, durationMs);
        RAISE_STATES.put(entityId, new RaiseState(System.currentTimeMillis(), safeDuration));
    }

    public static boolean isKickRaiseActive() {
        return TaczKickRaiseClient.getKickRaiseProgress() > 0.0f;
    }

    public static boolean isKickRaiseActiveForEntity(int entityId) {
        return TaczKickRaiseClient.getKickRaiseProgress(entityId) > 0.0f;
    }

    public static float getKickRaiseProgress() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.player.isSpectator()) {
            return 0.0f;
        }
        return TaczKickRaiseClient.getKickRaiseProgress(mc.player.getId());
    }

    public static float getKickRaiseProgress(int entityId) {
        RaiseState state = RAISE_STATES.get(entityId);
        if (state == null) {
            return 0.0f;
        }
        long elapsed = System.currentTimeMillis() - state.startMs();
        long durationMs = Math.max(50L, state.durationMs());
        if (elapsed < 0L || elapsed >= durationMs) {
            RAISE_STATES.remove(entityId);
            return 0.0f;
        }
        float t = (float)elapsed / (float)durationMs;
        float curve = t < 0.14f ? t / 0.14f : (t < 0.78f ? 1.0f : 1.0f - (t - 0.78f) / 0.22f);
        curve = Mth.clamp((float)curve, (float)0.0f, (float)1.0f);
        return curve * curve * (3.0f - 2.0f * curve);
    }

    public static void clearAll() {
        RAISE_STATES.clear();
    }

    public static void cleanup() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Integer, RaiseState>> iterator = RAISE_STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, RaiseState> entry = iterator.next();
            RaiseState state = entry.getValue();
            if (now - state.startMs() <= state.durationMs() + 1000L) continue;
            iterator.remove();
        }
    }

    private record RaiseState(long startMs, long durationMs) {
    }
}
