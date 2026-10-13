package com.locknar.taczammocheck;

import com.locknar.taczammocheck.TaczAmmoCheckCommon;
import com.locknar.taczammocheck.TaczGunCompat;
import com.locknar.taczammocheck.prone.ProneServer;
import java.lang.reflect.Method;
import java.util.Locale;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

public final class TaczAmmoCheckEventHooks {
    private TaczAmmoCheckEventHooks() {
    }

    public static void onAnyForgeEvent(Event event) {
        ServerPlayer player;
        if (event == null || !(event instanceof net.neoforged.bus.api.ICancellableEvent)) {
            return;
        }
        if (!TaczAmmoCheckEventHooks.isLikelyTaczFireEvent(event) && !TaczAmmoCheckEventHooks.isLikelyTaczReloadEvent(event)) {
            return;
        }
        LivingEntity shooter = TaczAmmoCheckEventHooks.getShooter(event);
        ItemStack gun = TaczAmmoCheckEventHooks.getGunStack(event, shooter);
        if (shooter == null || gun == null || gun.isEmpty()) {
            return;
        }
        if (TaczGunCompat.getGunInfo(gun) == null) {
            return;
        }
        if (TaczAmmoCheckCommon.isAmmoCheckActive(shooter) || shooter instanceof ServerPlayer && ProneServer.transitioning((Player)(player = (ServerPlayer)shooter))) {
            ((net.neoforged.bus.api.ICancellableEvent)event).setCanceled(true);
        }
    }

    private static boolean isLikelyTaczFireEvent(Event event) {
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
        String name = event.getClass().getName();
        if (name == null || !name.startsWith("com.tacz.guns.")) {
            return false;
        }
        return name.toLowerCase(Locale.ROOT).contains("reload");
    }

    private static LivingEntity getShooter(Object event) {
        String[] methodNames;
        for (String methodName : methodNames = new String[]{"getShooter", "getPlayer", "getEntity", "getLivingEntity", "getOwner", "getAttacker"}) {
            Object value = TaczAmmoCheckEventHooks.invokeNoArg(event, methodName);
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
            Object value = TaczAmmoCheckEventHooks.invokeNoArg(event, methodName);
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
        if (target == null || methodName == null) {
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
}
