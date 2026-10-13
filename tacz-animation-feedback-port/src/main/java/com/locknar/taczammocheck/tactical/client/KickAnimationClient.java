package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.client.ActionAnimationSampler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class KickAnimationClient {
    private static final long NORMAL_ANIMATION_MS = 520L;
    private static final long BACK_KICK_TURN_MS = 300L;
    private static final long BACK_KICK_HOLD_MS = 250L;
    private static final long BACK_KICK_RESTORE_MS = 300L;
    private static final long BACK_KICK_ANIMATION_MS = 850L;
    private static final long BACK_KICK_LEG_START_MS = 200L;
    private static final long BACK_KICK_LEG_END_MS = 550L;
    private static final float BACK_KICK_TURN_END = 0.3529412f;
    private static final float BACK_KICK_HOLD_END = 0.64705884f;
    private static final float BACK_KICK_LEG_START = 0.23529412f;
    private static final float BACK_KICK_LEG_END = 0.64705884f;
    private static final Map<Integer, Long> START_TIMES = new HashMap<Integer, Long>();
    private static final Map<Integer, Integer> ANIMATION_MODES = new HashMap<Integer, Integer>();
    private static final Map<Integer, SavedPose> SAVED_POSES = new HashMap<Integer, SavedPose>();
    private static final Set<Integer> PUSHED_POSE_STACKS = new HashSet<Integer>();
    private static ViewTurnAnimation LOCAL_BACK_KICK_VIEW;
    private static PlayerModel<?> FIRST_PERSON_DEFAULT_MODEL;
    private static PlayerModel<?> FIRST_PERSON_SLIM_MODEL;
    private static Matrix4f CAPTURED_FIRST_PERSON_POSE;
    private static Matrix3f CAPTURED_FIRST_PERSON_NORMAL;
    private static int CAPTURED_FIRST_PERSON_ENTITY_ID;

    private KickAnimationClient() {
    }

    public static void startForEntity(int entityId) {
        KickAnimationClient.startForEntity(entityId, 0);
    }

    public static void startForEntity(int entityId, int mode) {
        if (entityId < 0) {
            return;
        }
        int sanitizedMode = KickAnimationClient.isBackKickMode(mode) ? mode : 0;
        START_TIMES.put(entityId, System.currentTimeMillis());
        ANIMATION_MODES.put(entityId, sanitizedMode);
        KickAnimationClient.startLocalBackKickViewIfNeeded(entityId, sanitizedMode);
    }

    public static void startLocal() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.player != null) {
            KickAnimationClient.startForEntity(mc.player.getId());
        }
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
    }

    public static void captureFirstPersonKickLegPose(RenderHandEvent event) {
        CAPTURED_FIRST_PERSON_POSE = null;
        CAPTURED_FIRST_PERSON_NORMAL = null;
        CAPTURED_FIRST_PERSON_ENTITY_ID = -1;
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.options == null || mc.screen != null) {
            return;
        }
        if (!mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        float progress = KickAnimationClient.getFirstPersonKickProgress(mc.player.getId());
        if (progress < 0.0f) {
            return;
        }
        CAPTURED_FIRST_PERSON_POSE = new Matrix4f((Matrix4fc)event.getPoseStack().last().pose());
        CAPTURED_FIRST_PERSON_NORMAL = new Matrix3f((Matrix3fc)event.getPoseStack().last().normal());
        CAPTURED_FIRST_PERSON_ENTITY_ID = mc.player.getId();
    }

    public static void onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.options == null || mc.screen != null) {
            return;
        }
        if (!mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        float progress = KickAnimationClient.getFirstPersonKickProgress(mc.player.getId());
        if (progress < 0.0f) {
            return;
        }
        KickAnimationClient.drawFirstPersonSkinLeg(event, progress);
    }

    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        AbstractClientPlayer player;
        Player entity = event.getEntity();
        if (!(entity instanceof AbstractClientPlayer) || (player = (AbstractClientPlayer)entity).isSpectator()) {
            return;
        }
        int mode = KickAnimationClient.getMode(player.getId());
        float progress = KickAnimationClient.getProgress(player.getId());
        if (progress < 0.0f) {
            return;
        }
        boolean backKick = KickAnimationClient.isBackKickMode(mode);
        PlayerModel model = (PlayerModel)event.getRenderer().getModel();
        if (model == null) {
            return;
        }
        SAVED_POSES.put(player.getId(), SavedPose.capture(model));
        if (backKick) {
            KickAnimationClient.applyBackKickThirdPerson(event, (PlayerModel<?>)model, player.getId(), mode, progress);
            return;
        }
        KickAnimationClient.applyThirdPersonFrontKickPose((PlayerModel<?>)model, progress);
    }

    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        PlayerModel model;
        Player entity = event.getEntity();
        if (!(entity instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer player = (AbstractClientPlayer)entity;
        SavedPose pose = SAVED_POSES.remove(player.getId());
        if (pose != null && (model = (PlayerModel)event.getRenderer().getModel()) != null) {
            pose.restore((HumanoidModel<?>)model);
        }
        if (PUSHED_POSE_STACKS.remove(player.getId())) {
            event.getPoseStack().popPose();
        }
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        if (false) {
            return;
        }
        KickAnimationClient.updateLocalBackKickView();
    }

    public static void onCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (dev.zomboid.taczfeedback.ThirdPersonAimClient.isIso()) return;
        if (LOCAL_BACK_KICK_VIEW == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.options == null || !mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        long elapsed = System.currentTimeMillis() - LOCAL_BACK_KICK_VIEW.startedAtMs();
        if (elapsed < 0L || elapsed > 850L) {
            LOCAL_BACK_KICK_VIEW = null;
            return;
        }
        float progress = (float)elapsed / 850.0f;
        float curve = KickAnimationClient.backKickTurnCurve(progress);
        event.setYaw(event.getYaw() + LOCAL_BACK_KICK_VIEW.offsetYaw() * curve);
    }

    private static float getProgress(int entityId) {
        Long start = START_TIMES.get(entityId);
        if (start == null) {
            return -1.0f;
        }
        int mode = KickAnimationClient.getMode(entityId);
        long durationMs = KickAnimationClient.getDurationMs(mode);
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed < 0L) {
            KickAnimationClient.clearAnimation(entityId);
            return -1.0f;
        }
        if (elapsed > durationMs) {
            KickAnimationClient.clearAnimation(entityId);
            return -1.0f;
        }
        return (float)elapsed / (float)durationMs;
    }

    public static boolean isThirdPersonKickActive(int entityId) {
        return KickAnimationClient.getProgress(entityId) >= 0.0f;
    }

    private static int getMode(int entityId) {
        return ANIMATION_MODES.getOrDefault(entityId, 0);
    }

    private static float getFirstPersonKickProgress(int entityId) {
        int mode = KickAnimationClient.getMode(entityId);
        float progress = KickAnimationClient.getProgress(entityId);
        if (progress >= 0.0f) {
            return KickAnimationClient.isBackKickMode(mode) ? -1.0f : progress;
        }
        return -1.0f;
    }

    private static boolean isBackKickMode(int mode) {
        return mode == 1 || mode == 2;
    }

    private static long getDurationMs(int mode) {
        return KickAnimationClient.isBackKickMode(mode) ? 850L : 520L;
    }

    private static void clearAnimation(int entityId) {
        START_TIMES.remove(entityId);
        ANIMATION_MODES.remove(entityId);
    }

    private static void applyThirdPersonFrontKickPose(PlayerModel<?> model, float progress) {
        ActionAnimationSampler.applyModelPart(model.body, ActionAnimationSampler.sample(ActionAnimationSampler.Profile.THIRD_PERSON_KICK, "BODY", progress));
        ActionAnimationSampler.applyModelPart(model.rightLeg, ActionAnimationSampler.sample(ActionAnimationSampler.Profile.THIRD_PERSON_KICK, "RIGHT_LEG", progress));
        ActionAnimationSampler.applyModelPart(model.leftLeg, ActionAnimationSampler.sample(ActionAnimationSampler.Profile.THIRD_PERSON_KICK, "LEFT_LEG", progress));
        model.rightPants.copyFrom(model.rightLeg);
        model.leftPants.copyFrom(model.leftLeg);
    }

    private static void applyBackKickThirdPerson(RenderPlayerEvent.Pre event, PlayerModel<?> model, int entityId, int mode, float progress) {
        boolean fromLeftSide = mode == 1;
        float turn = KickAnimationClient.backKickTurnCurve(progress);
        float phase = KickAnimationClient.backKickKickPhase(progress);
        ActionAnimationSampler.Transform turnTransform = ActionAnimationSampler.sample(ActionAnimationSampler.Profile.BACK_KICK, "TURN", progress);
        float configuredTurn = (float)Math.abs(turnTransform.rotY);
        float turnDegrees = (fromLeftSide ? -configuredTurn : configuredTurn) * Math.max(0.0f, Math.min(1.0f, turn));
        event.getPoseStack().pushPose();
        PUSHED_POSE_STACKS.add(entityId);
        event.getPoseStack().mulPose(Axis.YP.rotationDegrees(turnDegrees));
    }

    public static void applyBackKickPoseAfterSetup(AbstractClientPlayer player, PlayerModel model) {
        if (player == null || model == null || player.isSpectator()) {
            return;
        }
        int mode = KickAnimationClient.getMode(player.getId());
        float progress = KickAnimationClient.getProgress(player.getId());
        if (progress < 0.0f || !KickAnimationClient.isBackKickMode(mode)) {
            return;
        }
        KickAnimationClient.applyBackKickLegPose(model, mode, progress);
    }

    private static void applyBackKickLegPose(PlayerModel model, int mode, float progress) {
        boolean fromLeftSide = mode == 1;
        ActionAnimationSampler.Transform body = ActionAnimationSampler.sample(ActionAnimationSampler.Profile.BACK_KICK, "BODY", progress);
        ActionAnimationSampler.Transform kickLeg = ActionAnimationSampler.sample(ActionAnimationSampler.Profile.BACK_KICK, "KICK_LEG", progress);
        ActionAnimationSampler.Transform supportLeg = ActionAnimationSampler.sample(ActionAnimationSampler.Profile.BACK_KICK, "SUPPORT_LEG", progress);
        if (fromLeftSide) {
            ActionAnimationSampler.applyModelPart(model.leftLeg, KickAnimationClient.mirrorBackKickTransform(kickLeg, -1.0, 1.0));
            ActionAnimationSampler.applyModelPart(model.rightLeg, KickAnimationClient.mirrorBackKickTransform(supportLeg, 1.0, 1.0));
            ActionAnimationSampler.applyModelPart(model.body, KickAnimationClient.mirrorBackKickTransform(body, 1.0, 1.0));
        } else {
            ActionAnimationSampler.applyModelPart(model.rightLeg, KickAnimationClient.mirrorBackKickTransform(kickLeg, 1.0, -1.0));
            ActionAnimationSampler.applyModelPart(model.leftLeg, KickAnimationClient.mirrorBackKickTransform(supportLeg, -1.0, -1.0));
            ActionAnimationSampler.applyModelPart(model.body, KickAnimationClient.mirrorBackKickTransform(body, -1.0, -1.0));
        }
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
    }

    private static ActionAnimationSampler.Transform mirrorBackKickTransform(ActionAnimationSampler.Transform source, double ySign, double zSign) {
        ActionAnimationSampler.Transform t = source == null ? new ActionAnimationSampler.Transform() : source.copy();
        t.rotY = Math.abs(t.rotY) * ySign;
        t.rotZ = Math.abs(t.rotZ) * zSign;
        t.y = Math.abs(t.y) * ySign;
        t.z = Math.abs(t.z) * zSign;
        return t;
    }

    private static void startLocalBackKickViewIfNeeded(int entityId, int mode) {
        if (!KickAnimationClient.isBackKickMode(mode)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.options == null || !mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        if (mc.player.getId() != entityId) {
            return;
        }
        float offset = mode == 1 ? 75.0f : -75.0f;
        LOCAL_BACK_KICK_VIEW = new ViewTurnAnimation(System.currentTimeMillis(), mc.player.getYRot(), offset);
    }

    private static void updateLocalBackKickView() {
        if (LOCAL_BACK_KICK_VIEW == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            LOCAL_BACK_KICK_VIEW = null;
            return;
        }
        long elapsed = System.currentTimeMillis() - LOCAL_BACK_KICK_VIEW.startedAtMs();
        if (elapsed < 0L || elapsed > 850L) {
            LOCAL_BACK_KICK_VIEW = null;
        }
    }

    public static void clearAll() {
        START_TIMES.clear();
        ANIMATION_MODES.clear();
        SAVED_POSES.clear();
        PUSHED_POSE_STACKS.clear();
        LOCAL_BACK_KICK_VIEW = null;
    }

    public static void cleanupOldAnimations() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Integer, Long>> iterator = START_TIMES.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Long> entry = iterator.next();
            int entityId = entry.getKey();
            int mode = KickAnimationClient.getMode(entityId);
            if (now - entry.getValue() <= KickAnimationClient.getDurationMs(mode) + 500L) continue;
            iterator.remove();
            ANIMATION_MODES.remove(entityId);
        }
    }

    private static void drawFirstPersonSkinLeg(RenderHandEvent event, float progress) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return;
        }
        LocalPlayer player = mc.player;
        PlayerModel<?> model = KickAnimationClient.getFirstPersonModel((AbstractClientPlayer)player);
        if (model == null) {
            return;
        }
        ModelPart rightLeg = model.rightLeg;
        ModelPart rightPants = model.rightPants;
        PartPose legPose = PartPose.capture(rightLeg);
        PartPose pantsPose = PartPose.capture(rightPants);
        try {
            float alpha = Math.max(0.35f, (float)Math.sin((double)progress * Math.PI));
            KickAnimationClient.resetPartToOrigin(rightLeg);
            KickAnimationClient.resetPartToOrigin(rightPants);
            PoseStack poseStack = KickAnimationClient.makeImmediateFirstPersonLegPoseStack(event, (AbstractClientPlayer)player);
            poseStack.pushPose();
            ActionAnimationSampler.applyPose(poseStack, ActionAnimationSampler.sample(ActionAnimationSampler.Profile.FIRST_PERSON_KICK, "RIGHT_LEG", progress));
            poseStack.translate(-0.08f, -0.1f, 0.0f);
            ResourceLocation skin = player.getSkin().texture();
            RenderType legRenderType = RenderType.entityTranslucent((ResourceLocation)skin);
            MultiBufferSource.BufferSource immediateBuffer = Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer consumer = immediateBuffer.getBuffer(legRenderType);
            int light = event.getPackedLight();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            rightLeg.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, (((int)(alpha*255))<<24)|0xFFFFFF);
            rightPants.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, (((int)(alpha*255))<<24)|0xFFFFFF);
            poseStack.popPose();
            immediateBuffer.endBatch(legRenderType);
            immediateBuffer.endBatch();
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        }
        finally {
            legPose.restore(rightLeg);
            pantsPose.restore(rightPants);
        }
    }

    private static PoseStack makeImmediateFirstPersonLegPoseStack(RenderHandEvent event, AbstractClientPlayer player) {
        PoseStack poseStack = new PoseStack();
        if (CAPTURED_FIRST_PERSON_ENTITY_ID == player.getId() && CAPTURED_FIRST_PERSON_POSE != null && CAPTURED_FIRST_PERSON_NORMAL != null) {
            poseStack.last().pose().set((Matrix4fc)CAPTURED_FIRST_PERSON_POSE);
            poseStack.last().normal().set((Matrix3fc)CAPTURED_FIRST_PERSON_NORMAL);
        } else if (event != null) {
            poseStack.last().pose().set((Matrix4fc)event.getPoseStack().last().pose());
            poseStack.last().normal().set((Matrix3fc)event.getPoseStack().last().normal());
        }
        return poseStack;
    }

    private static PlayerModel<?> getFirstPersonModel(AbstractClientPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getEntityModels() == null) {
            return null;
        }
        boolean slim = "slim".equals(player.getSkin().model().id());
        if (slim) {
            if (FIRST_PERSON_SLIM_MODEL == null) {
                FIRST_PERSON_SLIM_MODEL = new PlayerModel(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true);
            }
            return FIRST_PERSON_SLIM_MODEL;
        }
        if (FIRST_PERSON_DEFAULT_MODEL == null) {
            FIRST_PERSON_DEFAULT_MODEL = new PlayerModel(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
        }
        return FIRST_PERSON_DEFAULT_MODEL;
    }

    private static void resetPartToOrigin(ModelPart part) {
        part.setPos(0.0f, 0.0f, 0.0f);
        part.xRot = 0.0f;
        part.yRot = 0.0f;
        part.zRot = 0.0f;
        part.visible = true;
    }

    private static float backKickTurnCurve(float progress) {
        float p = Math.max(0.0f, Math.min(1.0f, progress));
        if (p < 0.3529412f) {
            return KickAnimationClient.easeOutCubic(p / 0.3529412f);
        }
        if (p < 0.64705884f) {
            return 1.0f;
        }
        return 1.0f - KickAnimationClient.easeInCubic((p - 0.64705884f) / 0.35294116f);
    }

    private static float backKickKickPhase(float progress) {
        float p = Math.max(0.0f, Math.min(1.0f, progress));
        if (p < 0.23529412f || p > 0.64705884f) {
            return 0.0f;
        }
        return KickAnimationClient.kickCurve((p - 0.23529412f) / 0.41176474f);
    }

    private static float kickCurve(float progress) {
        float p = Math.max(0.0f, Math.min(1.0f, progress));
        if (p < 0.32f) {
            return KickAnimationClient.easeOutCubic(p / 0.32f);
        }
        if (p < 0.56f) {
            return 1.0f;
        }
        return 1.0f - KickAnimationClient.easeInCubic((p - 0.56f) / 0.44f);
    }

    private static float smoothOut(float t) {
        float clamped = Math.max(0.0f, Math.min(1.0f, t));
        return 1.0f - (1.0f - clamped) * (1.0f - clamped);
    }

    private static float easeOutCubic(float t) {
        float c = Math.max(0.0f, Math.min(1.0f, t));
        return 1.0f - (float)Math.pow(1.0f - c, 3.0);
    }

    private static float easeInCubic(float t) {
        float c = Math.max(0.0f, Math.min(1.0f, t));
        return c * c * c;
    }

    static {
        CAPTURED_FIRST_PERSON_ENTITY_ID = -1;
    }

    private record SavedPose(PartPose body, PartPose rightLeg, PartPose rightPants, PartPose leftLeg, PartPose leftPants, PartPose rightArm, PartPose leftArm) {
        static SavedPose capture(HumanoidModel<?> model) {
            PlayerModel pm;
            PlayerModel playerModel = model instanceof PlayerModel ? (pm = (PlayerModel)model) : null;
            return new SavedPose(PartPose.capture(model.body), PartPose.capture(model.rightLeg), playerModel != null ? PartPose.capture(playerModel.rightPants) : PartPose.capture(model.rightLeg), PartPose.capture(model.leftLeg), playerModel != null ? PartPose.capture(playerModel.leftPants) : PartPose.capture(model.leftLeg), PartPose.capture(model.rightArm), PartPose.capture(model.leftArm));
        }

        void restore(HumanoidModel<?> model) {
            this.body.restore(model.body);
            this.rightLeg.restore(model.rightLeg);
            if (model instanceof PlayerModel) {
                PlayerModel playerModel = (PlayerModel)model;
                this.rightPants.restore(playerModel.rightPants);
                this.leftPants.restore(playerModel.leftPants);
            }
            this.leftLeg.restore(model.leftLeg);
            this.rightArm.restore(model.rightArm);
            this.leftArm.restore(model.leftArm);
        }
    }

    private record ViewTurnAnimation(long startedAtMs, float originalYaw, float offsetYaw) {
    }

    private record PartPose(float xRot, float yRot, float zRot, float x, float y, float z, boolean visible) {
        static PartPose capture(ModelPart part) {
            return new PartPose(part.xRot, part.yRot, part.zRot, part.x, part.y, part.z, part.visible);
        }

        void restore(ModelPart part) {
            part.xRot = this.xRot;
            part.yRot = this.yRot;
            part.zRot = this.zRot;
            part.x = this.x;
            part.y = this.y;
            part.z = this.z;
            part.visible = this.visible;
        }
    }
}
