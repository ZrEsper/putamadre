package dev.zomboid.survival;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "zomboid_survival", bus = EventBusSubscriber.Bus.MOD)
public final class GeneratorMenus {
    public static final MenuType<GeneratorMenu> TYPE = new MenuType<>(GeneratorMenu::new, FeatureFlags.DEFAULT_FLAGS);
    @SubscribeEvent public static void register(RegisterEvent event) {
        event.register(Registries.MENU, ResourceLocation.fromNamespaceAndPath("zomboid_survival", "generator"), () -> TYPE);
    }
}
