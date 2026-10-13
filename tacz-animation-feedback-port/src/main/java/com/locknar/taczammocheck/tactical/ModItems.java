package com.locknar.taczammocheck.tactical;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, (String)"locknar_doorkick");
    public static final DeferredHolder<Item, Item> MIRROR_GUN = ITEMS.register("mirror_gun", () -> new Item(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }
}
