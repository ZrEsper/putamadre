package com.locknar.taczammocheck.tactical.mixin;

import com.locknar.taczammocheck.client.PlayerBodyRenderContext;
import com.locknar.taczammocheck.tactical.client.ProneTacticalAnimationClient;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerRenderer.class})
public abstract class PlayerRendererMixin {
    @Inject(method={"render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="HEAD")})
    private void locknar$begin(AbstractClientPlayer p, float yaw, float partial, PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo ci) {
        PlayerBodyRenderContext.begin(p, (PlayerModel)((PlayerRenderer)(Object)this).getModel());
        ProneTacticalAnimationClient.beginPlayerRendererRender(p, partial, pose);
    }

    @Inject(method={"render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="RETURN")})
    private void locknar$end(AbstractClientPlayer p, float yaw, float partial, PoseStack pose, MultiBufferSource buffer, int light, CallbackInfo ci) {
        ProneTacticalAnimationClient.endPlayerRendererRender(p);
        PlayerBodyRenderContext.end(p, (PlayerModel)((PlayerRenderer)(Object)this).getModel());
    }

    @Inject(method={"setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void locknar$root(AbstractClientPlayer p, PoseStack pose, float age, float yaw, float partial, float scale, CallbackInfo ci) {
        if (ProneTacticalAnimationClient.applyLockedStationaryProneRotations(p, pose, partial)) {
            ci.cancel();
        }
    }
}
