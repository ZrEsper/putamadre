package dev.zomboid.survival;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid="zomboid_survival", bus=EventBusSubscriber.Bus.MOD)
public final class WaterItems {
    public static final DrinkItem CONTAMINATED = new DrinkItem(new Item.Properties().stacksTo(16), 0, 0.0f);
    @SubscribeEvent public static void register(RegisterEvent event) {
        event.register(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("zomboid_survival", "contaminated_water"), () -> CONTAMINATED);
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            Block block = MachineIds.dispenserBlock();
            if (block != null) event.register(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("zomboid_survival", "water_dispenser"),
                    () -> new BlockItem(block, new Item.Properties()));
        }
    }
}
