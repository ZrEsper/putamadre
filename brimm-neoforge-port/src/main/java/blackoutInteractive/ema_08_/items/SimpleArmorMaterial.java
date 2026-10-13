package blackoutInteractive.ema_08_.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public record SimpleArmorMaterial(String name, int enchantmentValue, net.minecraft.core.Holder<SoundEvent> equipSound, Ingredient repairIngredient, float toughness, float knockbackResistance, int defenseValue, int durabilityValue, ArmorItem.Type type) 
{
    public static final net.neoforged.neoforge.registries.DeferredRegister<ArmorMaterial> MATERIALS = net.neoforged.neoforge.registries.DeferredRegister.create(net.minecraft.core.registries.Registries.ARMOR_MATERIAL, "brimm");
    private static final java.util.Map<String,net.minecraft.core.Holder<ArmorMaterial>> HOLDERS=new java.util.HashMap<>();
    public void register() { HOLDERS.put(name, MATERIALS.register(name, () -> new ArmorMaterial(java.util.Map.of(type,defenseValue), enchantmentValue, equipSound, ()->repairIngredient, java.util.List.of(new ArmorMaterial.Layer(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("brimm", "base"))), toughness, knockbackResistance))); }
    public net.minecraft.core.Holder<ArmorMaterial> holder() { return HOLDERS.get(name); }
    public static SimpleArmorMaterial basic(String name, float toughness, float knockbackResistance, int defenseValue, int durabilityValue, ArmorItem.Type type) {
        return new SimpleArmorMaterial(name, 0, SoundEvents.ARMOR_EQUIP_GENERIC, Ingredient.EMPTY, toughness, knockbackResistance, defenseValue, durabilityValue, type);
    }

    public String getName() {
        return this.name;
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public net.minecraft.core.Holder<SoundEvent> getEquipSound() {
        return this.equipSound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    public int getDefenseForType(ArmorItem.Type t) {
        if (this.type != t) {
            throw new IllegalStateException("Unexpected armor type (" + String.valueOf(t) + ") for this material");
        }
        return this.defenseValue;
    }

    public int getDurabilityForType(ArmorItem.Type t) {
        if (this.type != t) {
            throw new IllegalStateException("Unexpected armor type (" + String.valueOf(t) + ") for this material");
        }
        return this.durabilityValue;
    }
}

