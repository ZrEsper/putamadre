package com.locknar.taczammocheck.tactical.client;

import java.lang.reflect.Method;
import java.util.Optional;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public final class TaczThirdPersonPoseCompat {
    private static boolean resolved;
    private static Method getGunDisplay;
    private static Method getThirdPersonAnimation;
    private static Method getAnimation;
    private static Method animateGunHold;

    private TaczThirdPersonPoseCompat() {
    }

    public static void applyGunHoldPose(AbstractClientPlayer player, PlayerModel model, ItemStack gun) {
        if (player == null || model == null || gun == null || gun.isEmpty()) {
            return;
        }
        TaczThirdPersonPoseCompat.resolve();
        if (getGunDisplay == null || getThirdPersonAnimation == null || getAnimation == null || animateGunHold == null) {
            return;
        }
        try {
            Optional optional;
            Object optionalValue = getGunDisplay.invoke(null, gun);
            if (!(optionalValue instanceof Optional) || (optional = (Optional)optionalValue).isEmpty()) {
                return;
            }
            Object display = optional.get();
            String animationName = String.valueOf(getThirdPersonAnimation.invoke(display, new Object[0]));
            Object animation = getAnimation.invoke(null, animationName);
            if (animation == null) {
                return;
            }
            animateGunHold.invoke(animation, player, model.rightArm, model.leftArm, model.body, model.head);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static synchronized void resolve() {
        if (resolved) {
            return;
        }
        resolved = true;
        try {
            Class<?> timelessApi = Class.forName("com.tacz.guns.api.TimelessAPI");
            getGunDisplay = timelessApi.getMethod("getGunDisplay", ItemStack.class);
            Class<?> displayClass = Class.forName("com.tacz.guns.client.resource.GunDisplayInstance");
            getThirdPersonAnimation = displayClass.getMethod("getThirdPersonAnimation", new Class[0]);
            Class<?> managerClass = Class.forName("com.tacz.guns.api.client.other.ThirdPersonManager");
            getAnimation = managerClass.getMethod("getAnimation", String.class);
            Class<?> animationClass = Class.forName("com.tacz.guns.api.client.other.IThirdPersonAnimation");
            animateGunHold = animationClass.getMethod("animateGunHold", LivingEntity.class, ModelPart.class, ModelPart.class, ModelPart.class, ModelPart.class);
        }
        catch (Throwable ignored) {
            getGunDisplay = null;
            getThirdPersonAnimation = null;
            getAnimation = null;
            animateGunHold = null;
        }
    }
}
