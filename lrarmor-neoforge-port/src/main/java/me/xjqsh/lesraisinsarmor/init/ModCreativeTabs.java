package me.xjqsh.lesraisinsarmor.init;

import me.xjqsh.lesraisinsarmor.init.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (String)"lrarmor");
    public static final ResourceLocation icon = ResourceLocation.fromNamespaceAndPath("lrarmor", "chemical_protective_chestplate");
    public static DeferredHolder<CreativeModeTab,CreativeModeTab> MAIN = TABS.register("other", () -> CreativeModeTab.builder().title((Component)Component.translatable((String)"itemGroup.tab.lrarmor")).icon(() -> ((Item)BuiltInRegistries.ITEM.get(icon)).getDefaultInstance()).displayItems((parameters, output) -> {
        for (DeferredHolder<Item,? extends Item> item : ModItems.REGISTER.getEntries()) {
            output.accept(((Item)item.get()).getDefaultInstance());
        }
    }).build());
}
