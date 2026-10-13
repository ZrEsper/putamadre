package com.locknar.taczammocheck.tactical.client;

import com.locknar.taczammocheck.tactical.TaczGunCompat;
import com.locknar.taczammocheck.tactical.client.ActionAnimationSampler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderHandEvent;

public final class DoorOpenHandAnimationClient {
    private static final long ANIMATION_MS = 260L;
    private static long startMs = -1L;
    private static final float SCREEN_X_RATIO = 0.4f;
    private static final float BASE_Y_OFFSET = 86.0f;
    private static final float SWING_X_OFFSET = 42.0f;
    private static final float SWING_Y_OFFSET = 96.0f;
    private static final float SCALE_BASE = 132.0f;
    private static final float SCALE_SWING = 36.0f;
    private static final float ROT_Z_BASE_DEG = 28.0f;
    private static final float ROT_Z_SWING_DEG = -18.0f;
    private static final float ROT_Y_BASE_DEG = -20.0f;
    private static final float ROT_Y_SWING_DEG = 8.0f;
    private static final float ROT_X_BASE_DEG = -28.0f;
    private static final float ROT_X_SWING_DEG = -90.0f;
    private static final float MODEL_TRANSLATE_X = -0.2f;
    private static final float MODEL_TRANSLATE_Y = -0.4f;
    private static final float MODEL_TRANSLATE_Z = 0.0f;
    private static PlayerModel<?> DEFAULT_MODEL;
    private static PlayerModel<?> SLIM_MODEL;

    private DoorOpenHandAnimationClient() {
    }

    public static void trigger() {
        startMs = System.currentTimeMillis();
    }

    public static void clear() {
        startMs = -1L;
    }

    public static boolean isActive() {
        if (startMs < 0L) {
            return false;
        }
        long elapsed = System.currentTimeMillis() - startMs;
        if (elapsed < 0L || elapsed > 260L) {
            startMs = -1L;
            return false;
        }
        return true;
    }

    public static boolean shouldTransformTaczLeftHandWhileOpenHand(ItemDisplayContext transformType) {
        if (transformType == null || !transformType.firstPerson()) {
            return false;
        }
        if (DoorOpenHandAnimationClient.getProgress() < 0.0f) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.options == null || mc.screen != null || !mc.options.getCameraType().isFirstPerson()) {
            return false;
        }
        return TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) != null;
    }

    public static boolean shouldSuppressTaczLeftHandWhileOpenHand() {
        return false;
    }

    public static void applyTaczLeftHandOpenTransform(PoseStack poseStack) {
        if (poseStack == null) {
            return;
        }
        float progress = DoorOpenHandAnimationClient.getProgress();
        if (progress < 0.0f) {
            return;
        }
        ActionAnimationSampler.applyPose(poseStack, ActionAnimationSampler.sample(ActionAnimationSampler.Profile.DOOR_OPEN, "LEFT_HAND", progress));
        poseStack.translate(-0.2f, -0.4f, 0.0f);
    }

    public static void onRenderGui(RenderGuiEvent.Post event) {
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
        float progress = DoorOpenHandAnimationClient.getProgress();
        if (progress < 0.0f) {
            return;
        }
        if (TaczGunCompat.getGunInfo(mc.player.getMainHandItem()) != null) {
            return;
        }
        DoorOpenHandAnimationClient.drawLeftArm(event, (AbstractClientPlayer)mc.player, progress);
    }

    private static float getProgress() {
        if (startMs < 0L) {
            return -1.0f;
        }
        long elapsed = System.currentTimeMillis() - startMs;
        if (elapsed < 0L || elapsed > 260L) {
            startMs = -1L;
            return -1.0f;
        }
        return (float)elapsed / 260.0f;
    }

    private static void drawLeftArm(RenderHandEvent event, AbstractClientPlayer player, float progress) {
        PlayerModel<?> model = DoorOpenHandAnimationClient.getModel(player);
        if (model == null) {
            return;
        }
        ModelPart arm = model.leftArm;
        ModelPart sleeve = model.leftSleeve;
        PartPose armPose = PartPose.capture(arm);
        PartPose sleevePose = PartPose.capture(sleeve);
        try {
            float fade = Math.max(0.25f, (float)Math.sin((double)progress * Math.PI));
            DoorOpenHandAnimationClient.resetPartToOrigin(arm);
            DoorOpenHandAnimationClient.resetPartToOrigin(sleeve);
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            ActionAnimationSampler.applyPose(poseStack, ActionAnimationSampler.sample(ActionAnimationSampler.Profile.DOOR_OPEN, "LEFT_HAND", progress));
            poseStack.translate(-0.2f, -0.4f, 0.0f);
            ResourceLocation skin = player.getSkin().texture();
            VertexConsumer consumer = event.getMultiBufferSource().getBuffer(RenderType.entityTranslucent((ResourceLocation)skin));
            int light = event.getPackedLight();
            arm.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, (((int)(fade*255))<<24)|0xFFFFFF);
            sleeve.render(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, (((int)(fade*255))<<24)|0xFFFFFF);
            poseStack.popPose();
        }
        finally {
            armPose.restore(arm);
            sleevePose.restore(sleeve);
        }
    }

    private static float swingCurve(float progress) {
        float p = Math.max(0.0f, Math.min(1.0f, progress));
        if (p < 0.46f) {
            return DoorOpenHandAnimationClient.easeOutCubic(p / 0.46f);
        }
        return 1.0f - DoorOpenHandAnimationClient.easeInCubic((p - 0.46f) / 0.54f);
    }

    private static float easeOutCubic(float t) {
        float c = Math.max(0.0f, Math.min(1.0f, t));
        return 1.0f - (float)Math.pow(1.0f - c, 3.0);
    }

    private static float easeInCubic(float t) {
        float c = Math.max(0.0f, Math.min(1.0f, t));
        return c * c * c;
    }

    private static PlayerModel<?> getModel(AbstractClientPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.getEntityModels() == null) {
            return null;
        }
        boolean slim = "slim".equals(player.getSkin().model().id());
        if (slim) {
            if (SLIM_MODEL == null) {
                SLIM_MODEL = new PlayerModel(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER_SLIM), true);
            }
            return SLIM_MODEL;
        }
        if (DEFAULT_MODEL == null) {
            DEFAULT_MODEL = new PlayerModel(mc.getEntityModels().bakeLayer(ModelLayers.PLAYER), false);
        }
        return DEFAULT_MODEL;
    }

    private static void resetPartToOrigin(ModelPart part) {
        part.setPos(0.0f, 0.0f, 0.0f);
        part.xRot = 0.0f;
        part.yRot = 0.0f;
        part.zRot = 0.0f;
        part.visible = true;
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
