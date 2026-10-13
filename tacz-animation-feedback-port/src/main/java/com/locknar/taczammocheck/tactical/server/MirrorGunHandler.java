package com.locknar.taczammocheck.tactical.server;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.prone.ProneServer;
import com.locknar.taczammocheck.tactical.DoorCompat;
import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.locknar.taczammocheck.tactical.ModItems;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public final class MirrorGunHandler {
    private static final double MAX_HORIZONTAL_DISTANCE = 2.0;
    private static final double MAX_HORIZONTAL_DISTANCE_SQ = 4.0;
    private static final double MIRROR_BOTTOM_LOCAL_Y = 0.3;
    private static final int MIRROR_VIEW_TICKS = -1;
    private static final int MIRROR_REUSE_COOLDOWN_TICKS = 0;
    private static final Map<UUID, PendingMirrorUse> PENDING_USE = new HashMap<UUID, PendingMirrorUse>();
    private static final Map<UUID, Integer> MIRROR_REUSE_COOLDOWNS = new HashMap<UUID, Integer>();
    private static final Set<UUID> ACTIVE_MIRROR_USERS = new HashSet<UUID>();

    private MirrorGunHandler() {
    }

    public static boolean isBusy(ServerPlayer player) {
        return ACTIVE_MIRROR_USERS.contains(player.getUUID()) || PENDING_USE.containsKey(player.getUUID());
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ServerPlayer activePlayer;
        ServerPlayer p;
        BlockState clickedState;
        ServerPlayer serverPlayer;
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (!stack.is((Item)ModItems.MIRROR_GUN.get())) {
            return;
        }
        Level level = event.getLevel();
        if (level.isClientSide() ? !DoorInteractionGlobalState.isClientEnabled() : player instanceof ServerPlayer && !DoorInteractionGlobalState.isServerEnabled((serverPlayer = (ServerPlayer)player).getServer())) {
            return;
        }
        BlockPos clickedPos = event.getPos();
        BlockPos lowerPos = MirrorGunHandler.normalizeLowerDoorPos(level, clickedPos, clickedState = level.getBlockState(clickedPos));
        if (lowerPos == null) {
            return;
        }
        BlockState lowerState = level.getBlockState(lowerPos);
        if (!MirrorGunHandler.isSupportedDoor(lowerState)) {
            return;
        }
        ((net.neoforged.bus.api.ICancellableEvent)event).setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (player.isSpectator() || player instanceof ServerPlayer && ProneServer.transitioning((Player)(p = (ServerPlayer)player))) {
            return;
        }
        if (!MirrorGunHandler.isClosedDoor(lowerState)) {
            return;
        }
        if (!MirrorGunHandler.isPlayerOnDoorFrontOrBack(player, lowerPos, lowerState)) {
            return;
        }
        if (player instanceof ServerPlayer && ACTIVE_MIRROR_USERS.contains((activePlayer = (ServerPlayer)player).getUUID())) {
            return;
        }
        if (!MirrorGunHandler.isCloseEnough(player, lowerPos)) {
            return;
        }
        BlockHitResult hit = event.getHitVec();
        if (hit == null || !MirrorGunHandler.isBottomMirrorArea(hit, lowerPos)) {
            return;
        }
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer2 = (ServerPlayer)player;
        if (MirrorGunHandler.isMirrorCoolingDown(serverPlayer2)) {
            return;
        }
        int useTicks = LocknarConfig.mirrorUseTicks();
        if (useTicks <= 0) {
            MirrorGunHandler.startMirrorView(serverPlayer2, lowerPos);
            return;
        }
        PENDING_USE.put(serverPlayer2.getUUID(), new PendingMirrorUse(serverPlayer2, lowerPos, useTicks));
        ModNetwork.sendMirrorProgress(serverPlayer2, useTicks);
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (false) {
            return;
        }
        if (!DoorInteractionGlobalState.isServerEnabled(event.getServer())) {
            MirrorGunHandler.clearAllServerState();
            return;
        }
        MirrorGunHandler.tickMirrorCooldowns();
        MirrorGunHandler.tickActiveMirrorUsers();
        if (PENDING_USE.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, PendingMirrorUse>> iterator = PENDING_USE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, PendingMirrorUse> entry = iterator.next();
            PendingMirrorUse pending = entry.getValue();
            ServerPlayer player = pending.player();
            if (player == null || player.isRemoved() || player.isSpectator() || player.isDeadOrDying()) {
                iterator.remove();
                continue;
            }
            Level level = player.level();
            if (!(level instanceof ServerLevel)) {
                iterator.remove();
                continue;
            }
            ServerLevel level2 = (ServerLevel)level;
            BlockState pendingState = level2.getBlockState(pending.lowerPos());
            if (!(MirrorGunHandler.isHoldingMirrorGun((Player)player) && MirrorGunHandler.isCloseEnough((Player)player, pending.lowerPos()) && MirrorGunHandler.isSupportedDoor(pendingState) && MirrorGunHandler.isClosedDoor(pendingState) && MirrorGunHandler.isPlayerOnDoorFrontOrBack((Player)player, pending.lowerPos(), pendingState))) {
                iterator.remove();
                continue;
            }
            --pending.remainingTicks;
            if (pending.remainingTicks > 0) continue;
            MirrorGunHandler.startMirrorView(player, pending.lowerPos());
            iterator.remove();
        }
    }

    public static void onMirrorCancel(ServerPlayer player) {
        if (player == null) {
            return;
        }
        PENDING_USE.remove(player.getUUID());
        ACTIVE_MIRROR_USERS.remove(player.getUUID());
    }

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)livingEntity;
            PENDING_USE.remove(player.getUUID());
            ACTIVE_MIRROR_USERS.remove(player.getUUID());
            MIRROR_REUSE_COOLDOWNS.remove(player.getUUID());
        }
    }

    public static void clearAllServerState() {
        PENDING_USE.clear();
        ACTIVE_MIRROR_USERS.clear();
        MIRROR_REUSE_COOLDOWNS.clear();
    }

    private static void tickActiveMirrorUsers() {
        if (ACTIVE_MIRROR_USERS.isEmpty()) {
            return;
        }
        Iterator<UUID> iterator = ACTIVE_MIRROR_USERS.iterator();
        while (iterator.hasNext()) {
            UUID uuid = iterator.next();
            ServerPlayer player = null;
            for (ServerPlayer online : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (!online.getUUID().equals(uuid)) continue;
                player = online;
                break;
            }
            if (player != null && !player.isRemoved() && !player.isSpectator() && !player.isDeadOrDying() && MirrorGunHandler.isHoldingMirrorGun((Player)player)) continue;
            iterator.remove();
        }
    }

    private static boolean isMirrorCoolingDown(ServerPlayer player) {
        Integer ticks = MIRROR_REUSE_COOLDOWNS.get(player.getUUID());
        return ticks != null && ticks > 0;
    }

    private static void tickMirrorCooldowns() {
        if (MIRROR_REUSE_COOLDOWNS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, Integer>> iterator = MIRROR_REUSE_COOLDOWNS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                iterator.remove();
                continue;
            }
            entry.setValue(remaining);
        }
    }

    private static void startMirrorView(ServerPlayer player, BlockPos lowerPos) {
        if (player == null || !DoorInteractionGlobalState.isServerEnabled(player.getServer())) {
            return;
        }
        Level level = player.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        BlockState lowerState = level2.getBlockState(lowerPos);
        if (!(MirrorGunHandler.isSupportedDoor(lowerState) && MirrorGunHandler.isClosedDoor(lowerState) && MirrorGunHandler.isPlayerOnDoorFrontOrBack((Player)player, lowerPos, lowerState))) {
            return;
        }
        ACTIVE_MIRROR_USERS.add(player.getUUID());
        Direction playerSide = MirrorGunHandler.getPlayerSide((Player)player, lowerPos);
        Direction lookDirection = playerSide.getOpposite();
        double x = (double)lowerPos.getX() + 0.5 + (double)lookDirection.getStepX() * 0.62;
        double y = (double)lowerPos.getY() + 0.18;
        double z = (double)lowerPos.getZ() + 0.5 + (double)lookDirection.getStepZ() * 0.62;
        float yaw = MirrorGunHandler.yawForDirection(lookDirection);
        float pitch = -5.0f;
        ModNetwork.sendMirrorView(player, x, y, z, yaw, pitch, -1);
    }

    private static BlockPos normalizeLowerDoorPos(Level level, BlockPos pos, BlockState state) {
        if (!MirrorGunHandler.isDoorBlock(state) || !state.hasProperty((Property)DoorBlock.HALF)) {
            return null;
        }
        if (state.getValue((Property)DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            return MirrorGunHandler.isDoorBlock(belowState) ? below : null;
        }
        return pos;
    }

    private static boolean isClosedDoor(BlockState state) {
        return state != null && state.hasProperty((Property)DoorBlock.OPEN) && (Boolean)state.getValue((Property)DoorBlock.OPEN) == false;
    }

    private static boolean isPlayerOnDoorFrontOrBack(Player player, BlockPos lowerPos, BlockState lowerState) {
        if (player == null || lowerPos == null || lowerState == null || !lowerState.hasProperty((Property)DoorBlock.FACING)) {
            return false;
        }
        Direction facing = (Direction)lowerState.getValue((Property)DoorBlock.FACING);
        Direction playerSide = MirrorGunHandler.getPlayerSide(player, lowerPos);
        return playerSide == facing || playerSide == facing.getOpposite();
    }

    private static boolean isBottomMirrorArea(BlockHitResult hit, BlockPos lowerPos) {
        if (!hit.getBlockPos().equals((Object)lowerPos)) {
            return false;
        }
        double localY = hit.getLocation().y - (double)lowerPos.getY();
        return localY >= 0.0 && localY <= 0.3;
    }

    private static boolean isDoorBlock(BlockState state) {
        return DoorCompat.isSupportedDoor(state);
    }

    private static boolean isSupportedDoor(BlockState state) {
        return DoorCompat.isSupportedDoor(state);
    }

    private static boolean isCloseEnough(Player player, BlockPos pos) {
        double closestZ;
        double dz;
        double px = player.getX();
        double pz = player.getZ();
        double minX = pos.getX();
        double maxX = (double)pos.getX() + 1.0;
        double minZ = pos.getZ();
        double maxZ = (double)pos.getZ() + 1.0;
        double closestX = MirrorGunHandler.clamp(px, minX, maxX);
        double dx = px - closestX;
        return dx * dx + (dz = pz - (closestZ = MirrorGunHandler.clamp(pz, minZ, maxZ))) * dz <= 4.0;
    }

    private static boolean isHoldingMirrorGun(Player player) {
        return player.getMainHandItem().is((Item)ModItems.MIRROR_GUN.get()) || player.getOffhandItem().is((Item)ModItems.MIRROR_GUN.get());
    }

    private static Direction getPlayerSide(Player player, BlockPos lowerPos) {
        Vec3 center = Vec3.atCenterOf((Vec3i)lowerPos);
        double dx = player.getX() - center.x;
        double dz = player.getZ() - center.z;
        if (Math.abs(dx) > Math.abs(dz)) {
            return dx >= 0.0 ? Direction.EAST : Direction.WEST;
        }
        return dz >= 0.0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static float yawForDirection(Direction direction) {
        return switch (direction) {
            case Direction.SOUTH -> 0.0f;
            case Direction.WEST -> 90.0f;
            case Direction.NORTH -> 180.0f;
            case Direction.EAST -> -90.0f;
            default -> 0.0f;
        };
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static final class PendingMirrorUse {
        private final ServerPlayer player;
        private final BlockPos lowerPos;
        private int remainingTicks;

        private PendingMirrorUse(ServerPlayer player, BlockPos lowerPos, int remainingTicks) {
            this.player = player;
            this.lowerPos = lowerPos;
            this.remainingTicks = remainingTicks;
        }

        private ServerPlayer player() {
            return this.player;
        }

        private BlockPos lowerPos() {
            return this.lowerPos;
        }
    }
}
