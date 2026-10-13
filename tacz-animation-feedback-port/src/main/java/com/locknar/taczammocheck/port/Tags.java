package com.locknar.taczammocheck.port;
import net.minecraft.world.item.ItemStack;import net.minecraft.nbt.CompoundTag;import net.minecraft.core.component.DataComponents;import net.minecraft.world.item.component.CustomData;
public class Tags {public static CompoundTag read(ItemStack s){return s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}}
