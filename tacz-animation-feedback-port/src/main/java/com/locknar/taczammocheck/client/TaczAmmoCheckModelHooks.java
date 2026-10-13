package com.locknar.taczammocheck.client;

import com.locknar.taczammocheck.TaczGunCompat;
import com.locknar.taczammocheck.client.AmmoCheckBedrockAnimationPlayer;
import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public final class TaczAmmoCheckModelHooks {
    private TaczAmmoCheckModelHooks() {
    }

    public static void beforeGunRender(Object bedrockGunModel, PoseStack poseStack, ItemStack gunItem, ItemDisplayContext transformType) {
        try {
            if (poseStack != null && AmmoCheckClient.shouldApplyNativeBedrockAmmoCheck(transformType, gunItem)) {
                String preset = AmmoCheckClient.getActiveNativePresetName();
                String gunId = TaczGunCompat.getGunId(gunItem);
                float releaseWeight = AmmoCheckClient.getNativeAnimationBlendWeight(0.0f);
                AmmoCheckBedrockAnimationPlayer.apply(bedrockGunModel, preset, AmmoCheckClient.getActiveAnimationName(), AmmoCheckClient.getActiveAnimationTimeSeconds(0.0f), releaseWeight, gunId);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static void renderDetachedMagazine(Object bedrockGunModel, PoseStack poseStack, ItemStack gunItem, ItemDisplayContext transformType, RenderType renderType, int light, int overlay) {
    }

    public static void afterGunRender(Object bedrockGunModel, PoseStack poseStack) {
    }
}
