package com.locknar.taczammocheck.tactical.server;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import com.locknar.taczammocheck.tactical.network.WeaponSwitchSyncPacket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class WeaponSwitchServerTracker {
    private static final Map<UUID, Snapshot> SNAPSHOTS = new ConcurrentHashMap<UUID, Snapshot>();
    private static final AtomicInteger GLOBAL_SEQUENCE = new AtomicInteger();

    private WeaponSwitchServerTracker() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        boolean visibleFirstSlotChanged;
        Player player;
        if (false || event.getEntity().level().isClientSide() || !((player = event.getEntity()) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        Snapshot previous = SNAPSHOTS.get(player2.getUUID());
        Snapshot current = Snapshot.capture(player2, previous);
        SNAPSHOTS.put(player2.getUUID(), current);
        if (previous == null) {
            ModNetwork.sendBackGunSync(player2);
            return;
        }
        boolean slotChanged = previous.selectedSlot != current.selectedSlot;
        boolean firstSlotChanged = !WeaponSwitchServerTracker.sameStack(previous.firstSlot, current.firstSlot);
        boolean bl = visibleFirstSlotChanged = firstSlotChanged && current.selectedSlot != 0;
        if (slotChanged && ((Boolean)LocknarConfig.THIRD_PERSON_WEAPON_SWITCH_ENABLED.get()).booleanValue()) {
            WeaponSwitchServerTracker.sendSwitchIfNeeded(player2, previous, current);
        }
        if (slotChanged || visibleFirstSlotChanged || player2.tickCount % 200 == 0) {
            ModNetwork.sendBackGunSync(player2);
        }
    }

    private static void sendSwitchIfNeeded(ServerPlayer player, Snapshot previous, Snapshot current) {
        boolean oldGun = TaczGunCompat.isTaczGun(previous.mainHand);
        boolean newGun = TaczGunCompat.isTaczGun(current.mainHand);
        if (!oldGun && !newGun) {
            return;
        }
        int sequence = GLOBAL_SEQUENCE.updateAndGet(value -> value == Integer.MAX_VALUE ? 1 : value + 1);
        if (oldGun && !newGun) {
            ModNetwork.sendWeaponSwitch(player, new WeaponSwitchSyncPacket(player.getId(), sequence, 0, previous.selectedSlot, current.selectedSlot, previous.mainHand, current.mainHand));
            return;
        }
        int type = oldGun ? 2 : 1;
        ModNetwork.sendWeaponSwitch(player, new WeaponSwitchSyncPacket(player.getId(), sequence, type, previous.selectedSlot, current.selectedSlot, previous.mainHand, current.mainHand));
    }

    public static void onStartTracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer viewer = (ServerPlayer)player;
            player = event.getTarget() instanceof Player ? (Player)event.getTarget() : null;
            if (player instanceof ServerPlayer) {
                ServerPlayer subject = (ServerPlayer)player;
                ModNetwork.sendBackGunSyncTo(viewer, subject);
            }
        }
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        WeaponSwitchServerTracker.clear(event.getEntity().getUUID());
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        WeaponSwitchServerTracker.clear(event.getEntity().getUUID());
    }

    public static void onPlayerClone(PlayerEvent.Clone event) {
        WeaponSwitchServerTracker.clear(event.getOriginal().getUUID());
        WeaponSwitchServerTracker.clear(event.getEntity().getUUID());
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)livingEntity;
            WeaponSwitchServerTracker.clear(player.getUUID());
        }
    }

    private static void clear(UUID uuid) {
        SNAPSHOTS.remove(uuid);
    }

    private static boolean sameStack(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return a == b;
        }
        if (a.isEmpty() || b.isEmpty()) {
            return a.isEmpty() && b.isEmpty();
        }
        return a.getCount() == b.getCount() && ItemStack.isSameItemSameComponents((ItemStack)a, (ItemStack)b);
    }

    private record Snapshot(int selectedSlot, ItemStack firstSlot, ItemStack mainHand) {
        private static Snapshot capture(ServerPlayer player, Snapshot previous) {
            ItemStack firstSlot;
            ItemStack liveFirstSlot = player.getInventory().getItem(0);
            ItemStack liveMainHand = player.getMainHandItem();
            int selectedSlot = player.getInventory().selected;
            ItemStack itemStack = firstSlot = previous != null && WeaponSwitchServerTracker.sameStack(previous.firstSlot, liveFirstSlot) ? previous.firstSlot : liveFirstSlot.copy();
            ItemStack mainHand = selectedSlot == 0 ? firstSlot : (previous != null && WeaponSwitchServerTracker.sameStack(previous.mainHand, liveMainHand) ? previous.mainHand : liveMainHand.copy());
            return new Snapshot(selectedSlot, firstSlot, mainHand);
        }
    }
}
