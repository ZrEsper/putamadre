package com.locknar.taczammocheck.tactical.server;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.TaczAmmoCheckCommon;
import com.locknar.taczammocheck.prone.ProneServer;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.TaczHighReadyCommon;
import com.locknar.taczammocheck.tactical.TaczWallObstructionCommon;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import com.locknar.taczammocheck.tactical.server.ServerKickHandler;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public final class TaczHighReadyServer {
    private static final net.minecraft.resources.ResourceLocation MOVEMENT_SPEED_MODIFIER_UUID = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "movement_speed_modifier_uuid");
    private static final String MOVEMENT_SPEED_MODIFIER_NAME = "locknar_doorkick_movement_speed_modifier";

    private TaczHighReadyServer() {
    }

    public static void handleHighReadyRequest(ServerPlayer player, boolean requestedActive) {
        if (player == null) {
            return;
        }
        boolean active = requestedActive && TaczHighReadyServer.canUseHighReady(player);
        TaczHighReadyServer.setHighReady(player, active, true);
    }

    public static boolean isHighReadyActive(ServerPlayer player) {
        return TaczHighReadyCommon.isHighReadyActive((LivingEntity)player);
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            TaczHighReadyServer.clearServerState(player2, false);
        }
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)livingEntity;
            TaczHighReadyServer.clearServerState(player, true);
        }
    }

    public static void clearServerState(ServerPlayer player, boolean sync) {
        if (player == null) {
            return;
        }
        boolean changed = TaczHighReadyCommon.setHighReadyActive((LivingEntity)player, false);
        TaczHighReadyServer.removeMovementSpeedModifier(player);
        TaczWallObstructionCommon.clearKickRaiseBlock((LivingEntity)player);
        ServerKickHandler.clearPendingForPlayer(player);
        if (sync && changed) {
            ModNetwork.sendHighReadySync(player, false);
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (false || event.getEntity().level().isClientSide()) {
            return;
        }
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        boolean highReadyActive = TaczHighReadyCommon.isHighReadyActive((LivingEntity)player2);
        if (highReadyActive && !TaczHighReadyServer.canUseHighReady(player2)) {
            highReadyActive = false;
            TaczHighReadyServer.setHighReady(player2, false, true);
        }
        if (highReadyActive && player2.tickCount % 20 == 0) {
            ModNetwork.sendHighReadySync(player2, true);
        }
        TaczHighReadyServer.applyMovementSpeed(player2, highReadyActive);
    }

    private static boolean canUseHighReady(ServerPlayer player) {
        if (player == null || player.isSpectator() || ProneServer.active((Player)player) || TaczAmmoCheckCommon.isAmmoCheckActive((LivingEntity)player)) {
            return false;
        }
        if (!TaczHighReadyServer.isSurvivalAdventureOrCreative(player)) {
            return false;
        }
        if (player.isShiftKeyDown() || player.isSprinting()) {
            return false;
        }
        return TaczGunCompat.getGunInfo(player.getMainHandItem()) != null;
    }

    private static boolean isSurvivalAdventureOrCreative(ServerPlayer player) {
        GameType mode = player.gameMode.getGameModeForPlayer();
        return mode == GameType.SURVIVAL || mode == GameType.ADVENTURE || mode == GameType.CREATIVE;
    }

    private static boolean isSurvivalOrAdventure(ServerPlayer player) {
        GameType mode = player.gameMode.getGameModeForPlayer();
        return mode == GameType.SURVIVAL || mode == GameType.ADVENTURE;
    }

    private static void setHighReady(ServerPlayer player, boolean active, boolean sync) {
        boolean changed = TaczHighReadyCommon.setHighReadyActive((LivingEntity)player, active);
        if (changed && sync) {
            ModNetwork.sendHighReadySync(player, active);
        }
    }

    private static void applyMovementSpeed(ServerPlayer player, boolean highReadyActive) {
        double highReadyMultiplier;
        AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute == null) {
            return;
        }
        attribute.removeModifier(MOVEMENT_SPEED_MODIFIER_UUID);
        if (!TaczHighReadyServer.isSurvivalOrAdventure(player)) {
            return;
        }
        if (TaczGunCompat.getGunInfo(player.getMainHandItem()) == null) {
            return;
        }
        if (player.isSprinting()) {
            return;
        }
        double walkMultiplier = TaczHighReadyServer.clamp((Double)LocknarConfig.PLAYER_WALK_SPEED_MULTIPLIER.get(), 0.05, 2.0);
        double totalMultiplier = walkMultiplier * (highReadyMultiplier = highReadyActive ? TaczHighReadyServer.clamp((Double)LocknarConfig.HIGH_READY_SPEED_MULTIPLIER.get(), 0.05, 2.0) : 1.0);
        if (Math.abs(totalMultiplier - 1.0) < 1.0E-4) {
            return;
        }
        attribute.addTransientModifier(new AttributeModifier(MOVEMENT_SPEED_MODIFIER_UUID, totalMultiplier - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    private static void removeMovementSpeedModifier(ServerPlayer player) {
        if (player == null) {
            return;
        }
        AttributeInstance attribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attribute != null) {
            attribute.removeModifier(MOVEMENT_SPEED_MODIFIER_UUID);
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
