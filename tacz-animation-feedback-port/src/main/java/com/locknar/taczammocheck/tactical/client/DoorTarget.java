package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.DoorCompat;
import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.locknar.taczammocheck.tactical.ModItems;
import com.locknar.taczammocheck.tactical.client.DoorAction;
import com.locknar.taczammocheck.tactical.client.MirrorViewClient;
import com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class DoorTarget {
    private static final double MAX_DOOR_HORIZONTAL_DISTANCE = 2.0;
    private static final double MAX_DOOR_HORIZONTAL_DISTANCE_SQ = 4.0;

    private DoorTarget() {
    }

    public static boolean isSupportedDoor(BlockState state) {
        return DoorCompat.isSupportedDoor(state);
    }

    public static DoorAction getLookingDoorAction(Minecraft mc) {
        if (mc == null || mc.player == null || mc.level == null || mc.screen != null) {
            return DoorAction.NONE;
        }
        if (!DoorInteractionGlobalState.isClientEnabled()) {
            return DoorAction.NONE;
        }
        if (mc.player.isSpectator()) {
            return DoorAction.NONE;
        }
        if (MissionScoreDoorLockCompat.shouldSuppressDoorInteractionForHeldWedge()) {
            return DoorAction.NONE;
        }
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult)) {
            return DoorAction.NONE;
        }
        BlockHitResult hit = (BlockHitResult)hitResult;
        if (hit.getType() != HitResult.Type.BLOCK) {
            return DoorAction.NONE;
        }
        if (!DoorTarget.isCloseEnoughToDoor(mc, hit)) {
            return DoorAction.NONE;
        }
        ClientLevel level = mc.level;
        BlockPos pos = hit.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!DoorTarget.isSupportedDoor(state)) {
            return DoorAction.NONE;
        }
        if (!state.hasProperty((Property)DoorBlock.HALF)) {
            return DoorAction.NONE;
        }
        DoubleBlockHalf half = (DoubleBlockHalf)state.getValue((Property)DoorBlock.HALF);
        if (DoorTarget.isHoldingMirrorGun(mc)) {
            if (MirrorViewClient.isMirrorReuseCoolingDown()) {
                return DoorAction.NONE;
            }
            if (half == DoubleBlockHalf.LOWER && DoorTarget.isClosedDoor(state) && DoorTarget.isPlayerOnDoorFrontOrBack(mc, pos, state) && DoorTarget.isMirrorBottomArea(hit, pos)) {
                return DoorAction.MIRROR;
            }
            return DoorAction.NONE;
        }
        if (half == DoubleBlockHalf.UPPER) {
            if (MissionScoreDoorLockCompat.shouldTakeOverCurrentUpperDoor()) {
                return DoorAction.NONE;
            }
            return DoorAction.OPEN;
        }
        return DoorAction.KICK;
    }

    public static boolean isLookingBackKickCandidate(Minecraft mc) {
        BlockHitResult hit;
        if (mc == null || mc.player == null || mc.level == null || mc.screen != null) {
            return false;
        }
        if (!DoorInteractionGlobalState.isClientEnabled()) {
            return false;
        }
        if (MissionScoreDoorLockCompat.shouldSuppressDoorInteractionForHeldWedge()) {
            return false;
        }
        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult) || (hit = (BlockHitResult)hitResult).getType() != HitResult.Type.BLOCK) {
            return false;
        }
        if (!DoorTarget.isCloseEnoughToDoor(mc, hit)) {
            return false;
        }
        BlockPos pos = hit.getBlockPos();
        BlockState state = mc.level.getBlockState(pos);
        if (!DoorTarget.isSupportedDoor(state) || !state.hasProperty((Property)DoorBlock.HALF) || state.getValue((Property)DoorBlock.HALF) != DoubleBlockHalf.LOWER) {
            return false;
        }
        if (!state.hasProperty((Property)DoorBlock.OPEN) || ((Boolean)state.getValue((Property)DoorBlock.OPEN)).booleanValue()) {
            return false;
        }
        if (!state.hasProperty((Property)DoorBlock.FACING)) {
            return false;
        }
        Direction facing = (Direction)state.getValue((Property)DoorBlock.FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();
        double dx = mc.player.getX() - ((double)pos.getX() + 0.5);
        double dz = mc.player.getZ() - ((double)pos.getZ() + 0.5);
        double leftDot = dx * (double)left.getStepX() + dz * (double)left.getStepZ();
        double rightDot = dx * (double)right.getStepX() + dz * (double)right.getStepZ();
        double frontBackDot = Math.abs(dx * (double)facing.getStepX() + dz * (double)facing.getStepZ());
        double sideDot = Math.max(leftDot, rightDot);
        return sideDot >= 0.35 && sideDot > frontBackDot;
    }

    public static BlockHitResult getLookingDoorHit(Minecraft mc) {
        HitResult hitResult;
        if (mc == null || mc.player == null || mc.level == null || !((hitResult = mc.hitResult) instanceof BlockHitResult)) {
            return null;
        }
        BlockHitResult hit = (BlockHitResult)hitResult;
        if (!DoorInteractionGlobalState.isClientEnabled()) {
            return null;
        }
        if (mc.player.isSpectator()) {
            return null;
        }
        if (MissionScoreDoorLockCompat.shouldSuppressDoorInteractionForHeldWedge()) {
            return null;
        }
        if (!DoorTarget.isCloseEnoughToDoor(mc, hit)) {
            return null;
        }
        BlockState state = mc.level.getBlockState(hit.getBlockPos());
        return DoorTarget.isSupportedDoor(state) ? hit : null;
    }

    public static boolean isHoldingMirrorGun(Minecraft mc) {
        if (mc == null || mc.player == null) {
            return false;
        }
        ItemStack mainHand = mc.player.getMainHandItem();
        ItemStack offHand = mc.player.getOffhandItem();
        return mainHand.is((Item)ModItems.MIRROR_GUN.get()) || offHand.is((Item)ModItems.MIRROR_GUN.get());
    }

    private static boolean isClosedDoor(BlockState state) {
        return state != null && state.hasProperty((Property)DoorBlock.OPEN) && (Boolean)state.getValue((Property)DoorBlock.OPEN) == false;
    }

    private static boolean isPlayerOnDoorFrontOrBack(Minecraft mc, BlockPos lowerPos, BlockState lowerState) {
        if (mc == null || mc.player == null || lowerPos == null || lowerState == null || !lowerState.hasProperty((Property)DoorBlock.FACING)) {
            return false;
        }
        Direction facing = (Direction)lowerState.getValue((Property)DoorBlock.FACING);
        Direction playerSide = DoorTarget.getPlayerSide(mc.player.getX(), mc.player.getZ(), lowerPos);
        return playerSide == facing || playerSide == facing.getOpposite();
    }

    private static Direction getPlayerSide(double playerX, double playerZ, BlockPos lowerPos) {
        double centerX = (double)lowerPos.getX() + 0.5;
        double centerZ = (double)lowerPos.getZ() + 0.5;
        double dx = playerX - centerX;
        double dz = playerZ - centerZ;
        if (Math.abs(dx) > Math.abs(dz)) {
            return dx >= 0.0 ? Direction.EAST : Direction.WEST;
        }
        return dz >= 0.0 ? Direction.SOUTH : Direction.NORTH;
    }

    private static boolean isMirrorBottomArea(BlockHitResult hit, BlockPos lowerPos) {
        if (hit == null || lowerPos == null || !hit.getBlockPos().equals((Object)lowerPos)) {
            return false;
        }
        double localY = hit.getLocation().y - (double)lowerPos.getY();
        return localY >= 0.0 && localY <= 0.3;
    }

    private static boolean isCloseEnoughToDoor(Minecraft mc, BlockHitResult hit) {
        double closestZ;
        double dz;
        if (mc == null || mc.player == null || hit == null) {
            return false;
        }
        BlockPos pos = hit.getBlockPos();
        double px = mc.player.getX();
        double pz = mc.player.getZ();
        double minX = pos.getX();
        double maxX = (double)pos.getX() + 1.0;
        double minZ = pos.getZ();
        double maxZ = (double)pos.getZ() + 1.0;
        double closestX = DoorTarget.clamp(px, minX, maxX);
        double dx = px - closestX;
        return dx * dx + (dz = pz - (closestZ = DoorTarget.clamp(pz, minZ, maxZ))) * dz <= 4.0;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
