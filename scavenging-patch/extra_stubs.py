from pathlib import Path
import sys
r=Path(sys.argv[1])
sources={
 'net.neoforged.neoforge.client.event.RenderGuiEvent':'public class RenderGuiEvent {public static class Post {}}',
 'net.neoforged.neoforge.event.entity.player.PlayerEvent':'public class PlayerEvent {public static class PlayerLoggedOutEvent {}}',
 'net.minecraft.world.level.block.ChestBlock':'public class ChestBlock extends Block {public static Object getContainer(Object block,Object state,Object level,Object pos,boolean force){return null;}}',
 'dev.survivorcreator.skills.Skills':'public class Skills {public static double awarded;public static final Object SKILL=new Object();public static Object get(String id){return SKILL;}public static int level(Object p,Object skill){return 0;}public static void addXp(Object p,Object skill,double amount){awarded+=amount;}}',
 'net.minecraft.core.registries.BuiltInRegistries':'public class BuiltInRegistries {public static final Reg BLOCK=new Reg(),ITEM=new Reg(),SOUND_EVENT=new Reg();public static class Reg {private final java.util.Map<String,Object> values=new java.util.HashMap<>();private final java.util.Map<Object,net.minecraft.resources.ResourceLocation> keys=new java.util.IdentityHashMap<>();public Object get(net.minecraft.resources.ResourceLocation id){return values.get(id.toString());}public net.minecraft.resources.ResourceLocation getKey(Object o){return keys.getOrDefault(o,net.minecraft.resources.ResourceLocation.parse("minecraft:air"));}public void put(String name,Object o){values.put(name,o);keys.put(o,net.minecraft.resources.ResourceLocation.parse(name));}}}',
 'net.minecraft.world.level.block.entity.ChestBlockEntity':'public class ChestBlockEntity extends BlockEntity {private final net.minecraft.nbt.CompoundTag data=new net.minecraft.nbt.CompoundTag();public ChestBlockEntity(){super(null,null,null);}public net.minecraft.nbt.CompoundTag getPersistentData(){return data;}}',
 'net.minecraft.resources.ResourceLocation':'public final class ResourceLocation {private final String value;private ResourceLocation(String v){value=v;}public static ResourceLocation parse(String v){return new ResourceLocation(v);}public static ResourceLocation fromNamespaceAndPath(String a,String b){return parse(a+":"+b);}public String toString(){return value;}public boolean equals(Object o){return o instanceof ResourceLocation r&&value.equals(r.value);}public int hashCode(){return value.hashCode();}}',
 'net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent':'public class BuildCreativeModeTabContentsEvent {}',
 'net.minecraft.server.level.ServerPlayer':'public class ServerPlayer extends net.minecraft.world.entity.player.Player {}',
 'dev.survivorcreator.skills.SkillEffect':'public interface SkillEffect {void apply(net.minecraft.server.level.ServerPlayer player,int level,net.minecraft.resources.ResourceLocation id);net.minecraft.network.chat.Component describe(int level);}',
 'dev.zomboid.survival.MachineIds':'public class MachineIds {public static net.minecraft.world.level.block.Block dispenserBlock(){return null;}}',
 'net.minecraft.world.level.saveddata.SavedData': 'import net.minecraft.nbt.CompoundTag;import net.minecraft.core.HolderLookup;import java.util.function.*;public abstract class SavedData {public boolean dirty;public void setDirty(){dirty=true;}public abstract CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup);public record Factory<T extends SavedData>(Supplier<T> constructor,BiFunction<CompoundTag,HolderLookup.Provider,T> deserializer,net.minecraft.util.datafix.DataFixTypes type) {}}',
 'net.minecraft.util.datafix.DataFixTypes':'public enum DataFixTypes {SAVED_DATA_COMMAND_STORAGE}',
 'net.neoforged.bus.api.EventPriority':'public enum EventPriority {HIGHEST,HIGH,NORMAL,LOW,LOWEST}',
 'net.neoforged.bus.api.SubscribeEvent':'import java.lang.annotation.*;@Retention(RetentionPolicy.RUNTIME)@Target(ElementType.METHOD)public @interface SubscribeEvent {EventPriority priority() default EventPriority.NORMAL;boolean receiveCanceled() default false;}',
 'net.neoforged.neoforge.event.server.ServerStoppingEvent':'public class ServerStoppingEvent {}',
}
for name,s in sources.items():
 p=r.joinpath(*name.split('.')).with_suffix('.java');p.parent.mkdir(parents=True,exist_ok=True);p.write_text('package '+name.rsplit('.',1)[0]+';\n'+s+'\n')
p=r/'net/minecraft/nbt/CompoundTag.java';s=p.read_text().replace('public boolean isEmpty()', 'public java.util.Set<String> getAllKeys(){return values.keySet();}public boolean isEmpty()');p.write_text(s)
p=r/'net/neoforged/neoforge/event/entity/player/PlayerInteractEvent.java';s=p.read_text().replace('public class PlayerInteractEvent {','public class PlayerInteractEvent {public static class RightClickBlock {}public static class EntityInteract {}public static class EntityInteractSpecific {}');p.write_text(s)

p=r/'net/minecraft/world/item/ItemStack.java';s=p.read_text().replace('public int getMaxStackSize()', 'public ItemStack copyWithCount(int n){ItemStack s=copy();s.count=n;return s;}public boolean isDamageableItem(){return false;}public int getMaxDamage(){return 0;}public void setDamageValue(int n){}public int getMaxStackSize()');p.write_text(s)

p=r/'dev/zomboid/survival/DrinkItem.java';s=p.read_text().replace('public DrinkItem(Properties p,int water,float saturation){super(p);}', 'public final int water;public final float saturation;public DrinkItem(Properties p,int water,float saturation){super(p);this.water=water;this.saturation=saturation;}');p.write_text(s)

p=r/'net/minecraft/core/BlockPos.java';s=p.read_text().replace('public BlockPos(', 'public long asLong(){return ((long)getX()<<38)^((long)getZ()<<12)^getY();}public BlockPos(');p.write_text(s)
