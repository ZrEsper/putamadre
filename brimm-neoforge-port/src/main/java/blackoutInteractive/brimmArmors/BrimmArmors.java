package blackoutInteractive.brimmArmors;

import blackoutInteractive.brimmArmors.client.ClientProxy;
import blackoutInteractive.brimmArmors.common.CommonProxy;
import blackoutInteractive.brimmArmors.common.configurations.ConfigsManager;
import blackoutInteractive.brimmArmors.common.packets.ConfigCheckPacket;
import blackoutInteractive.brimmArmors.common.packets.CraftPacket;
import blackoutInteractive.brimmArmors.common.packets.PatchesAdjusterDebugPacket;
import blackoutInteractive.brimmArmors.common.registries.BlockRegistry;
import blackoutInteractive.brimmArmors.common.registries.ItemRegistry;
import blackoutInteractive.brimmArmors.common.registries.RecipeSerializersRegistry;
import blackoutInteractive.brimmArmors.common.registries.TileRegistry;
import blackoutInteractive.brimmArmors.common.workbench.CraftsManager;
import blackoutInteractive.brimmArmors.server.ServerProxy;
import blackoutInteractive.brimmArmors.server.commands.BrimmDebugCommand;
import blackoutInteractive.ema_08_.birgadierWrapper.CommandBuilder;
import blackoutInteractive.ema_08_.birgadierWrapper.IRegistrableCommand;
import blackoutInteractive.ema_08_.items.effectsProvidingArmors.v2.ArmorEffectsManager;
import blackoutInteractive.ema_08_.rendering.geom.RotQuaternionPool;
import blackoutInteractive.ema_08_.simpleNet.SimpleChannelHandler;
import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;

import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value="brimm")
public class BrimmArmors {
    public static final String MOD_ID = "brimm";
    public static SimpleChannelHandler network;
    public static CommonProxy proxy;
    public static final Logger LOGGER;

    public BrimmArmors(IEventBus eventBus) {
        proxy = FMLEnvironment.dist.isClient() ? new ClientProxy() : new ServerProxy();
        network = new SimpleChannelHandler(MOD_ID, "main", "1", List.of(CraftPacket.class, ConfigCheckPacket.class, PatchesAdjusterDebugPacket.class));
        eventBus.addListener(network::registerPayloads);
        if (FMLEnvironment.dist.isClient()) {
            eventBus.addListener(ClientProxy::registerExtensions);
            eventBus.addListener(ClientProxy::registerReloadListeners);
        }
        ConfigsManager.init();
        ItemRegistry.register(eventBus);
        BlockRegistry.register(eventBus);
        TileRegistry.register(eventBus);
        RecipeSerializersRegistry.register(eventBus);
        eventBus.addListener(this::preInit);
        eventBus.addListener(this::init);
        eventBus.addListener(this::server);
        eventBus.addListener(this::client);
        NeoForge.EVENT_BUS.register((Object)this);
        NeoForge.EVENT_BUS.register(ArmorEffectsManager.class);
    }

    private void preInit(FMLCommonSetupEvent event) {
        proxy.preInit(event);
    }

    private void init(FMLLoadCompleteEvent event) {
        proxy.init(event);
        CraftsManager.buildAll();
        RotQuaternionPool.freeAll();
    }

    private void server(FMLDedicatedServerSetupEvent event) {
        proxy.server(event);
    }

    private void client(FMLClientSetupEvent event) {
        proxy.client(event);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        IRegistrableCommand[] COMMANDS;
        CommandDispatcher dispatcher = event.getDispatcher();
        for (IRegistrableCommand cmd : COMMANDS = new IRegistrableCommand[]{new BrimmDebugCommand()}) {
            dispatcher.register(CommandBuilder.build(cmd.getBuilt()));
        }
    }

    static {
        proxy = new CommonProxy(){};
        LOGGER = LogManager.getLogger((String)MOD_ID);
    }
}

