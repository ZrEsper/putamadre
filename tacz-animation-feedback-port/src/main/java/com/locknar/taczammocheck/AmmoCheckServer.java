package com.locknar.taczammocheck;

import com.locknar.taczammocheck.LegacyMagazineRecovery;
import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.TaczAmmoCheckCommon;
import com.locknar.taczammocheck.TaczGunCompat;
import com.locknar.taczammocheck.network.AmmoCheckPosePacket;
import com.locknar.taczammocheck.network.ModNetwork;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class AmmoCheckServer {
    private static final Map<UUID, Session> SESSIONS = new HashMap<UUID, Session>();

    private AmmoCheckServer() {
    }

    public static void state(ServerPlayer p, UUID token, boolean active, long duration, String animation) {
        Session old = SESSIONS.get(p.getUUID());
        if (!active) {
            if (old != null && old.token.equals(token)) {
                AmmoCheckServer.cancel(p);
            }
            return;
        }
        if (old != null || !((Boolean)LocknarConfig.AMMO_CHECK_ENABLED.get()).booleanValue() || !p.isAlive() || p.isSpectator() || p.containerMenu != p.inventoryMenu || !TaczGunCompat.isTaczGun(p.getMainHandItem()) || TaczGunCompat.isAmmoCheckDisabled(TaczGunCompat.getGunId(p.getMainHandItem())) || !animation.equals("ammo_check") && !animation.equals("ammo_check_empty")) {
            return;
        }
        long bounded = Math.max(250L, Math.min(10000L, duration));
        long now = p.level().getGameTime();
        Session s = new Session(token, p.getMainHandItem(), p.getInventory().selected, TaczGunCompat.getGunId(p.getMainHandItem()), now, bounded, now + (bounded + 349L) / 50L);
        SESSIONS.put(p.getUUID(), s);
        TaczAmmoCheckCommon.startAmmoCheck((LivingEntity)p, bounded + 300L);
        ModNetwork.broadcast(p, s.packet(p, true));
    }

    public static void cancel(ServerPlayer p) {
        Session s = SESSIONS.remove(p.getUUID());
        if (s != null) {
            ModNetwork.broadcast(p, s.packet(p, false));
        }
        TaczAmmoCheckCommon.clearAmmoCheck((LivingEntity)p);
    }

    @SubscribeEvent
    public static void tick(PlayerTickEvent.Pre event) {
        Player player;
        if (false || !((player = event.getEntity()) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer p = (ServerPlayer)player;
        Session s = SESSIONS.get(p.getUUID());
        if (!(s == null || s.bound(p) && p.level().getGameTime() <= s.expires && ((Boolean)LocknarConfig.AMMO_CHECK_ENABLED.get()).booleanValue())) {
            AmmoCheckServer.cancel(p);
        }
        for (int i = 0; i < p.getInventory().getContainerSize(); ++i) {
            LegacyMagazineRecovery.recover(p.getInventory().getItem(i));
        }
    }

    @SubscribeEvent
    public static void tracking(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer target;
            Session s;
            ServerPlayer viewer = (ServerPlayer)player;
            player = event.getTarget() instanceof Player ? (Player)event.getTarget() : null;
            if (player instanceof ServerPlayer && (s = SESSIONS.get((target = (ServerPlayer)player).getUUID())) != null && s.bound(target)) {
                ModNetwork.send(viewer, s.packet(target, true));
            }
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent e) {
        Player player = e.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)player;
            AmmoCheckServer.cancel(p);
        }
    }

    @SubscribeEvent
    public static void death(LivingDeathEvent e) {
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer p = (ServerPlayer)livingEntity;
            AmmoCheckServer.cancel(p);
        }
    }

    @SubscribeEvent
    public static void join(EntityJoinLevelEvent e) {
        Entity entity;
        if (!e.getLevel().isClientSide && (entity = e.getEntity()) instanceof ItemEntity) {
            ItemEntity item = (ItemEntity)entity;
            LegacyMagazineRecovery.recover(item.getItem());
        }
    }

    private record Session(UUID token, ItemStack gun, int slot, String gunId, long started, long duration, long expires) {
        boolean bound(ServerPlayer p) {
            return p.isAlive() && !p.isSpectator() && p.getMainHandItem() == this.gun && p.getInventory().selected == this.slot && p.containerMenu == p.inventoryMenu && Objects.equals(this.gunId, TaczGunCompat.getGunId(this.gun));
        }

        AmmoCheckPosePacket packet(ServerPlayer p, boolean active) {
            return new AmmoCheckPosePacket(p.getId(), p.getUUID(), this.token, active, this.started, this.duration, this.gunId);
        }
    }
}
