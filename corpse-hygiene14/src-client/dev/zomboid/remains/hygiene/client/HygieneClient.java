package dev.zomboid.remains.hygiene.client;
import dev.zomboid.remains.hygiene.HygieneContent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;import net.minecraft.core.particles.SimpleParticleType;
@EventBusSubscriber(modid="zombie_remains_loot",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class HygieneClient{
 @SubscribeEvent public static void providers(RegisterParticleProvidersEvent event){event.registerSpriteSet((SimpleParticleType)HygieneContent.FLY,FlyParticle.Provider::new);}
}
