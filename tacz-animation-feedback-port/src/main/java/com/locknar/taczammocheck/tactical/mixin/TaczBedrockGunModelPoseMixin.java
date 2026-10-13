package com.locknar.taczammocheck.tactical.mixin;

import com.locknar.taczammocheck.tactical.client.TaczFirstPersonModelPoseHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.tacz.guns.client.model.BedrockGunModel"}, remap=false, priority=850)
public abstract class TaczBedrockGunModelPoseMixin {
    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/client/renderer/RenderType;II)V"}, at={@At(value="HEAD")}, require=0)
    private void locknarDoorKick$beforeFirstPersonPose(PoseStack matrixStack, ItemStack gunItem, ItemDisplayContext transformType, RenderType renderType, int light, int overlay, CallbackInfo ci) {
        TaczFirstPersonModelPoseHooks.beforeGunRender(matrixStack, gunItem, transformType);
    }

    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/client/renderer/RenderType;II)V"}, at={@At(value="RETURN")}, require=0)
    private void locknarDoorKick$afterFirstPersonPose(PoseStack matrixStack, ItemStack gunItem, ItemDisplayContext transformType, RenderType renderType, int light, int overlay, CallbackInfo ci) {
        TaczFirstPersonModelPoseHooks.afterGunRender(matrixStack);
    }
}
