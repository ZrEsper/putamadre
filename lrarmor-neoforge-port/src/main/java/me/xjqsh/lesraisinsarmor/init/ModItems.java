package me.xjqsh.lesraisinsarmor.init;

import java.util.function.Supplier;
import me.xjqsh.lesraisinsarmor.init.ModEffects;
import me.xjqsh.lesraisinsarmor.item.LrArmorItem;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

public class ModItems {
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(Registries.ITEM, "lrarmor");

    public static void registerSingle(ArmorItem.Type slotType, String name) {
        String slotName = slotType.getName();
        REGISTER.register(name + "_" + slotName, () -> new LrArmorItem(name, slotType, new Item.Properties(), null));
    }

    public static void registerInBatch(String name) {
        for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
            if(slotType==ArmorItem.Type.BODY)continue;
            String slotName = slotType.getName();
            REGISTER.register(name + "_" + slotName, () -> new LrArmorItem(name, slotType, new Item.Properties(), null));
        }
    }

    public static void registerInBatch(String name, Supplier<MobEffect> supplier) {
        for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
            if(slotType==ArmorItem.Type.BODY)continue;
            String slotName = slotType.getName();
            REGISTER.register(name + "_" + slotName, () -> new LrArmorItem(name, slotType, new Item.Properties(), supplier));
        }
    }

    static {
        ModItems.registerInBatch("armored_chemical");
        ModItems.registerInBatch("attacker", ModEffects.TOUGH);
        ModItems.registerInBatch("chemical_protective");
        ModItems.registerInBatch("defender", ModEffects.HEAVY_ARMOR);
        ModItems.registerInBatch("medical", ModEffects.RESCUE);
        ModItems.registerInBatch("scout", ModEffects.LIGHT_LEG);
        ModItems.registerInBatch("sniper");
        ModItems.registerInBatch("dea_armed");
        ModItems.registerInBatch("dea");
        ModItems.registerInBatch("atf");
        ModItems.registerInBatch("atf_vest");
        ModItems.registerInBatch("irs");
        ModItems.registerInBatch("fbi");
        ModItems.registerInBatch("fbi_armed");
        ModItems.registerInBatch("joker");
        ModItems.registerSingle(ArmorItem.Type.HELMET, "joker_armed");
        ModItems.registerSingle(ArmorItem.Type.CHESTPLATE, "joker_armed");
    }
}
