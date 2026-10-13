package blackoutInteractive.brimmArmors.common.blocks;

import blackoutInteractive.brimmArmors.client.screens.WorkbenchScreen;
import blackoutInteractive.brimmArmors.common.tile.WorkbenchTileEntity;
import blackoutInteractive.ema_08_.rendering.obj.providers.IObjModelProvider;
import java.util.List;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class WorkbenchBlock
extends Block
implements IObjModelProvider,
EntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private final int modelId;

    public WorkbenchBlock(int lightLevel, int modelId) {
        super(BlockBehaviour.Properties.of().strength(3.5f).noOcclusion().lightLevel(state -> lightLevel));
        this.modelId = modelId;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue((Property)FACING, (Comparable)Direction.NORTH));
    }

    @NotNull
    public List<ItemStack> getDrops(@NotNull BlockState state, // Could not load outer class - annotation placement on inner may be incorrect
     @NotNull LootParams.Builder builder) {
        return List.of(new ItemStack((ItemLike)this));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{FACING});
    }

    @NotNull
    public InteractionResult useWithoutItem(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
        if (world.isClientSide) {
            WorkbenchBlock.openScreen();
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @OnlyIn(value=Dist.CLIENT)
    private static void openScreen() {
        Minecraft.getInstance().setScreen((Screen)new WorkbenchScreen());
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Player player = context.getPlayer();
        if (player != null) {
            Direction facing = Direction.getNearest((double)player.getLookAngle().x, (double)0.0, (double)player.getLookAngle().z);
            boolean flag = facing == Direction.SOUTH || facing == Direction.NORTH;
            return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)(flag ? facing : facing.getOpposite()));
        }
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)context.getHorizontalDirection().getOpposite());
    }

    @NotNull
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public WorkbenchTileEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkbenchTileEntity(pos, state);
    }

    @Override
    public int getModelId() {
        return this.modelId;
    }
}

