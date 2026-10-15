package net.minecraft.world.item;public record Item(String id)implements net.minecraft.world.level.ItemLike{public int getDefaultMaxStackSize(){return 64;}}
