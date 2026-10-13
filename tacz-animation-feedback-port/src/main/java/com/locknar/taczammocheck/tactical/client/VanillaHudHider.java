package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

public final class VanillaHudHider {
    private static final Set<String> HIDDEN_VANILLA_OVERLAYS = Set.of("player_health", "armor_level", "food_level", "air_level", "mount_health", "vehicle_health", "jump_bar", "experience_bar", "potion_icons");

    private VanillaHudHider() {
    }

    public static void onRenderGuiOverlayPre(RenderGuiLayerEvent.Pre event) {
        if (!((Boolean)LocknarConfig.HIDE_VANILLA_HUD.get()).booleanValue()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null || mc.options.hideGui || mc.player == null) {
            return;
        }
        ResourceLocation overlayId = event.getName();
        if (overlayId == null) {
            return;
        }
        String path = overlayId.getPath();
        if (overlayId.getNamespace().equals("minecraft") && HIDDEN_VANILLA_OVERLAYS.contains(path)) {
            ((net.neoforged.bus.api.ICancellableEvent)event).setCanceled(true);
        }
    }
}
