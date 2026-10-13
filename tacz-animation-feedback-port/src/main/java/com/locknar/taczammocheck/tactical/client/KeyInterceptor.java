package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.locknar.taczammocheck.prone.ProneClient;
import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.DoorAction;
import com.locknar.taczammocheck.tactical.client.DoorHudFeedbackClient;
import com.locknar.taczammocheck.tactical.client.DoorOpenHandAnimationClient;
import com.locknar.taczammocheck.tactical.client.DoorTarget;
import com.locknar.taczammocheck.tactical.client.MirrorViewClient;
import com.locknar.taczammocheck.tactical.client.MissionScoreDoorLockCompat;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;

public final class KeyInterceptor {
    private KeyInterceptor() {
    }

    public static boolean handleRawKey(long windowPointer, int key, int scanCode, int action, int modifiers) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null) {
            return false;
        }
        if (windowPointer != mc.getWindow().getWindow()) {
            return false;
        }
        if (MirrorViewClient.shouldBlockRawDropKey(key, scanCode, action)) {
            return true;
        }
        if (MirrorViewClient.handleRawEscapeKey(key, scanCode, action)) {
            return true;
        }
        if (key != 70) {
            return false;
        }
        if (!DoorInteractionGlobalState.isClientEnabled()) {
            return false;
        }
        if (mc.player == null || mc.level == null || mc.gameMode == null) {
            return false;
        }
        if (mc.screen != null) {
            return false;
        }
        if (action == 0) {
            return false;
        }
        if (action != 1 && action != 2) {
            return false;
        }
        DoorAction doorAction = DoorTarget.getLookingDoorAction(mc);
        if ((doorAction == DoorAction.OPEN || doorAction == DoorAction.KICK) && (AmmoCheckClient.isActive() || ProneClient.transitioning((AbstractClientPlayer)mc.player))) {
            return true;
        }
        if (doorAction == DoorAction.KICK && ProneClient.active((AbstractClientPlayer)mc.player)) {
            return true;
        }
        if ((doorAction == DoorAction.OPEN || doorAction == DoorAction.KICK) && DoorHudFeedbackClient.isDoorHudSuppressed()) {
            return true;
        }
        if (doorAction == DoorAction.OPEN) {
            if (action == 1) {
                KeyInterceptor.openDoor(mc);
            }
            return true;
        }
        if (doorAction == DoorAction.KICK) {
            if (action == 1) {
                KeyInterceptor.kickDoor(mc);
            }
            return true;
        }
        return false;
    }

    private static boolean openDoor(Minecraft mc) {
        InteractionResult result;
        BlockHitResult hit = DoorTarget.getLookingDoorHit(mc);
        if (hit == null || mc.player == null || mc.gameMode == null) {
            return false;
        }
        if (MissionScoreDoorLockCompat.isCurrentTargetLocked()) {
            if (MissionScoreDoorLockCompat.isCurrentTargetRevealed()) {
                return true;
            }
            if (MissionScoreDoorLockCompat.requestCurrentDoorOpenAttempt()) {
                return true;
            }
        }
        if ((result = mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit)).consumesAction()) {
            mc.player.swing(InteractionHand.MAIN_HAND);
            DoorHudFeedbackClient.triggerActionFeedback(DoorAction.OPEN);
            if (TaczGunCompat.isTaczGun(mc.player.getMainHandItem())) {
                DoorOpenHandAnimationClient.trigger();
            }
        }
        return true;
    }

    private static boolean kickDoor(Minecraft mc) {
        BlockHitResult hit = DoorTarget.getLookingDoorHit(mc);
        if (hit == null || mc.player == null) {
            return false;
        }
        DoorHudFeedbackClient.triggerActionFeedback(DoorAction.KICK, DoorTarget.isLookingBackKickCandidate(mc));
        ModNetwork.sendKickDoor(hit.getBlockPos());
        return true;
    }
}
