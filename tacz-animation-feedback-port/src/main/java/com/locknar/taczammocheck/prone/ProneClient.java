package com.locknar.taczammocheck.prone;

import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.locknar.taczammocheck.prone.ProneNetwork;
import com.locknar.taczammocheck.tactical.client.MirrorViewClient;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.EventPriority;

public final class ProneClient {
    private static final KeyMapping KEY = new KeyMapping("key.locknar_tacz_ammo_check.prone", InputConstants.Type.KEYSYM, 67, "key.categories.locknar_doorkick");
    private static final Map<Integer, ProneNetwork.Sync> STATES = new HashMap<Integer, ProneNetwork.Sync>();
    private static long sequence;
    private static long moveSequence;
    private static long lastMoveTick;
    private static boolean lastMoving;
    private static boolean jumpHeld;
    private static boolean awaitingAlignment;
    private static Object level;

    private ProneClient() {
    }

    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KEY);
    }

    public static void init() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ProneClient::input);
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, ProneClient::tick);
    }

    public static boolean handleKey(int key, int scan, int action) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.screen != null || !KEY.matches(key, scan)) {
            return false;
        }
        if (!(action != 1 || ProneClient.transitioning((AbstractClientPlayer)mc.player) || AmmoCheckClient.isActive() || MirrorViewClient.isBusy())) {
            ProneNetwork.request(++sequence, !ProneClient.active((AbstractClientPlayer)mc.player));
        }
        return true;
    }

    public static void accept(ProneNetwork.Sync sync) {
        if (sync.phase() < 0 || sync.phase() > 4 || !Float.isFinite(sync.anchor()) || !Float.isFinite(sync.target()) || sync.duration() < 0 || sync.duration() > 100) {
            return;
        }
        ProneNetwork.Sync previous = STATES.get(sync.entity());
        if (previous != null && previous.uuid().equals(sync.uuid()) && previous.revision() >= sync.revision()) {
            return;
        }
        STATES.put(sync.entity(), sync);
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getUUID().equals(sync.uuid())) {
            if (sync.phase() != 0) {
                mc.player.setForcedPose(Pose.SWIMMING);
                mc.player.setPose(Pose.SWIMMING);
            } else {
                if (mc.player.getForcedPose() == Pose.SWIMMING) {
                    mc.player.setForcedPose(null);
                }
                if (!mc.player.isInWaterOrBubble()) {
                    mc.player.setPose(Pose.STANDING);
                }
                lastMoving = false;
                lastMoveTick = 0L;
            }
        }
    }

    public static ProneNetwork.Sync state(AbstractClientPlayer player) {
        if (player == null) {
            return null;
        }
        ProneNetwork.Sync value = STATES.get(player.getId());
        return value != null && value.uuid().equals(player.getUUID()) && value.phase() != 0 ? value : null;
    }

    public static boolean active(AbstractClientPlayer player) {
        return ProneClient.state(player) != null;
    }

    public static boolean suppressVanillaRecovery(LivingEntity entity) {
        ProneNetwork.Sync s = STATES.get(entity.getId());
        return s != null && s.uuid().equals(entity.getUUID()) && s.phase() == 0 && entity.level().getGameTime() - s.started() < 14L && !entity.isInWaterOrBubble() && !entity.isSwimming();
    }

    public static boolean transitioning(AbstractClientPlayer player) {
        ProneNetwork.Sync s = ProneClient.state(player);
        return s != null && s.phase() != 2;
    }

    public static double progress(AbstractClientPlayer player, float partial) {
        ProneNetwork.Sync s = ProneClient.state(player);
        if (s == null) {
            return 0.0;
        }
        double ticks = Math.max(2, s.duration());
        return Math.max(0.0, Math.min(1.0, (double)((float)(player.level().getGameTime() - s.started()) + partial) / ticks));
    }

    private static void tick(ClientTickEvent.Pre event) {
        if (false) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (!mc.options.keyJump.isDown()) {
            jumpHeld = false;
        }
        if (level != mc.level) {
            STATES.clear();
            level = mc.level;
        }
        if (mc.player != null && ProneClient.active((AbstractClientPlayer)mc.player)) {
            mc.player.setForcedPose(Pose.SWIMMING);
            mc.player.setPose(Pose.SWIMMING);
            mc.player.setSprinting(false);
        }
    }

    private static void input(MovementInputUpdateEvent event) {
        boolean awaitingTurn;
        Player player = event.getEntity();
        if (!(player instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player2 = (AbstractClientPlayer)player;
        if (!ProneClient.active(player2)) {
            if (jumpHeld) {
                event.getInput().jumping = false;
            }
            return;
        }
        Input input = event.getInput();
        boolean jumping = Minecraft.getInstance().options.keyJump.isDown();
        if (jumping && !jumpHeld && !ProneClient.transitioning(player2) && !AmmoCheckClient.isActive()) {
            ProneNetwork.request(++sequence, false);
        }
        jumpHeld = jumping;
        input.jumping = false;
        input.shiftKeyDown = false;
        boolean moving = (double)(Math.abs(input.forwardImpulse) + Math.abs(input.leftImpulse)) > 0.01;
        ProneNetwork.Sync s = ProneClient.state(player2);
        double angle = Math.abs(Mth.wrapDegrees((float)(player2.getYRot() - s.anchor())));
        if (moving && !lastMoving) {
            boolean bl = awaitingAlignment = angle > 8.0;
        }
        if (!moving || angle <= 8.0 || s.phase() == 4) {
            awaitingAlignment = false;
        }
        long now = player2.level().getGameTime();
        if (moving != lastMoving || now - lastMoveTick >= 3L) {
            ProneNetwork.movement(++moveSequence, moving, player2.getYRot());
            lastMoving = moving;
            lastMoveTick = now;
        }
        boolean bl = awaitingTurn = moving && s.phase() == 2 && (awaitingAlignment || angle > 60.0);
        if (ProneClient.transitioning(player2) || awaitingTurn) {
            input.forwardImpulse = 0.0f;
            input.leftImpulse = 0.0f;
        }
        player2.setSprinting(false);
    }

    public static void clear() {
        STATES.clear();
        sequence = 0L;
        moveSequence = 0L;
        lastMoveTick = 0L;
        lastMoving = false;
        level = null;
    }
}
