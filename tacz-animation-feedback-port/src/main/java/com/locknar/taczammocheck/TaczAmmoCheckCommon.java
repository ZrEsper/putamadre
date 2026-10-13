package com.locknar.taczammocheck;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.LivingEntity;

public final class TaczAmmoCheckCommon {
    private static final Map<UUID, Long> AMMO_CHECK_UNTIL = new ConcurrentHashMap<UUID, Long>();

    private TaczAmmoCheckCommon() {
    }

    public static void startAmmoCheck(LivingEntity entity, long durationMs) {
        if (entity == null || durationMs <= 0L) {
            return;
        }
        AMMO_CHECK_UNTIL.put(entity.getUUID(), System.currentTimeMillis() + durationMs);
    }

    public static boolean isAmmoCheckActive(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        Long until = AMMO_CHECK_UNTIL.get(entity.getUUID());
        if (until == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now >= until) {
            AMMO_CHECK_UNTIL.remove(entity.getUUID());
            return false;
        }
        return true;
    }

    public static void clearAmmoCheck(LivingEntity entity) {
        if (entity != null) {
            AMMO_CHECK_UNTIL.remove(entity.getUUID());
        }
    }
}
