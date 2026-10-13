package com.locknar.taczammocheck.tactical;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.TaczHighReadyCommon;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;

public final class TaczWallObstructionCommon {
    private static final Map<UUID, Long> KICK_RAISE_BLOCK_UNTIL = new ConcurrentHashMap<UUID, Long>();

    private TaczWallObstructionCommon() {
    }

    public static void startKickRaiseBlock(LivingEntity entity, long durationMs) {
        if (entity == null || durationMs <= 0L) {
            return;
        }
        KICK_RAISE_BLOCK_UNTIL.put(entity.getUUID(), System.currentTimeMillis() + durationMs);
    }

    public static boolean isKickRaiseBlocked(LivingEntity entity) {
        if (entity == null) {
            return false;
        }
        Long until = KICK_RAISE_BLOCK_UNTIL.get(entity.getUUID());
        if (until == null) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now >= until) {
            KICK_RAISE_BLOCK_UNTIL.remove(entity.getUUID());
            return false;
        }
        return true;
    }

    public static void clearKickRaiseBlock(LivingEntity entity) {
        if (entity != null) {
            KICK_RAISE_BLOCK_UNTIL.remove(entity.getUUID());
        }
    }

    public static void onAnyForgeEvent(Event event) {
        boolean fireLike = TaczWallObstructionCommon.isLikelyTaczFireEvent(event);
        boolean reloadLike = TaczWallObstructionCommon.isLikelyTaczReloadEvent(event);
        if (!fireLike && !reloadLike) {
            return;
        }
        if (!(event instanceof net.neoforged.bus.api.ICancellableEvent)) {
            return;
        }
        LivingEntity shooter = TaczWallObstructionCommon.getShooter(event);
        ItemStack gun = TaczWallObstructionCommon.getGunStack(event, shooter);
        if (shooter == null || gun == null || gun.isEmpty()) {
            return;
        }
        TaczGunCompat.GunInfo info = TaczGunCompat.getGunInfo(gun);
        if (info == null) {
            return;
        }
        if (fireLike && TaczHighReadyCommon.isHighReadyActive(shooter)) {
            ((net.neoforged.bus.api.ICancellableEvent)event).setCanceled(true);
            return;
        }
        if (reloadLike) {
            return;
        }
        if (!((Boolean)LocknarConfig.TACZ_WALL_OBSTRUCTION_ENABLED.get()).booleanValue()) {
            return;
        }
        if (TaczWallObstructionCommon.isMuzzleObstructed(shooter, info.length()) || TaczWallObstructionCommon.isKickRaiseBlocked(shooter)) {
            ((net.neoforged.bus.api.ICancellableEvent)event).setCanceled(true);
        }
    }

    private static boolean isLikelyTaczFireEvent(Event event) {
        if (event == null) {
            return false;
        }
        String name = event.getClass().getName();
        if (name == null || !name.startsWith("com.tacz.guns.")) {
            return false;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        boolean gunRelated = lower.contains("gun") || lower.contains("shoot") || lower.contains("fire") || lower.contains("trigger");
        boolean shotLike = lower.contains("fire") || lower.contains("shoot") || lower.contains("trigger") || lower.contains("charge");
        return gunRelated && shotLike;
    }

    private static boolean isLikelyTaczReloadEvent(Event event) {
        if (event == null) {
            return false;
        }
        String name = event.getClass().getName();
        if (name == null || !name.startsWith("com.tacz.guns.")) {
            return false;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.contains("reload");
    }

    private static LivingEntity getShooter(Object event) {
        String[] methodNames;
        for (String methodName : methodNames = new String[]{"getShooter", "getPlayer", "getEntity", "getLivingEntity", "getOwner", "getAttacker"}) {
            Object value = TaczWallObstructionCommon.invokeNoArg(event, methodName);
            if (!(value instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity)value;
            return living;
        }
        return null;
    }

    private static ItemStack getGunStack(Object event, LivingEntity shooter) {
        String[] methodNames;
        for (String methodName : methodNames = new String[]{"getGunItemStack", "getGunStack", "getGun", "getWeapon", "getItemStack", "getStack", "getFirearm", "getGunItem"}) {
            ItemStack stack;
            Object value = TaczWallObstructionCommon.invokeNoArg(event, methodName);
            if (!(value instanceof ItemStack) || (stack = (ItemStack)value).isEmpty()) continue;
            return stack;
        }
        if (shooter != null) {
            ItemStack main = shooter.getMainHandItem();
            if (TaczGunCompat.getGunInfo(main) != null) {
                return main;
            }
            ItemStack off = shooter.getOffhandItem();
            if (TaczGunCompat.getGunInfo(off) != null) {
                return off;
            }
        }
        return ItemStack.EMPTY;
    }

    private static Object invokeNoArg(Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(methodName, new Class[0]);
            return method.invoke(target, new Object[0]);
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    public static boolean isMuzzleObstructed(LivingEntity entity, double gunLength) {
        double safeLength;
        Vec3 look;
        Vec3 end;
        Vec3 start;
        if (entity == null || entity.level() == null || entity.isSpectator()) {
            return false;
        }
        Level level = entity.level();
        BlockHitResult hit = level.clip(new ClipContext(start = entity.getEyePosition(1.0f), end = start.add((look = entity.getLookAngle()).scale(safeLength = Math.max(0.05, Math.min(2.5, gunLength)))), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)entity));
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }
        BlockState state = level.getBlockState(hit.getBlockPos());
        return !state.isAir() && !state.getCollisionShape((BlockGetter)level, hit.getBlockPos()).isEmpty();
    }
}
