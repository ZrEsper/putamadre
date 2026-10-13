package blackoutInteractive.brimmArmors.client;

import blackoutInteractive.brimmArmors.client.render.WorkbenchRender;
import blackoutInteractive.brimmArmors.common.CommonProxy;
import blackoutInteractive.brimmArmors.common.registries.TileRegistry;
import blackoutInteractive.ema_08_.rendering.obj.ObjModelsManager;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy
extends CommonProxy {
    public static void registerReloadListeners(net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((net.minecraft.server.packs.resources.ResourceManagerReloadListener) manager -> {
            ObjModelsManager.invalidate();
            blackoutInteractive.ema_08_.rendering.twoToThreeD.RenderablePngsManager.invalidate();
        });
    }
    public static void registerExtensions(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent event) {
        for (var entry : blackoutInteractive.brimmArmors.common.registries.ItemRegistry.ITEMS.getEntries()) {
            if (entry.get() instanceof blackoutInteractive.brimmArmors.common.items.BrimmArmor armor) armor.registerClientExtension(extension -> event.registerItem(extension,armor));
        }
    }
    @Override
    public void client(FMLClientSetupEvent event) {
        ObjModelsManager.freeze();

        BlockEntityRenderers.register((BlockEntityType)((BlockEntityType)TileRegistry.WORKBENCH_TILE.get()), context -> new WorkbenchRender());

    }
}

