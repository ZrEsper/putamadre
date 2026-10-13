package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;

public final class TaczProneReloadLegClient {
    private static final ResourceLocation TACZ_ONCE_UPPER = ResourceLocation.fromNamespaceAndPath("tacz", "once_upper_animation");
    private static final float MOVE_THRESHOLD = 0.025f;
    private static boolean playerAnimatorReflectionResolved;
    private static boolean playerAnimatorReflectionAvailable;
    private static Method getAssociatedDataMethod;
    private static boolean taczReflectionResolved;
    private static boolean taczReflectionAvailable;
    private static Method gunOperatorFromLivingEntityMethod;
    private static Method getSynReloadStateMethod;
    private static Method timelessGetGunDisplayMethod;
    private static Method assetManagerGetMethod;
    private static Method assetManagerGetAnimationsMethod;
    private static Class<?> keyframeAnimationPlayerClass;

    private TaczProneReloadLegClient() {
    }

    public static void applyAfterSetup(AbstractClientPlayer player, PlayerModel model, float limbSwing, float limbSwingAmount, float ageInTicks) {
        if (player == null || model == null || player.isSpectator()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (player == mc.player && mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        if (!TaczProneReloadLegClient.isGroundProne(player)) {
            return;
        }
        if (!TaczGunCompat.isTaczGun(ThirdPersonWeaponSwitchClient.getRenderedMainHand((LivingEntity)player))) {
            return;
        }
        boolean movementIntent = TaczProneReloadLegClient.hasLocalMovementIntent(player);
        float movement = Math.abs(limbSwingAmount);
        if (movement < 0.025f && !movementIntent && player.getDeltaMovement().horizontalDistanceSqr() < 8.0E-5) {
            return;
        }
        if (!TaczProneReloadLegClient.isTaczReloading(player)) {
            return;
        }
        float strength = Mth.clamp((float)Math.max(movement, movementIntent ? 0.58f : 0.34f), (float)0.0f, (float)1.0f);
        float phase = ageInTicks * 0.72f;
        float sin = Mth.sin((float)phase);
        float cos = Mth.cos((float)phase);
        float hipPitch = 0.48f * strength;
        float hipYaw = 0.16f * strength;
        float hipRoll = 0.1f * strength;
        model.leftLeg.xRot += sin * hipPitch;
        model.rightLeg.xRot -= sin * hipPitch;
        model.leftLeg.yRot += cos * hipYaw;
        model.rightLeg.yRot -= cos * hipYaw;
        model.leftLeg.zRot += cos * hipRoll;
        model.rightLeg.zRot -= cos * hipRoll;
        float slideZ = cos * 0.24f * strength;
        float slideY = Math.abs(sin) * 0.06f * strength;
        model.leftLeg.z += slideZ;
        model.rightLeg.z -= slideZ;
        model.leftLeg.y += slideY;
        model.rightLeg.y += 0.06f * strength - slideY;
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
    }

    public static boolean isTaczReloading(AbstractClientPlayer player) {
        if (player == null) {
            return false;
        }
        Boolean byState = TaczProneReloadLegClient.queryTaczReloadState(player);
        if (byState != null) {
            return byState;
        }
        return TaczProneReloadLegClient.isTaczReloadAnimationActive(player);
    }

    public static void handleProneExitDuringReload(AbstractClientPlayer player) {
        if (player == null || !TaczProneReloadLegClient.isTaczReloading(player)) {
            return;
        }
        TaczProneReloadLegClient.switchOnceUpperToStandingReload(player);
    }

    private static boolean isGroundProne(AbstractClientPlayer player) {
        return player.getPose() == Pose.SWIMMING && player.onGround() && !player.isInWaterOrBubble();
    }

    private static boolean hasLocalMovementIntent(AbstractClientPlayer player) {
        LocalPlayer local;
        block3: {
            block2: {
                if (!(player instanceof LocalPlayer)) break block2;
                local = (LocalPlayer)player;
                if (local.input != null) break block3;
            }
            return false;
        }
        return Math.abs(local.input.forwardImpulse) > 0.05f || Math.abs(local.input.leftImpulse) > 0.05f;
    }

    private static Boolean queryTaczReloadState(AbstractClientPlayer player) {
        try {
            Boolean b;
            TaczProneReloadLegClient.resolveTaczReflection();
            if (!taczReflectionAvailable || gunOperatorFromLivingEntityMethod == null || getSynReloadStateMethod == null) {
                return null;
            }
            Object operator = gunOperatorFromLivingEntityMethod.invoke(null, player);
            if (operator == null) {
                return null;
            }
            Object reloadState = getSynReloadStateMethod.invoke(operator, new Object[0]);
            if (reloadState == null) {
                return null;
            }
            Object stateType = TaczProneReloadLegClient.invokeNoArg(reloadState, "getStateType");
            if (stateType == null) {
                return null;
            }
            Object value = TaczProneReloadLegClient.invokeNoArg(stateType, "isReloading");
            return value instanceof Boolean ? (b = (Boolean)value) : null;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static void switchOnceUpperToStandingReload(AbstractClientPlayer player) {
        try {
            int oldStop;
            TaczProneReloadLegClient.resolvePlayerAnimatorReflection();
            TaczProneReloadLegClient.resolveTaczReflection();
            if (!playerAnimatorReflectionAvailable || getAssociatedDataMethod == null) {
                return;
            }
            Object associatedData = getAssociatedDataMethod.invoke(null, player);
            Object onceLayer = TaczProneReloadLegClient.lookupAssociatedData(associatedData, TACZ_ONCE_UPPER);
            if (onceLayer == null) {
                return;
            }
            Object oldAnimation = TaczProneReloadLegClient.invokeNoArg(onceLayer, "getAnimation");
            String oldName = TaczProneReloadLegClient.findAnimationName(oldAnimation, 0);
            if (oldName != null && !oldName.toLowerCase(Locale.ROOT).contains("lie_reload")) {
                return;
            }
            int oldTick = TaczProneReloadLegClient.intValue(TaczProneReloadLegClient.invokeNoArg(oldAnimation, "getCurrentTick"), TaczProneReloadLegClient.intValue(TaczProneReloadLegClient.invokeNoArg(oldAnimation, "getTick"), 0));
            double progress = Mth.clamp((double)((double)oldTick / (double)Math.max(1, oldStop = TaczProneReloadLegClient.intValue(TaczProneReloadLegClient.invokeNoArg(oldAnimation, "getStopTick"), Math.max(1, oldTick + 1)))), (double)0.0, (double)0.98);
            Object newPlayer = TaczProneReloadLegClient.createStandingReloadPlayer(player, progress);
            if (newPlayer != null) {
                if (!TaczProneReloadLegClient.invokeSingleArgMethod(onceLayer, "setAnimation", newPlayer)) {
                    TaczProneReloadLegClient.invokeSingleArgMethod(onceLayer, "setAnimation", null);
                }
            } else {
                TaczProneReloadLegClient.invokeSingleArgMethod(onceLayer, "setAnimation", null);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static Object createStandingReloadPlayer(AbstractClientPlayer player, double normalizedProgress) {
        try {
            Class<?>[] p;
            if (!taczReflectionAvailable || timelessGetGunDisplayMethod == null || assetManagerGetMethod == null || assetManagerGetAnimationsMethod == null || keyframeAnimationPlayerClass == null) {
                return null;
            }
            ItemStack gun = ThirdPersonWeaponSwitchClient.getRenderedMainHand((LivingEntity)player);
            Object displayOptional = timelessGetGunDisplayMethod.invoke(null, gun);
            Object display = TaczProneReloadLegClient.optionalValue(displayOptional);
            if (display == null) {
                return null;
            }
            Object animator3rd = TaczProneReloadLegClient.invokeNoArg(display, "getPlayerAnimator3rd");
            if (!(animator3rd instanceof ResourceLocation)) {
                return null;
            }
            ResourceLocation animatorId = (ResourceLocation)animator3rd;
            Object assetManager = assetManagerGetMethod.invoke(null, new Object[0]);
            if (assetManager == null) {
                return null;
            }
            Object targetOptional = assetManagerGetAnimationsMethod.invoke(assetManager, animatorId, "reload_upper");
            Object targetData = TaczProneReloadLegClient.optionalValue(targetOptional);
            if (targetData == null) {
                return null;
            }
            int targetStop = TaczProneReloadLegClient.intValue(TaczProneReloadLegClient.readField(targetData, "stopTick"), 0);
            if (targetStop <= 0) {
                Object stop = TaczProneReloadLegClient.invokeNoArg(targetData, "getStopTick");
                targetStop = TaczProneReloadLegClient.intValue(stop, 1);
            }
            int targetTick = Math.max(0, Math.min(Math.max(0, targetStop - 1), (int)Math.round(normalizedProgress * (double)Math.max(1, targetStop))));
            for (Constructor<?> ctor : keyframeAnimationPlayerClass.getConstructors()) {
                p = ctor.getParameterTypes();
                if (p.length != 2 || p[1] != Integer.TYPE || !p[0].isInstance(targetData)) continue;
                ctor.setAccessible(true);
                return ctor.newInstance(targetData, targetTick);
            }
            for (Constructor<?> ctor : keyframeAnimationPlayerClass.getDeclaredConstructors()) {
                p = ctor.getParameterTypes();
                if (p.length != 2 || p[1] != Integer.TYPE || !p[0].isInstance(targetData)) continue;
                ctor.setAccessible(true);
                return ctor.newInstance(targetData, targetTick);
            }
            return null;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static void resolveTaczReflection() {
        if (taczReflectionResolved) {
            return;
        }
        taczReflectionResolved = true;
        try {
            ClassLoader loader = TaczProneReloadLegClient.class.getClassLoader();
            Class<?> gunOperator = Class.forName("com.tacz.guns.api.entity.IGunOperator", false, loader);
            gunOperatorFromLivingEntityMethod = TaczProneReloadLegClient.findCompatibleStaticMethod(gunOperator, "fromLivingEntity", LivingEntity.class);
            getSynReloadStateMethod = TaczProneReloadLegClient.findNoArgMethod(gunOperator, "getSynReloadState");
            Class<?> timeless = Class.forName("com.tacz.guns.api.TimelessAPI", false, loader);
            timelessGetGunDisplayMethod = TaczProneReloadLegClient.findCompatibleStaticMethod(timeless, "getGunDisplay", ItemStack.class);
            Class<?> assetManager = Class.forName("com.tacz.guns.compat.playeranimator.animation.PlayerAnimatorAssetManager", false, loader);
            assetManagerGetMethod = TaczProneReloadLegClient.findCompatibleStaticMethod(assetManager, "get", new Class[0]);
            assetManagerGetAnimationsMethod = TaczProneReloadLegClient.findMethodBySignature(assetManager, "getAnimations", ResourceLocation.class, String.class);
            keyframeAnimationPlayerClass = Class.forName("dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer", false, loader);
            taczReflectionAvailable = gunOperatorFromLivingEntityMethod != null && getSynReloadStateMethod != null;
        }
        catch (Throwable ignored) {
            taczReflectionAvailable = false;
        }
    }

    private static void resolvePlayerAnimatorReflection() {
        if (playerAnimatorReflectionResolved) {
            return;
        }
        playerAnimatorReflectionResolved = true;
        try {
            Class<?> access = Class.forName("dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess", false, TaczProneReloadLegClient.class.getClassLoader());
            getAssociatedDataMethod = access.getMethod("getPlayerAssociatedData", AbstractClientPlayer.class);
            getAssociatedDataMethod.setAccessible(true);
            playerAnimatorReflectionAvailable = true;
        }
        catch (Throwable ignored) {
            playerAnimatorReflectionAvailable = false;
            getAssociatedDataMethod = null;
        }
    }

    private static Object lookupAssociatedData(Object associatedData, ResourceLocation key) {
        if (associatedData == null || key == null) {
            return null;
        }
        if (associatedData instanceof Map) {
            Map map = (Map)associatedData;
            return map.get(key);
        }
        try {
            Method method = associatedData.getClass().getMethod("get", ResourceLocation.class);
            method.setAccessible(true);
            return method.invoke(associatedData, key);
        }
        catch (Throwable ignored) {
            for (Method method : associatedData.getClass().getMethods()) {
                Class<?> parameter;
                if (!"get".equals(method.getName()) || method.getParameterCount() != 1 || !(parameter = method.getParameterTypes()[0]).isAssignableFrom(key.getClass()) && !parameter.isInstance(key)) continue;
                try {
                    method.setAccessible(true);
                    return method.invoke(associatedData, key);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
            }
            return null;
        }
    }

    public static boolean isTaczReloadAnimationActive(AbstractClientPlayer player) {
        try {
            Boolean value;
            TaczProneReloadLegClient.resolvePlayerAnimatorReflection();
            if (!playerAnimatorReflectionAvailable || getAssociatedDataMethod == null) {
                return false;
            }
            Object associatedData = getAssociatedDataMethod.invoke(null, player);
            Object layer = TaczProneReloadLegClient.lookupAssociatedData(associatedData, TACZ_ONCE_UPPER);
            if (layer == null) {
                return false;
            }
            Object animation = TaczProneReloadLegClient.invokeNoArg(layer, "getAnimation");
            if (animation == null) {
                return false;
            }
            Object active = TaczProneReloadLegClient.invokeNoArg(animation, "isActive");
            if (active instanceof Boolean && !(value = (Boolean)active).booleanValue()) {
                return false;
            }
            String name = TaczProneReloadLegClient.findAnimationName(animation, 0);
            if (name == null) {
                return false;
            }
            return name.toLowerCase(Locale.ROOT).contains("reload");
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static String findAnimationName(Object object, int depth) {
        String[] fields;
        String[] methods;
        if (object == null || depth > 4) {
            return null;
        }
        Object data = TaczProneReloadLegClient.invokeNoArg(object, "getData");
        String fromData = TaczProneReloadLegClient.extractNameFromData(data);
        if (fromData != null) {
            return fromData;
        }
        for (String method : methods = new String[]{"getAnimation", "getCurrentAnimation", "getAnim", "getTargetAnimation", "getNewAnimation"}) {
            String value;
            Object nested = TaczProneReloadLegClient.invokeNoArg(object, method);
            if (nested == null || nested == object || (value = TaczProneReloadLegClient.findAnimationName(nested, depth + 1)) == null) continue;
            return value;
        }
        for (String fieldName : fields = new String[]{"animation", "currentAnimation", "anim", "target", "newAnimation"}) {
            String value;
            Object nested = TaczProneReloadLegClient.readField(object, fieldName);
            if (nested == null || nested == object || (value = TaczProneReloadLegClient.findAnimationName(nested, depth + 1)) == null) continue;
            return value;
        }
        return null;
    }

    private static String extractNameFromData(Object data) {
        Map map;
        Object value;
        if (data == null) {
            return null;
        }
        Object extraData = TaczProneReloadLegClient.readField(data, "extraData");
        if (!(extraData instanceof Map)) {
            extraData = TaczProneReloadLegClient.invokeNoArg(data, "getExtraData");
        }
        if (extraData instanceof Map && (value = (map = (Map)extraData).get("name")) != null) {
            return String.valueOf(value);
        }
        return null;
    }

    private static Object optionalValue(Object optional) {
        Boolean b;
        if (optional instanceof Optional) {
            Optional opt = (Optional)optional;
            return opt.orElse(null);
        }
        if (optional == null) {
            return null;
        }
        Object present = TaczProneReloadLegClient.invokeNoArg(optional, "isPresent");
        if (present instanceof Boolean && !(b = (Boolean)present).booleanValue()) {
            return null;
        }
        Object value = TaczProneReloadLegClient.invokeNoArg(optional, "get");
        return value == optional ? null : value;
    }

    private static Method findCompatibleStaticMethod(Class<?> type, String name, Class<?> ... desiredArgs) {
        if (type == null) {
            return null;
        }
        for (Method method : type.getMethods()) {
            Class<?>[] p;
            if (!name.equals(method.getName()) || !Modifier.isStatic(method.getModifiers()) || (p = method.getParameterTypes()).length != desiredArgs.length) continue;
            boolean ok = true;
            for (int i = 0; i < p.length; ++i) {
                if (p[i].isAssignableFrom(desiredArgs[i]) || desiredArgs[i].isAssignableFrom(p[i])) continue;
                ok = false;
                break;
            }
            if (!ok) continue;
            method.setAccessible(true);
            return method;
        }
        return null;
    }

    private static Method findNoArgMethod(Class<?> type, String name) {
        if (type == null) {
            return null;
        }
        try {
            Method method = type.getMethod(name, new Class[0]);
            method.setAccessible(true);
            return method;
        }
        catch (Throwable ignored) {
            try {
                Method method = type.getDeclaredMethod(name, new Class[0]);
                method.setAccessible(true);
                return method;
            }
            catch (Throwable ignored2) {
                return null;
            }
        }
    }

    private static Method findMethodBySignature(Class<?> type, String name, Class<?> ... params) {
        if (type == null) {
            return null;
        }
        try {
            Method method = type.getMethod(name, params);
            method.setAccessible(true);
            return method;
        }
        catch (Throwable ignored) {
            try {
                Method method = type.getDeclaredMethod(name, params);
                method.setAccessible(true);
                return method;
            }
            catch (Throwable ignored2) {
                return null;
            }
        }
    }

    private static boolean invokeSingleArgMethod(Object target, String name, Object value) {
        if (target == null) {
            return false;
        }
        for (Method method : target.getClass().getMethods()) {
            if (!name.equals(method.getName()) || method.getParameterCount() != 1) continue;
            Class<?> p = method.getParameterTypes()[0];
            if (value != null && !p.isInstance(value) && !p.isAssignableFrom(value.getClass())) continue;
            try {
                method.setAccessible(true);
                method.invoke(target, value);
                return true;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return false;
    }

    private static Object invokeNoArg(Object target, String methodName) {
        if (target == null) {
            return null;
        }
        try {
            Method method = target.getClass().getMethod(methodName, new Class[0]);
            method.setAccessible(true);
            return method.invoke(target, new Object[0]);
        }
        catch (Throwable ignored) {
            try {
                Method method = target.getClass().getDeclaredMethod(methodName, new Class[0]);
                method.setAccessible(true);
                return method.invoke(target, new Object[0]);
            }
            catch (Throwable ignored2) {
                return null;
            }
        }
    }

    private static Object readField(Object target, String fieldName) {
        if (target == null) {
            return null;
        }
        for (Class<?> type = target.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            }
            catch (Throwable ignored) {
                continue;
            }
        }
        return null;
    }

    private static int intValue(Object value, int fallback) {
        int n;
        if (value instanceof Number) {
            Number n2 = (Number)value;
            n = n2.intValue();
        } else {
            n = fallback;
        }
        return n;
    }
}
