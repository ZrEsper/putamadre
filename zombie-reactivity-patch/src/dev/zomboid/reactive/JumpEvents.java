package dev.zomboid.reactive;
import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.neoforge.event.entity.living.LivingEvent;
@EventBusSubscriber(modid="zomboid_hordes")
public class JumpEvents {
 @SubscribeEvent public static void jump(LivingEvent.LivingJumpEvent event){Reactive.jump(event.getEntity());}
}
