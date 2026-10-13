package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.client.DoorAction;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class DoorHudFeedbackClient {
    private static final ResourceLocation OPEN_IDLE_TEXTURE = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "textures/gui/open_idle.png");
    private static final ResourceLocation OPEN_FLASH_TEXTURE = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "textures/gui/open_flash.png");
    private static final ResourceLocation KICK_IDLE_TEXTURE = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "textures/gui/kick_idle.png");
    private static final ResourceLocation KICK_FLASH_TEXTURE = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "textures/gui/kick_flash.png");
    private static final ResourceLocation BACK_KICK_IDLE_TEXTURE = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "textures/gui/back_kick_idle.png");
    private static final ResourceLocation BACK_KICK_FLASH_TEXTURE = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "textures/gui/back_kick_flash.png");
    private static final long GUI_COOLDOWN_MS = 1000L;
    private static final long FLASH_ON_MS = 90L;
    private static final long FLASH_OFF_MS = 70L;
    private static final int FLASH_COUNT = 2;
    private static final int TEXTURE_SIZE = 64;
    private static final float ICON_SCALE = 0.5f;
    private static final int ICON_SIZE = 32;
    private static final int ICON_OFFSET_Y = 12;
    private static final int KEY_TEXT_GAP = 3;
    private static long hudSuppressUntilMs = 0L;
    private static DoorAction flashAction = DoorAction.NONE;
    private static boolean flashBackKick = false;
    private static long flashStartedAtMs = 0L;

    private DoorHudFeedbackClient() {
    }

    public static void triggerActionFeedback(DoorAction action) {
        DoorHudFeedbackClient.triggerActionFeedback(action, false);
    }

    public static void triggerActionFeedback(DoorAction action, boolean backKick) {
        if (action != DoorAction.OPEN && action != DoorAction.KICK) {
            return;
        }
        long now = Util.getMillis();
        hudSuppressUntilMs = now + 1000L;
        flashAction = action;
        flashBackKick = action == DoorAction.KICK && backKick;
        flashStartedAtMs = now;
    }

    public static boolean isDoorHudSuppressed() {
        return Util.getMillis() < hudSuppressUntilMs;
    }

    public static void clear() {
        hudSuppressUntilMs = 0L;
        flashAction = DoorAction.NONE;
        flashStartedAtMs = 0L;
        flashBackKick = false;
    }

    public static boolean renderFlashIfActive(RenderGuiEvent.Post event, Minecraft mc) {
        if (mc == null) {
            return false;
        }
        long now = Util.getMillis();
        if (flashAction == DoorAction.NONE) {
            return false;
        }
        long elapsed = now - flashStartedAtMs;
        long phaseMs = 160L;
        long totalMs = 2L * phaseMs;
        if (elapsed >= totalMs) {
            flashAction = DoorAction.NONE;
            flashBackKick = false;
            return false;
        }
        long phaseOffset = elapsed % phaseMs;
        if (phaseOffset >= 90L) {
            return false;
        }
        ResourceLocation texture = flashAction == DoorAction.KICK ? (flashBackKick ? BACK_KICK_FLASH_TEXTURE : KICK_FLASH_TEXTURE) : OPEN_FLASH_TEXTURE;
        DoorHudFeedbackClient.renderCenteredIcon(event.getGuiGraphics(), mc, texture);
        return true;
    }

    public static void renderIdleHud(RenderGuiEvent.Post event, Minecraft mc, DoorAction action) {
        DoorHudFeedbackClient.renderIdleHud(event, mc, action, false);
    }

    public static void renderIdleHud(RenderGuiEvent.Post event, Minecraft mc, DoorAction action, boolean backKick) {
        if (mc == null || action != DoorAction.OPEN && action != DoorAction.KICK) {
            return;
        }
        ResourceLocation texture = action == DoorAction.KICK ? (backKick ? BACK_KICK_IDLE_TEXTURE : KICK_IDLE_TEXTURE) : OPEN_IDLE_TEXTURE;
        GuiGraphics guiGraphics = event.getGuiGraphics();
        DoorHudFeedbackClient.renderCenteredIcon(guiGraphics, mc, texture);
        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int centerX = screenWidth / 2;
        int iconTopY = screenHeight / 2 + 12;
        int keyY = iconTopY + 32 + 3;
        String keyText = "F";
        int keyX = centerX - font.width(keyText) / 2;
        guiGraphics.drawString(font, keyText, keyX, keyY, 0xFFFFFF, true);
    }

    private static void renderCenteredIcon(GuiGraphics guiGraphics, Minecraft mc, ResourceLocation texture) {
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int centerX = screenWidth / 2;
        int iconX = centerX - 16;
        int iconY = screenHeight / 2 + 12;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate((float)iconX, (float)iconY, 0.0f);
        guiGraphics.pose().scale(0.5f, 0.5f, 1.0f);
        guiGraphics.blit(texture, 0, 0, 0.0f, 0.0f, 64, 64, 64, 64);
        guiGraphics.pose().popPose();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}
