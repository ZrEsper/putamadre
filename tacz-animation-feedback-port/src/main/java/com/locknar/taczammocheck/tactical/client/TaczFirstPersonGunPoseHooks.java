package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.client.TaczWallObstructionClient;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class TaczFirstPersonGunPoseHooks {
    private static final ThreadLocal<Deque<Boolean>> POSE_PUSH_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private TaczFirstPersonGunPoseHooks() {
    }

    public static void beforeRenderFirstPerson(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, float partialTick) {
        boolean pushed;
        block6: {
            pushed = false;
            try {
                if (poseStack != null && TaczWallObstructionClient.hasActiveFirstPersonGunPose(stack, transformType, partialTick)) {
                    poseStack.pushPose();
                    pushed = true;
                    if (TaczWallObstructionClient.applyActiveFirstPersonGunPoseTransform(poseStack, stack, transformType, partialTick)) {
                        TaczWallObstructionClient.markFirstPersonWrapperHookActive();
                    }
                }
            }
            catch (Throwable ignored) {
                if (!pushed) break block6;
                try {
                    poseStack.popPose();
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                pushed = false;
            }
        }
        POSE_PUSH_STACK.get().push(pushed);
    }

    public static void afterRenderFirstPerson(PoseStack poseStack) {
        Deque<Boolean> stack = POSE_PUSH_STACK.get();
        if (stack.isEmpty()) {
            return;
        }
        boolean pushed = stack.pop();
        if (!pushed || poseStack == null) {
            return;
        }
        try {
            poseStack.popPose();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}
