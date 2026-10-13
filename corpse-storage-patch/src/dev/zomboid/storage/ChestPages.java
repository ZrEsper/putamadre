package dev.zomboid.storage;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

public final class ChestPages extends SimpleContainer {
    final ChestStorage storage;
    int page;
    ItemStack previous, next, label;
    public ChestPages(ChestStorage storage) { super(54); this.storage = storage; update(); }
    public int pages() { return Math.max(1, (storage.getContainerSize() + 44) / 45); }
    public void move(int delta) { page = Math.max(0, Math.min(pages() - 1, page + delta)); update(); }
    static ItemStack icon(String item, String name) {
        Object base = Reflect.get(Reflect.type("net.minecraft.world.item.Items"), item);
        ItemStack stack = (ItemStack) Reflect.make("net.minecraft.world.item.ItemStack", base);
        Reflect.call(stack, "set", Reflect.get(Reflect.type("net.minecraft.core.component.DataComponents"), "CUSTOM_NAME"), Reflect.call(Reflect.type("net.minecraft.network.chat.Component"), "literal", name));
        return stack;
    }
    void update() { previous = icon("ARROW", "Página anterior"); next = icon("ARROW", "Página siguiente"); label = icon("PAPER", "Página " + (page + 1) + " / " + pages()); }
    @Override public ItemStack getItem(int slot) { if (slot >= 0 && slot < 45) return storage.getItem(page * 45 + slot); return slot == 45 ? previous : slot == 49 ? label : slot == 53 ? next : ItemStack.EMPTY; }
    @Override public void setItem(int slot, ItemStack stack) { if (slot >= 0 && slot < 45) storage.setItem(page * 45 + slot, stack); }
    @Override public ItemStack removeItem(int slot, int amount) { return slot >= 0 && slot < 45 ? storage.removeItem(page * 45 + slot, amount) : ItemStack.EMPTY; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return slot >= 0 && slot < 45 ? storage.removeItemNoUpdate(page * 45 + slot) : ItemStack.EMPTY; }
    @Override public void clearContent() { /* A page is a view; closing it must never clear storage. */ }
    @Override public void setChanged() { if (storage != null) storage.setChanged(); }
    @Override public boolean stillValid(Player player) { return storage.stillValid(player); }
    @Override public void startOpen(Player player) { storage.startOpen(player); }
    @Override public void stopOpen(Player player) { storage.stopOpen(player); }
}
