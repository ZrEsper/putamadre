package com.locknar.taczammocheck.tactical.server;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.TaczAmmoCheckCommon;
import com.locknar.taczammocheck.prone.ProneServer;
import com.locknar.taczammocheck.tactical.DoorCompat;
import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.locknar.taczammocheck.tactical.TaczWallObstructionCommon;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ServerKickHandler {
    private static final String LAST_KICK_TAG = "locknarDoorKickLastTick";
    private static final int WINDUP_TICKS = 5;
    private static final int COOLDOWN_TICKS = 40;
    private static final int BACK_KICK_IMPACT_DELAY_TICKS = 7;
    private static final double MAX_HORIZONTAL_DISTANCE = 2.0;
    private static final double MAX_HORIZONTAL_DISTANCE_SQ = 4.0;
    private static final List<PendingKick> PENDING_KICKS = new ArrayList<PendingKick>();
    private static final List<PendingKickImpact> PENDING_KICK_IMPACTS = new ArrayList<PendingKickImpact>();

    private ServerKickHandler() {
    }

    public static void tryKickDoor(ServerPlayer player, BlockPos clickedPos) {
        long last;
        if (player == null || clickedPos == null) {
            return;
        }
        if (!DoorInteractionGlobalState.isServerEnabled(player.getServer())) {
            return;
        }
        if (!ServerKickHandler.canKickNow(player, clickedPos, true)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        MinecraftServer server = player.getServer();
        long now = server != null ? server.overworld().getGameTime() : level.getGameTime();
        if (now - (last = player.getPersistentData().getLong(LAST_KICK_TAG)) < 40L) {
            return;
        }
        player.getPersistentData().putLong(LAST_KICK_TAG, now);
        TaczWallObstructionCommon.startKickRaiseBlock((LivingEntity)player, 1000L);
        ModNetwork.sendKickRaise(player, 1000L);
        ResourceKey dimension = level.dimension();
        PENDING_KICKS.add(new PendingKick(player.getUUID(), (ResourceKey<Level>)dimension, clickedPos.immutable(), now + 5L));
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (false) {
            return;
        }
        MinecraftServer server = event.getServer();
        if (server == null) {
            return;
        }
        if (!DoorInteractionGlobalState.isServerEnabled(server)) {
            ServerKickHandler.clearAllPending();
            return;
        }
        if (PENDING_KICKS.isEmpty() && PENDING_KICK_IMPACTS.isEmpty()) {
            return;
        }
        long now = server.overworld().getGameTime();
        Iterator<PendingKick> iterator = PENDING_KICKS.iterator();
        while (iterator.hasNext()) {
            PendingKick pending = iterator.next();
            if (now < pending.executeTick()) continue;
            iterator.remove();
            ServerKickHandler.executePendingKick(server, pending);
        }
        Iterator<PendingKickImpact> impactIterator = PENDING_KICK_IMPACTS.iterator();
        while (impactIterator.hasNext()) {
            PendingKickImpact impact = impactIterator.next();
            if (now < impact.executeTick()) continue;
            impactIterator.remove();
            ServerKickHandler.executePendingKickImpact(server, impact);
        }
    }

    private static void executePendingKick(MinecraftServer server, PendingKick pending) {
        ServerPlayer player = server.getPlayerList().getPlayer(pending.playerId());
        if (player == null) {
            return;
        }
        if (!player.level().dimension().equals(pending.dimension())) {
            return;
        }
        if (!ServerKickHandler.canKickNow(player, pending.lowerDoorPos(), false)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        BlockPos lowerPos = pending.lowerDoorPos();
        BlockState state = level.getBlockState(lowerPos);
        int kickMode = ServerKickHandler.resolveKickAnimationMode(player, lowerPos, state);
        ModNetwork.sendKickAnimation(player, kickMode);
        if (ServerKickHandler.isBackKickMode(kickMode)) {
            long now = server.overworld().getGameTime();
            PENDING_KICK_IMPACTS.add(new PendingKickImpact(player.getUUID(), (ResourceKey<Level>)level.dimension(), lowerPos.immutable(), now + 7L));
            return;
        }
        ServerKickHandler.performKickImpact(player, lowerPos);
    }

    private static void executePendingKickImpact(MinecraftServer server, PendingKickImpact impact) {
        ServerPlayer player = server.getPlayerList().getPlayer(impact.playerId());
        if (player == null) {
            return;
        }
        if (!player.level().dimension().equals(impact.dimension())) {
            return;
        }
        if (!ServerKickHandler.canKickNow(player, impact.lowerDoorPos(), false)) {
            return;
        }
        ServerKickHandler.performKickImpact(player, impact.lowerDoorPos());
    }

    private static void performKickImpact(ServerPlayer player, BlockPos lowerPos) {
        if (player == null || lowerPos == null) {
            return;
        }
        ServerLevel level = player.serverLevel();
        BlockState state = level.getBlockState(lowerPos);
        boolean success = ServerKickHandler.rollKickSuccess();
        boolean kickedClosedDoorOpen = success && state.hasProperty((Property)DoorBlock.OPEN) && (Boolean)state.getValue((Property)DoorBlock.OPEN) == false;
        ServerKickHandler.playKickEffects(level, lowerPos, kickedClosedDoorOpen);
        if (success) {
            ServerKickHandler.openDoorAndPaired(level, player, lowerPos, state);
        }
    }

    private static int resolveKickAnimationMode(ServerPlayer player, BlockPos lowerPos, BlockState state) {
        if (player == null || lowerPos == null || state == null) {
            return 0;
        }
        if (!ServerKickHandler.isSupportedDoor(state) || !state.hasProperty((Property)DoorBlock.OPEN) || ((Boolean)state.getValue((Property)DoorBlock.OPEN)).booleanValue()) {
            return 0;
        }
        if (!state.hasProperty((Property)DoorBlock.FACING)) {
            return 0;
        }
        Direction facing = (Direction)state.getValue((Property)DoorBlock.FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();
        double dx = player.getX() - ((double)lowerPos.getX() + 0.5);
        double dz = player.getZ() - ((double)lowerPos.getZ() + 0.5);
        double leftDot = dx * (double)left.getStepX() + dz * (double)left.getStepZ();
        double rightDot = dx * (double)right.getStepX() + dz * (double)right.getStepZ();
        double frontBackDot = Math.abs(dx * (double)facing.getStepX() + dz * (double)facing.getStepZ());
        double sideDot = Math.max(leftDot, rightDot);
        if (sideDot < 0.35 || sideDot <= frontBackDot) {
            return 0;
        }
        return leftDot > rightDot ? 1 : 2;
    }

    private static boolean isBackKickMode(int mode) {
        return mode == 1 || mode == 2;
    }

    private static boolean rollKickSuccess() {
        int chance = LocknarConfig.kickSuccessChance();
        if (chance <= 0) {
            return false;
        }
        if (chance >= 100) {
            return true;
        }
        return ThreadLocalRandom.current().nextInt(100) < chance;
    }

    private static boolean canKickNow(ServerPlayer player, BlockPos lowerDoorPos, boolean requireLookingAtDoor) {
        if (player == null || lowerDoorPos == null || ProneServer.active((Player)player) || TaczAmmoCheckCommon.isAmmoCheckActive((LivingEntity)player)) {
            return false;
        }
        if (!DoorInteractionGlobalState.isServerEnabled(player.getServer())) {
            return false;
        }
        if (player.isSpectator() || player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            return false;
        }
        ServerLevel level = player.serverLevel();
        BlockState state = level.getBlockState(lowerDoorPos);
        if (!ServerKickHandler.isSupportedDoor(state)) {
            return false;
        }
        if (!state.hasProperty((Property)DoorBlock.HALF) || state.getValue((Property)DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
            return false;
        }
        if (!ServerKickHandler.isCloseEnough(player, lowerDoorPos)) {
            return false;
        }
        return !requireLookingAtDoor || ServerKickHandler.isLookingAtDoor(player, lowerDoorPos);
    }

    private static boolean isLookingAtDoor(ServerPlayer player, BlockPos lowerDoorPos) {
        Vec3 end;
        Vec3 start;
        if (player == null || lowerDoorPos == null || ProneServer.active((Player)player) || TaczAmmoCheckCommon.isAmmoCheckActive((LivingEntity)player)) {
            return false;
        }
        ServerLevel level = player.serverLevel();
        BlockHitResult hit = level.clip(new ClipContext(start = player.getEyePosition(1.0f), end = start.add(player.getLookAngle().scale(4.5)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, (Entity)player));
        if (hit == null || hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }
        BlockPos hitPos = hit.getBlockPos();
        return lowerDoorPos.equals((Object)hitPos) || lowerDoorPos.above().equals((Object)hitPos);
    }

    private static boolean isSupportedDoor(BlockState state) {
        return DoorCompat.isSupportedDoor(state);
    }

    private static boolean isCloseEnough(ServerPlayer player, BlockPos pos) {
        double closestZ;
        double dz;
        double px = player.getX();
        double pz = player.getZ();
        double minX = pos.getX();
        double maxX = (double)pos.getX() + 1.0;
        double minZ = pos.getZ();
        double maxZ = (double)pos.getZ() + 1.0;
        double closestX = ServerKickHandler.clamp(px, minX, maxX);
        double dx = px - closestX;
        return dx * dx + (dz = pz - (closestZ = ServerKickHandler.clamp(pz, minZ, maxZ))) * dz <= 4.0;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static void openDoorAndPaired(ServerLevel level, ServerPlayer player, BlockPos lowerPos, BlockState lowerState) {
        ServerKickHandler.openSingleDoor(level, player, lowerPos, lowerState);
        BlockPos pairedPos = ServerKickHandler.findPairedDoubleDoor(level, lowerPos, lowerState);
        if (pairedPos != null) {
            BlockState pairedState = level.getBlockState(pairedPos);
            ServerKickHandler.openSingleDoor(level, player, pairedPos, pairedState);
        }
    }

    private static void openSingleDoor(ServerLevel level, ServerPlayer player, BlockPos lowerPos, BlockState lowerState) {
        if (!ServerKickHandler.isSupportedDoor(lowerState) || !lowerState.hasProperty((Property)DoorBlock.OPEN)) {
            return;
        }
        DoorBlock door = (DoorBlock)lowerState.getBlock();
        if (!((Boolean)lowerState.getValue((Property)DoorBlock.OPEN)).booleanValue()) {
            door.setOpen((Entity)player, (Level)level, lowerState, lowerPos, true);
        } else {
            level.playSound(null, lowerPos, SoundEvents.WOODEN_DOOR_OPEN, SoundSource.BLOCKS, 0.55f, 0.75f);
        }
    }

    private static BlockPos findPairedDoubleDoor(ServerLevel level, BlockPos lowerPos, BlockState lowerState) {
        Direction[] candidates;
        if (!ServerKickHandler.isSupportedDoor(lowerState)) {
            return null;
        }
        if (!(lowerState.hasProperty((Property)DoorBlock.FACING) && lowerState.hasProperty((Property)DoorBlock.HINGE) && lowerState.hasProperty((Property)DoorBlock.HALF))) {
            return null;
        }
        if (lowerState.getValue((Property)DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
            return null;
        }
        Direction facing = (Direction)lowerState.getValue((Property)DoorBlock.FACING);
        DoorHingeSide hinge = (DoorHingeSide)lowerState.getValue((Property)DoorBlock.HINGE);
        for (Direction side : candidates = new Direction[]{facing.getClockWise(), facing.getCounterClockWise()}) {
            BlockPos candidatePos = lowerPos.relative(side);
            BlockState candidateState = level.getBlockState(candidatePos);
            if (!ServerKickHandler.isSupportedDoor(candidateState) || candidateState.getBlock() != lowerState.getBlock() || !candidateState.hasProperty((Property)DoorBlock.FACING) || !candidateState.hasProperty((Property)DoorBlock.HINGE) || !candidateState.hasProperty((Property)DoorBlock.HALF) || candidateState.getValue((Property)DoorBlock.HALF) != DoubleBlockHalf.LOWER || candidateState.getValue((Property)DoorBlock.FACING) != facing || candidateState.getValue((Property)DoorBlock.HINGE) == hinge) continue;
            return candidatePos;
        }
        return null;
    }

    private static void playKickEffects(ServerLevel level, BlockPos lowerPos, boolean kickedOpen) {
        double x = (double)lowerPos.getX() + 0.5;
        double y = (double)lowerPos.getY() + 0.8;
        double z = (double)lowerPos.getZ() + 0.5;
        if (kickedOpen) {
            level.playSound(null, lowerPos, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1.25f, 0.72f);
            level.playSound(null, lowerPos, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.BLOCKS, 1.15f, 0.95f);
            level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, x, y, z, 12, 0.25, 0.2, 0.25, 0.025);
            return;
        }
        level.playSound(null, lowerPos, SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.9f, 0.75f);
        level.playSound(null, lowerPos, SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, SoundSource.BLOCKS, 0.65f, 0.85f);
        level.sendParticles((ParticleOptions)ParticleTypes.CLOUD, x, y, z, 8, 0.22, 0.18, 0.22, 0.02);
    }

    public static void clearPendingForPlayer(ServerPlayer player) {
        if (player == null) {
            return;
        }
        UUID playerId = player.getUUID();
        PENDING_KICKS.removeIf(pending -> pending.playerId().equals(playerId));
        PENDING_KICK_IMPACTS.removeIf(impact -> impact.playerId().equals(playerId));
    }

    public static void clearAllPending() {
        PENDING_KICKS.clear();
        PENDING_KICK_IMPACTS.clear();
    }

    private record PendingKick(UUID playerId, ResourceKey<Level> dimension, BlockPos lowerDoorPos, long executeTick) {
    }

    private record PendingKickImpact(UUID playerId, ResourceKey<Level> dimension, BlockPos lowerDoorPos, long executeTick) {
    }
}
