package dev.zomboid.scavenging;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.saveddata.SavedData;
public final class History extends SavedData {
 private final Map<String,Integer> opened=new HashMap<>();
 public static History load(CompoundTag tag,HolderLookup.Provider lookup){History h=new History();CompoundTag entries=tag.getCompound("Opened");for(String k:entries.getAllKeys())h.opened.put(k,entries.getInt(k));return h;}
 @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){CompoundTag entries=new CompoundTag();opened.forEach(entries::putInt);tag.put("Opened",entries);return tag;}
 public boolean has(String key){return opened.containsKey(key);}
 public int tier(String key){return opened.getOrDefault(key,0);}
 public void mark(String key,int tier){opened.put(key,tier);setDirty();}
 public static History get(Object level){Object storage=R.call(level,"getDataStorage");Object factory=R.make("net.minecraft.world.level.saveddata.SavedData$Factory",(java.util.function.Supplier<History>)History::new,(java.util.function.BiFunction<CompoundTag,HolderLookup.Provider,History>)History::load,null);return (History)R.call(storage,"computeIfAbsent",factory,"zomboid_scavenging");}
}
