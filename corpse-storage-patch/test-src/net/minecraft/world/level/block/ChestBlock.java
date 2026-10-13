package net.minecraft.world.level.block;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
public class ChestBlock extends Block {
    public ChestBlock(){super("minecraft:chest");}
    public static Container getContainer(Object block,Object state,ChestBlockEntity.TestLevel level,Object pos,boolean ignore){return level.container;}
}
