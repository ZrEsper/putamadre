package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.LocknarDoorKickMod;
import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public final class MissionScoreDoorLockCompat {
    private static final String MOD_ID = "locknar_mission_score";
    private static final String API_CLASS = "com.locknar.missionscore.client.DoorLockClientApi";
    private static boolean initialized;
    private static boolean available;
    private static Method isLockedMethod;
    private static Method isRevealedMethod;
    private static Method shouldTakeOverMethod;
    private static Method requestOpenAttemptMethod;
    private static Method suppressForHeldWedgeMethod;

    private MissionScoreDoorLockCompat() {
    }

    public static boolean isCurrentTargetLocked() {
        return MissionScoreDoorLockCompat.invokeBoolean(MissionScoreDoorLockCompat.isLockedMethodOrInit());
    }

    public static boolean isCurrentTargetRevealed() {
        MissionScoreDoorLockCompat.ensureInitialized();
        return MissionScoreDoorLockCompat.invokeBoolean(isRevealedMethod);
    }

    public static boolean shouldTakeOverCurrentUpperDoor() {
        MissionScoreDoorLockCompat.ensureInitialized();
        return MissionScoreDoorLockCompat.invokeBoolean(shouldTakeOverMethod);
    }

    public static boolean requestCurrentDoorOpenAttempt() {
        MissionScoreDoorLockCompat.ensureInitialized();
        return MissionScoreDoorLockCompat.invokeBoolean(requestOpenAttemptMethod);
    }

    public static boolean shouldSuppressDoorInteractionForHeldWedge() {
        MissionScoreDoorLockCompat.ensureInitialized();
        if (MissionScoreDoorLockCompat.invokeBoolean(suppressForHeldWedgeMethod)) {
            return true;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return false;
        }
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(minecraft.player.getMainHandItem().getItem());
        return itemId != null && MOD_ID.equals(itemId.getNamespace()) && "door_wedge".equals(itemId.getPath());
    }

    private static Method isLockedMethodOrInit() {
        MissionScoreDoorLockCompat.ensureInitialized();
        return isLockedMethod;
    }

    private static void ensureInitialized() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!ModList.get().isLoaded(MOD_ID)) {
            return;
        }
        try {
            Class<?> api = Class.forName(API_CLASS, false, MissionScoreDoorLockCompat.class.getClassLoader());
            isLockedMethod = api.getMethod("isCurrentTargetLocked", new Class[0]);
            isRevealedMethod = api.getMethod("isCurrentTargetRevealed", new Class[0]);
            shouldTakeOverMethod = api.getMethod("shouldTakeOverCurrentUpperDoor", new Class[0]);
            requestOpenAttemptMethod = api.getMethod("requestCurrentDoorOpenAttempt", new Class[0]);
            try {
                suppressForHeldWedgeMethod = api.getMethod("shouldSuppressDoorInteractionForHeldWedge", new Class[0]);
            }
            catch (NoSuchMethodException ignored) {
                try {
                    suppressForHeldWedgeMethod = api.getMethod("isHoldingDoorWedge", new Class[0]);
                }
                catch (NoSuchMethodException ignoredAgain) {
                    suppressForHeldWedgeMethod = null;
                }
            }
            available = true;
        }
        catch (LinkageError | ReflectiveOperationException error) {
            available = false;
            LocknarDoorKickMod.LOGGER.warn("locknar_mission_score is installed but its optional door-lock client API is unavailable", error);
        }
    }

    private static boolean invokeBoolean(Method method) {
        if (!available || method == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(method.invoke(null, new Object[0]));
        }
        catch (LinkageError | ReflectiveOperationException error) {
            available = false;
            LocknarDoorKickMod.LOGGER.warn("Disabling locknar_mission_score door-lock soft compatibility after an API invocation failure", error);
            return false;
        }
    }
}
