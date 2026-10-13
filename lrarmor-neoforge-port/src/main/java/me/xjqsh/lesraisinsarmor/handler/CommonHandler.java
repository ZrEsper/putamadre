package me.xjqsh.lesraisinsarmor.handler;

import me.xjqsh.lesraisinsarmor.network.NetworkHandler;
import me.xjqsh.lesraisinsarmor.network.s2c.ArmorDataMessage;
import me.xjqsh.lesraisinsarmor.resource.ArmorDataManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid="lrarmor")
public class CommonHandler {
    @SubscribeEvent
    public static void onServerResourceReload(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener)ArmorDataManager.getInstance());
    }

    @SubscribeEvent
    public static void onServerResourceReload(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            for (ServerPlayer s : event.getPlayerList().getPlayers()) {
                NetworkHandler.sendToClientPlayer(new ArmorDataMessage(ArmorDataManager.getInstance().getNetworkCache()),s);
            }
        } else {
            NetworkHandler.sendToClientPlayer(new ArmorDataMessage(ArmorDataManager.getInstance().getNetworkCache()),event.getPlayer());
        }
    }
    @net.neoforged.bus.api.SubscribeEvent
    public static void attributes(net.neoforged.neoforge.event.ItemAttributeModifierEvent event){
        if(event.getItemStack().getItem() instanceof me.xjqsh.lesraisinsarmor.item.LrArmorItem item){
            event.removeIf(entry->entry.modifier().id().getNamespace().equals("minecraft")&&entry.modifier().id().getPath().startsWith("armor."));
            for(var entry:item.getDefaultAttributeModifiers(event.getItemStack()).modifiers())event.replaceModifier(entry.attribute(),entry.modifier(),entry.slot());
        }
    }
}
