package dev.zomboid.taczfeedback;
import java.util.*;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.TimelessAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid="locknar_doorkick",value=Dist.CLIENT)
public class FeedbackClient {
 private record Hit(Vec3 source,long time){}
 private record Glint(LivingEntity shooter,boolean visible){}
 private static final ArrayList<Hit> hits=new ArrayList<>();
 private static final ArrayList<Glint> snipers=new ArrayList<>();
 private static final ResourceLocation FLARE=ResourceLocation.fromNamespaceAndPath("tacz","textures/effect/scope_glare.png");
 private static int ticks;
 private static Object world;
 public static void accept(HitDirection p){
  if(!Double.isFinite(p.x())||!Double.isFinite(p.y())||!Double.isFinite(p.z()))return;
  var source=new Vec3(p.x(),p.y(),p.z());hits.removeIf(h->h.source.distanceToSqr(source)<4);if(hits.size()>=8)hits.remove(0);hits.add(new Hit(source,System.nanoTime()));
 }
 @SubscribeEvent public static void tick(ClientTickEvent.Post e){
  var mc=Minecraft.getInstance();if(world!=mc.level){hits.clear();snipers.clear();world=mc.level;}
  if(mc.level==null||mc.player==null){hits.clear();snipers.clear();return;}
  if(mc.isPaused()||++ticks%4!=0)return;
  snipers.clear();Vec3 camera=mc.gameRenderer.getMainCamera().getPosition();
  var nearby=new ArrayList<LivingEntity>();
  for(var entity:mc.level.entitiesForRendering())if(entity instanceof LivingEntity living&&living!=mc.player&&living.isAlive()&&living.distanceToSqr(mc.player)<160*160){
   var stack=living.getMainHandItem();var gun=IGun.getIGunOrNull(stack);if(gun==null)continue;
   var index=TimelessAPI.getCommonGunIndex(gun.getGunId(stack));if(index.isEmpty()||!index.get().getType().equals("sniper"))continue;
   if(gun.getAimingZoom(stack)<2||!IGunOperator.fromLivingEntity(living).getSynIsAiming())continue;
   nearby.add(living);
  }
  nearby.sort(Comparator.comparingDouble(l->l.distanceToSqr(mc.player)));
  for(int i=0;i<Math.min(24,nearby.size());i++){
   var living=nearby.get(i);var eye=living.getEyePosition();
   boolean visible=mc.level.clip(new ClipContext(camera,eye,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,mc.player)).getType()==HitResult.Type.MISS;
   snipers.add(new Glint(living,visible));
  }
 }
 @SubscribeEvent public static void hud(RenderGuiEvent.Post e){
  var mc=Minecraft.getInstance();if(mc.level==null||mc.player==null||mc.options.hideGui||mc.screen!=null)return;
  long now=System.nanoTime();hits.removeIf(h->now-h.time>1_150_000_000L);
  int width=mc.getWindow().getGuiScaledWidth(),height=mc.getWindow().getGuiScaledHeight();double radius=Math.max(28,Math.min(72,Math.min(width,height)*.22));
  var origin=mc.player.getEyePosition();float yaw=mc.gameRenderer.getMainCamera().getYRot();
  for(var hit:hits){float fade=FeedbackMath.fade((now-hit.time)/1_000_000);int color=((int)(190*fade)<<24)|0xFF3030;double bearing=FeedbackMath.bearing(hit.source.x-origin.x,hit.source.z-origin.z,yaw);
   for(int i=-12;i<=12;i++){double angle=bearing+i*.012;int x=(int)(width*.5+Math.sin(angle)*radius),y=(int)(height*.5-Math.cos(angle)*radius);e.getGuiGraphics().fill(x-1,y-1,x+2,y+2,color);}
  }
 }
 @SubscribeEvent public static void glare(RenderLevelStageEvent e){
  if(e.getStage()!=RenderLevelStageEvent.Stage.AFTER_PARTICLES)return;
  var mc=Minecraft.getInstance();if(mc.player==null||mc.level==null)return;
  Vec3 camera=e.getCamera().getPosition();var type=RenderType.entityTranslucent(FLARE);var buffers=mc.renderBuffers().bufferSource();var consumer=buffers.getBuffer(type);boolean drew=false;
  float partial=e.getPartialTick().getGameTimeDeltaPartialTick(false);
  for(var glint:snipers){var shooter=glint.shooter;if(!shooter.isAlive()||!glint.visible)continue;
   Vec3 eye=shooter.getEyePosition(partial),observer=ThirdPersonAimClient.isIso()?mc.player.getEyePosition(partial):camera,toCamera=observer.subtract(eye);double distance=toCamera.length();double alignment=distance==0?0:shooter.getViewVector(partial).dot(toCamera.scale(1/distance));
   if(!FeedbackMath.glare(alignment,distance,IGunOperator.fromLivingEntity(shooter).getSynIsAiming(),true,true,true))continue;
   // Check changed camera position each render only for eligible visible scopes: no through-wall glint.
   if(mc.level.clip(new ClipContext(camera,eye,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,mc.player)).getType()!=HitResult.Type.MISS)continue;
   Vec3 source=eye.add(shooter.getViewVector(partial).scale(.35)).add(0,-.1,0);float size=(float)Math.min(.65,.09+distance*.0035);int alpha=(int)(210*Math.min(1,(alignment-.978)/.022));if(alpha<=0)continue;
   PoseStack pose=e.getPoseStack();pose.pushPose();pose.translate(source.x-camera.x,source.y-camera.y,source.z-camera.z);pose.mulPose(e.getCamera().rotation());var matrix=pose.last().pose();
   consumer.addVertex(matrix,-size,-size,0).setColor(255,255,255,alpha).setUv(0,1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(pose.last(),0,0,1);
   consumer.addVertex(matrix,size,-size,0).setColor(255,255,255,alpha).setUv(1,1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(pose.last(),0,0,1);
   consumer.addVertex(matrix,size,size,0).setColor(255,255,255,alpha).setUv(1,0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(pose.last(),0,0,1);
   consumer.addVertex(matrix,-size,size,0).setColor(255,255,255,alpha).setUv(0,0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(0xF000F0).setNormal(pose.last(),0,0,1);
   pose.popPose();drew=true;
  }
  if(drew)buffers.endBatch(type);
 }
}
