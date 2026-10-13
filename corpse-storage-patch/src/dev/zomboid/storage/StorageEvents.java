package dev.zomboid.storage;

import java.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/** Uses NeoForge's supported events; no injection into ServerPlayer or Containers is required. */
@EventBusSubscriber(modid="quietly")
public final class StorageEvents {
    @SubscribeEvent
    public static void open(PlayerContainerEvent.Open event) {
        Object player=Reflect.call(event,"getEntity"), menu=Reflect.call(event,"getContainer");
        if(!Reflect.type("net.minecraft.server.level.ServerPlayer").isInstance(player) || Reflect.get(player,"containerMenu")!=menu || !(menu instanceof ChestMenu original) || menu instanceof ChestPageMenu) return;
        Container container=original.getContainer();
        if(!ChestMenus.nativeChest(container)) return;
        ChestStorage storage=ChestStorage.wrap(container);
        if(!storage.expanded()) return;
        Object title=container.getClass().getName().equals("net.minecraft.world.CompoundContainer")
            ? Reflect.call(Reflect.type("net.minecraft.network.chat.Component"),"translatable","container.chestDouble",new Object[0])
            : Reflect.call(container,"getDisplayName");
        // Close the already-open native menu first, balancing its opener before the new one.
        Reflect.call(player,"closeContainer");
        MenuConstructor factory=(id,inventory,owner)->new ChestPageMenu(id,inventory,storage,original,false);
        Reflect.call(player,"openMenu",Reflect.make("net.minecraft.world.SimpleMenuProvider",factory,title));
        // The nested Open event sees ChestPageMenu and exits, so this cannot reopen recursively.
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void broken(BlockDropsEvent event) {
        Object blockEntity=Reflect.call(event,"getBlockEntity");
        if(!(blockEntity instanceof Container container) || !ChestMenus.nativeChest(container)) return;
        ChestStorage storage=ChestStorage.wrap(container);
        if(storage.extra.isEmpty()) return;
        Object level=Reflect.call(event,"getLevel"),pos=Reflect.call(event,"getPos");
        double x=((Number)Reflect.call(pos,"getX")).doubleValue()+.5,y=((Number)Reflect.call(pos,"getY")).doubleValue()+.5,z=((Number)Reflect.call(pos,"getZ")).doubleValue()+.5;
        List<Object> added=new ArrayList<>();
        for(ItemStack stack:storage.extra) if(!stack.isEmpty()) added.add(Reflect.make("net.minecraft.world.entity.item.ItemEntity",level,x,y,z,stack.copy()));
        // Deliberate breaking is distinct from search overflow; vanilla handles native slots.
        ((Collection<Object>)Reflect.call(event,"getDrops")).addAll(added);
        Collections.fill(storage.extra,ItemStack.EMPTY);storage.setChanged();
    }
}
