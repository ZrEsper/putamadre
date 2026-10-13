package com.locknar.taczammocheck.tactical.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.item.ItemStack;

public final class BackGunClientCache {
    private static final Map<Integer, Entry> ENTRIES = new ConcurrentHashMap<Integer, Entry>();

    private BackGunClientCache() {
    }

    public static void update(int entityId, ItemStack firstSlot, int selectedSlot) {
        ENTRIES.put(entityId, new Entry(firstSlot == null ? ItemStack.EMPTY : firstSlot.copy(), selectedSlot));
    }

    public static ItemStack getFirstSlot(AbstractClientPlayer player) {
        if (player == null) {
            return ItemStack.EMPTY;
        }
        if (player.isLocalPlayer()) {
            return player.getInventory().getItem(0);
        }
        Entry entry = ENTRIES.get(player.getId());
        return entry == null ? ItemStack.EMPTY : entry.firstSlot;
    }

    public static int getSelectedSlot(AbstractClientPlayer player) {
        if (player == null) {
            return -1;
        }
        if (player.isLocalPlayer()) {
            return player.getInventory().selected;
        }
        Entry entry = ENTRIES.get(player.getId());
        return entry == null ? -1 : entry.selectedSlot;
    }

    public static void prune(ClientLevel level) {
        if (level == null || ENTRIES.isEmpty()) {
            return;
        }
        ENTRIES.keySet().removeIf(entityId -> level.getEntity(entityId.intValue()) == null);
    }

    public static void clear() {
        ENTRIES.clear();
    }

    private record Entry(ItemStack firstSlot, int selectedSlot) {
    }
}
