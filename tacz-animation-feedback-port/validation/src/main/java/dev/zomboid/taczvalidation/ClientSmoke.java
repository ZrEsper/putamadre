package dev.zomboid.taczvalidation;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.block.Blocks;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.entity.IGunOperator;
import dev.zomboid.taczfeedback.ThirdPersonAimClient;
import dev.zomboid.taczfeedback.FeedbackServer;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
@EventBusSubscriber(modid="tacz_validation",value=Dist.CLIENT)
public class ClientSmoke {
 static int ticks;static Zombie sniper;static boolean failed;
 static void check(boolean ok,String reason){if(!ok){failed=true;throw new AssertionError(reason);}}
 @SubscribeEvent public static void tick(ClientTickEvent.Post event){
  if(!Boolean.getBoolean("tacz.validation.scene"))return;var mc=Minecraft.getInstance();if(mc.player==null||mc.level==null)return;
  try{
   int n=++ticks;
   if(n==5){var uuid=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{
    var server=mc.getSingleplayerServer();var level=server.overworld();var p=server.getPlayerList().getPlayer(uuid);level.setDayTime(6000);
    for(int x=-10;x<=10;x++)for(int z=-10;z<=10;z++)level.setBlockAndUpdate(new BlockPos(x,0,z),Blocks.STONE.defaultBlockState());
    for(int x=-3;x<=3;x++)for(int y=1;y<=5;y++)level.setBlockAndUpdate(new BlockPos(x,y,6),Blocks.STONE.defaultBlockState());
    p.teleportTo(level,.5,1,.5,0,0);p.setInvulnerable(true);
    var gun=new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("tacz:modern_kinetic_gun")));var igun=IGun.getIGunOrNull(gun);igun.setGunId(gun,ResourceLocation.parse("tacz:ak47"));igun.setCurrentAmmoCount(gun,30);p.setItemSlot(EquipmentSlot.MAINHAND,gun);
    sniper=new Zombie(level);sniper.setPos(.9,1,4);sniper.setYRot(180);sniper.yBodyRot=180;sniper.yHeadRot=180;sniper.setNoAi(true);sniper.setInvulnerable(true);sniper.setItemSlot(EquipmentSlot.HEAD,new ItemStack(net.minecraft.world.item.Items.IRON_HELMET));
    var rifle=new ItemStack(gun.getItem());var ir=IGun.getIGunOrNull(rifle);ir.setGunId(rifle,ResourceLocation.parse("tacz:ai_awp"));ir.setCurrentAmmoCount(rifle,5);
    var scope=new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse("tacz:attachment")));var id=com.tacz.guns.api.TimelessAPI.getAllCommonAttachmentIndex().stream().map(e->e.getKey()).filter(i->i.getPath().equals("scope_acog_ta31")).findFirst().orElse(ResourceLocation.parse("tacz:scope_standard_4x"));((IAttachment)scope.getItem()).setAttachmentId(scope,id);ir.installAttachment(level.registryAccess(),rifle,scope);
    sniper.setItemSlot(EquipmentSlot.MAINHAND,rifle);level.addFreshEntity(sniper);IGunOperator.fromLivingEntity(sniper).draw(()->rifle);IGunOperator.fromLivingEntity(sniper).aim(true);
   });mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);mc.options.framerateLimit().set(60);}
   if(n==40){check(ThirdPersonAimClient.active(),"Native third person aiming did not activate");var target=ThirdPersonAimClient.pick();check(target!=null&&Double.isFinite(target.x),"No camera target");var oldX=mc.player.getXRot();var oldY=mc.player.getYRot();var state=ThirdPersonAimClient.prepareShot();ThirdPersonAimClient.restore(state);check(mc.player.getXRot()==oldX&&mc.player.getYRot()==oldY,"Aiming caused accumulating camera drift");
    var sprite=((net.minecraft.client.renderer.texture.TextureAtlas)mc.getTextureManager().getTexture(ResourceLocation.parse("minecraft:textures/atlas/particles.png"))).getSprite(ResourceLocation.parse("tacz:bullet_hole"));check(!sprite.contents().name().equals(net.minecraft.client.renderer.texture.MissingTextureAtlasSprite.getLocation()),"Impact texture missing from particle atlas");
    mc.level.addParticle(new com.tacz.guns.particles.BulletHoleOption(Direction.NORTH,new BlockPos(0,3,6),"tacz:762x39","tacz:ak47","tacz:ak47"),.5,3.4,5.999,0,0,0);
   }
   if(n==50){var uuid=mc.player.getUUID();mc.getSingleplayerServer().execute(()->{var p=mc.getSingleplayerServer().getPlayerList().getPlayer(uuid);var id=ResourceLocation.parse("tacz:ai_awp");IGunOperator.fromLivingEntity(sniper).aim(true);FeedbackServer.shot(new EntityHurtByGunEvent.Post(null,p,sniper,id,id,1,null,false,1,net.neoforged.fml.LogicalSide.SERVER));});}
   if(n==55){var hits=dev.zomboid.taczfeedback.FeedbackClient.class.getDeclaredField("hits");hits.setAccessible(true);check(!((java.util.List<?>)hits.get(null)).isEmpty(),"Server gunshot direction did not reach client HUD");mc.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);mc.player.setYRot(12);}
   if(n==58){net.minecraft.client.Screenshot.grab(mc.gameDirectory,mc.getMainRenderTarget(),c->System.out.println("SCENE_SCREENSHOT "+c.getString()));}
   if(n==70){var entity=mc.level.getEntity(sniper.getId());check(entity instanceof net.minecraft.world.entity.LivingEntity,"Scoped shooter did not sync");var living=(net.minecraft.world.entity.LivingEntity)entity;System.out.println("SCENE_SCOPE_ZOOM "+IGun.getIGunOrNull(living.getMainHandItem()).getAimingZoom(living.getMainHandItem())+" aiming="+IGunOperator.fromLivingEntity(living).getSynIsAiming());
    // Verify compatibility guard with the provided Dungeons Iso mode.
    var field=Class.forName("com.cleannrooster.dungeons_iso.mod.Mod").getDeclaredField("enabled");field.setAccessible(true);field.set(null,true);check(!ThirdPersonAimClient.active(),"Third person override stole isometric aiming");
    var camera=mc.gameRenderer.getMainCamera();var angle=new net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles(camera,0,37,45,0);
    var mts=Class.forName("mcinterface1211.InterfaceEventsEntityRendering");mts.getField("adjustedCamera").setBoolean(null,true);mts.getMethod("onIVCameraSetup",net.neoforged.neoforge.client.event.ViewportEvent.ComputeCameraAngles.class).invoke(null,angle);
    check(!mts.getField("adjustedCamera").getBoolean(null),"Vehicle seat camera remained armed in isometric mode");check(angle.getYaw()==37&&angle.getPitch()==45,"Vehicle changed Iso rotation");
    var fov=new net.neoforged.neoforge.client.event.ViewportEvent.ComputeFov(mc.gameRenderer,camera,0,70,true);com.tacz.guns.client.event.CameraSetupEvent.applyScopeMagnification(fov);check(fov.getFOV()==70,"Scope zoom changed Iso projection");
    com.tacz.guns.client.event.CameraSetupEvent.applyLevelCameraAnimation(angle);check(angle.getYaw()==37&&angle.getPitch()==45,"Gun animation moved Iso camera");
    field.set(null,false);
   }
   if(n==78){var glints=dev.zomboid.taczfeedback.FeedbackClient.class.getDeclaredField("snipers");glints.setAccessible(true);check(!((java.util.List<?>)glints.get(null)).isEmpty(),"Scoped sniper did not enter glare render cache");var living=(net.minecraft.world.entity.LivingEntity)mc.level.getEntity(sniper.getId());var delta=mc.gameRenderer.getMainCamera().getPosition().subtract(living.getEyePosition());System.out.println("GLARE_GEOMETRY dot="+living.getViewVector(1).dot(delta.normalize())+" camera="+mc.gameRenderer.getMainCamera().getPosition()+" yaw="+living.getYRot()+" head="+living.yHeadRot+" cached="+glints.get(null));net.minecraft.client.Screenshot.grab(mc.gameDirectory,mc.getMainRenderTarget(),c->System.out.println("GLARE_SCREENSHOT "+c.getString()));}
   if(n==85){System.out.println("CLIENT_SCENE_PASSED: native client/world, adaptive aim, camera restore, impact atlas/particle, server hit packet and isometric guard");java.nio.file.Files.writeString(java.nio.file.Path.of("client-scene-result.txt"),"PASS");mc.stop();}
  }catch(Throwable e){if(!failed){failed=true;e.printStackTrace();}try{java.nio.file.Files.writeString(java.nio.file.Path.of("client-scene-result.txt"),"FAIL: "+e);}catch(Exception ignored){}mc.stop();}
 }
}
