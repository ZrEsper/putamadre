package blackoutInteractive.ema_08_.rendering.twoToThreeD;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class RenderablePng {
    private static final float Z = 0.001f;
    private static final int W = 1;
    private final ResourceLocation texture;
    private final String name;
    private final float height;

    public RenderablePng(ResourceLocation texture, String name, int texWidth, int texHeight) {
        this.texture = texture;
        this.name = name;
        this.height = (float)texHeight / (float)texWidth;
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int overlay, float partialTick) {
        VertexConsumer builder = bufferSource.getBuffer(RenderType.entityCutout((ResourceLocation)this.texture));
        Matrix4f matrix = poseStack.last().pose();
        builder.addVertex(matrix, 0.0f, 0.0f, 0.001f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(packedLight).setNormal(0.0f, 0.0f, 1.0f);
        builder.addVertex(matrix, 1.0f, 0.0f, 0.001f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(packedLight).setNormal(0.0f, 0.0f, 1.0f);
        builder.addVertex(matrix, 1.0f, this.height, 0.001f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(packedLight).setNormal(0.0f, 0.0f, 1.0f);
        builder.addVertex(matrix, 0.0f, this.height, 0.001f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(packedLight).setNormal(0.0f, 0.0f, 1.0f);
        builder.addVertex(matrix, 0.0f, 0.0f, 0.001f).setColor(255, 255, 255, 255).setUv(0.0f, 0.0f).setOverlay(overlay).setLight(packedLight).setNormal(0.0f, 0.0f, -1.0f);
        builder.addVertex(matrix, 0.0f, this.height, 0.001f).setColor(255, 255, 255, 255).setUv(0.0f, 1.0f).setOverlay(overlay).setLight(packedLight).setNormal(0.0f, 0.0f, -1.0f);
        builder.addVertex(matrix, 1.0f, this.height, 0.001f).setColor(255, 255, 255, 255).setUv(1.0f, 1.0f).setOverlay(overlay).setLight(packedLight).setNormal(0.0f, 0.0f, -1.0f);
        builder.addVertex(matrix, 1.0f, 0.0f, 0.001f).setColor(255, 255, 255, 255).setUv(1.0f, 0.0f).setOverlay(overlay).setLight(packedLight).setNormal(0.0f, 0.0f, -1.0f);
    }

    public String toString() {
        return this.getClass().getSimpleName() + ":" + this.name;
    }
}

