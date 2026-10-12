package dev.zomboid.gore;
import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.api.distmarker.Dist;import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
@EventBusSubscriber(modid="zomboid_gore",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT) public final class ClientEvents {@SubscribeEvent public static void setup(FMLClientSetupEvent event){event.enqueueWork(Gore::registerVisuals);}}
