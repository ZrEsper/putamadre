package net.minecraft.world.level.block.entity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.player.Player;
public class ChestBlockEntity extends SimpleContainer {
    public CompoundTag persistent=new CompoundTag();public final TestLevel level=new TestLevel();public int opens,dirty;public boolean valid=true;
    public ChestBlockEntity(){super(27);}
    public net.minecraft.network.chat.Component getDisplayName(){return net.minecraft.network.chat.Component.literal("Custom chest");}public CompoundTag getPersistentData(){return persistent;}
    public TestLevel getLevel(){return level;}
    @Override public void setChanged(){dirty++;}
    @Override public boolean stillValid(Player p){return valid;}
    @Override public void startOpen(Player p){opens++;}
    @Override public void stopOpen(Player p){opens--;}
    public static class TestLevel {
        public net.minecraft.world.Container container;
        public HolderLookup.Provider registryAccess(){return new HolderLookup.Provider(){};}
        public State getBlockState(Object pos){return new State();}
    }
    public static class State { public Object getBlock(){return new net.minecraft.world.level.block.ChestBlock();} }
}
