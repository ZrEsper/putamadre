package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.client.TaczWallObstructionClient;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class TaczFirstPersonModelPoseHooks {
    private static final ThreadLocal<Deque<Boolean>> POSE_PUSH_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private TaczFirstPersonModelPoseHooks() {
    }

    public static void beforeGunRender(PoseStack poseStack, ItemStack gunItem, ItemDisplayContext transformType) {
        boolean pushed;
        block6: {
            pushed = false;
            try {
                if (poseStack != null && transformType != null && transformType.firstPerson() && !TaczWallObstructionClient.isFirstPersonRenderHandHookRecentlyActive() && !TaczWallObstructionClient.isFirstPersonWrapperHookRecentlyActive() && TaczWallObstructionClient.hasActiveFirstPersonGunPose(gunItem, transformType, TaczFirstPersonModelPoseHooks.getFramePartialTick())) {
                    poseStack.pushPose();
                    pushed = true;
                    if (TaczWallObstructionClient.applyActiveFirstPersonGunPoseTransform(poseStack, gunItem, transformType, TaczFirstPersonModelPoseHooks.getFramePartialTick())) {
                        TaczWallObstructionClient.markFirstPersonModelHookActive();
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

    public static void afterGunRender(PoseStack poseStack) {
        Deque<Boolean> stack = POSE_PUSH_STACK.get();
        if (stack.isEmpty()) {
            return;
        }
        boolean pushed = stack.pop();
        if (pushed && poseStack != null) {
            try {
                poseStack.popPose();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private static float getFramePartialTick() {
        try {
            Minecraft mc = Minecraft.getInstance();
            return mc == null ? 0.0f : mc.getTimer().getGameTimeDeltaPartialTick(false);
        }
        catch (Throwable ignored) {
            return 0.0f;
        }
    }
}
