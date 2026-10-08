package dev.zomboid.survival;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = "zomboid_survival", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class GeneratorScreens {
    @SubscribeEvent public static void register(RegisterMenuScreensEvent event) {
        event.register(GeneratorMenus.TYPE, GeneratorScreen::new);
    }
}
