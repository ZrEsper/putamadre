package blackoutInteractive.brimmArmors.common.registries;

import blackoutInteractive.brimmArmors.common.blocks.WorkbenchBlock;
import blackoutInteractive.brimmArmors.common.registries.ItemRegistry;
import blackoutInteractive.ema_08_.rendering.obj.SideSafeModelDeclarator;
import blackoutInteractive.ema_08_.rendering.obj.modelsHolders.ModelDeclaration;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, (String)"brimm");
    public static final DeferredHolder<Block, Block> workbench = BlockRegistry.register("workbench", () -> new WorkbenchBlock(0, SideSafeModelDeclarator.decleare(ModelDeclaration.ofBlockEntity("workbench", false))), new Item.Properties());

    private static <T extends Block> DeferredHolder<Block, T> register(String id, Supplier<T> blockSupplier, Item.Properties properties) {
        return BlockRegistry.register(id, blockSupplier, (T block) -> new BlockItem(block, properties));
    }

    private static <T extends Block> DeferredHolder<Block, T> register(String id, Supplier<T> blockSupplier, @Nullable Function<T, BlockItem> supplier) {
        var registryObject = BLOCKS.register(id, blockSupplier);
        if (supplier != null) {
            ItemRegistry.registerAndAddToMiscTab(id, () -> (BlockItem)supplier.apply(registryObject.get()));
        }
        return registryObject;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

