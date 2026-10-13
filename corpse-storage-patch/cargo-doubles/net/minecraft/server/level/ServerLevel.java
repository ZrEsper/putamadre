package net.minecraft.server.level;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import dev.zomboid.remains.ZombieRemainsLoot;
public class ServerLevel extends Level {
    public ZombieRemainsLoot.CorpseData data=new ZombieRemainsLoot.CorpseData();
    public boolean available=true,rejectPlacement;public long time=20;public int scans;
    public final Map<Long,State> states=new HashMap<>();
    public HolderLookup.Provider registryAccess(){return new HolderLookup.Provider(){};}
    public long getGameTime(){return time;}
    public boolean hasChunkAt(BlockPos pos){return true;}
    public boolean isInWorldBounds(BlockPos pos){return true;}
    public State getBlockState(BlockPos pos){scans++;return states.getOrDefault(pos.asLong(),new State(pos.y()<0||!available?"minecraft:stone":"minecraft:air"));}
    public boolean setBlock(BlockPos pos,State state,int flags){if(rejectPlacement)return false;states.put(pos.asLong(),state);return true;}
    public record State(String id){public boolean canBeReplaced(){return id.equals("minecraft:air");}public State getFluidState(){return this;}public boolean isEmpty(){return true;}public boolean isFaceSturdy(Object level,BlockPos pos,Direction direction){return id.equals("minecraft:stone");}}
}
