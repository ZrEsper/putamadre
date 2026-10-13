package com.locknar.taczammocheck.mixin;

import com.locknar.taczammocheck.client.AmmoCheckBedrockAnimationPlayer;
import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.locknar.taczammocheck.client.AmmoCheckHudAnchor;
import com.locknar.taczammocheck.client.TaczAmmoCheckModelHooks;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Predicate;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets={"com.tacz.guns.client.model.BedrockGunModel"}, remap=false, priority=850)
public abstract class TaczBedrockGunModelAmmoCheckMixin {
    @Unique
    private ItemDisplayContext locknar$ammoCheckContext;
    @Unique
    private ItemStack locknar$ammoCheckGun;

    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/client/renderer/RenderType;II)V"}, at={@At(value="HEAD")}, require=0)
    private void locknar$taczAmmoCheck$beforeAmmoCheck(PoseStack matrixStack, ItemStack gunItem, ItemDisplayContext transformType, RenderType renderType, int light, int overlay, CallbackInfo ci) {
        this.locknar$ammoCheckContext = transformType;
        this.locknar$ammoCheckGun = gunItem;
        TaczAmmoCheckModelHooks.beforeGunRender(this, matrixStack, gunItem, transformType);
    }

    @Inject(method={"ammoHiddenRender(Lcom/tacz/guns/client/model/bedrock/BedrockPart;Ljava/util/function/Predicate;)Lcom/tacz/guns/client/model/IFunctionalRenderer;"}, at={@At(value="RETURN")}, require=0)
    private void locknar$ammoCheck$cartridgeVisibility(@Coerce Object part, Predicate<?> predicate, CallbackInfoReturnable<Object> cir) {
        if (this.locknar$ammoCheckContext == null || !AmmoCheckClient.shouldApplyNativeBedrockAmmoCheck(this.locknar$ammoCheckContext, this.locknar$ammoCheckGun)) {
            return;
        }
        try {
            String name = (String)part.getClass().getField("name").get(part);
            Boolean visible = AmmoCheckBedrockAnimationPlayer.inspectionCartridgeVisibility(name);
            if (visible != null) {
                part.getClass().getField("visible").setBoolean(part, visible);
            }
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
    }

    @Inject(method={"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lnet/minecraft/client/renderer/RenderType;II)V"}, at={@At(value="RETURN")}, require=0)
    private void locknar$taczAmmoCheck$afterAmmoCheck(PoseStack matrixStack, ItemStack gunItem, ItemDisplayContext transformType, RenderType renderType, int light, int overlay, CallbackInfo ci) {
        TaczAmmoCheckModelHooks.renderDetachedMagazine(this, matrixStack, gunItem, transformType, renderType, light, overlay);
        TaczAmmoCheckModelHooks.afterGunRender(this, matrixStack);
        AmmoCheckHudAnchor.capture(this, matrixStack, gunItem, transformType);
        this.locknar$ammoCheckContext = null;
        this.locknar$ammoCheckGun = null;
    }
}
