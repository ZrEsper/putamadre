package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.BackGunClientCache;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import com.locknar.taczammocheck.tactical.client.WeaponVisualConfigClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class FrontSlingGunRenderLayer
extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public FrontSlingGunRenderLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!((Boolean)LocknarConfig.FRONT_SLING_GUN_ENABLED.get()).booleanValue() || player == null || !player.isAlive() || player.isSpectator() || player.isInvisible()) {
            return;
        }
        int selectedSlot = player.isLocalPlayer() ? player.getInventory().selected : BackGunClientCache.getSelectedSlot(player);
        ItemStack stack = ThirdPersonWeaponSwitchClient.getFrontSlingStack(player, BackGunClientCache.getFirstSlot(player), selectedSlot);
        TaczGunCompat.GunInfo gunInfo = TaczGunCompat.getGunInfo(stack);
        if (gunInfo == null || gunInfo.kind() == TaczGunCompat.GunKind.PISTOL) {
            return;
        }
        WeaponVisualConfigClient.Transform transform = WeaponVisualConfigClient.get().frontGun;
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        poseStack.pushPose();
        ((PlayerModel)(Object)this.getParentModel()).body.translateAndRotate(poseStack);
        poseStack.translate(transform.x, transform.y, transform.z);
        poseStack.mulPose(Axis.XP.rotationDegrees((float)transform.rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees((float)transform.rotY));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float)transform.rotZ));
        float scale = (float)Math.max(0.01, transform.scale);
        poseStack.scale(-scale, -scale, scale);
        renderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), player.getId());
        poseStack.popPose();
    }
}
