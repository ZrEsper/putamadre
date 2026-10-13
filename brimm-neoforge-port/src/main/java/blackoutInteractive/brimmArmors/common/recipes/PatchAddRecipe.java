package blackoutInteractive.brimmArmors.common.recipes;

import blackoutInteractive.brimmArmors.common.items.ArmorPatch;
import blackoutInteractive.brimmArmors.common.items.BrimmArmor;
import blackoutInteractive.brimmArmors.common.registries.RecipeSerializersRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class PatchAddRecipe
extends CustomRecipe {
    public PatchAddRecipe(CraftingBookCategory cat) {
        super(cat);
    }

    public boolean matches(CraftingInput inv, Level level) {
        ItemStack armor = ItemStack.EMPTY;
        ItemStack patch = ItemStack.EMPTY;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();
            if (item instanceof BrimmArmor) {
                BrimmArmor basic = (BrimmArmor)item;
                if (!armor.isEmpty()) {
                    return false;
                }
                if (basic.getPatch(stack) != null) {
                    return false;
                }
                if (basic.patchesPositions(stack) == null || basic.patchesPositions(stack).isEmpty()) {
                    return false;
                }
                armor = stack;
                continue;
            }
            if (stack.getItem() instanceof ArmorPatch) {
                if (!patch.isEmpty()) {
                    return false;
                }
                patch = stack;
                continue;
            }
            return false;
        }
        return !armor.isEmpty() && !patch.isEmpty();
    }

    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registryAccess) {
        ItemStack armor = ItemStack.EMPTY;
        ArmorPatch patchItem = null;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.getItem() instanceof BrimmArmor) {
                armor = stack;
            }
            if (!(stack.getItem() instanceof ArmorPatch)) continue;
            patchItem = (ArmorPatch)stack.getItem();
        }
        if (armor.isEmpty() || patchItem == null) {
            return ItemStack.EMPTY;
        }
        ItemStack result = armor.copy();
        ((BrimmArmor)armor.getItem()).setPatch(result, patchItem);
        return result;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    public RecipeSerializer<?> getSerializer() {
        return (RecipeSerializer)RecipeSerializersRegistry.PATCH_ADD.get();
    }
}

