package com.locknar.taczammocheck;

import com.locknar.taczammocheck.AmmoCheckServer;
import com.locknar.taczammocheck.ConfigMigration;
import com.locknar.taczammocheck.LocknarConfig;
import com.locknar.taczammocheck.ModSounds;
import com.locknar.taczammocheck.TaczAmmoCheckEventHooks;
import com.locknar.taczammocheck.client.ClientInit;
import com.locknar.taczammocheck.network.ModNetwork;
import com.locknar.taczammocheck.prone.ProneNetwork;
import com.locknar.taczammocheck.prone.ProneServer;
import com.locknar.taczammocheck.tactical.LocknarDoorKickMod;
import java.io.Serializable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import com.locknar.taczammocheck.port.DistExecutor;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.IConfigSpec;
import net.neoforged.fml.config.ModConfig;

import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value="locknar_doorkick")
public class LocknarTaczAmmoCheckMod {
    public static final String MOD_ID = "locknar_doorkick";
    public static final Logger LOGGER = LoggerFactory.getLogger((String)"locknar_doorkick");

    public LocknarTaczAmmoCheckMod(IEventBus modEventBus, net.neoforged.fml.ModContainer container) {

        ModSounds.SOUND_EVENTS.register(modEventBus);
        ConfigMigration.migrate(FMLPaths.CONFIGDIR.get());
        container.registerConfig(ModConfig.Type.COMMON, (IConfigSpec)LocknarConfig.COMMON_SPEC, "locknar_doorkick-common.toml");
        DistExecutor.safeRunWhenOn((Dist)Dist.CLIENT, () -> (DistExecutor.SafeRunnable & Serializable)() -> ClientInit.initModBus(modEventBus));
        ModNetwork.register();
        modEventBus.addListener(com.locknar.taczammocheck.port.SimpleChannel::registerAll);
        modEventBus.addListener(dev.zomboid.taczfeedback.FeedbackServer::register);
        new LocknarDoorKickMod(modEventBus);
        ProneNetwork.register();
        NeoForge.EVENT_BUS.register(ProneServer.class);
        NeoForge.EVENT_BUS.register(AmmoCheckServer.class);
        if (ModList.get().isLoaded("tacz")) {
            NeoForge.EVENT_BUS.addListener((com.tacz.guns.api.event.common.GunFireEvent event)->TaczAmmoCheckEventHooks.onAnyForgeEvent(event));
            NeoForge.EVENT_BUS.addListener((com.tacz.guns.api.event.common.GunReloadEvent event)->TaczAmmoCheckEventHooks.onAnyForgeEvent(event));
        }
        DistExecutor.safeRunWhenOn((Dist)Dist.CLIENT, () -> ClientInit::init);
    }
}
