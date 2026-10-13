package cn.kafei.mixin;
import dev.zomboid.storage.ChestMenus;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets="net.minecraft.server.level.ServerPlayer", remap=false)
public abstract class ExpandedChestMenuMixin {
    @Redirect(method="openMenu", at=@At(value="INVOKE", target="Lnet/minecraft/world/MenuProvider;createMenu(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/AbstractContainerMenu;"))
    private AbstractContainerMenu quietly$expanded(MenuProvider provider, int id, Inventory inventory, Player player) {
        return ChestMenus.wrap(provider.createMenu(id, inventory, player), id, inventory);
    }
}
