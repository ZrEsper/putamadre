package blackoutInteractive.brimmArmors.common.registries;

import blackoutInteractive.brimmArmors.common.registries.BlockRegistry;
import blackoutInteractive.brimmArmors.common.tile.WorkbenchTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import net.neoforged.neoforge.registries.DeferredHolder;

public class TileRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, (String)"brimm");
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WorkbenchTileEntity>> WORKBENCH_TILE = BLOCK_ENTITIES.register("workbench_tile", () -> BlockEntityType.Builder.of(WorkbenchTileEntity::new, (Block[])new Block[]{(Block)BlockRegistry.workbench.get()}).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

