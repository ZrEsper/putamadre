package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.DoorInteractionGlobalState;
import com.locknar.taczammocheck.tactical.LocknarDoorKickMod;
import com.locknar.taczammocheck.tactical.ModItems;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public final class MirrorViewClient {
    private static Vec3 cameraPos = Vec3.ZERO;
    private static float baseYaw = 0.0f;
    private static float basePitch = 0.0f;
    private static float startPlayerYaw = 0.0f;
    private static float startPlayerPitch = 0.0f;
    private static boolean active = false;
    private static boolean useProgressActive = false;
    private static int useProgressTotal = 0;
    private static int useProgressRemaining = 0;
    private static int useProgressWaitingTicks = 0;
    private static boolean cancelProgressActive = false;
    private static int cancelProgressTotal = 0;
    private static int cancelProgressRemaining = 0;
    private static int lockedHotbarSlot = -1;
    private static int cancelGraceTicks = 0;
    private static int mirrorReuseCooldownTicks = 0;
    private static Method setPositionMethod;
    private static boolean setPositionResolved;

    private MirrorViewClient() {
    }

    public static void startUseProgress(int totalTicks) {
        if (!DoorInteractionGlobalState.isClientEnabled()) {
            MirrorViewClient.stop();
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        int safeTicks = Math.max(1, totalTicks);
        active = false;
        cancelProgressActive = false;
        useProgressActive = true;
        useProgressTotal = safeTicks;
        useProgressRemaining = safeTicks;
        useProgressWaitingTicks = 0;
        MirrorViewClient.lockCurrentHotbarSlot(mc);
        MirrorViewClient.playMirrorSound(SoundEvents.SPYGLASS_USE, 0.85f, 0.95f);
    }

    public static void start(double x, double y, double z, float yaw, float pitch, int durationTicks) {
        if (!DoorInteractionGlobalState.isClientEnabled()) {
            MirrorViewClient.stop();
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        boolean wasUsingProgress = useProgressActive;
        cameraPos = new Vec3(x, y, z);
        baseYaw = yaw;
        basePitch = pitch;
        active = true;
        useProgressActive = false;
        cancelProgressActive = false;
        cancelProgressTotal = 0;
        cancelProgressRemaining = 0;
        cancelGraceTicks = 4;
        MirrorViewClient.lockCurrentHotbarSlot(mc);
        if (mc != null && mc.player != null) {
            startPlayerYaw = mc.player.getYRot();
            startPlayerPitch = mc.player.getXRot();
        } else {
            startPlayerYaw = yaw;
            startPlayerPitch = pitch;
        }
        if (!wasUsingProgress) {
            MirrorViewClient.playMirrorSound(SoundEvents.SPYGLASS_USE, 0.85f, 0.95f);
        }
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean isBusy() {
        return active || useProgressActive || cancelProgressActive;
    }

    public static boolean isMirrorReuseCoolingDown() {
        return mirrorReuseCooldownTicks > 0;
    }

    public static boolean shouldBlockRawDropKey(int key, int scanCode, int action) {
        if (!MirrorViewClient.isBusy()) {
            return false;
        }
        if (action != 1 && action != 2) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        return mc != null && mc.options != null && mc.options.keyDrop.matches(key, scanCode);
    }

    public static boolean handleRawEscapeKey(int key, int scanCode, int action) {
        if (!active) {
            return false;
        }
        if (key != 256) {
            return false;
        }
        if (action != 1 && action != 2) {
            return false;
        }
        if (cancelGraceTicks > 0) {
            return true;
        }
        if (!cancelProgressActive) {
            MirrorViewClient.startCancelProgress();
        }
        return true;
    }

    public static void stop() {
        active = false;
        useProgressActive = false;
        cancelProgressActive = false;
        useProgressTotal = 0;
        useProgressRemaining = 0;
        useProgressWaitingTicks = 0;
        cancelProgressTotal = 0;
        cancelProgressRemaining = 0;
        cancelGraceTicks = 0;
        lockedHotbarSlot = -1;
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        mirrorReuseCooldownTicks = 0;
        if (!MirrorViewClient.isBusy()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null || mc.player.isSpectator() || mc.player.isDeadOrDying()) {
            if (MirrorViewClient.isBusy()) {
                ModNetwork.sendMirrorCancel();
            }
            MirrorViewClient.stop();
            return;
        }
        MirrorViewClient.freezePlayerInput(mc);
        MirrorViewClient.blockInventoryAndItemSwitch(mc);
        if (!MirrorViewClient.isHoldingMirrorGun(mc)) {
            MirrorViewClient.stop();
            return;
        }
        if (useProgressActive) {
            if (useProgressRemaining > 0) {
                --useProgressRemaining;
            } else if (++useProgressWaitingTicks > 10) {
                MirrorViewClient.stop();
            }
            while (mc.options != null && mc.options.keyUse.consumeClick()) {
            }
            return;
        }
        if (!active) {
            return;
        }
        if (cancelGraceTicks > 0) {
            --cancelGraceTicks;
            while (mc.options != null && mc.options.keyUse.consumeClick()) {
            }
            return;
        }
        if (cancelProgressActive) {
            while (mc.options != null && mc.options.keyUse.consumeClick()) {
            }
            if (--cancelProgressRemaining <= 0) {
                MirrorViewClient.finishMirrorCancel();
            }
            return;
        }
        if (mc.options != null && mc.options.keyUse.consumeClick()) {
            MirrorViewClient.startCancelProgress();
        }
    }

    public static void onRenderHand(RenderHandEvent event) {
        if (active) {
            ((net.neoforged.bus.api.ICancellableEvent)event).setCanceled(true);
        }
    }

    public static void onMovementInputUpdate(MovementInputUpdateEvent event) {
        if (!MirrorViewClient.isBusy() || event.getInput() == null) {
            return;
        }
        event.getInput().leftImpulse = 0.0f;
        event.getInput().forwardImpulse = 0.0f;
        event.getInput().jumping = false;
        event.getInput().shiftKeyDown = false;
    }

    public static void onRightClickBlockClient(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        if (!MirrorViewClient.isBusy()) {
            return;
        }
        if (active && !cancelProgressActive && cancelGraceTicks <= 0) {
            MirrorViewClient.startCancelProgress();
        }
        ((net.neoforged.bus.api.ICancellableEvent)event).setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (dev.zomboid.taczfeedback.ThirdPersonAimClient.isIso()) return;
        if (!active) {
            return;
        }
        Camera camera = event.getCamera();
        if (camera == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        float yaw = baseYaw;
        float pitch = basePitch;
        if (mc != null && mc.player != null) {
            float yawDelta = Mth.wrapDegrees((float)(mc.player.getYRot() - startPlayerYaw));
            float pitchDelta = mc.player.getXRot() - startPlayerPitch;
            yaw += Mth.clamp((float)yawDelta, (float)-25.0f, (float)25.0f);
            pitch += Mth.clamp((float)pitchDelta, (float)-15.0f, (float)15.0f);
            pitch = Mth.clamp((float)pitch, (float)-35.0f, (float)20.0f);
        }
        if (MirrorViewClient.setCameraPosition(camera, cameraPos)) {
            event.setYaw(yaw);
            event.setPitch(pitch);
            event.setRoll(0.0f);
        }
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!MirrorViewClient.isBusy()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null || mc.options.hideGui || mc.player == null || mc.screen != null) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        if (active && LocknarConfig.mirrorOldTvEffect()) {
            MirrorViewClient.renderOldTvEffect(graphics, screenWidth, screenHeight, mc.player.tickCount);
        }
        if (useProgressActive) {
            MirrorViewClient.renderProgressHud(graphics, mc.font, (Component)Component.translatable((String)"hud.locknar_doorkick.mirror_use_progress"), useProgressTotal, useProgressRemaining, screenWidth, screenHeight);
            return;
        }
        if (active && cancelProgressActive) {
            MirrorViewClient.renderProgressHud(graphics, mc.font, (Component)Component.translatable((String)"hud.locknar_doorkick.mirror_cancel_progress"), cancelProgressTotal, cancelProgressRemaining, screenWidth, screenHeight);
            return;
        }
        if (active) {
            MutableComponent text = Component.translatable((String)"hud.locknar_doorkick.mirror_view_cancel");
            Font font = mc.font;
            int x = (screenWidth - font.width((FormattedText)text)) / 2;
            int y = screenHeight - 34;
            graphics.drawString(font, (Component)text, x, y, 0x66CCFF, true);
        }
    }

    private static void freezePlayerInput(Minecraft mc) {
        if (mc.player == null) {
            return;
        }
        if (mc.player.input != null) {
            mc.player.input.leftImpulse = 0.0f;
            mc.player.input.forwardImpulse = 0.0f;
            mc.player.input.jumping = false;
            mc.player.input.shiftKeyDown = false;
        }
        mc.player.setDeltaMovement(0.0, mc.player.getDeltaMovement().y, 0.0);
    }

    private static void blockInventoryAndItemSwitch(Minecraft mc) {
        if (mc == null || mc.player == null || mc.options == null) {
            return;
        }
        if (lockedHotbarSlot >= 0) {
            mc.player.getInventory().selected = lockedHotbarSlot;
        }
        while (mc.options.keyInventory.consumeClick()) {
        }
        while (mc.options.keySwapOffhand.consumeClick()) {
        }
        while (mc.options.keyDrop.consumeClick()) {
        }
        if (mc.screen instanceof InventoryScreen) {
            mc.setScreen(null);
        }
    }

    private static boolean isHoldingMirrorGun(Minecraft mc) {
        return mc != null && mc.player != null && (mc.player.getMainHandItem().is((Item)ModItems.MIRROR_GUN.get()) || mc.player.getOffhandItem().is((Item)ModItems.MIRROR_GUN.get()));
    }

    private static void lockCurrentHotbarSlot(Minecraft mc) {
        if (mc != null && mc.player != null) {
            lockedHotbarSlot = mc.player.getInventory().selected;
        }
    }

    private static void startCancelProgress() {
        int ticks = LocknarConfig.mirrorCancelTicks();
        MirrorViewClient.playMirrorSound(SoundEvents.SPYGLASS_STOP_USING, 0.85f, 0.9f);
        if (ticks <= 0) {
            MirrorViewClient.stop();
            return;
        }
        cancelProgressActive = true;
        cancelProgressRemaining = cancelProgressTotal = Math.max(1, ticks);
    }

    private static void finishMirrorCancel() {
        mirrorReuseCooldownTicks = 0;
        ModNetwork.sendMirrorCancel();
        MirrorViewClient.stop();
    }

    private static void playMirrorSound(SoundEvent sound, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.player == null || sound == null) {
            return;
        }
        mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), sound, SoundSource.PLAYERS, volume, pitch, false);
    }

    private static void renderProgressHud(GuiGraphics graphics, Font font, Component text, int totalTicks, int remainingTicks, int screenWidth, int screenHeight) {
        int safeTotal = Math.max(1, totalTicks);
        float progress = 1.0f - (float)Mth.clamp((int)remainingTicks, (int)0, (int)safeTotal) / (float)safeTotal;
        int barWidth = 118;
        int barHeight = 8;
        int x = (screenWidth - barWidth) / 2;
        int y = screenHeight - 45;
        int fill = Mth.clamp((int)((int)((float)barWidth * progress)), (int)0, (int)barWidth);
        int textX = (screenWidth - font.width((FormattedText)text)) / 2;
        graphics.drawString(font, text, textX, y - 13, 0x66CCFF, true);
        graphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, -872415232);
        graphics.fill(x, y, x + barWidth, y + barHeight, -1441129958);
        graphics.fill(x, y, x + fill, y + barHeight, -10040065);
    }

    private static void renderOldTvEffect(GuiGraphics graphics, int screenWidth, int screenHeight, int tick) {
        graphics.fill(0, 0, screenWidth, screenHeight, 0x12000000);
        for (int y = 0; y < screenHeight; y += 5) {
            graphics.fill(0, y, screenWidth, y + 1, 0x24000000);
        }
        int seed = tick * 1103515245 + 12345;
        for (int i = 0; i < 28; ++i) {
            seed = seed * 1664525 + 1013904223;
            int x = MirrorViewClient.positiveMod(seed, Math.max(1, screenWidth));
            seed = seed * 1664525 + 1013904223;
            int y = MirrorViewClient.positiveMod(seed, Math.max(1, screenHeight));
            seed = seed * 1664525 + 1013904223;
            int w = 4 + MirrorViewClient.positiveMod(seed, 22);
            int color = (i & 1) == 0 ? 0x22FFFFFF : 0x22000000;
            graphics.fill(x, y, Math.min(screenWidth, x + w), Math.min(screenHeight, y + 1), color);
        }
        int edge = Math.max(10, Math.min(screenWidth, screenHeight) / 18);
        graphics.fill(0, 0, screenWidth, edge, 0x22000000);
        graphics.fill(0, screenHeight - edge, screenWidth, screenHeight, 0x22000000);
        graphics.fill(0, 0, edge, screenHeight, 0x22000000);
        graphics.fill(screenWidth - edge, 0, screenWidth, screenHeight, 0x22000000);
    }

    private static int positiveMod(int value, int mod) {
        int result = value % mod;
        return result < 0 ? result + mod : result;
    }

    private static boolean setCameraPosition(Camera camera, Vec3 pos) {
        Method method = MirrorViewClient.getSetPositionMethod();
        if (method == null) {
            return false;
        }
        try {
            method.invoke((Object)camera, pos.x, pos.y, pos.z);
            return true;
        }
        catch (Throwable throwable) {
            LocknarDoorKickMod.LOGGER.warn("Failed to move mirror gun camera.", throwable);
            MirrorViewClient.stop();
            return false;
        }
    }

    private static Method getSetPositionMethod() {
        if (setPositionResolved) {
            return setPositionMethod;
        }
        setPositionResolved = true;
        String[] candidates = new String[]{"setPosition", "setPosition"};
        for (String name : candidates) {
            try {
                Method method = Camera.class.getDeclaredMethod(name, Double.TYPE, Double.TYPE, Double.TYPE);
                method.setAccessible(true);
                setPositionMethod = method;
                return method;
            }
            catch (NoSuchMethodException method) {
            }
        }
        for (Method method : Camera.class.getDeclaredMethods()) {
            Class<?>[] params = method.getParameterTypes();
            if (method.getReturnType() != Void.TYPE || params.length != 3 || params[0] != Double.TYPE || params[1] != Double.TYPE || params[2] != Double.TYPE || !Modifier.isPrivate(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) continue;
            method.setAccessible(true);
            setPositionMethod = method;
            return method;
        }
        LocknarDoorKickMod.LOGGER.warn("Mirror gun could not find Camera#setPosition. Mirror view will be disabled on this runtime.");
        return null;
    }

    static {
        setPositionResolved = false;
    }
}
