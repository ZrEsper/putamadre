package dev.zomboid.storage;

import java.lang.ref.WeakReference;
import java.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Keeps the native slots intact and saves additional rows in the block entity's own NBT. */
public final class ChestStorage extends SimpleContainer {
    static final String KEY = "QuietlyOverflow14";
    static final Map<Container, WeakReference<ChestStorage>> CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    final Container base;
    final List<ItemStack> extra = new ArrayList<>();
    final ChestStorage first, second;

    private ChestStorage(Container base) {
        super(0);
        this.base = base;
        if (base.getClass().getName().equals("net.minecraft.world.CompoundContainer")) {
            first = wrap((Container) Reflect.get(base, "container1"));
            second = wrap((Container) Reflect.get(base, "container2"));
        } else {
            first = second = null;
            Object tag = Reflect.call(Reflect.call(base, "getPersistentData"), "getCompound", KEY);
            int size = ((Number) Reflect.call(tag, "getInt", "Size")).intValue();
            if (size < 0 || size > 1048576) throw new IllegalStateException("Invalid expanded chest size");
            while (extra.size() < size) extra.add(ItemStack.EMPTY);
            Object items = Reflect.call(tag, "getList", "Items", 10);
            Object registry = registry();
            for (int i = 0; i < ((Number) Reflect.call(items, "size")).intValue(); i++) {
                Object entry = Reflect.call(items, "getCompound", i);
                int slot = ((Number) Reflect.call(entry, "getInt", "Slot")).intValue();
                if (slot < 0 || slot >= size) throw new IllegalStateException("Invalid expanded chest slot");
                extra.set(slot, (ItemStack) Reflect.call(ItemStack.class, "parseOptional", registry, Reflect.call(entry, "getCompound", "Stack")));
            }
        }
    }

    public static ChestStorage wrap(Container base) {
        if (base instanceof ChestStorage storage) return storage;
        WeakReference<ChestStorage> ref = CACHE.get(base);
        ChestStorage storage = ref == null ? null : ref.get();
        if (storage == null) { storage = new ChestStorage(base); CACHE.put(base, new WeakReference<>(storage)); }
        return storage;
    }

    Object registry() { return Reflect.call(Reflect.call(base, "getLevel"), "registryAccess"); }
    public boolean expanded() { return first == null ? !extra.isEmpty() : first.expanded() || second.expanded(); }
    public void ensure(int minimum) {
        if (minimum <= getContainerSize()) return;
        if (first != null) { second.ensure(second.getContainerSize() + minimum - getContainerSize()); return; }
        int needed = minimum - base.getContainerSize();
        int rounded = Math.addExact(needed, 8) / 9 * 9;
        while (extra.size() < rounded) extra.add(ItemStack.EMPTY);
        setChanged();
    }
    @Override public int getContainerSize() { return first == null ? base.getContainerSize() + extra.size() : first.getContainerSize() + second.getContainerSize(); }
    @Override public int getMaxStackSize() { return base.getMaxStackSize(); }
    @Override public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= getContainerSize()) return ItemStack.EMPTY;
        if (first != null) return slot < first.getContainerSize() ? first.getItem(slot) : second.getItem(slot - first.getContainerSize());
        return slot < base.getContainerSize() ? base.getItem(slot) : extra.get(slot - base.getContainerSize());
    }
    @Override public void setItem(int slot, ItemStack stack) {
        if (slot < 0) return;
        if (stack.isEmpty() && slot >= getContainerSize()) return;
        ensure(slot + 1);
        if (first != null) { if (slot < first.getContainerSize()) first.setItem(slot, stack); else second.setItem(slot - first.getContainerSize(), stack); }
        else if (slot < base.getContainerSize()) base.setItem(slot, stack);
        else extra.set(slot - base.getContainerSize(), stack);
        setChanged();
    }
    @Override public ItemStack removeItem(int slot, int count) { ItemStack value = getItem(slot); if (value.isEmpty()) return ItemStack.EMPTY; ItemStack taken = value.split(count); if (value.isEmpty()) setItem(slot, ItemStack.EMPTY); setChanged(); return taken; }
    @Override public ItemStack removeItemNoUpdate(int slot) { ItemStack value = getItem(slot); if (!value.isEmpty()) setItem(slot, ItemStack.EMPTY); return value; }
    @Override public boolean isEmpty() { for (int i = 0; i < getContainerSize(); i++) if (!getItem(i).isEmpty()) return false; return true; }
    @Override public void clearContent() { for (int i = 0; i < getContainerSize(); i++) setItem(i, ItemStack.EMPTY); }
    @Override public boolean stillValid(Player player) { return base.stillValid(player); }
    @Override public void startOpen(Player player) { base.startOpen(player); }
    @Override public void stopOpen(Player player) { base.stopOpen(player); }
    @Override public void setChanged() {
        if (base == null) return;
        if (first != null) { first.setChanged(); second.setChanged(); return; }
        Object tag = Reflect.make("net.minecraft.nbt.CompoundTag");
        Reflect.call(tag, "putInt", "Size", extra.size());
        Object items = Reflect.make("net.minecraft.nbt.ListTag");
        Object registry = registry();
        for (int i = 0; i < extra.size(); i++) if (!extra.get(i).isEmpty()) {
            Object entry = Reflect.make("net.minecraft.nbt.CompoundTag");
            Reflect.call(entry, "putInt", "Slot", i);
            Reflect.call(entry, "put", "Stack", Reflect.call(extra.get(i), "save", registry));
            Reflect.call(items, "add", entry);
        }
        Reflect.call(tag, "put", "Items", items);
        Reflect.call(Reflect.call(base, "getPersistentData"), "put", KEY, tag);
        base.setChanged();
    }
}
