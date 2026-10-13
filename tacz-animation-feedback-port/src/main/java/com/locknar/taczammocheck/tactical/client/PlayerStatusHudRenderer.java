package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.TaczHighReadyClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class PlayerStatusHudRenderer {
    private static final int TEXTURE_WIDTH = 461;
    private static final int TEXTURE_HEIGHT = 567;
    private static final int HUD_HEIGHT = 96;
    private static final int HUD_WIDTH = Math.round(78.05291f);
    private static final int MARGIN_LEFT = 8;
    private static final int MARGIN_BOTTOM = 8;
    private static final float HEALTH_TINT_FULL = 20.0f;
    private static final float HEALTH_TINT_LOW = 6.0f;
    private static final float LOW_HEALTH_RED = 0.654902f;
    private static final float LOW_HEALTH_GREEN = 0.0f;
    private static final float LOW_HEALTH_BLUE = 0.011764706f;
    private static final ResourceLocation[] STATUS_TEXTURES = new ResourceLocation[]{null, PlayerStatusHudRenderer.statusTexture(1), PlayerStatusHudRenderer.statusTexture(2), PlayerStatusHudRenderer.statusTexture(3), PlayerStatusHudRenderer.statusTexture(4), PlayerStatusHudRenderer.statusTexture(5), PlayerStatusHudRenderer.statusTexture(6), PlayerStatusHudRenderer.statusTexture(7), PlayerStatusHudRenderer.statusTexture(8)};

    private PlayerStatusHudRenderer() {
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!((Boolean)LocknarConfig.PLAYER_STATUS_HUD_ENABLED.get()).booleanValue()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null || mc.options.hideGui || mc.player == null || mc.level == null || mc.screen != null) {
            return;
        }
        if (mc.player.isSpectator()) {
            return;
        }
        int index = PlayerStatusHudRenderer.getStatusTextureIndex(mc);
        if (index < 1 || index >= STATUS_TEXTURES.length) {
            return;
        }
        PlayerStatusHudRenderer.renderStatusTexture(event.getGuiGraphics(), mc, STATUS_TEXTURES[index]);
    }

    private static int getStatusTextureIndex(Minecraft mc) {
        boolean highReady;
        ItemStack mainHand = mc.player.getMainHandItem();
        boolean holdingTaczGun = TaczGunCompat.isTaczGun(mainHand);
        boolean wearingChestArmor = !mc.player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        boolean sneaking = mc.player.isShiftKeyDown();
        boolean bl = highReady = holdingTaczGun && TaczHighReadyClient.isLocalHighReadyActive();
        if (!holdingTaczGun) {
            return wearingChestArmor ? 7 : 8;
        }
        if (highReady) {
            return wearingChestArmor ? 6 : 3;
        }
        if (wearingChestArmor) {
            return sneaking ? 5 : 1;
        }
        return sneaking ? 4 : 2;
    }

    private static void renderStatusTexture(GuiGraphics guiGraphics, Minecraft mc, ResourceLocation texture) {
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int x = 8;
        int y = screenHeight - 96 - 8;
        float scaleX = (float)HUD_WIDTH / 461.0f;
        float scaleY = 0.16931216f;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float[] tint = PlayerStatusHudRenderer.getHealthTint(mc);
        RenderSystem.setShaderColor((float)tint[0], (float)tint[1], (float)tint[2], (float)1.0f);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float)x, (float)y, 0.0f);
        guiGraphics.pose().scale(scaleX, scaleY, 1.0f);
        guiGraphics.blit(texture, 0, 0, 0.0f, 0.0f, 461, 567, 461, 567);
        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private static float[] getHealthTint(Minecraft mc) {
        float health = mc.player.getHealth();
        if (health >= 20.0f) {
            return new float[]{1.0f, 1.0f, 1.0f};
        }
        if (health <= 6.0f) {
            return new float[]{0.654902f, 0.0f, 0.011764706f};
        }
        float t = (20.0f - health) / 14.0f;
        float r = PlayerStatusHudRenderer.lerp(1.0f, 0.654902f, t);
        float g = PlayerStatusHudRenderer.lerp(1.0f, 0.0f, t);
        float b = PlayerStatusHudRenderer.lerp(1.0f, 0.011764706f, t);
        return new float[]{r, g, b};
    }

    private static float lerp(float from, float to, float t) {
        return from + (to - from) * t;
    }

    private static ResourceLocation statusTexture(int index) {
        return ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "textures/gui/status/" + index + ".png");
    }
}
