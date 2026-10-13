package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.client.FrontSlingGunRenderLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class ClientRenderLayers {
    private ClientRenderLayers() {
    }

    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        ClientRenderLayers.add((PlayerRenderer)event.getSkin(net.minecraft.client.resources.PlayerSkin.Model.WIDE));
        ClientRenderLayers.add((PlayerRenderer)event.getSkin(net.minecraft.client.resources.PlayerSkin.Model.SLIM));
    }

    private static void add(PlayerRenderer renderer) {
        if (renderer != null) {
            renderer.addLayer((RenderLayer)new FrontSlingGunRenderLayer((RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>)renderer));
        }
    }
}
