package dev.zomboid.decor.needs.client;
import dev.zomboid.decor.needs.Content;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;import net.minecraft.core.particles.SimpleParticleType;
@EventBusSubscriber(modid="doomsday_decoration",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class Particles{
 @SubscribeEvent public static void providers(RegisterParticleProvidersEvent e){e.registerSpriteSet((SimpleParticleType)Content.PEE,s->new DropParticle.Provider(s,false));e.registerSpriteSet((SimpleParticleType)Content.POOP,s->new DropParticle.Provider(s,true));}
}
