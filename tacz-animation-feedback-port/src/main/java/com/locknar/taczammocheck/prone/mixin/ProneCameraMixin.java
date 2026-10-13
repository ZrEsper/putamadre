package com.locknar.taczammocheck.prone.mixin;

import com.locknar.taczammocheck.prone.ProneClient;
import com.locknar.taczammocheck.prone.ProneNetwork;
import com.locknar.taczammocheck.tactical.client.ProneTacticalAnimationConfigClient;
import net.minecraft.client.Camera;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Camera.class})
public abstract class ProneCameraMixin {
    @Shadow
    private float eyeHeight;
    @Shadow
    private float eyeHeightOld;

    @Shadow
    protected abstract void setPosition(double var1, double var3, double var5);

    @Shadow
    protected abstract void setRotation(float var1, float var2);

    @Shadow
    protected abstract void move(float var1, float var3, float var5);

    @Shadow
    private float getMaxZoom(float distance) {
        throw new AssertionError();
    }

    @Inject(method={"setup"}, at={@At(value="TAIL")}, require=1)
    private void locknar$proneCamera(BlockGetter level, Entity entity, boolean detached, boolean mirror, float partial, CallbackInfo ci) {
        if(dev.zomboid.taczfeedback.ThirdPersonAimClient.isIso()) return;
        double prone;
        boolean completed;
        if (!(entity instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)entity;
        ProneNetwork.Sync state = ProneClient.state(player);
        boolean bl = completed = state == null && ProneClient.suppressVanillaRecovery((LivingEntity)player);
        if (state == null && !completed) {
            return;
        }
        double d = prone = completed ? 0.0 : 1.0;
        if (state != null && (state.phase() == 1 || state.phase() == 3)) {
            boolean entering = state.phase() == 1;
            ProneTacticalAnimationConfigClient.Motion motion = entering ? ProneTacticalAnimationConfigClient.Motion.PRONE_ENTER : ProneTacticalAnimationConfigClient.Motion.PRONE_EXIT;
            double progress = ProneClient.progress(player, partial);
            if (!entering) {
                progress = Mth.clamp((double)((progress - 0.24) / 0.76), (double)0.0, (double)1.0);
            }
            ProneTacticalAnimationConfigClient.Sample sample = ProneTacticalAnimationConfigClient.sample(motion, progress);
            prone = Mth.clamp((double)((double)(entering ? 1 : 0) + sample.root().rotX / 90.0), (double)0.0, (double)1.0);
        }
        double standing = completed && player.isCrouching() ? 1.27 : 1.62;
        double desired = Mth.lerp((double)prone, (double)standing, (double)0.4);
        this.eyeHeight = this.eyeHeightOld = (float)desired;
        if (detached) {
            float original = player.getViewXRot(partial) * (float)(mirror ? -1 : 1);
            float pitch = mirror ? Mth.lerp((float)((float)prone), (float)original, (float)Math.max(8.0f, original)) : original;
            this.setPosition(Mth.lerp((double)partial, (double)player.xo, (double)player.getX()), Mth.lerp((double)partial, (double)player.yo, (double)player.getY()) + (mirror ? Mth.lerp((double)prone, (double)standing, (double)0.55) : desired), Mth.lerp((double)partial, (double)player.zo, (double)player.getZ()));
            this.setRotation(player.getViewYRot(partial) + (float)(mirror ? 180 : 0), pitch);
            this.move(-this.getMaxZoom(4.0f), 0.0f, 0.0f);
            return;
        }
        this.setPosition(Mth.lerp((double)partial, (double)player.xo, (double)player.getX()), Mth.lerp((double)partial, (double)player.yo, (double)player.getY()) + desired, Mth.lerp((double)partial, (double)player.zo, (double)player.getZ()));
    }
}
