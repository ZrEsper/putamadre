package dev.zomboid.storage;

import net.minecraft.world.inventory.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.ItemStack;

public final class ChestPageMenu extends ChestMenu {
    final ChestPages view;
    final ChestMenu original;
    public ChestPageMenu(int id, Inventory inventory, ChestStorage storage, ChestMenu original) { this(id, inventory, storage, original, true); }
    public ChestPageMenu(int id, Inventory inventory, ChestStorage storage, ChestMenu original, boolean originalOpen) { this(id, inventory, new ChestPages(storage), original, originalOpen); }
    private ChestPageMenu(int id, Inventory inventory, ChestPages view, ChestMenu original, boolean originalOpen) {
        super(MenuType.GENERIC_9x6, id, inventory, view, 6);
        this.view = view; this.original = original;
        for (int i = 45; i < 54; i++) {
            Slot old = slots.get(i);
            Slot control = new Slot(view, i, old.x, old.y) {
                @Override public boolean mayPlace(ItemStack stack) { return false; }
                @Override public boolean mayPickup(Player player) { return false; }
            };
            control.index = old.index; slots.set(i, control);
        }
        // The native provider has already opened its inventory; keep exactly one opener.
        if(originalOpen) original.getContainer().stopOpen(inventory.player);
    }
    @Override public boolean stillValid(Player player) { return original.stillValid(player) && view.stillValid(player); }
    @Override public void clicked(int slot, int button, ClickType type, Player player) {
        if (slot >= 45 && slot < 54) {
            if (type == ClickType.PICKUP && button == 0 && (slot == 45 || slot == 53)) { resetQuickCraft(); view.move(slot == 45 ? -1 : 1); broadcastChanges(); }
            return;
        }
        super.clicked(slot, button, type, player);
    }
    @Override public ItemStack quickMoveStack(Player player, int slot) { return slot >= 45 && slot < 54 ? ItemStack.EMPTY : super.quickMoveStack(player, slot); }
}
