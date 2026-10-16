from pathlib import Path
r=Path(__file__).resolve().parent.parent/'build/fixtures-src'
sources={
'net/minecraft/core/registries/BuiltInRegistries.java':'''package net.minecraft.core.registries;public class BuiltInRegistries{public static final Registry BLOCK=new Registry(),ENTITY_TYPE=new Registry();public static class Registry{public Object getKey(Object key){return key;}}}''',
'net/minecraft/tags/BlockTags.java':'''package net.minecraft.tags;public class BlockTags{public static final Object IMPERMEABLE=new Object();}''',
'net/minecraft/world/entity/Mob.java':'''package net.minecraft.world.entity;public class Mob{}''',
'net/minecraft/resources/ResourceLocation.java':'''package net.minecraft.resources;public record ResourceLocation(String id){public static ResourceLocation fromNamespaceAndPath(String n,String p){return new ResourceLocation(n+":"+p);}}''',
'net/minecraft/sounds/SoundSource.java':'''package net.minecraft.sounds;public class SoundSource{public static final Object BLOCKS=new Object();}''',
'net/minecraft/sounds/SoundEvent.java':'''package net.minecraft.sounds;public record SoundEvent(Object id){public static SoundEvent createVariableRangeEvent(net.minecraft.resources.ResourceLocation id){return new SoundEvent(id);}}''',
'net/minecraft/core/registries/Registries.java':'''package net.minecraft.core.registries;public class Registries{public static final Object SOUND_EVENT=new Object();}''',
'net/minecraft/network/chat/Component.java':'''package net.minecraft.network.chat;public class Component{public static Object translatable(String key,Object... args){return key;}}''',
'net/tslat/smartbrainlib/util/BrainUtils.java':'''package net.tslat.smartbrainlib.util;import dev.zomboid.turretpower.Checks.T;public class BrainUtils{public static Object getTargetOfEntity(Object t){return ((T)t).target;}}''',
'com/entropy/tacz_turrets/turret/TurretEntity.java':'''package com.entropy.tacz_turrets.turret;public class TurretEntity extends dev.zomboid.turretpower.Checks.T{public TurretEntity(dev.zomboid.turretpower.Checks.W w){super(w);}}''',
'dev/zomboid/survival/MachineEntity.java':'''package dev.zomboid.survival;import java.util.*;public class MachineEntity{public static final Map<Object,Set<Object>> GENERATORS=new WeakHashMap<>();public String name="generator";public int zsRawWater,zsCleanWater,zsFuelCarry,zsFuelTotal;public boolean isGenerator=true;public Energy energy=new Energy();public int changes;public String id(){return name;}public boolean generator(){return isGenerator;}public boolean freezer(){return name.equals("freezer");}public boolean oven(){return name.equals("electricoven");}public void setChanged(){changes++;}public static class Energy{public int stored=10000,max=128000;public int extractEnergy(int n,boolean sim){int r=Math.min(n,stored);if(!sim)stored-=r;return r;}public int getEnergyStored(){return stored;}public int getMaxEnergyStored(){return max;}}}''',
'dev/zomboid/survival/MachineIds.java':'''package dev.zomboid.survival;public class MachineIds{public static boolean dispenser(String id){return id.equals("water_dispenser");}}''',
}
for n,s in sources.items():p=r/n;p.parent.mkdir(parents=True,exist_ok=True);p.write_text(s)
