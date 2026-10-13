package com.locknar.taczammocheck.tactical.mixin;

import com.locknar.taczammocheck.tactical.client.DoorOpenHandAnimationClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.tacz.guns.client.model.functional.LeftHandRender"}, remap=false, priority=900)
public abstract class TaczLeftHandRenderMixin {
    @Unique
    private static final ThreadLocal<Deque<Boolean>> locknarDoorKick$poseStackState = ThreadLocal.withInitial(ArrayDeque::new);

    @Inject(method={"render"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void locknarDoorKick$beforeTaczLeftHand(PoseStack poseStack, VertexConsumer vertexBuffer, ItemDisplayContext transformType, int light, int overlay, CallbackInfo ci) {
        if (transformType == null || !transformType.firstPerson()) {
            locknarDoorKick$poseStackState.get().push(false);
            return;
        }
        if (DoorOpenHandAnimationClient.shouldTransformTaczLeftHandWhileOpenHand(transformType)) {
            poseStack.pushPose();
            DoorOpenHandAnimationClient.applyTaczLeftHandOpenTransform(poseStack);
            locknarDoorKick$poseStackState.get().push(true);
        } else {
            locknarDoorKick$poseStackState.get().push(false);
        }
    }

    @Inject(method={"render"}, at={@At(value="RETURN")}, require=0)
    private void locknarDoorKick$afterTaczLeftHand(PoseStack poseStack, VertexConsumer vertexBuffer, ItemDisplayContext transformType, int light, int overlay, CallbackInfo ci) {
        Deque<Boolean> stack = locknarDoorKick$poseStackState.get();
        if (stack.isEmpty()) {
            return;
        }
        boolean pushed = stack.pop();
        if (pushed) {
            poseStack.popPose();
        }
    }
}
