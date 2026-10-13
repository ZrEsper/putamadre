package blackoutInteractive.ema_08_.rendering.twoToThreeD;

import blackoutInteractive.ema_08_.rendering.twoToThreeD.IRenderablePngProvider;
import net.minecraft.resources.ResourceLocation;

public interface IDefaultPatchesRenderablePngProvider
extends IRenderablePngProvider {
    @Override
    default public ResourceLocation getTexturePng() {
        return ResourceLocation.fromNamespaceAndPath("brimm", "textures/patches/" + this.getTextureName() + ".png");
    }
}

