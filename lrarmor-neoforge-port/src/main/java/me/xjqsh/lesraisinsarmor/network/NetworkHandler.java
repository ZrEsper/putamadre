package me.xjqsh.lesraisinsarmor.network;
import me.xjqsh.lesraisinsarmor.network.s2c.ArmorDataMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
@EventBusSubscriber(modid="lrarmor",bus=EventBusSubscriber.Bus.MOD)
public final class NetworkHandler {
 @SubscribeEvent public static void register(RegisterPayloadHandlersEvent event){event.registrar("1").playToClient(ArmorDataMessage.TYPE,ArmorDataMessage.CODEC,(message,context)->context.enqueueWork(()->ArmorDataMessage.onReceive(message)));}
 public static void sendToClientPlayer(Object message,Player player){if(player instanceof ServerPlayer server)PacketDistributor.sendToPlayer(server,(ArmorDataMessage)message);}
 public static void sendToNearby(Level world,BlockPos pos,Object message){if(world instanceof ServerLevel level)for(ServerPlayer player:level.players())if(player.distanceToSqr(pos.getX(),pos.getY(),pos.getZ())<192*192)sendToClientPlayer(message,player);}
 public static void sendToNearby(Entity entity,Object message,int distance){if(entity.level() instanceof ServerLevel level)for(ServerPlayer player:level.players())if(player.distanceToSqr(entity)<distance*distance)sendToClientPlayer(message,player);}
 public static void sendToNearby(Entity entity,Object message){sendToNearby(entity,message,192);}
}
