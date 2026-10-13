package me.xjqsh.lesraisinsarmor.resource.data;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.Holder;
import com.mojang.serialization.JsonOps;

public class ArmorPartData {
    private ImmutableMultimap<Holder<Attribute>, AttributeModifier> attributes;
    private Ingredient repairIngredient;
    private SoundEvent equipSound;
    private int enchantmentValue;
    private int maxDurability;
    private int defense = 0;
    private float toughness = 0.0f;
    private float knockbackResistance = 0.0f;

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributes() {
        return this.attributes;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }

    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public int getMaxDurability() {
        return this.maxDurability;
    }

    public int getDefense() {
        return this.defense;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public static ArmorPartData fromJson(UUID uuid, Struct rawData) {
        ArmorPartData data = new ArmorPartData();
        ImmutableMultimap.Builder<Holder<Attribute>,AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("lrarmor","armor_"+uuid), (double)rawData.defense, AttributeModifier.Operation.ADD_VALUE));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("lrarmor","toughness_"+uuid), (double)rawData.toughness, AttributeModifier.Operation.ADD_VALUE));
        if (rawData.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("lrarmor","knockback_"+uuid), (double)rawData.knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
        }
        data.attributes = builder.build();
        try {
            data.repairIngredient = rawData.repairIngredient==null?Ingredient.EMPTY:Ingredient.CODEC.parse(JsonOps.INSTANCE,rawData.repairIngredient).getOrThrow();
        }
        catch (RuntimeException jsonParseException) {
            // empty catch block
        }
        data.equipSound = SoundEvent.createVariableRangeEvent((ResourceLocation)rawData.sound);
        data.enchantmentValue = rawData.enchantmentValue;
        data.maxDurability = rawData.maxDurability;
        data.defense = rawData.defense;
        data.toughness = rawData.toughness;
        data.knockbackResistance = rawData.knockbackResistance;
        return data;
    }

    public static class Struct {
        private int defense = 0;
        private int toughness = 0;
        private int knockbackResistance = 0;
        private int enchantmentValue = 5;
        private int maxDurability = 128;
        private ResourceLocation sound = ResourceLocation.parse("item.armor.equip_leather");
        private JsonElement repairIngredient = null;
    }
}
