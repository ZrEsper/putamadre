package dev.zomboid.survival;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public final class GeneratorMenu extends AbstractContainerMenu {
    public static final int FUEL_SLOTS = 54;
    private final Container fuel;
    private final ContainerData data;

    public GeneratorMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(FUEL_SLOTS), new SimpleContainerData(7));
    }

    public GeneratorMenu(int id, Inventory inventory, Container fuel, ContainerData data) {
        super(GeneratorMenus.TYPE, id);
        checkContainerSize(fuel, FUEL_SLOTS);
        checkContainerDataCount(data, 7);
        this.fuel = fuel;
        this.data = data;
        fuel.startOpen(inventory.player);
        // Slot 0 is the main fuel inlet. Expose all old storage slots as a reserve bank.
        addSlot(fuelSlot(0, 76, 63));
        for (int i = 1; i < FUEL_SLOTS; i++) {
            int reserve = i - 1;
            addSlot(fuelSlot(i, 174 + reserve % 9 * 18, 28 + reserve / 9 * 18));
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 140 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 198));
        addDataSlots(data);
    }

    private Slot fuelSlot(int index, int x, int y) {
        return new Slot(fuel, index, x, y) {
            @Override public boolean mayPlace(ItemStack stack) {
                return stack.getBurnTime(RecipeType.SMELTING) > 0 && fuel.canPlaceItem(index, stack);
            }
        };
    }

    @Override public boolean stillValid(Player player) { return fuel.stillValid(player); }
    @Override public void removed(Player player) { super.removed(player); fuel.stopOpen(player); }

    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < FUEL_SLOTS) {
            if (!moveItemStackTo(stack, FUEL_SLOTS, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            if (stack.getBurnTime(RecipeType.SMELTING) <= 0 || !moveItemStackTo(stack, 0, FUEL_SLOTS, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY); else slot.setChanged();
        return original;
    }

    private int fullInt(int index) { return (data.get(index) & 65535) | ((data.get(index + 1) & 65535) << 16); }
    public int burn() { return Math.max(0, fullInt(0)); }
    public int energy() { return Math.max(0, Math.min(64000, fullInt(2))); }
    public int fuelTotal() { return Math.max(1, fullInt(4)); }
    public long remainingTicks() {
        long furnaceTicks = 0;
        for (int i = 0; i < FUEL_SLOTS; i++) {
            ItemStack stack = fuel.getItem(i);
            furnaceTicks += (long) Math.max(0, stack.getBurnTime(RecipeType.SMELTING)) * stack.getCount();
        }
        return burn() + (furnaceTicks * 45 + (data.get(6) & 127)) / 128;
    }
}
