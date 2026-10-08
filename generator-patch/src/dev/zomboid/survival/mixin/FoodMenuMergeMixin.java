package dev.zomboid.survival.mixin;

import dev.zomboid.survival.FoodStacking;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerMenu.class)
public abstract class FoodMenuMergeMixin {
    @Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean survival$clickMerge(ItemStack first, ItemStack second) { return FoodStacking.mergeable(first, second); }
    @Redirect(method = "moveItemStackTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean survival$quickMoveMerge(ItemStack first, ItemStack second) { return FoodStacking.mergeable(first, second); }
    @Redirect(method = "canItemQuickReplace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private static boolean survival$dragMerge(ItemStack first, ItemStack second) { return FoodStacking.mergeable(first, second); }
}
