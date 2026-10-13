package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.LocknarDoorKickMod;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;

public final class PlayerAnimatorBridge {
    private static final ResourceLocation KICK_LAYER_ID = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "kick_layer");
    private static final ResourceLocation TACZ_WALL_LAYER_ID = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "tacz_wall_layer");
    private static final ResourceLocation KICK_ANIMATION_ID = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "kick_door");
    private static final ResourceLocation TACZ_WALL_LONG_ANIMATION_ID = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "tacz_wall_long");
    private static final ResourceLocation TACZ_WALL_PISTOL_ANIMATION_ID = ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "tacz_wall_pistol");
    private static boolean initialized = false;
    private static boolean available = false;
    private static Class<?> playerAnimationFactoryClass;
    private static Class<?> modifierLayerClass;
    private static Class<?> iAnimationClass;
    private static Class<?> keyframeAnimationClass;
    private static Class<?> keyframeAnimationPlayerClass;
    private static Class<?> playerAnimationAccessClass;
    private static Class<?> playerAnimationRegistryClass;

    private PlayerAnimatorBridge() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        if (!ModList.get().isLoaded("playeranimator")) {
            try {
                Class.forName("dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory");
            }
            catch (Throwable ignored) {
                LocknarDoorKickMod.LOGGER.info("player-animation-lib not found. Using native fallbacks where available.");
                return;
            }
        }
        try {
            playerAnimationFactoryClass = Class.forName("dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory");
            modifierLayerClass = Class.forName("dev.kosmx.playerAnim.api.layered.ModifierLayer");
            iAnimationClass = Class.forName("dev.kosmx.playerAnim.api.layered.IAnimation");
            keyframeAnimationClass = Class.forName("dev.kosmx.playerAnim.core.data.KeyframeAnimation");
            keyframeAnimationPlayerClass = Class.forName("dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer");
            playerAnimationAccessClass = Class.forName("dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess");
            playerAnimationRegistryClass = Class.forName("dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry");
            PlayerAnimatorBridge.registerLayerFactory(KICK_LAYER_ID, 1500);
            PlayerAnimatorBridge.registerLayerFactory(TACZ_WALL_LAYER_ID, 1450);
            available = true;
            LocknarDoorKickMod.LOGGER.info("player-animation-lib bridge enabled for locknar_doorkick.");
        }
        catch (Throwable throwable) {
            available = false;
            LocknarDoorKickMod.LOGGER.warn("Failed to initialize player-animation-lib bridge. Native fallback will be used.", throwable);
        }
    }

    private static void registerLayerFactory(ResourceLocation layerId, int priority) throws ReflectiveOperationException {
        Field factoryField = playerAnimationFactoryClass.getField("ANIMATION_DATA_FACTORY");
        Object factoryHolder = factoryField.get(null);
        Object factoryProxy = Proxy.newProxyInstance(PlayerAnimatorBridge.class.getClassLoader(), new Class[]{playerAnimationFactoryClass}, (proxy, method, args) -> {
            if ("toString".equals(method.getName())) {
                return "locknar_doorkick_layer_factory_" + String.valueOf(layerId);
            }
            if ("hashCode".equals(method.getName())) {
                return System.identityHashCode(proxy);
            }
            if ("equals".equals(method.getName())) {
                return proxy == args[0];
            }
            Class<?> returnType = method.getReturnType();
            if (returnType == Void.TYPE) {
                return null;
            }
            if (iAnimationClass.isAssignableFrom(returnType) || returnType == Object.class) {
                return modifierLayerClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            return null;
        });
        Method registerFactory = factoryHolder.getClass().getMethod("registerFactory", ResourceLocation.class, Integer.TYPE, playerAnimationFactoryClass);
        registerFactory.invoke(factoryHolder, layerId, priority, factoryProxy);
    }

    public static boolean isAvailable() {
        return available;
    }

    public static void playKickForEntity(int entityId) {
        PlayerAnimatorBridge.playAnimationForEntity(entityId, KICK_LAYER_ID, KICK_ANIMATION_ID, false);
    }

    public static void playTaczWallForEntity(int entityId, boolean pistol) {
        PlayerAnimatorBridge.playAnimationForEntity(entityId, TACZ_WALL_LAYER_ID, pistol ? TACZ_WALL_PISTOL_ANIMATION_ID : TACZ_WALL_LONG_ANIMATION_ID, false);
    }

    public static void stopTaczWallForEntity(int entityId) {
        PlayerAnimatorBridge.clearLayerForEntity(entityId, TACZ_WALL_LAYER_ID);
    }

    private static void playAnimationForEntity(int entityId, ResourceLocation layerId, ResourceLocation animationId, boolean warnEveryTime) {
        if (!available) {
            return;
        }
        try {
            AbstractClientPlayer player;
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.level == null) {
                return;
            }
            Entity entity = mc.level.getEntity(entityId);
            if (!(entity instanceof AbstractClientPlayer) || (player = (AbstractClientPlayer)entity).isSpectator()) {
                return;
            }
            Object layer = PlayerAnimatorBridge.getLayer(player, layerId);
            if (layer == null) {
                return;
            }
            Object animation = PlayerAnimatorBridge.getAnimation(animationId);
            if (animation == null) {
                if (warnEveryTime) {
                    LocknarDoorKickMod.LOGGER.warn("PlayerAnimator animation '{}' was not loaded.", (Object)animationId);
                }
                return;
            }
            Constructor<?> constructor = keyframeAnimationPlayerClass.getConstructor(keyframeAnimationClass);
            Object playerAnimation = constructor.newInstance(animation);
            Method setAnimation = modifierLayerClass.getMethod("setAnimation", iAnimationClass);
            setAnimation.invoke(layer, playerAnimation);
        }
        catch (Throwable throwable) {
            LocknarDoorKickMod.LOGGER.warn("Failed to play player-animation-lib animation '{}' for entity {}.", new Object[]{animationId, entityId, throwable});
        }
    }

    private static void clearLayerForEntity(int entityId, ResourceLocation layerId) {
        if (!available) {
            return;
        }
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.level == null) {
                return;
            }
            Entity entity = mc.level.getEntity(entityId);
            if (!(entity instanceof AbstractClientPlayer)) {
                return;
            }
            AbstractClientPlayer player = (AbstractClientPlayer)entity;
            Object layer = PlayerAnimatorBridge.getLayer(player, layerId);
            if (layer == null) {
                return;
            }
            Method setAnimation = modifierLayerClass.getMethod("setAnimation", iAnimationClass);
            setAnimation.invoke(layer, new Object[]{null});
        }
        catch (Throwable throwable) {
            LocknarDoorKickMod.LOGGER.debug("Failed to clear player-animation-lib layer '{}' for entity {}.", new Object[]{layerId, entityId, throwable});
        }
    }

    private static Object getLayer(AbstractClientPlayer player, ResourceLocation layerId) throws ReflectiveOperationException {
        Method getPlayerAssociatedData = playerAnimationAccessClass.getMethod("getPlayerAssociatedData", AbstractClientPlayer.class);
        Object associatedData = getPlayerAssociatedData.invoke(null, player);
        Method get = associatedData.getClass().getMethod("get", ResourceLocation.class);
        return get.invoke(associatedData, layerId);
    }

    private static Object getAnimation(ResourceLocation animationId) throws ReflectiveOperationException {
        Method getAnimation = playerAnimationRegistryClass.getMethod("getAnimation", ResourceLocation.class);
        return getAnimation.invoke(null, animationId);
    }
}
