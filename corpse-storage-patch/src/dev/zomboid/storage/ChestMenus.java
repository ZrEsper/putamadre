package dev.zomboid.storage;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.*;
import net.minecraft.world.entity.player.*;

public final class ChestMenus {
    static boolean nativeChest(Container container) {
        String name = container.getClass().getName();
        return name.equals("net.minecraft.world.level.block.entity.ChestBlockEntity") || name.equals("net.minecraft.world.level.block.entity.BarrelBlockEntity") || name.equals("net.minecraft.world.CompoundContainer");
    }
    public static AbstractContainerMenu wrap(AbstractContainerMenu menu, int id, Inventory inventory) {
        if (menu instanceof ChestMenu chest && !(menu instanceof ChestPageMenu) && nativeChest(chest.getContainer())) {
            ChestStorage storage = ChestStorage.wrap(chest.getContainer());
            if (storage.expanded()) return new ChestPageMenu(id, inventory, storage, chest);
        }
        return menu;
    }
    public static Container drops(Container container) { return nativeChest(container) ? ChestStorage.wrap(container) : container; }
}
