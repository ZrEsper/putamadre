package dev.zomboid.hordes.mixin;
@org.spongepowered.asm.mixin.Mixin(targets="net.minecraft.world.level.storage.loot.LootTable")
public class ContainerProfileMixin {
 @com.llamalad7.mixinextras.injector.ModifyExpressionValue(method="fill",at=@org.spongepowered.asm.mixin.injection.At(value="INVOKE",target="Lnet/minecraft/world/level/storage/loot/LootTable;getRandomItems(Lnet/minecraft/world/level/storage/loot/LootParams;J)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"),require=1)
 private it.unimi.dsi.fastutil.objects.ObjectArrayList<net.minecraft.world.item.ItemStack> zloot$profile(it.unimi.dsi.fastutil.objects.ObjectArrayList<net.minecraft.world.item.ItemStack> original,net.minecraft.world.Container container,net.minecraft.world.level.storage.loot.LootParams params,long seed){return (it.unimi.dsi.fastutil.objects.ObjectArrayList<net.minecraft.world.item.ItemStack>)dev.zomboid.hordes.LootProfiles.container(original,container,params);}
}
