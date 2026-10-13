package com.locknar.taczammocheck.tactical.mixin;

import com.locknar.taczammocheck.client.AmmoCheckThirdPersonClient;
import com.locknar.taczammocheck.client.PlayerBodyRenderContext;
import com.locknar.taczammocheck.prone.ProneClient;
import com.locknar.taczammocheck.tactical.client.DirectionalLegAnimationClient;
import com.locknar.taczammocheck.tactical.client.GunRunAnimationClient;
import com.locknar.taczammocheck.tactical.client.KickAnimationClient;
import com.locknar.taczammocheck.tactical.client.ProneTacticalAnimationClient;
import com.locknar.taczammocheck.tactical.client.TaczProneReloadLegClient;
import com.locknar.taczammocheck.tactical.client.TaczWallObstructionThirdPersonClient;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerModel.class}, priority=500)
public abstract class PlayerModelMixin
extends HumanoidModel<LivingEntity> {
    public PlayerModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method={"setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="TAIL")}, require=1)
    private void locknarDoorKick$setupAnimNamed(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (entity instanceof AbstractClientPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer)entity;
            this.locknarDoorKick$applyTaczWallPose(player, limbSwing, limbSwingAmount, ageInTicks);
        }
    }

    private void locknarDoorKick$applyTaczWallPose(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks) {
        if (!PlayerBodyRenderContext.active(player, (PlayerModel)(Object)this)) {
            return;
        }
        TaczWallObstructionThirdPersonClient.applyConfiguredPoseAfterSetup(player, (PlayerModel)(Object)this);
        ThirdPersonWeaponSwitchClient.applyPoseAfterSetup(player, (PlayerModel)(Object)this);
        DirectionalLegAnimationClient.applyPoseAfterSetup(player, (PlayerModel)(Object)this);
        GunRunAnimationClient.applyPoseAfterSetup(player, (PlayerModel)(Object)this);
        AmmoCheckThirdPersonClient.apply(player, (PlayerModel)(Object)this);
        ProneTacticalAnimationClient.applyPoseAfterSetup(player, (PlayerModel)(Object)this);
        if (!ProneClient.active(player)) {
            TaczProneReloadLegClient.applyAfterSetup(player, (PlayerModel)(Object)this, limbSwing, limbSwingAmount, ageInTicks);
        }
        KickAnimationClient.applyBackKickPoseAfterSetup(player, (PlayerModel)(Object)this);
    }
}
