package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.TaczWallObstructionCommon;
import com.locknar.taczammocheck.tactical.client.TaczHighReadyClient;
import com.locknar.taczammocheck.tactical.client.TaczKickRaiseClient;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.lang.reflect.Method;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

public final class TaczWallObstructionClient {
    private static float currentProgress;
    private static float previousProgress;
    private static TaczGunCompat.GunKind currentKind;
    private static String currentGunId;
    private static int lastBlockedTicks;
    private static long lastFirstPersonModelHookMs;
    private static long lastFirstPersonRenderHandHookMs;
    private static long lastFirstPersonWrapperHookMs;
    private static boolean suppressedAttackHold;
    private static int restoreAttackClickCooldown;
    private static int forceHeldAttackTicks;

    private TaczWallObstructionClient() {
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        previousProgress = currentProgress;
        boolean obstructedNow = TaczWallObstructionClient.computeObstructedNow();
        if (obstructedNow) {
            currentProgress = Math.min(1.0f, currentProgress + 0.22f);
            lastBlockedTicks = 4;
        } else {
            currentProgress = Math.max(0.0f, currentProgress - 0.28f);
            if (lastBlockedTicks > 0) {
                --lastBlockedTicks;
            }
        }
        TaczWallObstructionClient.repairAttackKeyAfterObstruction(TaczWallObstructionClient.isObstructedForShooting());
        TaczKickRaiseClient.cleanup();
    }

    private static boolean computeObstructedNow() {
        if (!((Boolean)LocknarConfig.TACZ_WALL_OBSTRUCTION_ENABLED.get()).booleanValue()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null || mc.screen != null || mc.player.isSpectator()) {
            return false;
        }
        ItemStack mainHand = mc.player.getMainHandItem();
        TaczGunCompat.GunInfo info = TaczGunCompat.getGunInfo(mainHand);
        if (info == null) {
            currentGunId = "";
            currentKind = TaczGunCompat.GunKind.RIFLE;
            return false;
        }
        currentGunId = info.gunId();
        currentKind = info.kind();
        return TaczWallObstructionCommon.isMuzzleObstructed((LivingEntity)mc.player, info.length());
    }

    public static boolean isObstructed() {
        return currentProgress > 0.35f || lastBlockedTicks > 0;
    }

    public static boolean isObstructedForShooting() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return false;
        }
        if (TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) == null) {
            return false;
        }
        if (TaczHighReadyClient.isLocalHighReadyForShooting()) {
            return true;
        }
        return TaczWallObstructionClient.isObstructed() || TaczKickRaiseClient.isKickRaiseActive();
    }

    public static boolean handleRawMouse(long windowPointer, int button, int action, int modifiers) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null || mc.screen != null) {
            return false;
        }
        if (windowPointer != mc.getWindow().getWindow()) {
            return false;
        }
        if (button != 0) {
            return false;
        }
        if (TaczWallObstructionClient.isObstructedForShooting() && (action == 1 || action == 2)) {
            suppressedAttackHold = true;
            TaczWallObstructionClient.forceAttackKeyDown(false);
            return false;
        }
        if (action == 0) {
            suppressedAttackHold = false;
            forceHeldAttackTicks = 0;
            TaczWallObstructionClient.forceAttackKeyDown(false);
        }
        return false;
    }

    private static void repairAttackKeyAfterObstruction(boolean obstructedNow) {
        boolean physicalLeftDown;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null || mc.options == null) {
            suppressedAttackHold = false;
            forceHeldAttackTicks = 0;
            return;
        }
        long window = mc.getWindow().getWindow();
        boolean bl = physicalLeftDown = GLFW.glfwGetMouseButton((long)window, (int)0) == 1;
        if (obstructedNow) {
            if (physicalLeftDown) {
                suppressedAttackHold = true;
            }
            TaczWallObstructionClient.forceAttackKeyDown(false);
            restoreAttackClickCooldown = 0;
            forceHeldAttackTicks = 0;
            return;
        }
        if (!physicalLeftDown) {
            suppressedAttackHold = false;
            restoreAttackClickCooldown = 0;
            forceHeldAttackTicks = 0;
            TaczWallObstructionClient.forceAttackKeyDown(false);
            return;
        }
        if (suppressedAttackHold) {
            TaczWallObstructionClient.forceAttackKeyDown(true);
            TaczWallObstructionClient.clickAttackKeyOnce();
            restoreAttackClickCooldown = 4;
            forceHeldAttackTicks = 6;
            suppressedAttackHold = false;
            return;
        }
        if (forceHeldAttackTicks > 0) {
            --forceHeldAttackTicks;
            TaczWallObstructionClient.forceAttackKeyDown(true);
            return;
        }
        if (restoreAttackClickCooldown > 0) {
            --restoreAttackClickCooldown;
            TaczWallObstructionClient.forceAttackKeyDown(true);
        }
    }

    private static void forceAttackKeyDown(boolean down) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null) {
            return;
        }
        try {
            InputConstants.Key key = mc.options.keyAttack.getKey();
            if (key != null) {
                KeyMapping.set((InputConstants.Key)key, (boolean)down);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            mc.options.keyAttack.setDown(down);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void clickAttackKeyOnce() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null) {
            return;
        }
        try {
            InputConstants.Key key = mc.options.keyAttack.getKey();
            if (key != null) {
                KeyMapping.click((InputConstants.Key)key);
                return;
            }
        }
        catch (Throwable key) {
            // empty catch block
        }
        try {
            Method getKey = mc.options.keyAttack.getClass().getMethod("getKey", new Class[0]);
            Object key = getKey.invoke((Object)mc.options.keyAttack, new Object[0]);
            Method click = KeyMapping.class.getMethod("click", InputConstants.Key.class);
            click.invoke(null, key);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public static void onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null || mc.screen != null) {
            return;
        }
        if (TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) != null) {
            return;
        }
        if (TaczWallObstructionClient.applyActiveFirstPersonGunPoseTransform(event.getPoseStack(), mc.player.getMainHandItem(), null, event.getPartialTick())) {
            TaczWallObstructionClient.markFirstPersonRenderHandHookActive();
        }
    }

    public static boolean hasActiveFirstPersonGunPose(ItemStack gunStack, ItemDisplayContext transformType, float partialTick) {
        float highReadyProgress;
        float kickRaiseProgress;
        if (transformType != null && !transformType.firstPerson()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null || mc.screen != null) {
            return false;
        }
        ItemStack stack = gunStack == null || gunStack.isEmpty() ? mc.player.getMainHandItem() : gunStack;
        TaczGunCompat.GunInfo info = TaczGunCompat.getGunInfo(stack);
        if (info == null) {
            return false;
        }
        float safePartial = Mth.clamp((float)partialTick, (float)0.0f, (float)1.0f);
        float wallProgress = Mth.lerp((float)safePartial, (float)previousProgress, (float)currentProgress);
        return Math.max(wallProgress, Math.max(kickRaiseProgress = TaczKickRaiseClient.getKickRaiseProgress(), highReadyProgress = TaczHighReadyClient.getLocalHighReadyProgress(safePartial))) > 0.01f;
    }

    public static boolean applyActiveFirstPersonGunPoseTransform(PoseStack poseStack, ItemStack gunStack, ItemDisplayContext transformType, float partialTick) {
        float highReadyProgress;
        float kickRaiseProgress;
        if (poseStack == null) {
            return false;
        }
        if (transformType != null && !transformType.firstPerson()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null || mc.screen != null) {
            return false;
        }
        ItemStack stack = gunStack == null || gunStack.isEmpty() ? mc.player.getMainHandItem() : gunStack;
        TaczGunCompat.GunInfo info = TaczGunCompat.getGunInfo(stack);
        if (info == null) {
            return false;
        }
        float safePartial = Mth.clamp((float)partialTick, (float)0.0f, (float)1.0f);
        float wallProgress = Mth.lerp((float)safePartial, (float)previousProgress, (float)currentProgress);
        float progress = Math.max(wallProgress, Math.max(kickRaiseProgress = TaczKickRaiseClient.getKickRaiseProgress(), highReadyProgress = TaczHighReadyClient.getLocalHighReadyProgress(safePartial)));
        if (progress <= 0.01f) {
            return false;
        }
        TaczWallObstructionClient.applyMuzzleUpTransform(poseStack, wallProgress, kickRaiseProgress, highReadyProgress, info.kind());
        return true;
    }

    public static void markFirstPersonModelHookActive() {
        lastFirstPersonModelHookMs = System.currentTimeMillis();
    }

    public static void markFirstPersonRenderHandHookActive() {
        lastFirstPersonRenderHandHookMs = System.currentTimeMillis();
    }

    public static boolean isFirstPersonRenderHandHookRecentlyActive() {
        return System.currentTimeMillis() - lastFirstPersonRenderHandHookMs < 120L;
    }

    public static void markFirstPersonWrapperHookActive() {
        lastFirstPersonWrapperHookMs = System.currentTimeMillis();
    }

    public static boolean isFirstPersonWrapperHookRecentlyActive() {
        return System.currentTimeMillis() - lastFirstPersonWrapperHookMs < 120L;
    }

    private static boolean isFirstPersonModelHookRecentlyActive() {
        return System.currentTimeMillis() - lastFirstPersonModelHookMs < 1500L;
    }

    private static void applyMuzzleUpTransform(PoseStack poseStack, float wallProgress, float kickRaiseProgress, float highReadyProgress, TaczGunCompat.GunKind kind) {
        boolean pistol;
        float wall = TaczWallObstructionClient.smooth(wallProgress);
        float kick = TaczWallObstructionClient.smooth(kickRaiseProgress);
        float highReady = TaczWallObstructionClient.smooth(highReadyProgress);
        float p = Math.max(wall, Math.max(kick, highReady));
        boolean bl = pistol = kind == TaczGunCompat.GunKind.PISTOL;
        if (pistol) {
            double readyLike = Math.max(kick, highReady);
            double x = 0.015 + (Double)LocknarConfig.WALL_PISTOL_RIGHT_OFFSET.get() * (double)wall + (Double)LocknarConfig.HIGH_READY_PISTOL_RIGHT_OFFSET.get() * readyLike;
            double y = -0.065 + (Double)LocknarConfig.WALL_PISTOL_VERTICAL_OFFSET.get() * (double)wall + (Double)LocknarConfig.HIGH_READY_PISTOL_VERTICAL_OFFSET.get() * readyLike;
            double z = -0.035 + (Double)LocknarConfig.WALL_PISTOL_BACK_OFFSET.get() * (double)wall + (Double)LocknarConfig.HIGH_READY_PISTOL_BACK_OFFSET.get() * readyLike;
            poseStack.translate(x * (double)p, y * (double)p, z * (double)p);
            poseStack.mulPose(Axis.XP.rotationDegrees(28.0f * p));
            poseStack.mulPose(Axis.YP.rotationDegrees(1.5f * p));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-2.0f * p));
        } else {
            double readyLike = Math.max(kick, highReady);
            double x = 0.02 + (Double)LocknarConfig.WALL_LONG_RIGHT_OFFSET.get() * (double)wall + (Double)LocknarConfig.HIGH_READY_LONG_RIGHT_OFFSET.get() * readyLike;
            double y = -0.115 + (Double)LocknarConfig.WALL_LONG_VERTICAL_OFFSET.get() * (double)wall + (Double)LocknarConfig.HIGH_READY_LONG_VERTICAL_OFFSET.get() * readyLike;
            double z = -0.075 + (Double)LocknarConfig.WALL_LONG_BACK_OFFSET.get() * (double)wall + (Double)LocknarConfig.HIGH_READY_LONG_BACK_OFFSET.get() * readyLike;
            poseStack.translate(x * (double)p, y * (double)p, z * (double)p);
            poseStack.mulPose(Axis.XP.rotationDegrees(54.0f * p));
            poseStack.mulPose(Axis.YP.rotationDegrees(3.0f * p));
            poseStack.mulPose(Axis.ZP.rotationDegrees(-7.0f * p));
        }
    }

    private static float smooth(float value) {
        float x = Mth.clamp((float)value, (float)0.0f, (float)1.0f);
        return x * x * (3.0f - 2.0f * x);
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
    }

    public static void clearClientState() {
        currentProgress = 0.0f;
        previousProgress = 0.0f;
        currentKind = TaczGunCompat.GunKind.RIFLE;
        currentGunId = "";
        lastBlockedTicks = 0;
        suppressedAttackHold = false;
        restoreAttackClickCooldown = 0;
        forceHeldAttackTicks = 0;
        lastFirstPersonModelHookMs = 0L;
        lastFirstPersonRenderHandHookMs = 0L;
        lastFirstPersonWrapperHookMs = 0L;
        TaczWallObstructionClient.forceAttackKeyDown(false);
    }

    public static String debugGunId() {
        return currentGunId;
    }

    static {
        currentKind = TaczGunCompat.GunKind.RIFLE;
        currentGunId = "";
    }
}
