package com.locknar.taczammocheck.client;

import com.locknar.taczammocheck.client.AmmoCheckClient;
import com.locknar.taczammocheck.client.ClientCleanup;
import com.locknar.taczammocheck.prone.ProneClient;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;

public final class ClientInit {
    private ClientInit() {
    }

    public static void initModBus(IEventBus modEventBus) {
        modEventBus.addListener(ProneClient::registerKeys);
    }

    public static void init() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, AmmoCheckClient::onRenderGui);
        NeoForge.EVENT_BUS.addListener(AmmoCheckClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(ClientCleanup::onClientLoggingOut);
        ProneClient.init();
    }
}
