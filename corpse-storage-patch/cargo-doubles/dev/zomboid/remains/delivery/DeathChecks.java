package dev.zomboid.remains.delivery;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import dev.zomboid.remains.ZombieRemainsLoot;

/** Executes the real delivery helper with explicit doubles for the world and loot generators. */
public class DeathChecks {
    static int checks;static void check(boolean test){if(!test)throw new AssertionError("Death delivery "+checks);checks++;}
    public static class Mob extends CargoChecks.Mob {public final ServerLevel level;Mob(ServerLevel level){this.level=level;}public ServerLevel level(){return level;}public Object getType(){return "tacznpcs:npc";}public BlockPos blockPosition(){return new BlockPos(0,0,0);}}
    public record Event(Mob mob,Collection<CargoChecks.Drop> drops){public Object getEntity(){return mob;}public Collection<CargoChecks.Drop> getDrops(){return drops;}}
    public record Server(ServerLevel level){public Iterable<ServerLevel> getAllLevels(){return List.of(level);}}
    public record Tick(Server server){public Server getServer(){return server;}}
    static int total(Object body){ZombieRemainsLoot.Corpse corpse=(ZombieRemainsLoot.Corpse)body;int n=0;for(int i=0;i<corpse.inventory.getContainerSize();i++)n+=corpse.inventory.getItem(i).getCount();return n;}
    static Event event(ServerLevel level){Mob mob=new Mob(level);ListTag bag=new ListTag();for(int i=0;i<150;i++)bag.add(new ItemStack("cargo:"+i,1,1).save(level.registryAccess()));mob.persistent.put("TacZNpcCalmInventory",bag);mob.persistent.put("TacZNpcStoredFlashlight13",new ItemStack("stored-light",1,1).save(level.registryAccess()));mob.persistent.put("TacZNpcStoredOffhand13",new ItemStack("stored-off",1,1).save(level.registryAccess()));return new Event(mob,new ArrayList<>(List.of(new CargoChecks.Drop(mob.main.copy()),new CargoChecks.Drop(new ItemStack("extra-ammo",11,64)))));}
    public static void main(String[]args){
        ServerLevel level=new ServerLevel();Event event=event(level);check(NpcCorpses.death(event));check(event.drops().isEmpty());check(level.data.corpses.size()==1);check(NpcCorpses.pending(level.data).isEmpty());check(total(level.data.corpses.values().iterator().next())==169);
        check(((ZombieRemainsLoot.Corpse)level.data.corpses.values().iterator().next()).inventory.getContainerSize()>54);
        check(NpcCorpses.death(event));check(level.data.corpses.size()==1);check(total(level.data.corpses.values().iterator().next())==169);
        ServerLevel blocked=new ServerLevel();blocked.available=false;Event pending=event(blocked);check(NpcCorpses.death(pending));check(pending.drops().isEmpty());check(blocked.data.corpses.isEmpty());check(NpcCorpses.pending(blocked.data).size()==1);
        CompoundTag saved=new CompoundTag();NpcCorpses.save(saved,blocked.data);blocked.data=new ZombieRemainsLoot.CorpseData();NpcCorpses.load(blocked.data,saved);check(NpcCorpses.pending(blocked.data).size()==1);
        blocked.available=true;blocked.rejectPlacement=true;NpcCorpses.tick(new Tick(new Server(blocked)));check(NpcCorpses.pending(blocked.data).size()==1);check(blocked.data.corpses.isEmpty());
        blocked.rejectPlacement=false;NpcCorpses.tick(new Tick(new Server(blocked)));check(NpcCorpses.pending(blocked.data).isEmpty());check(blocked.data.corpses.size()==1);check(total(blocked.data.corpses.values().iterator().next())==169);
        NpcCorpses.tick(new Tick(new Server(blocked)));check(blocked.data.corpses.size()==1);check(total(blocked.data.corpses.values().iterator().next())==169);
        ServerLevel busy=new ServerLevel();busy.available=false;for(int i=0;i<4;i++)check(NpcCorpses.death(event(busy)));busy.scans=0;NpcCorpses.tick(new Tick(new Server(busy)));check(NpcCorpses.pending(busy.data).size()==4);check(busy.scans<=2*405);
        System.out.println("Real NPC delivery helper: complete death transfer, repeated events, saved deferred corpses, rejected placements, bounded retries: "+checks+" checks (world/loot API doubles)");
    }
}
