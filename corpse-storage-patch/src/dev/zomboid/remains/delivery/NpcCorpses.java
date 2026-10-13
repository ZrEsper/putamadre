package dev.zomboid.remains.delivery;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.world.item.ItemStack;

/** Server-side death delivery. Deferred deliveries are saved in the existing Remains SavedData. */
public final class NpcCorpses {
    static final String FIELD="storage14$pending", KEY="NpcCorpseDeliveries14", RECEIPT="NpcCorpseQueued14";
    static final String[] STORED={"TacZNpcStoredFlashlight13","TacZNpcStoredOffhand13"};
    static Object root() { return Reflect.type("dev.zomboid.remains.ZombieRemainsLoot"); }
    static Object call(Object object,String name,Object...args) { return Reflect.call(object,name,args); }
    static Object tag() { return Reflect.make("net.minecraft.nbt.CompoundTag"); }
    @SuppressWarnings("unchecked") static List<Object> pending(Object data) {
        Object list=Reflect.get(data,FIELD);
        if (list==null) { list=new ArrayList<>(); Reflect.set(data,FIELD,list); }
        return (List<Object>)list;
    }
    static ItemStack parse(Object registry,Object tag) { return (ItemStack)call(ItemStack.class,"parseOptional",registry,tag); }
    static void append(List<ItemStack> list,ItemStack stack) { if (!stack.isEmpty()) list.add(stack.copy()); }
    public static List<ItemStack> collect(Object mob,Object level,Collection<?> drops) {
        List<ItemStack> gear=new ArrayList<>(), all=new ArrayList<>();
        append(gear,(ItemStack)call(mob,"getMainHandItem"));
        for(Object stack:(Iterable<?>)call(mob,"getArmorSlots")) append(gear,(ItemStack)stack);
        append(gear,(ItemStack)call(mob,"getOffhandItem"));
        for(ItemStack stack:gear) append(all,stack);
        // Vanilla death drops may contain the same equipped stacks. Subtract that equipment
        // once, but keep genuinely additional drops, even of an identical item type.
        List<ItemStack> budget=new ArrayList<>(); for(ItemStack stack:gear) budget.add(stack.copy());
        for(Object entity:drops) {
            ItemStack remainder=((ItemStack)call(entity,"getItem")).copy();
            for(ItemStack equipped:budget) if (!equipped.isEmpty() && ItemStack.isSameItemSameComponents(equipped,remainder)) {
                int count=Math.min(equipped.getCount(),remainder.getCount()); equipped.shrink(count); remainder.shrink(count);
            }
            append(all,remainder);
        }
        Object registry=call(level,"registryAccess"), persistent=call(mob,"getPersistentData");
        Object bag=call(persistent,"getList","TacZNpcCalmInventory",10);
        for(int i=0;i<((Number)call(bag,"size")).intValue();i++) append(all,parse(registry,call(bag,"getCompound",i)));
        for(String key:STORED) append(all,parse(registry,call(persistent,"getCompound",key)));
        return all;
    }
    static Object newCorpse(Object data,String block) { return Reflect.make("dev.zomboid.remains.ZombieRemainsLoot$Corpse",data,block,false); }
    static void absorb(Object corpse,ItemStack stack) { call(Reflect.get(corpse,"inventory"),"absorb",stack.copy()); }
    public static void add(Object corpse,Object stack) { absorb(corpse,(ItemStack)stack); }
    static Object encode(Object corpse,Object pos,Object registry) {
        Object record=tag(); call(record,"putLong","Pos",call(pos,"asLong"));
        call(record,"putInt","Quality",Reflect.get(corpse,"quality"));
        call(record,"putLong","Born",Reflect.get(corpse,"born"));
        Object items=Reflect.make("net.minecraft.nbt.ListTag"), storage=Reflect.get(corpse,"inventory");
        for(int i=0;i<((Number)call(storage,"getContainerSize")).intValue();i++) {
            ItemStack stack=(ItemStack)call(storage,"getItem",i);
            if(!stack.isEmpty()) call(items,"add",call(stack,"save",registry));
        }
        call(record,"put","Items",items); return record;
    }
    public static boolean death(Object event) {
        Object mob=call(event,"getEntity"), level=call(mob,"level");
        Object registry=Reflect.get(Reflect.type("net.minecraft.core.registries.BuiltInRegistries"),"ENTITY_TYPE");
        if(!call(registry,"getKey",call(mob,"getType")).toString().equals("tacznpcs:npc")) return false;
        if(!Reflect.type("net.minecraft.server.level.ServerLevel").isInstance(level)) return false;
        Collection<?> drops=(Collection<?>)call(event,"getDrops");
        Object persistent=call(mob,"getPersistentData");
        if(Boolean.TRUE.equals(call(persistent,"getBoolean",RECEIPT))) { drops.clear(); return true; }
        Object data=call(root(),"data",level), pos=call(mob,"blockPosition");
        // Freeze all loot before consuming the event; a full or unavailable corpse never spills.
        Object corpse=newCorpse(data,"zombie_remains_loot:skeletal_remains");
        Reflect.set(corpse,"quality",call(Reflect.type("dev.zomboid.remains.QualityRules"),"roll",ThreadLocalRandom.current().nextDouble(),true));
        Reflect.set(corpse,"born",call(level,"getGameTime"));
        for(ItemStack stack:collect(mob,level,drops)) absorb(corpse,stack);
        call(Reflect.type("dev.zomboid.remains.QualityLoot"),"populate",corpse,level,true);
        call(Reflect.type("dev.zomboid.remains.MilitaryDrops"),"extras",corpse,level);
        call(Reflect.type("dev.zomboid.remains.AddonDrops"),"extras",corpse,level);
        Object record=encode(corpse,pos,call(level,"registryAccess"));
        pending(data).add(record); call(data,"setDirty");
        call(persistent,"putBoolean",RECEIPT,true); drops.clear();
        // The staged record remains recoverable until placement and inventory delivery succeed.
        if(place(data,level,record)) { pending(data).remove(record); call(data,"setDirty"); }
        return true;
    }
    @SuppressWarnings("unchecked") static boolean place(Object data,Object level,Object record) {
        Object center=call(Reflect.type("net.minecraft.core.BlockPos"),"of",call(record,"getLong","Pos"));
        Map<Long,Object> map=(Map<Long,Object>)Reflect.get(data,"corpses");
        Object selected=null;
        search:for(int radius=0;radius<=4;radius++) for(int dy:new int[]{0,1,-1,2,-2}) for(int x=-radius;x<=radius;x++) for(int z=-radius;z<=radius;z++) {
            if(Math.max(Math.abs(x),Math.abs(z))!=radius) continue;
            Object pos=call(center,"offset",x,dy,z);
            if(!Boolean.TRUE.equals(call(level,"hasChunkAt",pos)) || !Boolean.TRUE.equals(call(level,"isInWorldBounds",pos)) || map.containsKey(((Number)call(pos,"asLong")).longValue())) continue;
            Object state=call(level,"getBlockState",pos), below=call(pos,"below");
            if(Boolean.TRUE.equals(call(state,"canBeReplaced")) && Boolean.TRUE.equals(call(call(state,"getFluidState"),"isEmpty")) && Boolean.TRUE.equals(call(call(level,"getBlockState",below),"isFaceSturdy",level,below,Reflect.get(Reflect.type("net.minecraft.core.Direction"),"UP")))) { selected=pos; break search; }
        }
        if(selected==null) return false;
        int[] poses=(int[])Reflect.get(Reflect.type("dev.zomboid.remains.RemainsRules"),"FLOOR");
        Object facing=call(Reflect.type("net.minecraft.core.Direction"),"fromYRot",0d);
        Object state=call(root(),"poseState",poses[ThreadLocalRandom.current().nextInt(poses.length)],0,facing);
        if(state==null) return false;
        Object corpse=newCorpse(data,call(root(),"blockId",state).toString());
        Reflect.set(corpse,"quality",call(record,"getInt","Quality")); Reflect.set(corpse,"born",call(record,"getLong","Born")); Reflect.set(corpse,"randomized",true);
        Object items=call(record,"getList","Items",10), registry=call(level,"registryAccess");
        for(int i=0;i<((Number)call(items,"size")).intValue();i++) absorb(corpse,parse(registry,call(items,"getCompound",i)));
        if(!Boolean.TRUE.equals(call(level,"setBlock",selected,state,3))) return false;
        map.put(((Number)call(selected,"asLong")).longValue(),corpse); call(data,"setDirty");
        return true;
    }
    public static void save(Object tag,Object data) {
        Object items=Reflect.make("net.minecraft.nbt.ListTag"); for(Object record:pending(data)) call(items,"add",record);
        call(tag,"put",KEY,items);
    }
    public static void load(Object data,Object tag) {
        List<Object> list=pending(data); list.clear(); Object saved=call(tag,"getList",KEY,10);
        for(int i=0;i<((Number)call(saved,"size")).intValue();i++) list.add(call(saved,"getCompound",i));
    }
    public static void tick(Object event) {
        Object server=call(event,"getServer");
        for(Object level:(Iterable<?>)call(server,"getAllLevels")) {
            if(((Number)call(level,"getGameTime")).longValue()%20!=0) continue;
            Object data=call(root(),"data",level); List<Object> list=pending(data);
            // Two bounded attempts per dimension per second; rotate blocked records fairly.
            for(int i=0, count=Math.min(2,list.size());i<count;i++) {
                Object record=list.remove(0);
                if(!place(data,level,record)) list.add(record);
                call(data,"setDirty");
            }
        }
    }
}
