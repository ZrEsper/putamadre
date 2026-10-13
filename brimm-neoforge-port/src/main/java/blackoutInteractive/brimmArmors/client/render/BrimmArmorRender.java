package blackoutInteractive.brimmArmors.client.render;

import blackoutInteractive.brimmArmors.common.items.ArmorPatch;
import blackoutInteractive.brimmArmors.common.items.BrimmArmor;
import blackoutInteractive.ema_08_.rendering.obj.ObjModelsManager;
import blackoutInteractive.ema_08_.rendering.obj.metadata.ArmorModelMetadata;
import blackoutInteractive.ema_08_.rendering.obj.modelsHolders.LoadedModel;
import blackoutInteractive.ema_08_.rendering.overlay.OverlayLocation;
import blackoutInteractive.ema_08_.rendering.overlay.OverlayPos;
import blackoutInteractive.ema_08_.rendering.twoToThreeD.RenderablePngsManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BrimmArmorRender
extends HumanoidModel<LivingEntity> {
    protected final BrimmArmor armor;
    protected final ItemStack is;

    public BrimmArmorRender(ModelPart root, ItemStack stack) {
        super(root);
        this.armor = (BrimmArmor)stack.getItem();
        this.is = stack;
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        int[] modelIds;
        MultiBufferSource.BufferSource buff = Minecraft.getInstance().renderBuffers().bufferSource();
        float ticks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        for (int modelId : modelIds = this.armor.getModelIds()) {
            poseStack.pushPose();
            LoadedModel model = ObjModelsManager.get(modelId);
            ArmorModelMetadata meta = (ArmorModelMetadata)model.getMetadata();
            switch (meta.type()) {
                case HELMET: {
                    this.head.translateAndRotate(poseStack);
                    break;
                }
                case CHESTPLATE: {
                    this.body.translateAndRotate(poseStack);
                    break;
                }
                case BOOTS_R: 
                case LEGGINGS_R: {
                    this.rightLeg.translateAndRotate(poseStack);
                    break;
                }
                case BOOTS_L: 
                case LEGGINGS_L: {
                    this.leftLeg.translateAndRotate(poseStack);
                    break;
                }
                default: {
                    throw new IllegalStateException("Invalid model type for armor " + this.armor.unlocName + ": " + String.valueOf((Object)meta.type()));
                }
            }
            meta.wearing_transform().apply(poseStack);
            model.render(poseStack, (MultiBufferSource)buff, packedLight, packedOverlay, ticks);
            poseStack.popPose();
        }
        ArmorPatch patch = this.armor.getPatch(this.is);
        Collection<OverlayLocation> locations = this.armor.patchesPositions(this.is);
        if (patch == null || locations.isEmpty()) {
            return;
        }
        for (OverlayLocation loc : locations) {
            poseStack.pushPose();
            OverlayPos pos = loc.pos();
            switch (pos) {
                case HUMANOID_HEAD: {
                    this.head.translateAndRotate(poseStack);
                    break;
                }
                case HUMANOID_TORSO: {
                    this.body.translateAndRotate(poseStack);
                    break;
                }
                default: {
                    throw new IllegalStateException("Invalid overlay position for armor " + this.armor.unlocName + ": " + String.valueOf((Object)pos));
                }
            }
            loc.localTransform().apply(poseStack);
            RenderablePngsManager.transformOrGet(patch).render(poseStack, (MultiBufferSource)buff, packedLight, packedOverlay, ticks);
            poseStack.popPose();
        }
    }
}

