package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.client.AmmoCheckThirdPersonClient;
import com.locknar.taczammocheck.prone.ProneClient;
import com.locknar.taczammocheck.prone.ProneNetwork;
import com.locknar.taczammocheck.prone.PronePoseMath;
import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.ProneTacticalAnimationConfigClient;
import com.locknar.taczammocheck.tactical.client.TaczProneReloadLegClient;
import com.locknar.taczammocheck.tactical.client.TaczThirdPersonPoseCompat;
import com.locknar.taczammocheck.tactical.client.ThirdPersonWeaponSwitchClient;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class ProneTacticalAnimationClient {
    private static final Map<Integer, Motion> MOTION = new HashMap<Integer, Motion>();
    private static final Map<Integer, Visual> RENDER = new HashMap<Integer, Visual>();
    private static final Map<Integer, float[]> YAW = new HashMap<Integer, float[]>();
    private static final Map<Integer, RenderSpace> SPACES = new HashMap<Integer, RenderSpace>();

    private ProneTacticalAnimationClient() {
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            ProneTacticalAnimationClient.clear();
            return;
        }
        for (AbstractClientPlayer p : mc.level.players()) {
            ProneNetwork.Sync state = ProneClient.state(p);
            if (state == null) {
                Motion old = MOTION.get(p.getId());
                if (old != null && old.standingAt == 0L) {
                    old.standingAt = System.nanoTime();
                }
                if (old != null && System.nanoTime() - old.standingAt < 500000000L) continue;
                MOTION.remove(p.getId());
                continue;
            }
            Motion m = MOTION.get(p.getId());
            if (m == null || !m.uuid.equals(p.getUUID())) {
                m = new Motion(p);
                MOTION.put(p.getId(), m);
            }
            m.tick(p, state.anchor());
        }
    }

    private static Visual visual(AbstractClientPlayer p, float partial) {
        Visual result;
        ProneNetwork.Sync state = ProneClient.state(p);
        if (state == null) {
            Motion old = MOTION.get(p.getId());
            if (old == null || old.lastVisual == null || old.exitRevision < 0L) {
                return null;
            }
            long now = System.nanoTime();
            if (old.standingAt == 0L) {
                old.standingAt = now;
            }
            if (now - old.standingAt >= 450000000L) {
                return null;
            }
            return new Visual(ProneTacticalAnimationConfigClient.Sample.ZERO, old.heading(p.yBodyRot), 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, true, TaczGunCompat.isTaczGun(p.getMainHandItem()), null, 0.0, 0.0, -1.0, 0.0);
        }
        boolean gun = TaczGunCompat.isTaczGun(ThirdPersonWeaponSwitchClient.getRenderedMainHand((LivingEntity)p));
        boolean transition = state.phase() == 1 || state.phase() == 3;
        float anchor = state.anchor();
        Motion m = MOTION.computeIfAbsent(p.getId(), id -> new Motion(p));
        if (state.phase() == 4) {
            if (m.turnRevision != state.revision()) {
                m.turnPose = m.lastVisual;
                m.turnRevision = state.revision();
            }
            double t = ProneClient.progress(p, partial);
            double recover = ProneTacticalAnimationClient.smooth(t / 0.82);
            double turn = ProneTacticalAnimationClient.smooth((t - 0.16) / 0.84);
            float yaw = anchor + Mth.wrapDegrees((float)(state.target() - anchor)) * (float)turn;
            Visual old = m.turnPose;
            ProneTacticalAnimationConfigClient.Sample start = old == null ? ProneTacticalAnimationConfigClient.sampleAngle(Mth.wrapDegrees((float)(p.getYRot() - anchor))) : old.sample;
            double roll = Mth.wrapDegrees((double)(old == null ? start.root().rotY : old.roll));
            ProneTacticalAnimationConfigClient.Sample sample = ProneTacticalAnimationConfigClient.Sample.lerp(start, ProneTacticalAnimationConfigClient.Sample.ZERO, recover);
            Visual result2 = new Visual(sample, yaw, 1.0, roll * (1.0 - recover), Mth.wrapDegrees((float)(p.getYRot() - yaw)), 0.0, 1.0, 0.0, 0.0, false, gun, null, 0.0, Math.sin(Math.PI * t), t, Math.signum(Mth.wrapDegrees((float)(state.target() - anchor))));
            m.heading = yaw;
            m.headingReady = true;
            m.aim = result2.aim;
            m.roll = result2.roll;
            m.rollNanos = System.nanoTime();
            m.lastVisual = result2;
            return result2;
        }
        if (transition) {
            Visual result3;
            double roll;
            m.standingAt = 0L;
            m.transitionUntil = System.nanoTime() + 350000000L;
            ProneTacticalAnimationConfigClient.Motion action = state.phase() == 1 ? ProneTacticalAnimationConfigClient.Motion.PRONE_ENTER : ProneTacticalAnimationConfigClient.Motion.PRONE_EXIT;
            double progress = ProneClient.progress(p, partial);
            Visual from = null;
            double recovery = 0.0;
            double sampleTime = progress;
            if (action == ProneTacticalAnimationConfigClient.Motion.PRONE_EXIT) {
                if (m.exitRevision != state.revision()) {
                    m.exitPose = m.displayed == null ? m.lastVisual : m.displayed;
                    m.exitRevision = state.revision();
                }
                if ((from = m.exitPose) != null) {
                    recovery = 1.0 - ProneTacticalAnimationClient.smooth(progress / 0.24);
                    sampleTime = Mth.clamp((double)((progress - 0.24) / 0.76), (double)0.0, (double)1.0);
                }
            }
            ProneTacticalAnimationConfigClient.Sample sample = ProneTacticalAnimationConfigClient.sample(action, sampleTime);
            double blend = action == ProneTacticalAnimationConfigClient.Motion.PRONE_ENTER ? 1.0 + sample.root().rotX / 90.0 : sample.root().rotX / 90.0;
            float turnAnchor = anchor;
            boolean entering = action == ProneTacticalAnimationConfigClient.Motion.PRONE_ENTER;
            double aim = 0.0;
            double d = roll = from == null ? 0.0 : Mth.wrapDegrees((double)from.roll) * recovery;
            if (entering) {
                if (!m.initialized) {
                    m.aim = 0.0;
                    m.initialized = true;
                    m.nanos = System.nanoTime();
                }
                aim = m.angle((double)Mth.wrapDegrees((float)(p.getYRot() - anchor)) * ProneTacticalAnimationClient.smooth((progress - 0.4) / 0.6));
                roll = m.roll(aim, ProneTacticalAnimationConfigClient.sampleAngle((double)aim).root().rotY) * ProneTacticalAnimationClient.smooth((progress - 0.65) / 0.35);
                m.heading = anchor;
                m.headingReady = true;
                m.headingNanos = System.nanoTime();
            } else {
                float target = anchor + Mth.wrapDegrees((float)(p.yBodyRot - anchor)) * (float)ProneTacticalAnimationClient.smooth((1.0 - blend - 0.35) / 0.65);
                turnAnchor = m.heading(target);
                aim = from == null ? 0.0 : from.aim * recovery;
            }
            m.lastVisual = result3 = new Visual(sample, turnAnchor, Mth.clamp((double)blend, (double)0.0, (double)1.0), roll, aim, 0.0, 1.0, 0.0, 0.0, true, gun, from, recovery, 0.0, entering ? progress : -1.0, 0.0);
            return result3;
        }
        anchor = m.heading(anchor);
        double target = Mth.wrapDegrees((float)(p.getYRot() - anchor));
        double angle = m.angle(target);
        double movement = ProneTacticalAnimationClient.smooth(Mth.lerp((double)partial, (double)m.previousWeight, (double)m.weight));
        ProneTacticalAnimationConfigClient.Sample sample = ProneTacticalAnimationConfigClient.sampleAngle(angle);
        double weight = 1.0;
        ProneTacticalAnimationConfigClient.Sample weighted = ProneTacticalAnimationConfigClient.Sample.lerp(ProneTacticalAnimationConfigClient.Sample.ZERO, sample, weight);
        double roll = m.roll(angle, sample.root().rotY);
        m.lastVisual = result = new Visual(weighted, anchor, 1.0, roll, angle * weight, movement, m.forward, m.side, Mth.lerp((double)partial, (double)m.previousPhase, (double)m.phase), false, gun, null, 0.0, 0.0, -1.0, 0.0);
        return result;
    }

    public static Visual currentVisual(AbstractClientPlayer player) {
        return RENDER.get(player.getId());
    }

    public static void beginPlayerRendererRender(AbstractClientPlayer p, float partial, PoseStack stack) {
        if (p == null || p.isSpectator()) {
            return;
        }
        Visual pose = ProneTacticalAnimationClient.visual(p, partial);
        if (pose == null) {
            return;
        }
        Motion motion = MOTION.get(p.getId());
        if (motion != null) {
            ProneNetwork.Sync state = ProneClient.state(p);
            int phase = state == null ? 0 : state.phase();
            long now = System.nanoTime();
            if (motion.rootPhase != phase) {
                motion.rootFrom = motion.displayed;
                motion.rootPhase = phase;
                motion.rootPhaseAt = now;
            }
            double blend = ProneTacticalAnimationClient.smooth((double)(now - motion.rootPhaseAt) / 2.0E8);
            if (motion.rootFrom != null && blend < 1.0) {
                Visual from = motion.rootFrom;
                ProneTacticalAnimationConfigClient.Sample a = pose.sample;
                ProneTacticalAnimationConfigClient.Transform root = ProneTacticalAnimationConfigClient.Transform.lerp(from.sample.root(), a.root(), blend);
                ProneTacticalAnimationConfigClient.Sample sample = new ProneTacticalAnimationConfigClient.Sample(a.leftLeg(), a.rightLeg(), a.body(), a.head(), a.leftArm(), a.rightArm(), root);
                pose = new Visual(sample, from.anchor + (float)((double)Mth.wrapDegrees((float)(pose.anchor - from.anchor)) * blend), Mth.lerp((double)blend, (double)from.prone, (double)pose.prone), from.roll + Mth.wrapDegrees((double)(pose.roll - from.roll)) * blend, pose.aim, pose.movement, pose.forward, pose.side, pose.stride, pose.transition, pose.gun, pose.recovering, pose.recovery, pose.support, pose.turnProgress, pose.turnDirection);
            }
            motion.displayed = pose;
        }
        RENDER.put(p.getId(), pose);
        Matrix4f matrix = stack.last().pose();
        SPACES.put(p.getId(), new RenderSpace(stack, matrix.transformPosition(new Vector3f()), matrix.transformDirection(new Vector3f(0.0f, 1.0f, 0.0f)).normalize()));
        YAW.put(p.getId(), new float[]{p.yBodyRot, p.yBodyRotO});
        p.yBodyRot = pose.anchor;
        p.yBodyRotO = pose.anchor;
    }

    public static void endPlayerRendererRender(AbstractClientPlayer p) {
        float[] old = YAW.remove(p.getId());
        if (old != null) {
            p.yBodyRot = old[0];
            p.yBodyRotO = old[1];
        }
        RENDER.remove(p.getId());
        SPACES.remove(p.getId());
    }

    public static boolean applyLockedStationaryProneRotations(AbstractClientPlayer p, PoseStack stack, float partial) {
        Visual v = RENDER.get(p.getId());
        if (v == null) {
            return false;
        }
        stack.mulPose(Axis.YP.rotationDegrees(180.0f - v.anchor));
        if (v.transition) {
            stack.translate(0.0, v.sample.root().y / 16.0, v.sample.root().z / 16.0);
        }
        stack.mulPose(Axis.XP.rotationDegrees((float)(-90.0 * v.prone)));
        stack.translate(0.0, -v.prone, 0.15 * v.prone);
        stack.mulPose(Axis.YP.rotationDegrees((float)v.roll));
        if (v.transition && v.recovering != null) {
            ProneTacticalAnimationConfigClient.Transform r = v.recovering.sample.root();
            stack.translate(r.x / 16.0 * v.recovery, r.y / 16.0 * v.recovery, r.z / 16.0 * v.recovery);
        }
        if (!v.transition) {
            stack.translate(v.sample.root().x / 16.0, v.sample.root().y / 16.0, v.sample.root().z / 16.0);
        }
        return true;
    }

    public static void applyPoseAfterSetup(AbstractClientPlayer p, PlayerModel model) {
        double settle;
        Visual v = RENDER.get(p.getId());
        if (v == null) {
            return;
        }
        if (ProneClient.state(p) == null) {
            ProneTacticalAnimationClient.smoothTransitionPose(p, model);
            ProneTacticalAnimationClient.copyWear(model);
            return;
        }
        PartPose animatedRight = model.rightArm.storePose();
        PartPose animatedLeft = model.leftArm.storePose();
        PartPose animatedHead = model.head.storePose();
        if (!v.gun) {
            animatedHead = PartPose.offsetAndRotation((float)0.0f, (float)0.0f, (float)0.0f, (float)(p.getXRot() * ((float)Math.PI / 180)), (float)(Mth.wrapDegrees((float)(p.yHeadRot - v.anchor)) * ((float)Math.PI / 180)), (float)0.0f);
            animatedRight = model.rightArm.getInitialPose();
            animatedLeft = model.leftArm.getInitialPose();
        }
        for (ModelPart part : List.of(model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg)) {
            part.loadPose(part.getInitialPose());
        }
        if (v.gun) {
            TaczThirdPersonPoseCompat.applyGunHoldPose(p, model, ThirdPersonWeaponSwitchClient.getRenderedMainHand((LivingEntity)p));
        }
        PartPose nativeRight = model.rightArm.storePose();
        PartPose nativeLeft = model.leftArm.storePose();
        for (ModelPart part : List.of(model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg)) {
            part.loadPose(part.getInitialPose());
        }
        ProneTacticalAnimationConfigClient.Sample s = v.sample;
        if (v.transition) {
            ProneTacticalAnimationClient.apply(model.leftLeg, s.leftLeg());
            ProneTacticalAnimationClient.apply(model.rightLeg, s.rightLeg());
            ProneTacticalAnimationClient.apply(model.head, s.head());
            ProneTacticalAnimationClient.apply(model.leftArm, s.leftArm());
            ProneTacticalAnimationClient.apply(model.rightArm, s.rightArm());
            if (v.gun) {
                double grip = ProneTacticalAnimationClient.smooth(Mth.clamp((double)((v.prone - 0.9) / 0.1), (double)0.0, (double)1.0));
                model.rightArm.xRot = (float)Math.toRadians(-90.0 - 90.0 * v.prone - s.body().rotX);
                model.rightArm.yRot = 0.0f;
                model.rightArm.zRot = 0.0f;
                double left = -180.0f + Mth.wrapDegrees((float)((nativeLeft.xRot - nativeRight.xRot) * 57.295776f));
                model.leftArm.xRot = Mth.lerp((float)((float)grip), (float)model.leftArm.xRot, (float)((float)Math.toRadians(left)));
                model.leftArm.yRot = Mth.lerp((float)((float)grip), (float)model.leftArm.yRot, (float)(nativeLeft.yRot - nativeRight.yRot));
                model.leftArm.zRot = Mth.lerp((float)((float)grip), (float)model.leftArm.zRot, (float)(nativeLeft.zRot - nativeRight.zRot));
                model.rightArm.x = Mth.lerp((float)((float)grip), (float)model.rightArm.x, (float)nativeRight.x);
                model.rightArm.y = Mth.lerp((float)((float)grip), (float)model.rightArm.y, (float)nativeRight.y);
                model.rightArm.z = Mth.lerp((float)((float)grip), (float)model.rightArm.z, (float)nativeRight.z);
                model.leftArm.x = Mth.lerp((float)((float)grip), (float)model.leftArm.x, (float)nativeLeft.x);
                model.leftArm.y = Mth.lerp((float)((float)grip), (float)model.leftArm.y, (float)nativeLeft.y);
                model.leftArm.z = Mth.lerp((float)((float)grip), (float)model.leftArm.z, (float)nativeLeft.z);
            }
            ProneTacticalAnimationClient.bendAtWaist(model, s.body());
            if (v.recovering != null && v.recovery > 0.0) {
                List<ModelPart> parts = List.of(model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg);
                List<PartPose> target = parts.stream().map(ModelPart::storePose).toList();
                for (ModelPart part : parts) {
                    part.loadPose(part.getInitialPose());
                }
                ProneTacticalAnimationClient.stationary(model, p, v.recovering, nativeRight, nativeLeft);
                for (int i = 0; i < parts.size(); ++i) {
                    ProneTacticalAnimationClient.blendPose(parts.get(i), target.get(i), 1.0 - v.recovery);
                }
            }
        } else {
            ProneTacticalAnimationClient.stationary(model, p, v, nativeRight, nativeLeft);
        }
        if (v.transition && v.turnProgress >= 0.0 && (settle = ProneTacticalAnimationClient.smooth((v.turnProgress - 0.65) / 0.35)) > 0.0) {
            List<ModelPart> parts = List.of(model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg);
            List<PartPose> previous = parts.stream().map(ModelPart::storePose).toList();
            for (ModelPart part : parts) {
                part.loadPose(part.getInitialPose());
            }
            Visual pose = new Visual(ProneTacticalAnimationConfigClient.sampleAngle(v.aim), v.anchor, 1.0, v.roll, v.aim, 0.0, 1.0, 0.0, 0.0, false, v.gun, null, 0.0, 0.0, -1.0, 0.0);
            ProneTacticalAnimationClient.stationary(model, p, pose, nativeRight, nativeLeft);
            List<PartPose> target = parts.stream().map(ModelPart::storePose).toList();
            for (int i = 0; i < parts.size(); ++i) {
                parts.get(i).loadPose(previous.get(i));
                ProneTacticalAnimationClient.blendPose(parts.get(i), target.get(i), settle);
            }
        }
        if (v.gun && !v.transition && (TaczProneReloadLegClient.isTaczReloadAnimationActive(p) || AmmoCheckThirdPersonClient.active(p))) {
            ProneTacticalAnimationClient.retargetReload(model, p, v, nativeRight, animatedRight, animatedLeft);
        }
        if (v.transition && v.recovering != null) {
            double handoff = ProneTacticalAnimationClient.smooth((0.45 - v.prone) / 0.45);
            ProneTacticalAnimationClient.blendUpright(model.head, animatedHead, v.prone, handoff);
            ProneTacticalAnimationClient.blendUpright(model.rightArm, animatedRight, v.prone, handoff);
            ProneTacticalAnimationClient.blendUpright(model.leftArm, animatedLeft, v.prone, handoff);
        }
        ProneTacticalAnimationClient.keepAboveGround(p, model);
        ProneTacticalAnimationClient.smoothTransitionPose(p, model);
        ProneTacticalAnimationClient.copyWear(model);
    }

    private static void copyWear(PlayerModel<?> model) {
        model.hat.copyFrom(model.head);
        model.jacket.copyFrom(model.body);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
    }

    private static void smoothTransitionPose(AbstractClientPlayer p, PlayerModel<?> model) {
        int phase;
        Motion m = MOTION.get(p.getId());
        if (m == null) {
            return;
        }
        long now = System.nanoTime();
        double dt = m.renderNanos == 0L ? 1.0 : (double)(now - m.renderNanos) / 1.0E9;
        m.renderNanos = now;
        List<ModelPart> parts = List.of(model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg);
        ProneNetwork.Sync state = ProneClient.state(p);
        int n = phase = state == null ? 0 : state.phase();
        if (m.posePhase != phase) {
            m.posePhase = phase;
            m.posePhaseAt = now;
            m.phasePose = m.rendered == null ? null : (PartPose[])m.rendered.clone();
        }
        double handoff = ProneTacticalAnimationClient.smooth((double)(now - m.posePhaseAt) / 2.4E8);
        if (m.phasePose != null && handoff < 1.0) {
            for (int i = 0; i < parts.size(); ++i) {
                ModelPart part = parts.get(i);
                PartPose target = part.storePose();
                part.loadPose(m.phasePose[i]);
                ProneTacticalAnimationClient.blendPose(part, target, handoff);
            }
        } else if (m.rendered != null && now < m.transitionUntil) {
            double alpha = 1.0 - Math.exp(-Math.max(0.0, dt) / 0.065);
            for (int i = 0; i < parts.size(); ++i) {
                ModelPart part = parts.get(i);
                PartPose target = part.storePose();
                part.loadPose(m.rendered[i]);
                ProneTacticalAnimationClient.blendPose(part, target, alpha);
            }
        }
        m.rendered = (PartPose[])parts.stream().map(ModelPart::storePose).toArray(PartPose[]::new);
    }

    private static void stationary(PlayerModel<?> model, AbstractClientPlayer p, Visual v, PartPose nativeRight, PartPose nativeLeft) {
        ProneTacticalAnimationConfigClient.Sample s = v.sample;
        model.leftLeg.zRot = -0.09f;
        model.rightLeg.zRot = 0.09f;
        ProneTacticalAnimationClient.bendAtWaist(model, s.body());
        ProneTacticalAnimationClient.apply(model.leftLeg, s.leftLeg());
        ProneTacticalAnimationClient.apply(model.rightLeg, s.rightLeg());
        float yaw = (float)Math.toRadians(v.aim);
        float pitch = (float)Math.toRadians(Mth.clamp((float)p.getXRot(), (float)-70.0f, (float)70.0f));
        float roll = (float)Math.toRadians(v.roll);
        float pi = (float)Math.PI;
        Quaternionf body = new Quaternionf().rotateX(-pi / 2.0f).rotateY(roll).rotateZ(pi);
        Quaternionf head = new Quaternionf((Quaternionfc)body).invert().rotateY(-yaw).rotateZ(pi).rotateX(pitch);
        ProneTacticalAnimationClient.rotate(model.head, head);
        Quaternionf arm = new Quaternionf((Quaternionfc)head).rotateX(-pi / 2.0f);
        ProneTacticalAnimationClient.rotate(model.leftArm, new Quaternionf((Quaternionfc)arm).rotateX((float)Math.toRadians(4.0)).rotateZ(-0.12f));
        ProneTacticalAnimationClient.rotate(model.rightArm, new Quaternionf((Quaternionfc)arm).rotateX((float)Math.toRadians(4.0)).rotateZ(0.12f));
        if (v.gun) {
            Quaternionf right = new Quaternionf().rotationZYX(nativeRight.zRot, nativeRight.yRot, nativeRight.xRot).invert();
            Quaternionf relativeLeft = new Quaternionf((Quaternionfc)right).mul((Quaternionfc)new Quaternionf().rotationZYX(nativeLeft.zRot, nativeLeft.yRot, nativeLeft.xRot));
            ProneTacticalAnimationClient.rotate(model.rightArm, arm);
            Quaternionf leftRotation = new Quaternionf((Quaternionfc)arm).mul((Quaternionfc)relativeLeft);
            Quaternionf nativeL = new Quaternionf().rotationZYX(nativeLeft.zRot, nativeLeft.yRot, nativeLeft.xRot);
            Vector3f wrist = nativeL.transform(new Vector3f(0.0f, 10.0f, 0.0f)).add(nativeLeft.x - nativeRight.x, nativeLeft.y - nativeRight.y, nativeLeft.z - nativeRight.z);
            right.transform(wrist);
            arm.transform(wrist);
            wrist.add(model.rightArm.x - model.leftArm.x, model.rightArm.y - model.leftArm.y, model.rightArm.z - model.leftArm.z);
            if (wrist.lengthSquared() > 0.01f) {
                Vector3f direction = leftRotation.transform(new Vector3f(0.0f, 1.0f, 0.0f));
                leftRotation.premul((Quaternionfc)new Quaternionf().rotationTo((Vector3fc)direction, (Vector3fc)wrist.normalize()));
            }
            ProneTacticalAnimationClient.rotate(model.leftArm, leftRotation);
        }
        ProneTacticalAnimationClient.apply(model.head, s.head());
        if (!v.gun) {
            ProneTacticalAnimationClient.apply(model.leftArm, s.leftArm());
            ProneTacticalAnimationClient.apply(model.rightArm, s.rightArm());
        }
        if (v.support > 0.0) {
            double support = v.support * ProneTacticalAnimationClient.smooth((100.0 - Math.abs(v.roll)) / 80.0);
            ProneTacticalAnimationClient.blendPose(model.leftArm, PartPose.offsetAndRotation((float)5.0f, (float)2.0f, (float)1.0f, (float)((float)Math.toRadians(-152.0)), (float)0.0f, (float)-0.22f), support);
            double t = v.turnProgress;
            double lead = ProneTacticalAnimationClient.smooth(t / 0.25) * (1.0 - ProneTacticalAnimationClient.smooth((t - 0.38) / 0.4));
            double trail = ProneTacticalAnimationClient.smooth((t - 0.2) / 0.3) * (1.0 - ProneTacticalAnimationClient.smooth((t - 0.65) / 0.35));
            double left = v.turnDirection < 0.0 ? trail : lead;
            double rightStep = v.turnDirection < 0.0 ? lead : trail;
            model.leftLeg.xRot += (float)(left * 0.4);
            model.rightLeg.xRot += (float)(rightStep * 0.4);
            model.leftLeg.zRot -= (float)(left * 0.3);
            model.rightLeg.zRot += (float)(rightStep * 0.3);
            model.leftLeg.y -= (float)(left * 1.25);
            model.rightLeg.y -= (float)(rightStep * 1.25);
        }
        ProneTacticalAnimationClient.gait(model, v);
        for (ModelPart leg : List.of(model.leftLeg, model.rightLeg)) {
            Quaternionf local = new Quaternionf().rotationZYX(leg.zRot, leg.yRot, leg.xRot);
            double brace = 65.0 * Math.sin(Math.toRadians(v.roll));
            ProneTacticalAnimationClient.rotate(leg, new Quaternionf().rotateY((float)Math.toRadians(brace)).mul((Quaternionfc)local));
        }
    }

    private static void blendUpright(ModelPart part, PartPose upright, double prone, double weight) {
        if (weight <= 0.0) {
            return;
        }
        Quaternionf q = new Quaternionf().rotateX((float)Math.toRadians(-90.0 * prone)).mul((Quaternionfc)new Quaternionf().rotationZYX(upright.zRot, upright.yRot, upright.xRot));
        Vector3f angles = PronePoseMath.eulerZYX((Quaternionfc)q);
        ProneTacticalAnimationClient.blendPose(part, PartPose.offsetAndRotation((float)upright.x, (float)upright.y, (float)upright.z, (float)angles.x, (float)angles.y, (float)angles.z), weight);
    }

    private static void retargetReload(PlayerModel<?> model, AbstractClientPlayer p, Visual v, PartPose grip, PartPose right, PartPose left) {
        float pi = (float)Math.PI;
        Quaternionf body = new Quaternionf().rotateX(-pi / 2.0f).rotateY((float)Math.toRadians(v.roll)).rotateZ(pi);
        Quaternionf aim = body.invert().rotateY((float)Math.toRadians(-v.aim)).rotateZ(pi).rotateX((float)Math.toRadians(Mth.clamp((float)p.getXRot(), (float)-70.0f, (float)70.0f))).rotateX(-pi / 2.0f);
        Quaternionf basis = aim.mul((Quaternionfc)new Quaternionf().rotationZYX(grip.zRot, grip.yRot, grip.xRot).invert());
        ModelPart[] arms = new ModelPart[]{model.rightArm, model.leftArm};
        PartPose[] poses = new PartPose[]{right, left};
        for (int i = 0; i < 2; ++i) {
            ModelPart part = arms[i];
            PartPose pose = poses[i];
            PartPose initial = part.getInitialPose();
            Vector3f offset = basis.transform(new Vector3f(pose.x - initial.x, pose.y - initial.y, pose.z - initial.z));
            part.x += offset.x;
            part.y += offset.y;
            part.z += offset.z;
            ProneTacticalAnimationClient.rotate(part, new Quaternionf((Quaternionfc)basis).mul((Quaternionfc)new Quaternionf().rotationZYX(pose.zRot, pose.yRot, pose.xRot)));
        }
    }

    private static void rotate(ModelPart part, Quaternionf rotation) {
        Vector3f e = PronePoseMath.eulerZYX((Quaternionfc)rotation);
        part.xRot = e.x;
        part.yRot = e.y;
        part.zRot = e.z;
    }

    private static void blendPose(ModelPart p, PartPose target, double t) {
        float f = (float)t;
        p.x = Mth.lerp((float)f, (float)p.x, (float)target.x);
        p.y = Mth.lerp((float)f, (float)p.y, (float)target.y);
        p.z = Mth.lerp((float)f, (float)p.z, (float)target.z);
        Quaternionf old = new Quaternionf().rotationZYX(p.zRot, p.yRot, p.xRot);
        ProneTacticalAnimationClient.rotate(p, old.slerp((Quaternionfc)new Quaternionf().rotationZYX(target.zRot, target.yRot, target.xRot), f));
    }

    private static void keepAboveGround(AbstractClientPlayer player, PlayerModel<?> model) {
        double correction;
        RenderSpace space = SPACES.get(player.getId());
        if (space == null) {
            return;
        }
        Visual v = RENDER.get(player.getId());
        List<ModelPart> contact = v != null && !v.transition ? List.of(model.body, model.head) : List.of(model.head, model.body, model.leftArm, model.rightArm, model.leftLeg, model.rightLeg);
        double lift = Math.max(0.0, 0.005 - ProneTacticalAnimationClient.lowest(space, contact));
        if (Double.isFinite(lift) && lift < 2.0) {
            Vector3f local = new Matrix4f((Matrix4fc)space.stack.last().pose()).invert().transformDirection(new Vector3f((Vector3fc)space.up).mul((float)lift));
            space.stack.translate(local.x, local.y, local.z);
        }
        if (v != null && !v.transition && v.gun && v.movement < 0.01 && !TaczProneReloadLegClient.isTaczReloadAnimationActive(player) && !AmmoCheckThirdPersonClient.active(player) && (correction = Math.max(0.0, 0.003 - ProneTacticalAnimationClient.lowest(space, List.of(model.leftArm, model.rightArm)))) > 0.0 && correction < 0.4) {
            Vector3f local = new Matrix4f((Matrix4fc)space.stack.last().pose()).invert().transformDirection(new Vector3f((Vector3fc)space.up).mul((float)correction)).mul(16.0f);
            for (ModelPart part : List.of(model.leftArm, model.rightArm)) {
                part.x += local.x;
                part.y += local.y;
                part.z += local.z;
            }
        }
        if (v != null && !v.transition) {
            for (ModelPart part : List.of(model.leftArm, model.rightArm, model.leftLeg, model.rightLeg)) {
                double correction2 = Math.max(0.0, 0.003 - ProneTacticalAnimationClient.lowest(space, List.of(part)));
                if (!(correction2 > 0.0) || !(correction2 < 0.4)) continue;
                Vector3f local = new Matrix4f((Matrix4fc)space.stack.last().pose()).invert().transformDirection(new Vector3f((Vector3fc)space.up).mul((float)correction2)).mul(16.0f);
                part.x += local.x;
                part.y += local.y;
                part.z += local.z;
            }
        }
    }

    private static double lowest(RenderSpace space, List<ModelPart> parts) {
        double[] min = new double[]{Double.POSITIVE_INFINITY};
        for (ModelPart part : parts) {
            part.visit(space.stack, (pose, path, index, cube) -> {
                for (int i = 0; i < 8; ++i) {
                    Vector3f point = new Vector3f((i & 1) == 0 ? cube.minX : cube.maxX, (i & 2) == 0 ? cube.minY : cube.maxY, (i & 4) == 0 ? cube.minZ : cube.maxZ).div(16.0f);
                    pose.pose().transformPosition(point);
                    point.sub((Vector3fc)space.origin);
                    min[0] = Math.min(min[0], (double)point.dot((Vector3fc)space.up));
                }
            });
        }
        return min[0];
    }

    private static void gait(PlayerModel<?> m, Visual v) {
        if (v.movement <= 0.0) {
            return;
        }
        double f = v.forward;
        double side = v.side;
        double w = v.movement;
        double cycle = v.stride * (double)(f < -0.15 ? -1 : 1);
        double a = Math.sin(cycle);
        double b = Math.cos(cycle);
        double left = (1.0 + b) * 0.5;
        double right = (1.0 - b) * 0.5;
        m.leftLeg.xRot += (float)((0.04 + left * 0.22) * w);
        m.rightLeg.xRot += (float)((0.04 + right * 0.22) * w);
        m.leftLeg.yRot += (float)(side * 0.2 * w);
        m.rightLeg.yRot += (float)(side * 0.2 * w);
        m.leftLeg.zRot -= (float)((0.1 + left * 0.2) * w);
        m.rightLeg.zRot += (float)((0.1 + right * 0.2) * w);
        m.leftLeg.y -= (float)(left * 2.8 * w);
        m.rightLeg.y -= (float)(right * 2.8 * w);
        m.leftLeg.x += (float)((a * 0.65 + side * left * 1.2) * w);
        m.rightLeg.x += (float)((-a * 0.65 + side * right * 1.2) * w);
        m.leftLeg.z += (float)(Math.max(0.0, a) * 1.25 * w);
        m.rightLeg.z += (float)(Math.max(0.0, -a) * 1.25 * w);
        if (!v.gun) {
            ProneTacticalAnimationClient.blendPose(m.leftArm, PartPose.offsetAndRotation((float)5.0f, (float)(2.0f + (float)(a * 0.9)), (float)0.0f, (float)((float)Math.toRadians(-174.0)), (float)0.0f, (float)-0.12f), w);
            ProneTacticalAnimationClient.blendPose(m.rightArm, PartPose.offsetAndRotation((float)-5.0f, (float)(2.0f - (float)(a * 0.9)), (float)0.0f, (float)((float)Math.toRadians(-174.0)), (float)0.0f, (float)0.12f), w);
        }
        if (v.gun) {
            m.rightArm.y += (float)(a * 1.5 * w);
            m.rightArm.x += (float)((b * 0.35 + side * a * 0.4) * w);
            m.rightArm.z += (float)(Math.max(0.0, a) * 0.9 * w);
            ProneTacticalAnimationClient.blendPose(m.leftArm, PartPose.offsetAndRotation((float)((float)(5.4 - a * 0.65 + side * right)), (float)((float)(2.0 - a * 2.6)), (float)((float)(Math.max(0.0, -a) * 1.35)), (float)((float)Math.toRadians(-166.0 - 7.0 * b)), (float)0.0f, (float)-0.12f), w);
        }
    }

    private static void bendAtWaist(PlayerModel<?> m, ProneTacticalAnimationConfigClient.Transform t) {
        double r = Math.toRadians(t.rotX);
        double c = Math.cos(r);
        double s = Math.sin(r);
        m.body.x = (float)t.x;
        m.body.y = (float)(t.y + 12.0 - 12.0 * c);
        m.body.z = (float)(t.z - 12.0 * s);
        m.body.xRot = (float)r;
        for (ModelPart p : List.of(m.head, m.leftArm, m.rightArm)) {
            double y = p.y - 12.0f;
            double z = p.z;
            p.y = (float)(12.0 + y * c - z * s + t.y);
            p.z = (float)(y * s + z * c + t.z);
            p.xRot += (float)r;
        }
    }

    private static void apply(ModelPart p, ProneTacticalAnimationConfigClient.Transform t) {
        p.x = (float)((double)p.x + t.x);
        p.y = (float)((double)p.y + t.y);
        p.z = (float)((double)p.z + t.z);
        p.xRot = (float)((double)p.xRot + Math.toRadians(t.rotX));
        p.yRot = (float)((double)p.yRot + Math.toRadians(t.rotY));
        p.zRot = (float)((double)p.zRot + Math.toRadians(t.rotZ));
    }

    private static double smooth(double x) {
        x = Mth.clamp((double)x, (double)0.0, (double)1.0);
        return x * x * (3.0 - 2.0 * x);
    }

    private static double proneArmDegrees(float radians) {
        return -180.0f + Mth.wrapDegrees((float)(radians * 57.295776f + 180.0f));
    }

    public static void onRenderPlayerPre(RenderPlayerEvent.Pre e) {
    }

    public static void onRenderPlayerPost(RenderPlayerEvent.Post e) {
    }

    public static void clear() {
        MOTION.clear();
        RENDER.clear();
        YAW.clear();
        SPACES.clear();
    }

    private static final class Motion {
        PartPose[] rendered;
        PartPose[] phasePose;
        long renderNanos;
        long transitionUntil;
        long standingAt;
        long posePhaseAt;
        long rootPhaseAt;
        int posePhase = -1;
        int rootPhase = -1;
        Visual displayed;
        Visual rootFrom;
        UUID uuid;
        double x;
        double z;
        double phase;
        double previousPhase;
        double weight;
        double previousWeight;
        double forward = 1.0;
        double side;
        double aim;
        double freeze;
        boolean initialized;
        boolean moving;
        long nanos;
        long exitRevision = -1L;
        long turnRevision = -1L;
        long headingNanos;
        Visual lastVisual;
        Visual exitPose;
        Visual turnPose;
        float heading;
        boolean headingReady;
        double roll;
        double rollSide = 1.0;
        long rollNanos;

        Motion(AbstractClientPlayer p) {
            this.uuid = p.getUUID();
            this.x = p.getX();
            this.z = p.getZ();
        }

        void tick(AbstractClientPlayer p, float anchor) {
            boolean target;
            double dx = p.getX() - this.x;
            double dz = p.getZ() - this.z;
            this.x = p.getX();
            this.z = p.getZ();
            double distance = Math.hypot(dx, dz);
            if (distance > 1.0) {
                distance = 0.0;
            }
            boolean bl = target = distance > 0.002;
            if (target != this.moving) {
                this.freeze = this.aim;
                this.moving = target;
            }
            this.previousWeight = this.weight;
            this.weight = Mth.clamp((double)(this.weight + (double)(this.moving ? 1 : -1) / 6.0), (double)0.0, (double)1.0);
            this.previousPhase = this.phase;
            if (target) {
                double yaw = Math.toRadians(anchor);
                this.forward = (-dx * Math.sin(yaw) + dz * Math.cos(yaw)) / distance;
                this.side = (dx * Math.cos(yaw) + dz * Math.sin(yaw)) / distance;
                this.phase += distance * 7.5;
            }
        }

        double angle(double target) {
            long now = System.nanoTime();
            double dt = this.nanos == 0L ? 1.0 : (double)(now - this.nanos) / 1.0E9;
            this.nanos = now;
            if (!this.initialized) {
                this.freeze = this.aim = target;
                this.initialized = true;
            }
            double alpha = dt > 0.25 ? 1.0 : 1.0 - Math.exp(-Math.max(0.0, dt) / 0.1);
            double step = Mth.wrapDegrees((double)(target - this.aim)) * alpha;
            if (dt <= 0.25) {
                step = Mth.clamp((double)step, (double)(-540.0 * dt), (double)(540.0 * dt));
            }
            this.aim = Mth.wrapDegrees((double)(this.aim + step));
            return this.aim;
        }

        float heading(float target) {
            long now = System.nanoTime();
            double dt = this.headingNanos == 0L ? 1.0 : (double)(now - this.headingNanos) / 1.0E9;
            this.headingNanos = now;
            if (!this.headingReady) {
                this.heading = target;
                this.headingReady = true;
            }
            this.heading += Mth.wrapDegrees((float)(target - this.heading)) * (float)(1.0 - Math.exp(-Math.max(0.0, dt) / 0.06));
            return this.heading;
        }

        double roll(double angle, double authored) {
            long now = System.nanoTime();
            double dt = this.rollNanos == 0L ? 0.016 : (double)(now - this.rollNanos) / 1.0E9;
            this.rollNanos = now;
            double delta = Mth.wrapDegrees((double)(authored - this.roll));
            this.roll += Mth.clamp((double)(delta * (1.0 - Math.exp(-Math.max(0.0, dt) / 0.12))), (double)(-330.0 * dt), (double)(330.0 * dt));
            return this.roll;
        }
    }

    public record Visual(ProneTacticalAnimationConfigClient.Sample sample, float anchor, double prone, double roll, double aim, double movement, double forward, double side, double stride, boolean transition, boolean gun, Visual recovering, double recovery, double support, double turnProgress, double turnDirection) {
    }

    private record RenderSpace(PoseStack stack, Vector3f origin, Vector3f up) {
    }
}
