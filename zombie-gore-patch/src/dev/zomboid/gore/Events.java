package dev.zomboid.gore;
import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;import net.neoforged.neoforge.event.tick.EntityTickEvent;
@EventBusSubscriber(modid="zomboid_gore") public final class Events {
 @SubscribeEvent public static void damage(LivingDamageEvent.Post event){Gore.hurt(event);}
 @SubscribeEvent public static void incoming(LivingIncomingDamageEvent event){Gore.incoming(event);}
 @SubscribeEvent public static void tick(EntityTickEvent.Post event){Gore.tick(event);}
}
