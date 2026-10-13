package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.DirectionalLegAnimationClient;
import com.locknar.taczammocheck.tactical.client.DirectionalLegAnimationConfigClient;
import com.locknar.taczammocheck.tactical.client.GunRunAnimationConfigClient;
import com.locknar.taczammocheck.tactical.client.KickAnimationClient;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class GunRunAnimationClient {
    private static final double POSITION_EPSILON = 0.001;
    private static final double RUN_MIN_SPEED = 0.035;
    private static final double MAX_VALID_TICK_DISPLACEMENT = 1.25;
    private static final int STATIONARY_CONFIRM_TICKS = 2;
    private static final int ENTRY_BLEND_TICKS = 3;
    private static final int ROOT_EXIT_BLEND_TICKS = 5;
    private static final int RECENT_RUN_SAMPLE_TICKS = 12;
    private static final Map<Integer, RunState> STATES = new HashMap<Integer, RunState>();
    private static final Map<Integer, Integer> PUSHED_ROOT_DEPTH = new HashMap<Integer, Integer>();
    private static final Map<Integer, GunRunAnimationConfigClient.Sample> RENDER_SAMPLE_CACHE = new HashMap<Integer, GunRunAnimationConfigClient.Sample>();

    private GunRunAnimationClient() {
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        RENDER_SAMPLE_CACHE.clear();
        PUSHED_ROOT_DEPTH.clear();
        if (mc.level == null) {
            GunRunAnimationClient.clear();
            return;
        }
        for (AbstractClientPlayer player : mc.level.players()) {
            GunRunAnimationClient.updatePlayer(player);
        }
        Iterator<Map.Entry<Integer, RunState>> iterator = STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, RunState> entry = iterator.next();
            if (mc.level.getEntity(entry.getKey().intValue()) != null) continue;
            iterator.remove();
        }
    }

    private static void updatePlayer(AbstractClientPlayer player) {
        boolean running;
        RunState state = STATES.get(player.getId());
        if (state == null || !state.playerId.equals(player.getUUID())) {
            state = new RunState(player);
            STATES.put(player.getId(), state);
        }
        Movement movement = state.capture(player);
        state.advanceRootExit();
        if (!GunRunAnimationClient.isBaseContextAllowed(player)) {
            if (player instanceof LocalPlayer && state.action.isBrake()) {
                ModNetwork.sendGunRunBrakeRequest(2);
            }
            state.resetAll();
            return;
        }
        boolean movingNow = movement.speed >= 0.035;
        boolean movementIntent = GunRunAnimationClient.hasMovementIntent(player);
        boolean interpolationGap = state.runMotion != GunRunAnimationConfigClient.Motion.NONE && movement.stationaryTicks < 2;
        boolean bl = running = player.isSprinting() && (movingNow || interpolationGap);
        if (player instanceof LocalPlayer && !movementIntent && state.runMotion != GunRunAnimationConfigClient.Motion.NONE) {
            running = false;
        }
        RunDirection direction = GunRunAnimationClient.detectRunDirection(player, movement);
        if (state.action != GunRunAnimationConfigClient.Motion.NONE) {
            boolean interrupt;
            boolean syncedCancel = state.syncedCancelRequested;
            boolean bl2 = interrupt = state.action.isBrake() && (syncedCancel || GunRunAnimationClient.shouldInterruptBrake(player, state, movement, movingNow, movementIntent));
            if (interrupt) {
                state.syncedCancelRequested = false;
                GunRunAnimationConfigClient.Sample from = state.sampleAtTick(1.0f);
                if (player instanceof LocalPlayer && !syncedCancel) {
                    ModNetwork.sendGunRunBrakeRequest(2);
                }
                if (player.isSprinting() && (movingNow || movementIntent)) {
                    GunRunAnimationClient.beginRun(state, direction.motion, from);
                    if (movingNow) {
                        GunRunAnimationClient.advanceRun(state, movement.speed);
                    }
                    state.rememberRunSample(state.sampleAtTick(1.0f));
                } else {
                    GunRunAnimationClient.beginDirectionalHandoff(player, state, from);
                }
                return;
            }
            boolean heldFinalFrame = state.actionProgress >= 1.0;
            GunRunAnimationClient.advanceAction(state);
            if (heldFinalFrame) {
                state.resetLowerBody();
            }
            return;
        }
        if (running) {
            if (state.runMotion == GunRunAnimationConfigClient.Motion.NONE) {
                GunRunAnimationClient.beginRun(state, direction.motion, GunRunAnimationClient.idleSample());
            }
            if (direction.motion != state.runMotion) {
                GunRunAnimationConfigClient.Sample from = state.sampleAtTick(1.0f);
                state.runMotion = direction.motion;
                state.transitionFrom = from;
                state.transition = 0.0f;
            }
            state.transition = Math.min(1.0f, state.transition + 0.33333334f);
            GunRunAnimationClient.advanceRun(state, movement.speed);
            state.wasRunning = true;
            state.rememberRunSample(state.sampleAtTick(1.0f));
            return;
        }
        if (state.runMotion != GunRunAnimationConfigClient.Motion.NONE || state.wasRunning) {
            GunRunAnimationConfigClient.Sample from = state.sampleAtTick(1.0f);
            if (GunRunAnimationClient.shouldBeginBrake(player, movement, movementIntent)) {
                GunRunAnimationClient.beginBrake(player, state, from, movement, null, player instanceof LocalPlayer);
            } else {
                GunRunAnimationClient.beginDirectionalHandoff(player, state, from);
            }
            return;
        }
        state.resetLowerBody();
    }

    private static void beginRun(RunState state, GunRunAnimationConfigClient.Motion motion, GunRunAnimationConfigClient.Sample from) {
        state.clearRootExit();
        state.action = GunRunAnimationConfigClient.Motion.NONE;
        state.runMotion = motion;
        state.transitionFrom = from == null ? GunRunAnimationClient.idleSample() : from;
        state.transition = 0.0f;
        state.wasRunning = true;
        state.syncedCancelRequested = false;
        state.clearBrakeTracking();
    }

    private static boolean shouldBeginBrake(AbstractClientPlayer player, Movement movement, boolean movementIntent) {
        if (player instanceof LocalPlayer) {
            return !movementIntent;
        }
        return movement.stationaryTicks > 0 || movement.speed < 0.001;
    }

    private static boolean shouldInterruptBrake(AbstractClientPlayer player, RunState state, Movement movement, boolean movingNow, boolean movementIntent) {
        if (player instanceof LocalPlayer) {
            return movementIntent;
        }
        if (!movingNow) {
            state.brakeSawStationary = true;
            state.brakeResumeMovingTicks = 0;
            return false;
        }
        if (player.isSprinting()) {
            return true;
        }
        if (!state.brakeSawStationary) {
            return false;
        }
        ++state.brakeResumeMovingTicks;
        return state.brakeResumeMovingTicks >= 2;
    }

    private static void beginBrake(AbstractClientPlayer player, RunState state, GunRunAnimationConfigClient.Sample from, Movement movement, GunRunAnimationConfigClient.Motion forcedMotion, boolean sync) {
        GunRunAnimationConfigClient.Motion brakeMotion;
        GunRunAnimationConfigClient.Sample source;
        GunRunAnimationConfigClient.Sample sample = source = from == null ? state.sampleAtTick(1.0f) : from;
        if (source == null) {
            source = state.recentRunSample();
        }
        if (source == null) {
            source = GunRunAnimationClient.idleSample();
        }
        if ((brakeMotion = forcedMotion) == null || !brakeMotion.isBrake()) {
            boolean leftPlanted = source.leftLeg().z >= source.rightLeg().z;
            brakeMotion = leftPlanted ? GunRunAnimationConfigClient.Motion.BRAKE_LEFT : GunRunAnimationConfigClient.Motion.BRAKE_RIGHT;
        }
        state.clearRootExit();
        state.action = brakeMotion;
        state.actionProgress = 0.0;
        state.actionStep = GunRunAnimationClient.actionStep(state.action);
        state.transitionFrom = source;
        state.transition = 0.0f;
        state.runMotion = GunRunAnimationConfigClient.Motion.NONE;
        state.phaseStep = 0.0;
        state.wasRunning = false;
        state.syncedCancelRequested = false;
        state.brakeSawStationary = movement.stationaryTicks > 0 || movement.speed < 0.001;
        state.brakeResumeMovingTicks = 0;
        state.rememberRunSample(source);
        if (sync && player instanceof LocalPlayer) {
            ModNetwork.sendGunRunBrakeRequest(state.action == GunRunAnimationConfigClient.Motion.BRAKE_LEFT ? 0 : 1);
        }
    }

    private static void beginDirectionalHandoff(AbstractClientPlayer player, RunState state, GunRunAnimationConfigClient.Sample from) {
        GunRunAnimationConfigClient.Sample source;
        GunRunAnimationConfigClient.Sample sample = source = from == null ? state.sampleAtTick(1.0f) : from;
        if (source == null) {
            source = GunRunAnimationClient.idleSample();
        }
        DirectionalLegAnimationClient.beginExternalBlend(player, GunRunAnimationClient.toDirectionalSample(source));
        state.beginRootExit(source.root());
        state.resetLowerBody();
    }

    private static void advanceRun(RunState state, double speed) {
        if (speed < 0.001) {
            state.phaseStep = 0.0;
            return;
        }
        GunRunAnimationConfigClient.Animation animation = GunRunAnimationConfigClient.animation(state.runMotion);
        double duration = animation == null ? 0.62 : Math.max(0.18, animation.duration);
        double playback = Mth.clamp((double)(speed / 0.14), (double)0.72, (double)1.42);
        state.phaseStep = playback / (duration * 20.0);
        state.phase = GunRunAnimationClient.wrap01(state.phase + state.phaseStep);
    }

    private static void advanceAction(RunState state) {
        state.actionProgress = Math.min(1.0, state.actionProgress + state.actionStep);
        state.transition = Math.min(1.0f, state.transition + 0.33333334f);
    }

    private static double actionStep(GunRunAnimationConfigClient.Motion motion) {
        GunRunAnimationConfigClient.Animation animation = GunRunAnimationConfigClient.animation(motion);
        double duration = animation == null ? 0.4 : Math.max(0.18, animation.duration);
        return 1.0 / (duration * 20.0);
    }

    private static RunDirection detectRunDirection(AbstractClientPlayer player, Movement movement) {
        double angle;
        if (player instanceof LocalPlayer local && local.input != null && local.input.forwardImpulse > 0.1f) {
            angle = local.input.leftImpulse > 0.25f ? -45.0 : (local.input.leftImpulse < -0.25f ? 45.0 : 0.0);
        } else if (movement.speed > 0.001) {
            double yaw = Math.toRadians(player.getYRot());
            double forward = -Math.sin(yaw) * movement.dx + Math.cos(yaw) * movement.dz;
            double right = -Math.cos(yaw) * movement.dx - Math.sin(yaw) * movement.dz;
            angle = Math.toDegrees(Math.atan2(right, forward));
        } else {
            angle = 0.0;
        }
        var motion = angle < -18.0 ? GunRunAnimationConfigClient.Motion.RUN_FORWARD_LEFT : (angle > 18.0 ? GunRunAnimationConfigClient.Motion.RUN_FORWARD_RIGHT : GunRunAnimationConfigClient.Motion.RUN_FORWARD);
        return new RunDirection(motion);
    }

    private static boolean hasMovementIntent(AbstractClientPlayer player) {
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

    private static boolean isBaseContextAllowed(AbstractClientPlayer player) {
        if (!((Boolean)LocknarConfig.GUN_RUN_ANIMATIONS_ENABLED.get()).booleanValue() || player == null || !player.isAlive() || player.isSpectator() || player.getVehicle() != null || player.isSwimming() || player.isFallFlying() || player.onClimbable() || player.getPose() == Pose.SLEEPING || player.getPose() == Pose.SWIMMING || !player.onGround() || player.isCrouching() || player.getPose() == Pose.CROUCHING || KickAnimationClient.isThirdPersonKickActive(player.getId())) {
            return false;
        }
        ItemStack gun = ThirdPersonWeaponSwitchClient.getRenderedMainHand((LivingEntity)player);
        return TaczGunCompat.isTaczGun(gun);
    }

    public static void handleBrakeSync(int entityId, int mode) {
        GunRunAnimationConfigClient.Motion motion;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.player.getId() == entityId) {
            return;
        }
        Entity entity = mc.level.getEntity(entityId);
        if (!(entity instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)entity;
        RunState state = STATES.get(entityId);
        if (state == null || !state.playerId.equals(player.getUUID())) {
            state = new RunState(player);
            STATES.put(entityId, state);
        }
        if (mode == 2) {
            if (state.action.isBrake()) {
                state.syncedCancelRequested = true;
            }
            return;
        }
        if (!GunRunAnimationClient.isBaseContextAllowed(player)) {
            return;
        }
        motion = mode == 0 ? GunRunAnimationConfigClient.Motion.BRAKE_LEFT : (mode == 1 ? GunRunAnimationConfigClient.Motion.BRAKE_RIGHT : GunRunAnimationConfigClient.Motion.NONE);
        if (!motion.isBrake()) {
            return;
        }
        GunRunAnimationConfigClient.Sample source = state.sampleAtTick(0.0f);
        if (source == null) {
            source = state.recentRunSample();
        }
        if (source == null) {
            source = GunRunAnimationClient.idleSample();
        }
        Movement movement = state.lastMovement == null ? new Movement(0.0, 0.0, 0.0, 2) : state.lastMovement;
        GunRunAnimationClient.beginBrake(player, state, source, movement, motion, false);
    }

    private static DirectionalLegAnimationConfigClient.Sample toDirectionalSample(GunRunAnimationConfigClient.Sample sample) {
        return new DirectionalLegAnimationConfigClient.Sample(GunRunAnimationClient.toDirectionalTransform(sample.leftLeg()), GunRunAnimationClient.toDirectionalTransform(sample.rightLeg()), GunRunAnimationClient.toDirectionalTransform(sample.body()));
    }

    private static DirectionalLegAnimationConfigClient.Transform toDirectionalTransform(GunRunAnimationConfigClient.Transform value) {
        DirectionalLegAnimationConfigClient.Transform result = new DirectionalLegAnimationConfigClient.Transform();
        if (value == null) {
            return result;
        }
        result.x = value.x;
        result.y = value.y;
        result.z = value.z;
        result.rotX = value.rotX;
        result.rotY = value.rotY;
        result.rotZ = value.rotZ;
        return result;
    }

    public static boolean isOverridingLowerBody(int entityId) {
        RunState state = STATES.get(entityId);
        return state != null && state.isLowerBodyActive();
    }

    public static GunRunAnimationConfigClient.Motion currentMotion(AbstractClientPlayer player) {
        if (player == null) {
            return GunRunAnimationConfigClient.Motion.NONE;
        }
        RunState state = STATES.get(player.getId());
        if (state == null) {
            return GunRunAnimationConfigClient.Motion.NONE;
        }
        return state.action != GunRunAnimationConfigClient.Motion.NONE ? state.action : state.runMotion;
    }

    public static void applyPoseAfterSetup(AbstractClientPlayer player, PlayerModel model) {
        if (player == null || model == null || player.isSpectator()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (player == mc.player && mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        if (KickAnimationClient.isThirdPersonKickActive(player.getId())) {
            return;
        }
        RunState state = STATES.get(player.getId());
        if (state == null || !state.isLowerBodyActive()) {
            return;
        }
        GunRunAnimationConfigClient.Sample sample = RENDER_SAMPLE_CACHE.get(player.getId());
        if (sample == null) {
            sample = GunRunAnimationClient.renderSample(player, mc.getTimer().getGameTimeDeltaPartialTick(false));
        }
        if (sample == null) {
            return;
        }
        GunRunAnimationClient.resetLegBase(model);
        GunRunAnimationClient.applyTransform(model.leftLeg, sample.leftLeg());
        GunRunAnimationClient.applyTransform(model.rightLeg, sample.rightLeg());
        GunRunAnimationClient.applyBodyTransform(model, sample.body());
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.jacket.copyFrom(model.body);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
    }

    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        AbstractClientPlayer player;
        Player player2 = event.getEntity();
        if (!(player2 instanceof AbstractClientPlayer) || (player = (AbstractClientPlayer)player2).isSpectator()) {
            return;
        }
        GunRunAnimationConfigClient.Sample sample = GunRunAnimationClient.renderRootSample(player, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
        if (sample == null) {
            RENDER_SAMPLE_CACHE.remove(player.getId());
            return;
        }
        RENDER_SAMPLE_CACHE.put(player.getId(), sample);
        if (sample.root() == null) {
            return;
        }
        GunRunAnimationConfigClient.Transform root = sample.root();
        if (Math.abs(root.x) < 1.0E-4 && Math.abs(root.y) < 1.0E-4 && Math.abs(root.z) < 1.0E-4 && Math.abs(root.rotX) < 1.0E-4 && Math.abs(root.rotY) < 1.0E-4 && Math.abs(root.rotZ) < 1.0E-4) {
            return;
        }
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        PUSHED_ROOT_DEPTH.merge(player.getId(), 1, Integer::sum);
        float bodyYaw = player.yBodyRotO + (float)(GunRunAnimationClient.wrapDegrees(player.yBodyRot - player.yBodyRotO) * (double)event.getPartialTick());
        float vanillaBodyRotation = 180.0f - bodyYaw;
        pose.mulPose(Axis.YP.rotationDegrees(vanillaBodyRotation));
        pose.translate(Mth.clamp((double)root.x, (double)-4.0, (double)4.0) / 16.0, Mth.clamp((double)root.y, (double)-4.0, (double)4.0) / 16.0, Mth.clamp((double)root.z, (double)-4.0, (double)4.0) / 16.0);
        pose.mulPose(Axis.XP.rotationDegrees((float)Mth.clamp((double)root.rotX, (double)-20.0, (double)20.0)));
        pose.mulPose(Axis.YP.rotationDegrees((float)Mth.clamp((double)root.rotY, (double)-15.0, (double)15.0)));
        pose.mulPose(Axis.ZP.rotationDegrees((float)Mth.clamp((double)root.rotZ, (double)-18.0, (double)18.0)));
        pose.mulPose(Axis.YP.rotationDegrees(-vanillaBodyRotation));
    }

    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        Player player = event.getEntity();
        if (!(player instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player2 = (AbstractClientPlayer)player;
        RENDER_SAMPLE_CACHE.remove(player2.getId());
        Integer depth = PUSHED_ROOT_DEPTH.get(player2.getId());
        if (depth == null || depth <= 0) {
            return;
        }
        event.getPoseStack().popPose();
        if (depth == 1) {
            PUSHED_ROOT_DEPTH.remove(player2.getId());
        } else {
            PUSHED_ROOT_DEPTH.put(player2.getId(), depth - 1);
        }
    }

    private static GunRunAnimationConfigClient.Sample renderSample(AbstractClientPlayer player, float partialTick) {
        RunState state = STATES.get(player.getId());
        if (state == null || !state.isLowerBodyActive()) {
            return null;
        }
        return state.sampleAtTick(partialTick);
    }

    private static GunRunAnimationConfigClient.Sample renderRootSample(AbstractClientPlayer player, float partialTick) {
        RunState state = STATES.get(player.getId());
        if (state == null) {
            return null;
        }
        if (state.isLowerBodyActive()) {
            return state.sampleAtTick(partialTick);
        }
        return state.rootExitSample(partialTick);
    }

    private static void resetLegBase(PlayerModel model) {
        model.leftLeg.x = 1.9f;
        model.rightLeg.x = -1.9f;
        model.leftLeg.y = 12.0f;
        model.rightLeg.y = 12.0f;
        model.leftLeg.z = 0.0f;
        model.rightLeg.z = 0.0f;
        model.rightLeg.xRot = 0.0f;
        model.leftLeg.xRot = 0.0f;
        model.rightLeg.yRot = 0.0f;
        model.leftLeg.yRot = 0.0f;
        model.rightLeg.zRot = 0.0f;
        model.leftLeg.zRot = 0.0f;
    }

    private static void applyTransform(ModelPart part, GunRunAnimationConfigClient.Transform transform) {
        part.x += (float)transform.x;
        part.y += (float)transform.y;
        part.z += (float)transform.z;
        part.xRot += (float)Math.toRadians(transform.rotX);
        part.yRot += (float)Math.toRadians(transform.rotY);
        part.zRot += (float)Math.toRadians(transform.rotZ);
    }

    private static void applyBodyTransform(PlayerModel model, GunRunAnimationConfigClient.Transform transform) {
        model.body.y += (float)Mth.clamp((double)transform.y, (double)-2.0, (double)2.0);
        model.body.xRot += (float)Math.toRadians(Mth.clamp((double)transform.rotX, (double)-25.0, (double)25.0));
        model.body.yRot += (float)Math.toRadians(Mth.clamp((double)transform.rotY, (double)-35.0, (double)35.0));
        model.body.zRot += (float)Math.toRadians(Mth.clamp((double)transform.rotZ, (double)-30.0, (double)30.0));
    }

    private static GunRunAnimationConfigClient.Sample idleSample() {
        DirectionalLegAnimationConfigClient.Sample idle = DirectionalLegAnimationConfigClient.sample(DirectionalLegAnimationConfigClient.Motion.GUN_IDLE, 0.0);
        return new GunRunAnimationConfigClient.Sample(GunRunAnimationClient.convert(idle.leftLeg()), GunRunAnimationClient.convert(idle.rightLeg()), GunRunAnimationClient.convert(idle.body()), new GunRunAnimationConfigClient.Transform());
    }

    private static GunRunAnimationConfigClient.Transform convert(DirectionalLegAnimationConfigClient.Transform value) {
        GunRunAnimationConfigClient.Transform result = new GunRunAnimationConfigClient.Transform();
        result.x = value.x;
        result.y = value.y;
        result.z = value.z;
        result.rotX = value.rotX;
        result.rotY = value.rotY;
        result.rotZ = value.rotZ;
        return result;
    }

    public static void clear() {
        STATES.clear();
        PUSHED_ROOT_DEPTH.clear();
        RENDER_SAMPLE_CACHE.clear();
    }

    private static double wrap01(double value) {
        return (value %= 1.0) < 0.0 ? value + 1.0 : value;
    }

    private static double wrapDegrees(double angle) {
        if ((angle %= 360.0) >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }

    private static final class RunState {
        private final UUID playerId;
        private GunRunAnimationConfigClient.Motion runMotion = GunRunAnimationConfigClient.Motion.NONE;
        private GunRunAnimationConfigClient.Motion action = GunRunAnimationConfigClient.Motion.NONE;
        private double phase;
        private double phaseStep;
        private double actionProgress;
        private double actionStep;
        private GunRunAnimationConfigClient.Sample transitionFrom;
        private float transition = 1.0f;
        private boolean wasRunning;
        private double lastX;
        private double lastZ;
        private float lastYaw;
        private boolean initialized;
        private double smoothedYawRate;
        private int stationaryTicks;
        private boolean brakeSawStationary;
        private int brakeResumeMovingTicks;
        private boolean syncedCancelRequested;
        private Movement lastMovement;
        private GunRunAnimationConfigClient.Sample recentRunSample;
        private int recentRunSampleAge = 13;
        private GunRunAnimationConfigClient.Transform rootExitFrom;
        private float rootExitProgress = 1.0f;

        private RunState(AbstractClientPlayer player) {
            this.playerId = player.getUUID();
            this.phase = GunRunAnimationClient.wrap01((double)player.getId() * 0.173);
            this.lastX = player.getX();
            this.lastZ = player.getZ();
            this.lastYaw = player.getYRot();
            this.initialized = true;
            this.lastMovement = new Movement(0.0, 0.0, 0.0, 0);
        }

        private Movement capture(AbstractClientPlayer player) {
            if (this.recentRunSample != null && this.recentRunSampleAge <= 12) {
                ++this.recentRunSampleAge;
            } else if (this.recentRunSampleAge > 12) {
                this.recentRunSample = null;
            }
            double x = player.getX();
            double z = player.getZ();
            float yaw = player.getYRot();
            if (!this.initialized) {
                this.lastX = x;
                this.lastZ = z;
                this.lastYaw = yaw;
                this.initialized = true;
                this.lastMovement = new Movement(0.0, 0.0, 0.0, this.stationaryTicks);
                return this.lastMovement;
            }
            double dx = x - this.lastX;
            double dz = z - this.lastZ;
            this.lastX = x;
            this.lastZ = z;
            double speed = Math.sqrt(dx * dx + dz * dz);
            if (!Double.isFinite(speed) || speed > 1.25) {
                speed = 0.0;
                dz = 0.0;
                dx = 0.0;
                this.stationaryTicks = 2;
            } else {
                this.stationaryTicks = speed < 0.001 ? Math.min(3, this.stationaryTicks + 1) : 0;
            }
            double yawDelta = GunRunAnimationClient.wrapDegrees(yaw - this.lastYaw);
            this.lastYaw = yaw;
            this.smoothedYawRate = this.smoothedYawRate * 0.65 + yawDelta * 0.35;
            this.lastMovement = new Movement(dx, dz, speed, this.stationaryTicks);
            return this.lastMovement;
        }

        private boolean isLowerBodyActive() {
            return this.runMotion != GunRunAnimationConfigClient.Motion.NONE || this.action != GunRunAnimationConfigClient.Motion.NONE;
        }

        private GunRunAnimationConfigClient.Sample sampleAtTick(float partialTick) {
            GunRunAnimationConfigClient.Sample authored;
            if (this.action != GunRunAnimationConfigClient.Motion.NONE) {
                double progress = Math.min(1.0, this.actionProgress + this.actionStep * (double)partialTick);
                authored = GunRunAnimationConfigClient.sample(this.action, progress);
            } else if (this.runMotion != GunRunAnimationConfigClient.Motion.NONE) {
                double renderPhase = GunRunAnimationClient.wrap01(this.phase + this.phaseStep * (double)partialTick);
                authored = GunRunAnimationConfigClient.sample(this.runMotion, renderPhase);
            } else {
                return null;
            }
            double entry = RunState.smooth(Math.min(1.0, (double)(this.transition + partialTick / 3.0f)));
            if (this.transitionFrom != null && entry < 1.0) {
                authored = GunRunAnimationConfigClient.Sample.lerp(this.transitionFrom, authored, entry);
            }
            if (this.runMotion != GunRunAnimationConfigClient.Motion.NONE && this.action == GunRunAnimationConfigClient.Motion.NONE) {
                GunRunAnimationConfigClient.Transform root = authored.root().copy();
                root.rotZ -= Mth.clamp((double)(this.smoothedYawRate * 0.65), (double)-8.0, (double)8.0);
                root.rotY += Mth.clamp((double)(this.smoothedYawRate * 0.12), (double)-2.5, (double)2.5);
                authored = new GunRunAnimationConfigClient.Sample(authored.leftLeg(), authored.rightLeg(), authored.body(), root);
            }
            return authored;
        }

        private void rememberRunSample(GunRunAnimationConfigClient.Sample sample) {
            if (sample == null) {
                return;
            }
            this.recentRunSample = sample;
            this.recentRunSampleAge = 0;
        }

        private GunRunAnimationConfigClient.Sample recentRunSample() {
            return this.recentRunSampleAge <= 12 ? this.recentRunSample : null;
        }

        private void beginRootExit(GunRunAnimationConfigClient.Transform root) {
            if (root == null) {
                this.clearRootExit();
                return;
            }
            this.rootExitFrom = root.copy();
            this.rootExitProgress = 0.0f;
        }

        private void advanceRootExit() {
            if (this.rootExitFrom == null) {
                return;
            }
            this.rootExitProgress = Math.min(1.0f, this.rootExitProgress + 0.2f);
            if (this.rootExitProgress >= 1.0f) {
                this.clearRootExit();
            }
        }

        private GunRunAnimationConfigClient.Sample rootExitSample(float partialTick) {
            if (this.rootExitFrom == null) {
                return null;
            }
            double progress = RunState.smooth(Math.min(1.0, (double)(this.rootExitProgress + partialTick / 5.0f)));
            GunRunAnimationConfigClient.Transform root = GunRunAnimationConfigClient.Transform.lerp(this.rootExitFrom, new GunRunAnimationConfigClient.Transform(), progress);
            return new GunRunAnimationConfigClient.Sample(new GunRunAnimationConfigClient.Transform(), new GunRunAnimationConfigClient.Transform(), new GunRunAnimationConfigClient.Transform(), root);
        }

        private void clearRootExit() {
            this.rootExitFrom = null;
            this.rootExitProgress = 1.0f;
        }

        private void resetLowerBody() {
            this.runMotion = GunRunAnimationConfigClient.Motion.NONE;
            this.action = GunRunAnimationConfigClient.Motion.NONE;
            this.phaseStep = 0.0;
            this.actionProgress = 0.0;
            this.actionStep = 0.0;
            this.transitionFrom = null;
            this.transition = 1.0f;
            this.wasRunning = false;
            this.syncedCancelRequested = false;
            this.clearBrakeTracking();
            this.smoothedYawRate *= 0.5;
        }

        private void resetAll() {
            this.resetLowerBody();
            this.clearRootExit();
            this.recentRunSample = null;
            this.recentRunSampleAge = 13;
        }

        private void clearBrakeTracking() {
            this.brakeSawStationary = false;
            this.brakeResumeMovingTicks = 0;
        }

        private static double smooth(double value) {
            return value * value * (3.0 - 2.0 * value);
        }
    }

    private record Movement(double dx, double dz, double speed, int stationaryTicks) {
    }

    private record RunDirection(GunRunAnimationConfigClient.Motion motion) {
    }
}
