package me.xjqsh.lesraisinsarmor.client;

import me.xjqsh.lesraisinsarmor.client.resource.ArmorRenderConfigManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;

@OnlyIn(value=Dist.CLIENT)
@EventBusSubscriber(modid="lrarmor", bus=EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
public class ClientSetupHandler {
    @SubscribeEvent
    public static void onAddResourceListener(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((PreparableReloadListener)ArmorRenderConfigManager.getInstance());
    }
}
