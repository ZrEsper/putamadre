package blackoutInteractive.brimmArmors.common.recipes;

import blackoutInteractive.brimmArmors.common.items.ArmorPatch;
import blackoutInteractive.brimmArmors.common.items.BrimmArmor;
import blackoutInteractive.brimmArmors.common.registries.RecipeSerializersRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class PatchRemoveRecipe
extends CustomRecipe {
    public PatchRemoveRecipe(CraftingBookCategory category) {
        super(category);
    }

    public boolean matches(CraftingInput inv, Level level) {
        ItemStack armor = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();
            if (item instanceof BrimmArmor) {
                BrimmArmor basic = (BrimmArmor)item;
                if (!armor.isEmpty()) {
                    return false;
                }
                if (basic.getPatch(stack) == null) {
                    return false;
                }
                armor = stack;
                continue;
            }
            return false;
        }
        return !armor.isEmpty();
    }

    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        ItemStack armor = ItemStack.EMPTY;
        ArmorPatch patch = null;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();
            if (!(item instanceof BrimmArmor)) continue;
            BrimmArmor basic = (BrimmArmor)item;
            armor = stack;
            patch = basic.getPatch(stack);
        }
        if (armor.isEmpty() || patch == null) {
            return ItemStack.EMPTY;
        }
        ItemStack resultArmor = armor.copy();
        ((BrimmArmor)armor.getItem()).removePatch(resultArmor);
        return resultArmor;
    }

    public NonNullList<ItemStack> getRemainingItems(CraftingInput inv) {
        NonNullList remaining = NonNullList.withSize((int)inv.size(), (Object)ItemStack.EMPTY);
        for (int i = 0; i < inv.size(); ++i) {
            BrimmArmor basic;
            ArmorPatch patch;
            ItemStack stack = inv.getItem(i);
            Item item = stack.getItem();
            if (!(item instanceof BrimmArmor) || (patch = (basic = (BrimmArmor)item).getPatch(stack)) == null) continue;
            remaining.set(i, (Object)new ItemStack((ItemLike)patch));
        }
        return remaining;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer)RecipeSerializersRegistry.PATCH_REMOVE.get();
    }
}

