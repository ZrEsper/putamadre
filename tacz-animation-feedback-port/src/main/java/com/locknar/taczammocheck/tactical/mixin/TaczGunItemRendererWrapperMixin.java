package com.locknar.taczammocheck.tactical.mixin;

import com.locknar.taczammocheck.tactical.client.TaczFirstPersonGunPoseHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.tacz.guns.client.renderer.item.GunItemRendererWrapper"}, remap=false, priority=850)
public abstract class TaczGunItemRendererWrapperMixin {
    @Inject(method={"renderFirstPerson(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"}, at={@At(value="HEAD")}, require=0)
    private void locknarDoorKick$beforeFirstPersonGunPose(LocalPlayer player, ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource bufferSource, int light, float partialTick, CallbackInfo ci) {
        TaczFirstPersonGunPoseHooks.beforeRenderFirstPerson(stack, ctx, poseStack, partialTick);
    }

    @Inject(method={"renderFirstPerson(Lnet/minecraft/client/player/LocalPlayer;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V"}, at={@At(value="RETURN")}, require=0)
    private void locknarDoorKick$afterFirstPersonGunPose(LocalPlayer player, ItemStack stack, ItemDisplayContext ctx, PoseStack poseStack, MultiBufferSource bufferSource, int light, float partialTick, CallbackInfo ci) {
        TaczFirstPersonGunPoseHooks.afterRenderFirstPerson(poseStack);
    }
}
