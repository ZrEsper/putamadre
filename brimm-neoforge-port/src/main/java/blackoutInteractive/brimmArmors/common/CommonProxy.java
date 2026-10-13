package blackoutInteractive.brimmArmors.common;

import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

public abstract class CommonProxy {
    public void preInit(FMLCommonSetupEvent event) {
    }

    public void init(FMLLoadCompleteEvent event) {
    }

    public void client(FMLClientSetupEvent event) {
        throw new IllegalStateException("Wrong side");
    }

    public void server(FMLDedicatedServerSetupEvent event) {
        throw new IllegalStateException("Wrong side");
    }
}

