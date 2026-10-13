package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.locknar.taczammocheck.tactical.client.DoorAction;
import com.locknar.taczammocheck.tactical.client.DoorHudFeedbackClient;
import com.locknar.taczammocheck.tactical.client.DoorTarget;
import com.locknar.taczammocheck.tactical.client.MirrorViewClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public final class HudRenderer {
    private HudRenderer() {
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null || mc.options.hideGui || mc.player == null || mc.level == null || mc.screen != null) {
            return;
        }
        if (!DoorInteractionGlobalState.isClientEnabled()) {
            DoorHudFeedbackClient.clear();
            return;
        }
        if (MirrorViewClient.isBusy()) {
            return;
        }
        if (DoorHudFeedbackClient.renderFlashIfActive(event, mc)) {
            return;
        }
        DoorAction action = DoorTarget.getLookingDoorAction(mc);
        if (action == DoorAction.NONE) {
            return;
        }
        if (action == DoorAction.OPEN || action == DoorAction.KICK) {
            if (!DoorHudFeedbackClient.isDoorHudSuppressed()) {
                boolean backKick = action == DoorAction.KICK && DoorTarget.isLookingBackKickCandidate(mc);
                DoorHudFeedbackClient.renderIdleHud(event, mc, action, backKick);
            }
            return;
        }
        MutableComponent text = Component.translatable((String)"hud.locknar_doorkick.mirror");
        Font font = mc.font;
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        int x = (screenWidth - font.width((FormattedText)text)) / 2;
        int y = screenHeight / 2 + 16;
        event.getGuiGraphics().drawString(font, (Component)text, x, y, 0x66CCFF, true);
    }
}
