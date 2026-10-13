package com.locknar.taczammocheck.tactical;

import com.locknar.taczammocheck.ModSounds;
import com.locknar.taczammocheck.tactical.ClientSideBootstrap;
import com.locknar.taczammocheck.tactical.ModItems;
import com.locknar.taczammocheck.tactical.TaczWallObstructionCommon;
import com.locknar.taczammocheck.tactical.network.ModNetwork;
import com.locknar.taczammocheck.tactical.server.DoorInteractionGlobalController;
import com.locknar.taczammocheck.tactical.server.MirrorGunHandler;
import com.locknar.taczammocheck.tactical.server.ServerKickHandler;
import com.locknar.taczammocheck.tactical.server.TaczHighReadyServer;
import com.locknar.taczammocheck.tactical.server.WeaponSwitchServerTracker;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;

import net.minecraft.core.registries.BuiltInRegistries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocknarDoorKickMod {
    public static final String MOD_ID = "locknar_doorkick";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"locknar_doorkick");

    public LocknarDoorKickMod(IEventBus modEventBus) {

        ModItems.ITEMS.register(modEventBus);

        modEventBus.addListener(this::addCreativeTabItems);
        ClientSideBootstrap.initModBus(modEventBus);
        ModNetwork.register();
        NeoForge.EVENT_BUS.addListener(ServerKickHandler::onServerTick);
        NeoForge.EVENT_BUS.addListener(MirrorGunHandler::onServerTick);
        NeoForge.EVENT_BUS.addListener(MirrorGunHandler::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(MirrorGunHandler::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(TaczHighReadyServer::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(TaczHighReadyServer::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(TaczHighReadyServer::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(WeaponSwitchServerTracker::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(WeaponSwitchServerTracker::onStartTracking);
        NeoForge.EVENT_BUS.addListener(WeaponSwitchServerTracker::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(WeaponSwitchServerTracker::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(WeaponSwitchServerTracker::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(WeaponSwitchServerTracker::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(DoorInteractionGlobalController::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, DoorInteractionGlobalController::onCommand);
        NeoForge.EVENT_BUS.addListener(DoorInteractionGlobalController::onPlayerLoggedIn);
        if (ModList.get().isLoaded("tacz")) {
            NeoForge.EVENT_BUS.addListener((com.tacz.guns.api.event.common.GunFireEvent event)->TaczWallObstructionCommon.onAnyForgeEvent(event));
            NeoForge.EVENT_BUS.addListener((com.tacz.guns.api.event.common.GunReloadEvent event)->TaczWallObstructionCommon.onAnyForgeEvent(event));
        }
        ClientSideBootstrap.initForgeBus();
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES || event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept((ItemLike)ModItems.MIRROR_GUN.get());
        }
    }

}
