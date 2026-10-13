package com.locknar.taczammocheck.tactical.mixin;

import com.locknar.taczammocheck.LocknarConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.tacz.guns.client.renderer.other.HumanoidOffhandRender"}, remap=false)
public abstract class TaczHotbarGunRenderMixin {
    @Inject(method={"renderHotbarGun(Lnet/minecraft/world/entity/LivingEntity;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private static void locknarDoorKick$disableOriginalHotbarGun(LivingEntity entity, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (((Boolean)LocknarConfig.FRONT_SLING_GUN_ENABLED.get()).booleanValue() && entity instanceof AbstractClientPlayer) {
            ci.cancel();
        }
    }
}
