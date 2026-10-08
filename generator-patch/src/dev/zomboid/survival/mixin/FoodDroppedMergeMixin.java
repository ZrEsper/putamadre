package dev.zomboid.survival.mixin;

import dev.zomboid.survival.FoodClock;
import dev.zomboid.survival.FoodStacking;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class FoodDroppedMergeMixin {
    @Inject(method = "areMergable", at = @At("HEAD"), cancellable = true)
    private static void survival$droppedMerge(ItemStack first, ItemStack second, CallbackInfoReturnable<Boolean> callback) {
        if (FoodClock.perishable(first) && FoodClock.perishable(second)
                && first.getCount() + second.getCount() <= first.getMaxStackSize() && FoodStacking.mergeable(first, second)) {
            callback.setReturnValue(true);
        }
    }
}
