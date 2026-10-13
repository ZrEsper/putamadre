package com.locknar.taczammocheck.prone;

import com.locknar.taczammocheck.TaczAmmoCheckCommon;
import com.locknar.taczammocheck.prone.ProneNetwork;
import com.locknar.taczammocheck.tactical.client.ProneTacticalAnimationConfigClient;
import com.locknar.taczammocheck.tactical.server.MirrorGunHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.bus.api.SubscribeEvent;

public final class ProneServer {
    public static final int STANDING = 0;
    public static final int ENTER = 1;
    public static final int PRONE = 2;
    public static final int EXIT = 3;
    public static final int REORIENT = 4;
    public static final int ENTER_TICKS = 17;
    public static final int EXIT_TICKS = 15;
    private static final net.minecraft.resources.ResourceLocation SPEED_ID = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "speed_id");
    private static final Map<UUID, State> STATES = new HashMap<UUID, State>();
    private static final Map<UUID, Long> REQUESTS = new HashMap<UUID, Long>();
    private static final Map<UUID, MoveIntent> MOVEMENT = new HashMap<UUID, MoveIntent>();
    private static final Set<UUID> ALIGNED = new HashSet<UUID>();
    private static long revision;

    private ProneServer() {
    }

    public static void movement(ServerPlayer player, long sequence, boolean moving, float yaw) {
        if (player == null || !Float.isFinite(yaw) || !ProneServer.active((Player)player)) {
            return;
        }
        MoveIntent old = MOVEMENT.get(player.getUUID());
        if (old != null && sequence <= old.sequence()) {
            return;
        }
        if (!moving || old == null || !old.moving()) {
            ALIGNED.remove(player.getUUID());
        }
        MOVEMENT.put(player.getUUID(), new MoveIntent(sequence, moving, Mth.wrapDegrees((float)yaw), player.level().getGameTime()));
    }

    public static State state(Player player) {
        return STATES.get(player.getUUID());
    }

    public static boolean active(Player player) {
        return ProneServer.state(player) != null;
    }

    public static boolean transitioning(Player player) {
        State s = ProneServer.state(player);
        return s != null && s.phase() != 2;
    }

    public static boolean canStand(Player player) {
        return player.level().noCollision((Entity)player, player.getDimensions(Pose.STANDING).makeBoundingBox(player.position()).deflate(0.001));
    }

    public static void request(ServerPlayer player, long request, boolean down) {
        if (player == null || request <= REQUESTS.getOrDefault(player.getUUID(), 0L)) {
            return;
        }
        REQUESTS.put(player.getUUID(), request);
        State old = ProneServer.state((Player)player);
        if (old != null && old.phase() != 2) {
            return;
        }
        if (down == (old != null)) {
            return;
        }
        if (!player.isAlive() || player.isSpectator() || player.isPassenger() || player.isFallFlying() || player.isInWaterOrBubble() || player.onClimbable() || !player.onGround()) {
            return;
        }
        if (TaczAmmoCheckCommon.isAmmoCheckActive((LivingEntity)player) || MirrorGunHandler.isBusy(player)) {
            return;
        }
        if (!down && !ProneServer.canStand((Player)player)) {
            player.displayClientMessage((Component)Component.translatable((String)"message.locknar_doorkick.prone_blocked"), true);
            return;
        }
        if (down && player.getForcedPose() != null) {
            return;
        }
        State next = new State(down ? 1 : 3, player.level().getGameTime(), old == null ? player.getYRot() : old.anchor(), ++revision, ProneServer.duration(down));
        STATES.put(player.getUUID(), next);
        ProneServer.force(player, next);
        ProneNetwork.broadcast(player, next);
    }

    private static int duration(boolean down) {
        ProneTacticalAnimationConfigClient.Motion motion = down ? ProneTacticalAnimationConfigClient.Motion.PRONE_ENTER : ProneTacticalAnimationConfigClient.Motion.PRONE_EXIT;
        return Math.max(2, Math.min(100, (int)Math.round(ProneTacticalAnimationConfigClient.animation((ProneTacticalAnimationConfigClient.Motion)motion).duration * 20.0)));
    }

    private static void force(ServerPlayer player, State state) {
        player.setSprinting(false);
        player.setForcedPose(Pose.SWIMMING);
        player.setPose(Pose.SWIMMING);
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && speed.getModifier(SPEED_ID) == null) {
            speed.addTransientModifier(new AttributeModifier(SPEED_ID, -0.35, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
        if (state.phase() != 2) {
            Vec3 velocity = player.getDeltaMovement();
            player.setDeltaMovement(0.0, Math.min(0.0, velocity.y), 0.0);
        }
    }

    @SubscribeEvent
    public static void tick(PlayerTickEvent.Post event) {
        Player player;
        if (false || !((player = event.getEntity()) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        State state = ProneServer.state((Player)player2);
        if (state == null) {
            return;
        }
        if (!player2.isAlive() || player2.isSpectator() || player2.isPassenger() || player2.isFallFlying() || player2.isInWaterOrBubble() || player2.onClimbable()) {
            ProneServer.clear(player2, true);
            return;
        }
        long elapsed = player2.level().getGameTime() - state.start();
        if (state.phase() == 1 && elapsed >= (long)state.duration()) {
            state = new State(2, player2.level().getGameTime(), state.anchor(), ++revision, 0);
            STATES.put(player2.getUUID(), state);
            ProneNetwork.broadcast(player2, state);
        } else if (state.phase() == 4 && elapsed >= (long)state.duration()) {
            state = new State(2, player2.level().getGameTime(), state.target(), ++revision, 0);
            STATES.put(player2.getUUID(), state);
            ProneNetwork.broadcast(player2, state);
            ALIGNED.add(player2.getUUID());
        } else if (state.phase() == 3) {
            if (!ProneServer.canStand((Player)player2)) {
                state = new State(2, player2.level().getGameTime(), state.anchor(), ++revision, 0);
                STATES.put(player2.getUUID(), state);
                ProneNetwork.broadcast(player2, state);
            } else if (elapsed >= (long)state.duration()) {
                ProneServer.clear(player2, true);
                return;
            }
        }
        MoveIntent intent = MOVEMENT.get(player2.getUUID());
        if (state.phase() == 2 && intent != null && intent.moving() && player2.level().getGameTime() - intent.tick() < 12L) {
            float delta = Mth.wrapDegrees((float)(intent.yaw() - state.anchor()));
            float f = Math.abs(delta);
            int n = ALIGNED.contains(player2.getUUID()) ? 60 : 8;
            if (f > (float)n) {
                if (!TaczAmmoCheckCommon.isAmmoCheckActive((LivingEntity)player2)) {
                    int ticks = 8 + Math.round(Math.abs(delta) / 30.0f);
                    state = new State(4, player2.level().getGameTime(), state.anchor(), ++revision, ticks, state.anchor() + delta);
                    STATES.put(player2.getUUID(), state);
                    ProneNetwork.broadcast(player2, state);
                }
            } else if ((double)Math.abs(delta) > 0.5) {
                state = new State(2, player2.level().getGameTime(), intent.yaw(), ++revision, 0);
                STATES.put(player2.getUUID(), state);
                ProneNetwork.broadcast(player2, state);
            }
            if (state.phase() == 2) {
                ALIGNED.add(player2.getUUID());
            }
        }
        ProneServer.force(player2, state);
    }

    private static void clear(ServerPlayer player, boolean sync) {
        if (STATES.remove(player.getUUID()) != null) {
            AttributeInstance speed;
            MOVEMENT.remove(player.getUUID());
            ALIGNED.remove(player.getUUID());
            if (player.getForcedPose() == Pose.SWIMMING) {
                player.setForcedPose(null);
            }
            if (ProneServer.canStand((Player)player) && !player.isInWaterOrBubble()) {
                player.setPose(Pose.STANDING);
            }
            if ((speed = player.getAttribute(Attributes.MOVEMENT_SPEED)) != null) {
                speed.removeModifier(SPEED_ID);
            }
            if (sync) {
                ProneNetwork.broadcast(player, new State(0, player.level().getGameTime(), player.getYRot(), ++revision, 0));
            }
        }
    }

    @SubscribeEvent
    public static void jump(LivingEvent.LivingJumpEvent event) {
        ServerPlayer player;
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ServerPlayer && ProneServer.active((Player)(player = (ServerPlayer)livingEntity))) {
            Vec3 v = player.getDeltaMovement();
            player.setDeltaMovement(v.x, Math.min(0.0, v.y), v.z);
        }
    }

    @SubscribeEvent
    public static void track(PlayerEvent.StartTracking event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer subject;
            State state;
            ServerPlayer viewer = (ServerPlayer)player;
            player = event.getTarget() instanceof Player ? (Player)event.getTarget() : null;
            if (player instanceof ServerPlayer && (state = ProneServer.state((Player)(subject = (ServerPlayer)player))) != null) {
                ProneNetwork.send(viewer, subject, state);
            }
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            ProneServer.clear(player2, false);
            REQUESTS.remove(player2.getUUID());
        }
    }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            ProneServer.clear(player2, true);
        }
    }

    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer player2 = (ServerPlayer)player;
            ProneServer.clear(player2, true);
        }
    }

    @SubscribeEvent
    public static void stop(ServerStoppedEvent event) {
        STATES.clear();
        REQUESTS.clear();
        MOVEMENT.clear();
        ALIGNED.clear();
        revision = 0L;
    }

    private record MoveIntent(long sequence, boolean moving, float yaw, long tick) {
    }

    public record State(int phase, long start, float anchor, long revision, int duration, float target) {
        public State(int phase, long start, float anchor, long revision, int duration) {
            this(phase, start, anchor, revision, duration, anchor);
        }
    }
}
