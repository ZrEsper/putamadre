package me.xjqsh.lesraisinsarmor.client.renderer;

import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class BedrockArmorRenderer
extends GeoArmorRenderer<LrArmorItem> {
    private static final ResourceLocation animationLocation = ResourceLocation.fromNamespaceAndPath("lrarmor", "animations/item/armor/default.animation.json");

    public BedrockArmorRenderer(String suit) {
        super((GeoModel)new DefaultedItemGeoModel<LrArmorItem>(ResourceLocation.fromNamespaceAndPath("lrarmor", "armor/" + suit)){

            public ResourceLocation getAnimationResource(LrArmorItem animatable) {
                return animationLocation;
            }
        });
    }

    public ResourceLocation getTextureLocation(LrArmorItem animatable) {
        return super.getTextureLocation(animatable);
    }
}
