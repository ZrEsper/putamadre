package dev.zomboid.remains.delivery;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.item.*;
import dev.zomboid.remains.expand.ExpandedStorage;

public class CargoChecks {
    static int checks;
    static void check(boolean b){if(!b)throw new AssertionError("Cargo "+checks);checks++;}
    public static class Level {public HolderLookup.Provider registryAccess(){return new HolderLookup.Provider(){};}}
    public static class Mob {
        public CompoundTag persistent=new CompoundTag();public ItemStack main=new ItemStack("gun:full-components",1,1),off=new ItemStack("light:components",1,1);
        public ItemStack getMainHandItem(){return main;}public ItemStack getOffhandItem(){return off;}
        public List<ItemStack> getArmorSlots(){return List.of(new ItemStack("armor:damage-name-nbt",1,1));}
        public CompoundTag getPersistentData(){return persistent;}
    }
    public record Drop(ItemStack item){public ItemStack getItem(){return item;}}
    public static class Data {public Object storage14$pending;public int dirty;public void setDirty(){dirty++;}}
    public static class Body {public Object inventory;public int quality=3;public long born=21;Body(Object inv){inventory=inv;}}
    public static void main(String[]args) {
        Level level=new Level();Mob mob=new Mob();ListTag bag=new ListTag();
        for(int i=0;i<150;i++)bag.add(new ItemStack("cargo:"+i,1,1).save(level.registryAccess()));
        mob.persistent.put("TacZNpcCalmInventory",bag);
        mob.persistent.put("TacZNpcStoredFlashlight13",new ItemStack("stored-light:nbt",1,1).save(level.registryAccess()));
        mob.persistent.put("TacZNpcStoredOffhand13",new ItemStack("stored-tool:nbt",1,1).save(level.registryAccess()));
        List<Drop> drops=List.of(new Drop(mob.main.copy()),new Drop(new ItemStack("gun:full-components",2,1)),new Drop(new ItemStack("ammo:components",64,64)));
        List<ItemStack> all=NpcCorpses.collect(mob,level,drops);
        check(all.size()==157);int count=all.stream().mapToInt(ItemStack::getCount).sum();check(count==221);
        check(all.stream().filter(s->s.tag.equals("gun:full-components")).mapToInt(ItemStack::getCount).sum()==3);
        check(bag.size()==150);check(mob.main.getCount()==1);check(drops.get(0).item().getCount()==1);
        check(all.stream().anyMatch(s->s.tag.equals("cargo:149")));check(all.stream().anyMatch(s->s.tag.equals("stored-light:nbt")));check(all.stream().anyMatch(s->s.tag.equals("stored-tool:nbt")));
        Data data=new Data();ExpandedStorage storage=new ExpandedStorage(new SimpleContainer(27),data);Body body=new Body(storage);
        for(ItemStack stack:all)NpcCorpses.add(body,stack);
        check(storage.getContainerSize()>=157);int stored=0;for(int i=0;i<storage.getContainerSize();i++)stored+=storage.getItem(i).getCount();check(stored==count);
        Object encoded=NpcCorpses.encode(body,new BlockPos(-2,30,-7),level.registryAccess());
        NpcCorpses.pending(data).add(encoded);CompoundTag save=new CompoundTag();NpcCorpses.save(save,data);
        check(save.getList(NpcCorpses.KEY,10).size()==1);Data loaded=new Data();NpcCorpses.load(loaded,save);check(NpcCorpses.pending(loaded).size()==1);
        CompoundTag record=(CompoundTag)NpcCorpses.pending(loaded).get(0);check(BlockPos.of(record.getLong("Pos")).equals(new BlockPos(-2,30,-7)));
        check(record.getInt("Quality")==3);check(record.getLong("Born")==21);
        ListTag items=record.getList("Items",10);int recovered=0;for(int i=0;i<items.size();i++)recovered+=ItemStack.parseOptional(level.registryAccess(),items.getCompound(i)).getCount();check(recovered==count);
        System.out.println("NPC full cargo, stored equipment, captured-drop deduplication and pending persistence: "+checks+" checks");
    }
}
