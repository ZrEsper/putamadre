package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.DirectionalLegAnimationConfigClient;
import com.locknar.taczammocheck.tactical.client.GunRunAnimationClient;
import com.locknar.taczammocheck.tactical.client.KickAnimationClient;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class DirectionalLegAnimationClient {
    private static final double MOVE_EPSILON = 0.0025;
    private static final double POSITION_IDLE_EPSILON = 0.001;
    private static final double MAX_VALID_TICK_DISPLACEMENT = 1.25;
    private static final int STATIONARY_CONFIRM_TICKS = 2;
    private static final int REQUIRED_STABLE_TICKS = 2;
    private static final int BLEND_TICKS = 4;
    private static final double SECTOR_HYSTERESIS_DEGREES = 8.0;
    private static final Map<Integer, MotionState> STATES = new HashMap<Integer, MotionState>();

    private DirectionalLegAnimationClient() {
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            STATES.clear();
            return;
        }
        for (AbstractClientPlayer player : mc.level.players()) {
            DirectionalLegAnimationClient.updatePlayer(player);
        }
        Iterator<Map.Entry<Integer, MotionState>> iterator = STATES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, MotionState> entry = iterator.next();
            if (mc.level.getEntity(entry.getKey().intValue()) != null) continue;
            iterator.remove();
        }
    }

    private static void updatePlayer(AbstractClientPlayer player) {
        int requiredTicks;
        MotionState state = STATES.get(player.getId());
        if (state == null || !state.playerId.equals(player.getUUID())) {
            state = new MotionState(player);
            STATES.put(player.getId(), state);
        }
        MovementSample movement = state.captureMovement(player);
        if (!DirectionalLegAnimationClient.isAnimationContextAllowed(player)) {
            state.resetImmediately();
            return;
        }
        DirectionalLegAnimationConfigClient.Motion desired = DirectionalLegAnimationClient.detectMotion(player, state, movement);
        if (desired != state.pending) {
            state.pending = desired;
            state.pendingTicks = 1;
        } else {
            ++state.pendingTicks;
        }
        int n = requiredTicks = desired == DirectionalLegAnimationConfigClient.Motion.GUN_IDLE ? 1 : 2;
        if (state.current == DirectionalLegAnimationConfigClient.Motion.NONE) {
            state.previous = DirectionalLegAnimationConfigClient.Motion.NONE;
            state.current = desired;
            state.pending = desired;
            state.pendingTicks = requiredTicks;
            state.blend = state.externalPrevious != null ? -0.25f : (desired == DirectionalLegAnimationConfigClient.Motion.GUN_IDLE ? 1.0f : 0.0f);
        } else if (desired != state.current && state.pendingTicks >= requiredTicks) {
            state.previous = state.current;
            state.current = desired;
            state.blend = 0.0f;
        }
        state.blend = Math.min(1.0f, state.blend + 0.25f);
        if (state.blend >= 1.0f) {
            state.externalPrevious = null;
        }
        if (state.current == DirectionalLegAnimationConfigClient.Motion.GUN_IDLE || movement.speed() < 0.001) {
            state.phaseStep = 0.0;
            return;
        }
        DirectionalLegAnimationConfigClient.Animation animation = DirectionalLegAnimationConfigClient.animation(state.current);
        double duration = animation == null ? 0.8 : Math.max(0.2, animation.duration);
        double playback = Mth.clamp((double)(state.smoothedSpeed / 0.1), (double)0.65, (double)1.35);
        state.phaseStep = playback / (duration * 20.0);
        state.phase = DirectionalLegAnimationClient.wrap01(state.phase + state.phaseStep);
    }

    private static boolean isAnimationContextAllowed(AbstractClientPlayer player) {
        if (!((Boolean)LocknarConfig.DIRECTIONAL_LEG_ANIMATIONS_ENABLED.get()).booleanValue() || player == null || !player.isAlive() || player.isSpectator() || player.getVehicle() != null || player.isSwimming() || player.isFallFlying() || KickAnimationClient.isThirdPersonKickActive(player.getId()) || GunRunAnimationClient.isOverridingLowerBody(player.getId()) || player.onClimbable() || player.getPose() == Pose.SLEEPING || player.getPose() == Pose.SWIMMING || !player.onGround() || player.isSprinting() || player.isCrouching() || player.getPose() == Pose.CROUCHING) {
            return false;
        }
        ItemStack renderedGun = ThirdPersonWeaponSwitchClient.getRenderedMainHand((LivingEntity)player);
        return TaczGunCompat.isTaczGun(renderedGun);
    }

    private static DirectionalLegAnimationConfigClient.Motion detectMotion(AbstractClientPlayer player, MotionState state, MovementSample movement) {
        double distanceFromCurrent;
        double dx = movement.dx();
        double dz = movement.dz();
        double speed = movement.speed();
        if (movement.stationaryTicks() >= 2) {
            state.smoothedSpeed = 0.0;
            return DirectionalLegAnimationConfigClient.Motion.GUN_IDLE;
        }
        if (speed < 0.001) {
            state.smoothedSpeed *= 0.35;
            return state.current == DirectionalLegAnimationConfigClient.Motion.NONE ? DirectionalLegAnimationConfigClient.Motion.GUN_IDLE : state.current;
        }
        state.smoothedSpeed = state.smoothedSpeed * 0.55 + speed * 0.45;
        if (state.smoothedSpeed < 0.0025) {
            return DirectionalLegAnimationConfigClient.Motion.GUN_IDLE;
        }
        double yaw = Math.toRadians(player.getYRot());
        double forward = -Math.sin(yaw) * dx + Math.cos(yaw) * dz;
        double right = -Math.cos(yaw) * dx - Math.sin(yaw) * dz;
        double directionAngle = Math.toDegrees(Math.atan2(right, forward));
        DirectionalLegAnimationConfigClient.Motion current = state.current;
        if (current != DirectionalLegAnimationConfigClient.Motion.NONE && current != DirectionalLegAnimationConfigClient.Motion.GUN_IDLE && (distanceFromCurrent = Math.abs(DirectionalLegAnimationClient.wrapDegrees(directionAngle - current.directionAngle()))) <= 30.5) {
            return current;
        }
        return DirectionalLegAnimationConfigClient.Motion.nearest(directionAngle);
    }

    public static void applyPoseAfterSetup(AbstractClientPlayer player, PlayerModel model) {
        if (player == null || model == null || player.isSpectator()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (player == minecraft.player && minecraft.options.getCameraType().isFirstPerson()) {
            return;
        }
        if (KickAnimationClient.isThirdPersonKickActive(player.getId()) || GunRunAnimationClient.isOverridingLowerBody(player.getId())) {
            return;
        }
        MotionState state = STATES.get(player.getId());
        if (state == null || state.current == DirectionalLegAnimationConfigClient.Motion.NONE) {
            return;
        }
        DirectionalLegAnimationConfigClient.Motion motion = state.current;
        DirectionalLegAnimationConfigClient.Motion previous = state.previous;
        float partialTick = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        double phase = DirectionalLegAnimationClient.wrap01(state.phase + state.phaseStep * (double)partialTick);
        float blend = Math.min(1.0f, state.blend + partialTick / 4.0f);
        DirectionalLegAnimationConfigClient.Sample current = DirectionalLegAnimationConfigClient.sample(motion, phase);
        DirectionalLegAnimationConfigClient.Sample previousSample = state.externalPrevious != null ? state.externalPrevious : (previous == DirectionalLegAnimationConfigClient.Motion.NONE ? DirectionalLegAnimationConfigClient.Sample.ZERO : DirectionalLegAnimationConfigClient.sample(previous, phase));
        double smoothBlend = (double)(blend * blend) * (3.0 - 2.0 * (double)blend);
        DirectionalLegAnimationConfigClient.Sample sample = DirectionalLegAnimationConfigClient.Sample.lerp(previousSample, current, smoothBlend);
        DirectionalLegAnimationClient.resetLegBase(model);
        DirectionalLegAnimationClient.applyTransform(model.leftLeg, sample.leftLeg());
        DirectionalLegAnimationClient.applyTransform(model.rightLeg, sample.rightLeg());
        DirectionalLegAnimationClient.applyUpperBodyTransform(model, sample.body());
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.jacket.copyFrom(model.body);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
    }

    private static void resetLegBase(PlayerModel model) {
        model.leftLeg.x = 1.9f;
        model.rightLeg.x = -1.9f;
        model.leftLeg.y = 12.0f;
        model.rightLeg.y = 12.0f;
        model.leftLeg.z = 0.0f;
        model.rightLeg.z = 0.0f;
        model.leftLeg.xRot = 0.0f;
        model.rightLeg.xRot = 0.0f;
        model.leftLeg.yRot = 0.0f;
        model.rightLeg.yRot = 0.0f;
        model.leftLeg.zRot = 0.0f;
        model.rightLeg.zRot = 0.0f;
    }

    private static void applyTransform(ModelPart part, DirectionalLegAnimationConfigClient.Transform transform) {
        if (part == null || transform == null) {
            return;
        }
        part.x += (float)transform.x;
        part.y += (float)transform.y;
        part.z += (float)transform.z;
        part.xRot += (float)Math.toRadians(transform.rotX);
        part.yRot += (float)Math.toRadians(transform.rotY);
        part.zRot += (float)Math.toRadians(transform.rotZ);
    }

    private static void applyUpperBodyTransform(PlayerModel model, DirectionalLegAnimationConfigClient.Transform transform) {
        if (transform == null) {
            return;
        }
        model.body.y += (float)Mth.clamp((double)transform.y, (double)-1.5, (double)1.5);
        model.body.xRot += (float)Math.toRadians(Mth.clamp((double)transform.rotX, (double)-15.0, (double)15.0));
        model.body.yRot += (float)Math.toRadians(Mth.clamp((double)transform.rotY, (double)-25.0, (double)25.0));
        model.body.zRot += (float)Math.toRadians(Mth.clamp((double)transform.rotZ, (double)-20.0, (double)20.0));
    }

    public static void beginExternalBlend(AbstractClientPlayer player, DirectionalLegAnimationConfigClient.Sample from) {
        if (player == null || from == null) {
            return;
        }
        MotionState state = STATES.get(player.getId());
        if (state == null || !state.playerId.equals(player.getUUID())) {
            state = new MotionState(player);
            STATES.put(player.getId(), state);
        }
        state.current = DirectionalLegAnimationConfigClient.Motion.NONE;
        state.previous = DirectionalLegAnimationConfigClient.Motion.NONE;
        state.pending = DirectionalLegAnimationConfigClient.Motion.NONE;
        state.pendingTicks = 0;
        state.blend = -0.25f;
        state.externalPrevious = from;
    }

    public static DirectionalLegAnimationConfigClient.Motion currentMotion(AbstractClientPlayer player) {
        if (player == null) {
            return DirectionalLegAnimationConfigClient.Motion.NONE;
        }
        MotionState state = STATES.get(player.getId());
        return state == null ? DirectionalLegAnimationConfigClient.Motion.NONE : state.current;
    }

    public static void clear() {
        STATES.clear();
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

    private static final class MotionState {
        private final UUID playerId;
        private DirectionalLegAnimationConfigClient.Motion current = DirectionalLegAnimationConfigClient.Motion.NONE;
        private DirectionalLegAnimationConfigClient.Motion previous = DirectionalLegAnimationConfigClient.Motion.NONE;
        private DirectionalLegAnimationConfigClient.Motion pending = DirectionalLegAnimationConfigClient.Motion.NONE;
        private int pendingTicks;
        private float blend = 1.0f;
        private double phase;
        private double phaseStep;
        private double smoothedSpeed;
        private double lastX;
        private double lastZ;
        private boolean positionInitialized;
        private int stationaryTicks;
        private DirectionalLegAnimationConfigClient.Sample externalPrevious;

        private MotionState(AbstractClientPlayer player) {
            this.playerId = player.getUUID();
            this.phase = DirectionalLegAnimationClient.wrap01((double)player.getId() * 0.173);
            this.lastX = player.getX();
            this.lastZ = player.getZ();
            this.positionInitialized = true;
        }

        private void resetImmediately() {
            this.current = DirectionalLegAnimationConfigClient.Motion.NONE;
            this.previous = DirectionalLegAnimationConfigClient.Motion.NONE;
            this.pending = DirectionalLegAnimationConfigClient.Motion.NONE;
            this.pendingTicks = 0;
            this.blend = 1.0f;
            this.phaseStep = 0.0;
            this.smoothedSpeed = 0.0;
            this.externalPrevious = null;
        }

        private MovementSample captureMovement(AbstractClientPlayer player) {
            double x = player.getX();
            double z = player.getZ();
            if (!this.positionInitialized) {
                this.lastX = x;
                this.lastZ = z;
                this.positionInitialized = true;
                this.stationaryTicks = 0;
                return new MovementSample(0.0, 0.0, 0.0, this.stationaryTicks);
            }
            double dx = x - this.lastX;
            double dz = z - this.lastZ;
            this.lastX = x;
            this.lastZ = z;
            double speed = Math.sqrt(dx * dx + dz * dz);
            if (!Double.isFinite(speed) || speed > 1.25) {
                this.stationaryTicks = 2;
                return new MovementSample(0.0, 0.0, 0.0, this.stationaryTicks);
            }
            this.stationaryTicks = speed < 0.001 ? Math.min(3, this.stationaryTicks + 1) : 0;
            return new MovementSample(dx, dz, speed, this.stationaryTicks);
        }
    }

    private record MovementSample(double dx, double dz, double speed, int stationaryTicks) {
    }
}
