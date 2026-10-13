package dev.zomboid.remains;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.util.RandomSource;
import net.neoforged.fml.ModList;
import dev.zomboid.remains.expand.ExpandedStorage;
import java.util.*;
public final class AddonDrops {
 public static ItemStack gun(AddonCatalog.Gun spec,int quality){ItemStack stack=ZombieRemainsLoot.stack("tacz:modern_kinetic_gun",1);if(stack.isEmpty())return stack;CompoundTag tag=new CompoundTag();tag.putString("GunId",spec.id());tag.putString("GunFireMode",spec.mode());tag.putInt("GunCurrentAmmoCount",Math.min(spec.capacity(),Math.max(1,quality*2+1)));stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));return stack;}
 public static ItemStack melee(AddonCatalog.Melee spec){ItemStack stack=ZombieRemainsLoot.stack(spec.base(),1);if(!stack.isEmpty()){CompoundTag tag=new CompoundTag();tag.putString("MeleeWeaponId",spec.id());stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}return stack;}
 public static ItemStack attachment(String id){ItemStack stack=ZombieRemainsLoot.stack("tacz:attachment",1);if(!stack.isEmpty()){CompoundTag tag=new CompoundTag();tag.putString("AttachmentId",id);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}return stack;}
 public static ItemStack ammo(String id,int count){ItemStack stack=ZombieRemainsLoot.stack("tacz:ammo",count);if(!stack.isEmpty()){CompoundTag tag=new CompoundTag();tag.putString("AmmoId",id);stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));}return stack;}
 public static void insert(Object raw,ItemStack stack){if(stack.isEmpty())return;ZombieRemainsLoot.Corpse corpse=(ZombieRemainsLoot.Corpse)raw;if(corpse.inventory instanceof ExpandedStorage expanded)expanded.absorb(stack.copy());else ZombieRemainsLoot.add(corpse,stack.copy());}
 public static void extras(Object raw,Object world){ZombieRemainsLoot.Corpse corpse=(ZombieRemainsLoot.Corpse)raw;ServerLevel level=(ServerLevel)world;int q=Math.max(0,Math.min(5,corpse.quality));RandomSource random=level.random;
  if(ModList.get().isLoaded("valorantpack")&&ModList.get().isLoaded("tacz")){
   if(random.nextDouble()<new double[]{.015,.025,.05,.09,.14,.20}[q]){
    List<AddonCatalog.Gun> available=new ArrayList<>();for(var spec:AddonCatalog.GUNS)if(spec.quality()<=q)available.add(spec);
    if(!available.isEmpty()){var spec=available.get(random.nextInt(available.size()));insert(corpse,gun(spec,q));insert(corpse,ammo(spec.ammo(),8+q*6));}
   }
   if(q>=2&&random.nextDouble()<.01+q*.01)insert(corpse,attachment(AddonCatalog.ATTACHMENTS[random.nextInt(AddonCatalog.ATTACHMENTS.length)]));
   if(random.nextDouble()<new double[]{.025,.04,.06,.1,.14,.2}[q]){String[] ids={"valorant:wg50","valorant:wg100","crystals:crystal","crystals:crystal_acp","crystals:unstable_crystal"};insert(corpse,ammo(ids[random.nextInt(ids.length)],4+q*4));}
  }
  if(q>=1&&ModList.get().isLoaded("wtyj")&&ModList.get().isLoaded("lrtactical")&&random.nextDouble()<new double[]{0,.01,.025,.04,.07,.12}[q]){
   List<AddonCatalog.Melee> knives=new ArrayList<>();for(var spec:AddonCatalog.KNIVES)if(spec.quality()<=q)knives.add(spec);if(!knives.isEmpty())insert(corpse,melee(knives.get(random.nextInt(knives.size()))));
  }
  if(ModList.get().isLoaded("lrarmor")&&random.nextDouble()<new double[]{.025,.04,.075,.12,.18,.28}[q]){
   ItemStack stack=ZombieRemainsLoot.stack(AddonCatalog.ARMOR[random.nextInt(AddonCatalog.ARMOR.length)],1);if(!stack.isEmpty()&&stack.isDamageableItem())stack.setDamageValue((int)(stack.getMaxDamage()*(q>=3?.05:.25+random.nextDouble()*.30)));insert(corpse,stack);
  }
 }
}
