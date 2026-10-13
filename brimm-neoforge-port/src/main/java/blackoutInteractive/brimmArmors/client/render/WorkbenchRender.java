package blackoutInteractive.brimmArmors.client.render;

import blackoutInteractive.brimmArmors.common.blocks.WorkbenchBlock;
import blackoutInteractive.brimmArmors.common.tile.WorkbenchTileEntity;
import blackoutInteractive.ema_08_.rendering.obj.ObjModelsManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class WorkbenchRender
implements BlockEntityRenderer<WorkbenchTileEntity> {
    public void render(WorkbenchTileEntity tile, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockState state = tile.getBlockState();
        Block block = state.getBlock();
        if (block instanceof WorkbenchBlock) {
            float yRot=0.0f;
            WorkbenchBlock workbench = (WorkbenchBlock)block;
            Direction facing = (Direction)state.getValue((Property)WorkbenchBlock.FACING);
            matrix.pushPose();
            matrix.translate(0.5, 0.0, 0.5);
            switch (facing) {
                case NORTH: {
                    yRot = 180.0f;
                    break;
                }
                case SOUTH: {
                    yRot = 0.0f;
                    break;
                }
                case EAST: {
                    yRot = -90.0f;
                    break;
                }
                case WEST: {
                    yRot = 90.0f;
                    break;
                }
                default: {
                    yRot = 0.0f;
                }
            }
            if (yRot != 0.0f) {
                matrix.mulPose(Axis.YP.rotationDegrees(yRot));
            }
            ObjModelsManager.get(workbench.getModelId()).render(matrix, buffer, combinedLight, combinedOverlay, partialTicks);
            matrix.popPose();
        }
    }
}

