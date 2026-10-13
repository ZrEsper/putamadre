package blackoutInteractive.brimmArmors.common.registries;

import blackoutInteractive.brimmArmors.common.recipes.PatchAddRecipe;
import blackoutInteractive.brimmArmors.common.recipes.PatchRemoveRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import net.neoforged.neoforge.registries.DeferredHolder;

public class RecipeSerializersRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, (String)"brimm");
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PatchAddRecipe>> PATCH_ADD = SERIALIZERS.register("patch_add", () -> new SimpleCraftingRecipeSerializer(PatchAddRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<PatchRemoveRecipe>> PATCH_REMOVE = SERIALIZERS.register("patch_remove", () -> new SimpleCraftingRecipeSerializer(PatchRemoveRecipe::new));

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}

