package dev.zomboid.taczfeedback;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import net.minecraft.server.level.ServerPlayer;
@EventBusSubscriber(modid="locknar_doorkick")
public class FeedbackServer {
 public static void register(RegisterPayloadHandlersEvent e){e.registrar("feedback-1").playToClient(HitDirection.TYPE,HitDirection.CODEC,(p,c)->c.enqueueWork(()->FeedbackClient.accept(p)));}
 @SubscribeEvent public static void shot(EntityHurtByGunEvent.Post e){
  if(e.getLogicalSide().isClient()||e.isCanceled()||e.getBaseAmount()<=0||!(e.getHurtEntity() instanceof ServerPlayer player))return;
  var attacker=e.getAttacker();if(attacker==player)return;
  net.minecraft.world.phys.Vec3 source;
  if(attacker!=null)source=attacker.getEyePosition();else if(e.getBullet()!=null&&e.getBullet().getDeltaMovement().lengthSqr()>1e-8)source=player.getEyePosition().subtract(e.getBullet().getDeltaMovement().normalize().scale(16));else return;
  PacketDistributor.sendToPlayer(player,new HitDirection(source.x,source.y,source.z));
 }
}
