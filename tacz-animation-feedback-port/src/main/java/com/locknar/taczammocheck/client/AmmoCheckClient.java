package com.locknar.taczammocheck.client;

import com.locknar.taczammocheck.AmmoCheckCartridgeState;
import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.ModSounds;
import com.locknar.taczammocheck.TaczGunCompat;
import com.locknar.taczammocheck.client.AmmoCheckBedrockAnimationPlayer;
import com.locknar.taczammocheck.client.AmmoCheckHudAnchor;
import com.locknar.taczammocheck.network.ModNetwork;
import com.locknar.taczammocheck.prone.ProneClient;
import com.mojang.blaze3d.platform.InputConstants;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class AmmoCheckClient {
    private static final int DEFAULT_RELOAD_KEY = 82;
    private static final long END_BLEND_MS = 180L;
    public static final String ANIMATION_AMMO_CHECK = "ammo_check";
    public static final String ANIMATION_AMMO_CHECK_EMPTY = "ammo_check_empty";
    private static final Set<String> AUTHORED_AMMO_CHECK_SOUND_PRESETS = Set.of("ai_awp", "ak47", "aa12");
    private static final Map<String, Long> AMMO_CHECK_ENTRY_BLEND_MS = Map.of("ai_awp", 480L, "ak47", 420L);
    private static UUID sessionToken = UUID.randomUUID();
    private static long reloadPressedAtMs = -1L;
    private static boolean reloadPressTracking;
    private static long reloadTrackingWindow;
    private static int reloadTrackingKey;
    private static int reloadTrackingScanCode;
    private static int reloadTrackingModifiers;
    private static long animationStartedAtMs;
    private static TaczGunCompat.AmmoInfo lastAmmoInfo;
    private static AmmoCheckCartridgeState cartridgeState;
    private static int reloadKeyRestoreTicks;
    private static boolean synthesizingReloadKeyEvent;
    private static int pendingSyntheticReloadReleaseTicks;
    private static long pendingSyntheticReloadWindow;
    private static int pendingSyntheticReloadKey;
    private static int pendingSyntheticReloadScanCode;
    private static int pendingSyntheticReloadModifiers;
    private static int animationStartSelectedSlot;
    private static String animationStartGunId;
    private static String activeNativePresetName;
    private static String activeAnimationName;
    private static long activeAnimationDurationMs;
    private static boolean halfCheckSoundPlayed;
    private static boolean endCheckSoundPlayed;
    private static long authoredSoundSampleMs;
    private static long entryBlendStartedAtMs;
    private static long entryBlendDurationMs;
    private static long releaseBlendStartedAtMs;
    private static boolean releaseStopPacketSent;
    private static long reloadAmmoCheckBlockedUntilMs;

    private AmmoCheckClient() {
    }

    public static boolean handleRawReloadKey(long windowPointer, int key, int scanCode, int action, int modifiers) {
        if (synthesizingReloadKeyEvent) {
            return false;
        }
        if (!((Boolean)LocknarConfig.AMMO_CHECK_ENABLED.get()).booleanValue()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getWindow() == null || windowPointer != mc.getWindow().getWindow()) {
            return false;
        }
        if (mc.screen != null || mc.player == null || mc.level == null) {
            return false;
        }
        if (!AmmoCheckClient.isReloadKey(key) || TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) == null || TaczGunCompat.isAmmoCheckDisabled(TaczGunCompat.getGunId(mc.player.getMainHandItem()))) {
            return false;
        }
        if (ProneClient.transitioning((AbstractClientPlayer)mc.player)) {
            return true;
        }
        if (AmmoCheckClient.isActive()) {
            return true;
        }
        if (AmmoCheckClient.isReloadAmmoCheckBlocked()) {
            reloadPressTracking = false;
            reloadPressedAtMs = -1L;
            return false;
        }
        if (action == 1) {
            reloadPressTracking = true;
            reloadPressedAtMs = System.currentTimeMillis();
            reloadTrackingWindow = windowPointer;
            reloadTrackingKey = key;
            reloadTrackingScanCode = scanCode;
            reloadTrackingModifiers = modifiers;
            return true;
        }
        if (action == 0) {
            if (reloadPressTracking && reloadTrackingKey == key) {
                long heldMs = System.currentTimeMillis() - reloadPressedAtMs;
                reloadPressTracking = false;
                reloadPressedAtMs = -1L;
                if (heldMs < LocknarConfig.holdTriggerMs()) {
                    AmmoCheckClient.triggerNormalReloadKeyClick(windowPointer, key, scanCode, modifiers);
                }
            }
            return true;
        }
        return action == 2;
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        if (!((Boolean)LocknarConfig.AMMO_CHECK_ENABLED.get()).booleanValue()) {
            reloadPressTracking = false;
            reloadPressedAtMs = -1L;
            AmmoCheckClient.stopAmmoCheck(true);
            AmmoCheckClient.restoreReloadKeyIfNeeded();
            AmmoCheckClient.releaseSyntheticReloadIfNeeded();
            return;
        }
        AmmoCheckClient.restoreReloadKeyIfNeeded();
        AmmoCheckClient.releaseSyntheticReloadIfNeeded();
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null) {
            AmmoCheckClient.clearClientState();
            return;
        }
        if (reloadPressTracking) {
            if (TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) == null || mc.screen != null || TaczGunCompat.isAmmoCheckDisabled(TaczGunCompat.getGunId(mc.player.getMainHandItem()))) {
                reloadPressTracking = false;
                reloadPressedAtMs = -1L;
            } else {
                long heldMs = System.currentTimeMillis() - reloadPressedAtMs;
                if (heldMs >= LocknarConfig.holdTriggerMs()) {
                    reloadPressTracking = false;
                    reloadPressedAtMs = -1L;
                    if (!AmmoCheckClient.isReloadAmmoCheckBlocked()) {
                        AmmoCheckClient.startAmmoCheck(mc);
                    }
                }
            }
        }
        if (animationStartedAtMs >= 0L) {
            if (mc.screen != null || !mc.player.isAlive() || mc.player.isDeadOrDying() || TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) == null || mc.player.isSpectator()) {
                AmmoCheckClient.stopAmmoCheck(true);
                return;
            }
            if (mc.player.getInventory().selected != animationStartSelectedSlot || !AmmoCheckClient.sameGunId(animationStartGunId, TaczGunCompat.getGunId(mc.player.getMainHandItem()))) {
                AmmoCheckClient.stopAmmoCheck(true);
                return;
            }
            mc.player.setSprinting(false);
            long now = System.currentTimeMillis();
            long elapsed = now - animationStartedAtMs;
            if (AmmoCheckClient.usesAuthoredCurrentAnimationSounds()) {
                AmmoCheckClient.playAuthoredAnimationSounds(elapsed);
            } else if (!halfCheckSoundPlayed && elapsed >= 500L) {
                AmmoCheckClient.playCheckSound();
                halfCheckSoundPlayed = true;
            }
            if (releaseBlendStartedAtMs >= 0L) {
                if (now - releaseBlendStartedAtMs >= 180L) {
                    AmmoCheckClient.finishAmmoCheck(false);
                }
                return;
            }
            if (elapsed > AmmoCheckClient.activeAnimationDurationMs()) {
                AmmoCheckClient.beginNaturalRelease(true, !AmmoCheckClient.usesAuthoredCurrentAnimationSounds());
            }
        }
    }

    private static void startAmmoCheck(Minecraft mc) {
        if (!((Boolean)LocknarConfig.AMMO_CHECK_ENABLED.get()).booleanValue()) {
            return;
        }
        if (mc == null || mc.player == null) {
            return;
        }
        if (TaczGunCompat.isAmmoCheckDisabled(TaczGunCompat.getGunId(mc.player.getMainHandItem()))) {
            return;
        }
        TaczGunCompat.AmmoInfo ammoInfo = TaczGunCompat.getAmmoInfo(mc.player.getMainHandItem());
        if (ammoInfo == null) {
            return;
        }
        lastAmmoInfo = ammoInfo;
        cartridgeState = TaczGunCompat.getCartridgeState(mc.player.getMainHandItem());
        animationStartSelectedSlot = mc.player.getInventory().selected;
        animationStartGunId = TaczGunCompat.getGunId(mc.player.getMainHandItem());
        activeNativePresetName = AmmoCheckBedrockAnimationPlayer.selectPreset(animationStartGunId, TaczGunCompat.getGunInfo(mc.player.getMainHandItem()).kind());
        activeAnimationName = AmmoCheckBedrockAnimationPlayer.selectOrdinaryAnimation(activeNativePresetName, cartridgeState);
        AmmoCheckBedrockAnimationPlayer.preload(activeNativePresetName, activeAnimationName);
        activeAnimationDurationMs = AmmoCheckBedrockAnimationPlayer.getPresetDurationMs(activeNativePresetName, activeAnimationName, LocknarConfig.animationMs());
        halfCheckSoundPlayed = false;
        endCheckSoundPlayed = false;
        authoredSoundSampleMs = -1L;
        sessionToken = UUID.randomUUID();
        animationStartedAtMs = System.currentTimeMillis();
        entryBlendDurationMs = activeNativePresetName == null ? 0L : AmmoCheckBedrockAnimationPlayer.getEntryBlendMs(activeNativePresetName, activeAnimationName, Math.max(0L, AMMO_CHECK_ENTRY_BLEND_MS.getOrDefault(activeNativePresetName, 0L)));
        entryBlendStartedAtMs = entryBlendDurationMs > 0L ? animationStartedAtMs : -1L;
        releaseBlendStartedAtMs = -1L;
        releaseStopPacketSent = false;
        mc.player.setSprinting(false);
        ModNetwork.sendAmmoCheckState(sessionToken, true, AmmoCheckClient.activeAnimationDurationMs(), activeAnimationName);
    }

    private static boolean usesAuthoredCurrentAnimationSounds() {
        return AmmoCheckClient.isOrdinaryAmmoCheck() && activeNativePresetName != null && (AUTHORED_AMMO_CHECK_SOUND_PRESETS.contains(activeNativePresetName) || AmmoCheckBedrockAnimationPlayer.usesAuthoredSounds(activeNativePresetName, activeAnimationName));
    }

    private static void stopAmmoCheck(boolean syncServer) {
        AmmoCheckClient.stopAmmoCheck(syncServer, false);
    }

    private static void stopAmmoCheck(boolean syncServer, boolean playEndSound) {
        if (animationStartedAtMs >= 0L && playEndSound && !endCheckSoundPlayed) {
            AmmoCheckClient.playCheckSound();
            endCheckSoundPlayed = true;
        }
        if (animationStartedAtMs >= 0L && syncServer) {
            ModNetwork.sendAmmoCheckState(sessionToken, false, 0L, activeAnimationName);
        }
        AmmoCheckClient.finishAmmoCheck(false);
    }

    private static void beginNaturalRelease(boolean syncServer, boolean playEndSound) {
        if (animationStartedAtMs < 0L) {
            return;
        }
        if (playEndSound && !endCheckSoundPlayed) {
            AmmoCheckClient.playCheckSound();
            endCheckSoundPlayed = true;
        }
        if (syncServer && !releaseStopPacketSent) {
            ModNetwork.sendAmmoCheckState(sessionToken, false, 0L, activeAnimationName);
            releaseStopPacketSent = true;
        }
        if (releaseBlendStartedAtMs < 0L) {
            releaseBlendStartedAtMs = System.currentTimeMillis();
        }
    }

    private static void finishAmmoCheck(boolean syncServer) {
        if (animationStartedAtMs >= 0L && syncServer) {
            ModNetwork.sendAmmoCheckState(sessionToken, false, 0L, activeAnimationName);
        }
        animationStartedAtMs = -1L;
        releaseBlendStartedAtMs = -1L;
        releaseStopPacketSent = false;
        lastAmmoInfo = null;
        cartridgeState = AmmoCheckCartridgeState.UNKNOWN;
        animationStartSelectedSlot = -1;
        animationStartGunId = "";
        activeNativePresetName = null;
        activeAnimationName = ANIMATION_AMMO_CHECK;
        activeAnimationDurationMs = -1L;
        AmmoCheckClient.clearEntryBlend();
        halfCheckSoundPlayed = false;
        endCheckSoundPlayed = false;
        authoredSoundSampleMs = -1L;
    }

    public static boolean isActive() {
        if (!((Boolean)LocknarConfig.AMMO_CHECK_ENABLED.get()).booleanValue()) {
            return false;
        }
        if (animationStartedAtMs < 0L) {
            return false;
        }
        long now = System.currentTimeMillis();
        long elapsed = now - animationStartedAtMs;
        if (elapsed < 0L) {
            AmmoCheckClient.stopAmmoCheck(true);
            return false;
        }
        if (releaseBlendStartedAtMs >= 0L) {
            if (now - releaseBlendStartedAtMs >= 180L) {
                AmmoCheckClient.finishAmmoCheck(false);
                return false;
            }
            return true;
        }
        if (elapsed > AmmoCheckClient.activeAnimationDurationMs()) {
            AmmoCheckClient.beginNaturalRelease(true, !AmmoCheckClient.usesAuthoredCurrentAnimationSounds());
        }
        return true;
    }

    public static boolean shouldBlockShooting() {
        if (!((Boolean)LocknarConfig.AMMO_CHECK_ENABLED.get()).booleanValue()) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        return AmmoCheckClient.isActive() && mc != null && mc.player != null && TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) != null;
    }

    private static boolean sameGunId(String a, String b) {
        String left = a == null ? "" : a;
        String right = b == null ? "" : b;
        return left.equals(right);
    }

    private static void playCheckSound() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null) {
            return;
        }
        try {
            mc.level.playLocalSound(mc.player.getX(), mc.player.getY(), mc.player.getZ(), (SoundEvent)ModSounds.CHECK.get(), SoundSource.PLAYERS, LocknarConfig.soundVolume(), 1.0f, false);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static void playAuthoredAnimationSounds(long elapsedMs) {
        float fromSeconds;
        if (!AmmoCheckClient.usesAuthoredCurrentAnimationSounds() || activeNativePresetName == null || activeAnimationName == null) {
            return;
        }
        long clampedElapsed = Math.max(0L, Math.min(AmmoCheckClient.activeAnimationDurationMs(), elapsedMs));
        float toSeconds = (float)clampedElapsed / 1000.0f;
        if (toSeconds < (fromSeconds = authoredSoundSampleMs < 0L ? -0.001f : (float)authoredSoundSampleMs / 1000.0f)) {
            authoredSoundSampleMs = clampedElapsed;
            return;
        }
        for (AmmoCheckBedrockAnimationPlayer.AnimationSoundCue cue : AmmoCheckBedrockAnimationPlayer.getSoundCuesBetween(activeNativePresetName, activeAnimationName, fromSeconds, toSeconds)) {
            ResourceLocation id = AmmoCheckClient.resolveAuthoredSoundId(cue.effect());
            if (id == null) continue;
            AmmoCheckClient.playTaczAnimationSound(id);
        }
        authoredSoundSampleMs = clampedElapsed;
    }

    private static void playTaczAnimationSound(ResourceLocation id) {
        Minecraft mc = Minecraft.getInstance();
        if (id == null || mc == null || mc.player == null || mc.level == null) {
            return;
        }
        try {
            Class<?> soundManager = Class.forName("com.tacz.guns.client.sound.SoundPlayManager");
            Method playAnimationSound = soundManager.getMethod("playAnimationSound", Entity.class, ResourceLocation.class, Float.TYPE, Float.TYPE, Integer.TYPE);
            playAnimationSound.invoke(null, mc.player, id, Float.valueOf(1.0f), Float.valueOf(1.0f), 50);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static ResourceLocation resolveAuthoredSoundId(String effect) {
        if (effect == null || effect.isBlank()) {
            return null;
        }
        String trimmed = effect.trim();
        if (trimmed.indexOf(58) >= 0) {
            return ResourceLocation.tryParse((String)trimmed);
        }
        ResourceLocation aliased = AmmoCheckClient.resolveSoundAlias(trimmed);
        if (aliased != null) {
            return aliased;
        }
        if (trimmed.startsWith("iw8_phys_mag_drop_")) {
            return ResourceLocation.tryParse((String)"tacz:g36k/g36k_mag_drop");
        }
        ResourceLocation gunId = ResourceLocation.tryParse((String)animationStartGunId);
        String namespace = gunId == null ? "tacz" : gunId.getNamespace();
        return ResourceLocation.tryParse((String)(namespace + ":" + trimmed));
    }

    private static ResourceLocation resolveSoundAlias(String effect) {
        boolean m16Family;
        if (activeNativePresetName == null || activeAnimationName == null) {
            return null;
        }
        boolean bl = m16Family = "m16a4".equals(activeNativePresetName) || "m16a1".equals(activeNativePresetName);
        if (m16Family) {
            boolean empty = ANIMATION_AMMO_CHECK_EMPTY.equals(activeAnimationName);
            return switch (effect) {
                case "iw8_phys_mag_drop_ar_poly_dirt_02" -> ResourceLocation.tryParse((String)"tacz:g36k/g36k_mag_drop");
                case "p01_ar_mike16_reload_empty_fast_end" -> {
                    if (empty) {
                        yield ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_empty_maghit");
                    }
                    yield ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_tactical_magout");
                }
                case "p01_ar_mike16_reload_magin" -> {
                    if (empty) {
                        yield ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_empty_magin");
                    }
                    yield ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_tactical_magin");
                }
                case "p01_ar_mike4_inspect_empty_handle" -> ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_empty_charge");
                case "p01_ar_mike16_inspect_boltclose" -> ResourceLocation.tryParse((String)"tacz:hk_mp5a5/hk_mp5a5_reload_empty_boltclose");
                default -> null;
            };
        }
        if ("ump45".equals(activeNativePresetName)) {
            return switch (effect) {
                case "ump45_reload_magin" -> ResourceLocation.tryParse((String)"tacz:hk_mp5a5/hk_mp5a5_reload_magin");
                case "ump45_reload_end" -> ResourceLocation.tryParse((String)"tacz:hk_mp5a5/hk_mp5a5_reload_end");
                case "ump45_reload_empty_magin" -> ResourceLocation.tryParse((String)"tacz:hk_mp5a5/hk_mp5a5_reload_empty_magin");
                case "ump45_reload_empty_fast_charge" -> ResourceLocation.tryParse((String)"tacz:g36k/g36k_reload_empty_charge");
                case "ump45_reload_empty_bolt_release" -> ResourceLocation.tryParse((String)"tacz:hk_mp5a5/hk_mp5a5_reload_empty_boltclose");
                case "ump45_reload_empty_end" -> ResourceLocation.tryParse((String)"tacz:hk_mp5a5/hk_mp5a5_reload_empty_end");
                default -> null;
            };
        }
        if ("aa12".equals(activeNativePresetName)) {
            return switch (effect) {
                case "wfoly_sh_jak12_raise" -> ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_tactical_magout");
                case "aa12_inspect_magout", "aa12_reload_fast_magout", "aa12_reload_empty_magout" -> ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_tactical_magout");
                case "aa12_inspect_mvmnt", "aa12_reload_drum_mvmnt", "aa12_reload_mvmnt" -> ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_tactical_maghit");
                case "aa12_inspect_magin", "aa12_reload_magin", "aa12_reload_empty_magin" -> ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_empty_magin");
                case "aa12_reload_maghits" -> ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_empty_maghit");
                case "wfoly_sh_jak12_reload_empty_boltpull", "aa12_reload_empty_boltpull" -> ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_empty_charge");
                case "aa12_reload_empty_fast_drum_boltforward" -> ResourceLocation.tryParse((String)"tacz:hk_mp5a5/hk_mp5a5_reload_empty_boltclose");
                case "aa12_reload_end", "aa12_reload_empty_end" -> ResourceLocation.tryParse((String)"tacz:m4a1/m4a1_reload_empty_end");
                default -> null;
            };
        }
        return null;
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
        float[] magazineAnchor = AmmoCheckHudAnchor.consume();
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.options == null || mc.options.hideGui || mc.screen != null) {
            return;
        }
        if (!(mc.options.getCameraType().isFirstPerson() && AmmoCheckClient.isActive() && AmmoCheckClient.isOrdinaryAmmoCheck())) {
            return;
        }
        AmmoCheckClient.drawAmmoCount(event, magazineAnchor);
    }

    private static void drawAmmoCount(RenderGuiEvent.Post event, float[] magazineAnchor) {
        int y;
        if (lastAmmoInfo == null) {
            return;
        }
        float timeSeconds = AmmoCheckClient.getActiveAnimationTimeSeconds(0.0f);
        if (timeSeconds < 0.7f || timeSeconds > 2.0f) {
            return;
        }
        float visible = timeSeconds < 0.82f ? AmmoCheckClient.smooth((timeSeconds - 0.7f) / 0.12f) : (timeSeconds > 1.88f ? 1.0f - AmmoCheckClient.smooth((timeSeconds - 1.88f) / 0.12f) : 1.0f);
        if (visible <= 0.01f) {
            return;
        }
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        Minecraft mc = Minecraft.getInstance();
        String text = lastAmmoInfo.current() + "/" + lastAmmoInfo.capacity();
        float textScale = 1.25f;
        int textWidth = Mth.ceil((float)((float)mc.font.width(text) * textScale));
        Objects.requireNonNull(mc.font);
        int textHeight = Mth.ceil((float)(9.0f * textScale));
        int x = magazineAnchor == null ? screenWidth / 2 + 44 : Math.round(magazineAnchor[0] * (float)screenWidth) + 6;
        int n = y = magazineAnchor == null ? screenHeight / 2 + 8 : Math.round(magazineAnchor[1] * (float)screenHeight) - textHeight - 4;
        if (magazineAnchor != null && "p90".equals(activeNativePresetName)) {
            x += 36;
            y -= 24;
        }
        x += LocknarConfig.uiOffsetX();
        y += LocknarConfig.uiOffsetY();
        x = Mth.clamp((int)x, (int)4, (int)Math.max(4, screenWidth - textWidth - 4));
        y = Mth.clamp((int)y, (int)4, (int)Math.max(4, screenHeight - textHeight - 4));
        int scaledAmmo = lastAmmoInfo.current() * 5;
        int rgb = scaledAmmo < lastAmmoInfo.capacity() ? 0xFF5555 : (scaledAmmo > lastAmmoInfo.capacity() * 4 ? 0x55FF55 : 0xFFFFFF);
        int alpha = Math.round(255.0f * visible);
        if (alpha < 4) {
            return;
        }
        GuiGraphics graphics = event.getGuiGraphics();
        graphics.pose().pushPose();
        try {
            graphics.pose().translate((float)x, (float)y, 0.0f);
            graphics.pose().scale(textScale, textScale, 1.0f);
            graphics.drawString(mc.font, text, 0, 0, alpha << 24 | rgb, true);
        }
        finally {
            graphics.pose().popPose();
        }
    }

    public static boolean shouldTransformTaczGunDuringAmmoCheck(ItemDisplayContext transformType, ItemStack gunItem) {
        return false;
    }

    public static boolean shouldApplyNativeBedrockAmmoCheck(ItemDisplayContext transformType, ItemStack gunItem) {
        return transformType != null && transformType.firstPerson() && AmmoCheckClient.isActive() && activeNativePresetName != null && TaczGunCompat.getGunInfo(gunItem) != null;
    }

    public static String getActiveNativePresetName() {
        return activeNativePresetName;
    }

    public static String getActiveAnimationName() {
        return activeAnimationName;
    }

    public static boolean isOrdinaryAmmoCheck() {
        return ANIMATION_AMMO_CHECK.equals(activeAnimationName) || ANIMATION_AMMO_CHECK_EMPTY.equals(activeAnimationName);
    }

    public static AmmoCheckCartridgeState getCartridgeState() {
        return cartridgeState;
    }

    public static boolean shouldHideBulletDuringAmmoCheck() {
        return AmmoCheckClient.isActive() && AmmoCheckClient.isOrdinaryAmmoCheck() && lastAmmoInfo != null && lastAmmoInfo.current() <= 0;
    }

    public static boolean shouldUseTaczMagazineModel(ItemDisplayContext transformType, ItemStack gunItem) {
        return false;
    }

    public static float getActiveAnimationProgress(float partialTick) {
        return AmmoCheckClient.getAnimationProgress(partialTick);
    }

    public static float getActiveAnimationTimeSeconds(float partialTick) {
        if (animationStartedAtMs < 0L) {
            return 0.0f;
        }
        long elapsed = System.currentTimeMillis() - animationStartedAtMs;
        return Math.max(0.0f, Math.min((float)AmmoCheckClient.activeAnimationDurationMs() / 1000.0f, (float)elapsed / 1000.0f));
    }

    public static float getNativeAnimationBlendWeight(float partialTick) {
        if (animationStartedAtMs < 0L) {
            return 0.0f;
        }
        float weight = 1.0f;
        if (AmmoCheckClient.isOrdinaryAmmoCheck() && entryBlendStartedAtMs >= 0L && entryBlendDurationMs > 0L) {
            long entryElapsed = System.currentTimeMillis() - entryBlendStartedAtMs;
            float entryAlpha = Mth.clamp((float)((float)entryElapsed / (float)entryBlendDurationMs), (float)0.0f, (float)1.0f);
            weight *= AmmoCheckClient.smooth(entryAlpha);
        }
        if (releaseBlendStartedAtMs >= 0L) {
            long releaseElapsed = System.currentTimeMillis() - releaseBlendStartedAtMs;
            float releaseAlpha = Mth.clamp((float)((float)releaseElapsed / 180.0f), (float)0.0f, (float)1.0f);
            weight *= 1.0f - AmmoCheckClient.smooth(releaseAlpha);
        }
        return Mth.clamp((float)weight, (float)0.0f, (float)1.0f);
    }

    private static void clearEntryBlend() {
        entryBlendStartedAtMs = -1L;
        entryBlendDurationMs = 0L;
    }

    public static float getMagazineDetachProgress(float progress) {
        float remove = AmmoCheckClient.removeProgress(progress);
        float insert = AmmoCheckClient.insertProgress(progress);
        return Math.max(remove, AmmoCheckClient.inspectHoldProgress(progress)) * (1.0f - insert);
    }

    public static boolean shouldHideMagazineInGun(float progress) {
        return false;
    }

    public static float detachedMagazineAlpha(float progress) {
        return 0.0f;
    }

    private static float getAnimationProgress(float partialTick) {
        if (animationStartedAtMs < 0L) {
            return 0.0f;
        }
        if (releaseBlendStartedAtMs >= 0L) {
            return 1.0f;
        }
        long elapsed = System.currentTimeMillis() - animationStartedAtMs;
        return Mth.clamp((float)((float)elapsed / (float)AmmoCheckClient.activeAnimationDurationMs()), (float)0.0f, (float)1.0f);
    }

    private static long activeAnimationDurationMs() {
        return activeAnimationDurationMs > 0L ? activeAnimationDurationMs : LocknarConfig.animationMs();
    }

    private static float removeProgress(float progress) {
        return AmmoCheckClient.smooth(progress / 0.16666667f);
    }

    private static float inspectHoldProgress(float progress) {
        if (progress < 0.16666667f) {
            return 0.0f;
        }
        if (progress > 0.6666667f) {
            return 0.0f;
        }
        return 1.0f;
    }

    private static float insertProgress(float progress) {
        if (progress < 0.6666667f) {
            return 0.0f;
        }
        return AmmoCheckClient.smooth((progress - 0.6666667f) / 0.33333334f);
    }

    private static float smooth(float value) {
        float x = Mth.clamp((float)value, (float)0.0f, (float)1.0f);
        return x * x * (3.0f - 2.0f * x);
    }

    private static boolean isReloadAmmoCheckBlocked() {
        return reloadAmmoCheckBlockedUntilMs > 0L && System.currentTimeMillis() < reloadAmmoCheckBlockedUntilMs;
    }

    private static void markNormalReloadStarted() {
        reloadAmmoCheckBlockedUntilMs = System.currentTimeMillis() + LocknarConfig.reloadStartBlockMs();
    }

    private static void triggerNormalReloadKeyClick(long windowPointer, int key, int scanCode, int modifiers) {
        AmmoCheckClient.markNormalReloadStarted();
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.keyboardHandler != null && mc.getWindow() != null) {
            long window;
            long l = window = windowPointer != 0L ? windowPointer : mc.getWindow().getWindow();
            if (AmmoCheckClient.sendSyntheticReloadKey(mc.keyboardHandler, window, key, scanCode, modifiers)) {
                return;
            }
        }
        try {
            InputConstants.Key rKey = InputConstants.Type.KEYSYM.getOrCreate(key);
            KeyMapping.click((InputConstants.Key)rKey);
            KeyMapping.set((InputConstants.Key)rKey, (boolean)true);
            reloadKeyRestoreTicks = 2;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        for (KeyMapping mapping : AmmoCheckClient.findLikelyReloadMappings()) {
            try {
                KeyMapping.click((InputConstants.Key)mapping.getKey());
                mapping.setDown(true);
                reloadKeyRestoreTicks = 2;
            }
            catch (Throwable throwable) {}
        }
    }

    private static boolean sendSyntheticReloadKey(KeyboardHandler keyboardHandler, long windowPointer, int key, int scanCode, int modifiers) {
        Method method = AmmoCheckClient.findKeyboardKeyPressMethod(keyboardHandler);
        if (method == null) {
            return false;
        }
        synthesizingReloadKeyEvent = true;
        try {
            method.invoke((Object)keyboardHandler, windowPointer, key, scanCode, 1, modifiers);
            pendingSyntheticReloadWindow = windowPointer;
            pendingSyntheticReloadKey = key;
            pendingSyntheticReloadScanCode = scanCode;
            pendingSyntheticReloadModifiers = modifiers;
            pendingSyntheticReloadReleaseTicks = 2;
            boolean bl = true;
            return bl;
        }
        catch (Throwable ignored) {
            pendingSyntheticReloadReleaseTicks = 0;
            boolean bl = false;
            return bl;
        }
        finally {
            synthesizingReloadKeyEvent = false;
        }
    }

    private static void releaseSyntheticReloadIfNeeded() {
        if (pendingSyntheticReloadReleaseTicks <= 0) {
            return;
        }
        if (--pendingSyntheticReloadReleaseTicks > 0) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.keyboardHandler == null) {
            return;
        }
        Method method = AmmoCheckClient.findKeyboardKeyPressMethod(mc.keyboardHandler);
        if (method == null) {
            return;
        }
        synthesizingReloadKeyEvent = true;
        try {
            method.invoke((Object)mc.keyboardHandler, pendingSyntheticReloadWindow, pendingSyntheticReloadKey, pendingSyntheticReloadScanCode, 0, pendingSyntheticReloadModifiers);
        }
        catch (Throwable throwable) {
        }
        finally {
            synthesizingReloadKeyEvent = false;
        }
    }

    private static Method findKeyboardKeyPressMethod(KeyboardHandler keyboardHandler) {
        String[] names;
        for (String name : names = new String[]{"keyPress", "keyPress"}) {
            try {
                Method method = keyboardHandler.getClass().getDeclaredMethod(name, Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
                method.setAccessible(true);
                return method;
            }
            catch (Throwable throwable) {
            }
        }
        try {
            Method method = KeyboardHandler.class.getDeclaredMethod("keyPress", Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            method.setAccessible(true);
            return method;
        }
        catch (Throwable ignored) {
            return null;
        }
    }

    private static void restoreReloadKeyIfNeeded() {
        if (reloadKeyRestoreTicks <= 0) {
            return;
        }
        if (--reloadKeyRestoreTicks > 0) {
            return;
        }
        try {
            InputConstants.Key rKey = InputConstants.Type.KEYSYM.getOrCreate(82);
            KeyMapping.set((InputConstants.Key)rKey, (boolean)false);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        for (KeyMapping mapping : AmmoCheckClient.findLikelyReloadMappings()) {
            try {
                mapping.setDown(false);
            }
            catch (Throwable throwable) {}
        }
    }

    private static List<KeyMapping> findLikelyReloadMappings() {
        ArrayList<KeyMapping> result = new ArrayList<KeyMapping>();
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.options != null && mc.options.keyMappings != null) {
                KeyMapping[] keyMappingArray = mc.options.keyMappings;
                int n = keyMappingArray.length;
                for (int i = 0; i < n; ++i) {
                    KeyMapping mapping = keyMappingArray[i];
                    if (!AmmoCheckClient.isLikelyReloadMapping(mapping) || result.contains(mapping)) continue;
                    result.add(mapping);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            for (Field field : KeyMapping.class.getDeclaredFields()) {
                if (!Map.class.isAssignableFrom(field.getType())) continue;
                field.setAccessible(true);
                Object value = field.get(null);
                if (!(value instanceof Map)) continue;
                Map map = (Map)value;
                Collection values = map.values();
                for (Object entry : values) {
                    KeyMapping mapping;
                    if (!(entry instanceof KeyMapping) || !AmmoCheckClient.isLikelyReloadMapping(mapping = (KeyMapping)entry) || result.contains(mapping)) continue;
                    result.add(mapping);
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return result;
    }

    private static boolean isReloadKey(int key) {
        List<KeyMapping> mappings = AmmoCheckClient.findLikelyReloadMappings();
        if (!mappings.isEmpty()) {
            for (KeyMapping mapping : mappings) {
                try {
                    if (mapping.getKey() == null || mapping.getKey().getValue() != key) continue;
                    return true;
                }
                catch (Throwable throwable) {
                }
            }
            return false;
        }
        return key == 82;
    }

    private static boolean isLikelyReloadMapping(KeyMapping mapping) {
        if (mapping == null) {
            return false;
        }
        try {
            String name = mapping.getName() == null ? "" : mapping.getName().toLowerCase();
            String category = mapping.getCategory() == null ? "" : mapping.getCategory().toLowerCase();
            boolean reloadName = name.contains("reload") || name.contains("\u6362\u5f39") || name.contains("\u63db\u5f48");
            boolean taczRelated = name.contains("tacz") || category.contains("tacz") || category.contains("gun") || category.contains("guns");
            boolean boundToReloadDefault = mapping.getKey() != null && mapping.getKey().getValue() == 82;
            return reloadName || taczRelated && boundToReloadDefault;
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    public static void clearClientState() {
        reloadPressTracking = false;
        reloadPressedAtMs = -1L;
        reloadTrackingWindow = 0L;
        reloadTrackingKey = 82;
        reloadTrackingScanCode = 0;
        reloadTrackingModifiers = 0;
        AmmoCheckClient.stopAmmoCheck(false);
        reloadKeyRestoreTicks = 0;
        pendingSyntheticReloadReleaseTicks = 0;
        pendingSyntheticReloadWindow = 0L;
        pendingSyntheticReloadKey = 82;
        pendingSyntheticReloadScanCode = 0;
        pendingSyntheticReloadModifiers = 0;
        synthesizingReloadKeyEvent = false;
        animationStartSelectedSlot = -1;
        animationStartGunId = "";
        activeNativePresetName = null;
        activeAnimationName = ANIMATION_AMMO_CHECK;
        activeAnimationDurationMs = -1L;
        AmmoCheckClient.clearEntryBlend();
        halfCheckSoundPlayed = false;
        endCheckSoundPlayed = false;
        authoredSoundSampleMs = -1L;
        releaseBlendStartedAtMs = -1L;
        releaseStopPacketSent = false;
        reloadAmmoCheckBlockedUntilMs = -1L;
        try {
            InputConstants.Key reloadKey = InputConstants.Type.KEYSYM.getOrCreate(82);
            KeyMapping.set((InputConstants.Key)reloadKey, (boolean)false);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    static {
        reloadTrackingKey = 82;
        animationStartedAtMs = -1L;
        cartridgeState = AmmoCheckCartridgeState.UNKNOWN;
        pendingSyntheticReloadKey = 82;
        animationStartSelectedSlot = -1;
        animationStartGunId = "";
        activeNativePresetName = null;
        activeAnimationName = ANIMATION_AMMO_CHECK;
        activeAnimationDurationMs = -1L;
        authoredSoundSampleMs = -1L;
        entryBlendStartedAtMs = -1L;
        entryBlendDurationMs = 0L;
        releaseBlendStartedAtMs = -1L;
        reloadAmmoCheckBlockedUntilMs = -1L;
    }
}
