package blackoutInteractive.ema_08_.rendering.obj.modelsHolders;

import blackoutInteractive.ema_08_.rendering.obj.modelsHolders.ModelMetadataHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.neoforge.client.model.renderable.CompositeRenderable;
import net.neoforged.neoforge.client.model.renderable.ITextureRenderTypeLookup;

public final class LoadedModel
extends ModelMetadataHolder {
    private final CompositeRenderable bakedModel;
    private final ITextureRenderTypeLookup renderType;

    public <T> LoadedModel(CompositeRenderable bm, ITextureRenderTypeLookup rt, T m) {
        super(m);
        this.bakedModel = Objects.requireNonNull(bm, "Missing baked model");
        this.renderType = Objects.requireNonNull(rt, "Missing render type");
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay, float partialTicks) {
        this.bakedModel.render(poseStack, bufferSource, this.renderType, combinedLight, combinedOverlay, partialTicks, CompositeRenderable.Transforms.EMPTY);
    }
}

