package dev.zomboid.scavenging;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
@EventBusSubscriber(modid="quietly")
public final class Events {
 @SubscribeEvent public static void logout(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event){Search.logout(event);}
 @SubscribeEvent public static void stop(net.neoforged.neoforge.event.server.ServerStoppingEvent e){Search.clear();}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void block(PlayerInteractEvent.RightClickBlock event){Search.event(event);}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void entity(PlayerInteractEvent.EntityInteract event){Search.entityEvent(event);}
 @SubscribeEvent(priority=EventPriority.HIGHEST) public static void entityAt(PlayerInteractEvent.EntityInteractSpecific event){Search.entityEvent(event);}
}
