package dev.zomboid.remains;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import dev.zomboid.remains.expand.ExpandedStorage;
public class ZombieRemainsLoot {
    public static CorpseData data(ServerLevel level){return level.data;}
    public static String blockId(ServerLevel.State state){return state.id();}
    public static ServerLevel.State poseState(int pose,int stage,Object facing){return new ServerLevel.State("doomsday_decoration:remains_1");}
    public static class CorpseData {public Object storage14$pending;public final Map<Long,Object> corpses=new HashMap<>();public int dirty;public void setDirty(){dirty++;}}
    public static class Corpse {public String block;public int quality;public long born;public boolean randomized;public ExpandedStorage inventory;public Corpse(CorpseData data,String block,boolean mutant){this.block=block;inventory=new ExpandedStorage(new SimpleContainer(27),data);}}
}
