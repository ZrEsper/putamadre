package net.minecraft.nbt; public class CompoundTag { public boolean loot; public void putBoolean(String name,boolean value){if(!name.equals("Loot"))throw new AssertionError(name);loot=value;} }
