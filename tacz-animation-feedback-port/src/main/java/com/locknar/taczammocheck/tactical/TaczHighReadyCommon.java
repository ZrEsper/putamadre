package com.locknar.taczammocheck.tactical;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.LivingEntity;

public final class TaczHighReadyCommon {
    private static final Set<UUID> HIGH_READY_PLAYERS = ConcurrentHashMap.newKeySet();

    private TaczHighReadyCommon() {
    }

    public static boolean isHighReadyActive(LivingEntity entity) {
        return entity != null && HIGH_READY_PLAYERS.contains(entity.getUUID());
    }

    public static boolean setHighReadyActive(LivingEntity entity, boolean active) {
        if (entity == null) {
            return false;
        }
        if (active) {
            return HIGH_READY_PLAYERS.add(entity.getUUID());
        }
        return HIGH_READY_PLAYERS.remove(entity.getUUID());
    }

    public static void clearHighReady(LivingEntity entity) {
        if (entity != null) {
            HIGH_READY_PLAYERS.remove(entity.getUUID());
        }
    }
}
