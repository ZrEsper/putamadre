package blackoutInteractive.brimmArmors.server;

import blackoutInteractive.brimmArmors.common.CommonProxy;
import blackoutInteractive.brimmArmors.server.ConfigsMatcher;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

public class ServerProxy
extends CommonProxy {
    @Override
    public void server(FMLDedicatedServerSetupEvent event) {
        NeoForge.EVENT_BUS.register((Object)new ConfigsMatcher());
    }
}

