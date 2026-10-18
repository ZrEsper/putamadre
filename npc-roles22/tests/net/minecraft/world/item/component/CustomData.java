package net.minecraft.world.item.component;import net.minecraft.nbt.CompoundTag;public record CustomData(CompoundTag tag){public static CustomData of(CompoundTag tag){return new CustomData(tag);}}
