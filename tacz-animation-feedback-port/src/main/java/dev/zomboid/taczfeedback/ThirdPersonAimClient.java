package dev.zomboid.taczfeedback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.resources.ResourceLocation;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import java.lang.reflect.Field;
public class ThirdPersonAimClient {
 private static Matrix4f projection;
 private static boolean resolved;
 private static Field isoEnabled;
 private static Vec3 target;
 private static Vec3 desired;
 public static boolean isIso(){
  if(!resolved){resolved=true;try{isoEnabled=Class.forName("com.cleannrooster.dungeons_iso.mod.Mod").getDeclaredField("enabled");isoEnabled.setAccessible(true);}catch(ReflectiveOperationException ignored){}}
  if(isoEnabled!=null)try{return Boolean.TRUE.equals(isoEnabled.get(null));}catch(IllegalAccessException ignored){}return false;
 }
 public static boolean active(){var mc=Minecraft.getInstance();return !isIso()&&mc.player!=null&&mc.level!=null&&mc.screen==null&&mc.options.getCameraType()==CameraType.THIRD_PERSON_BACK&&IGun.mainHandHoldGun(mc.player);}
 public static void projection(Matrix4f matrix){projection=new Matrix4f(matrix);}
 /** Camera picks the desired target; the player's eye ray checks the muzzle path to that target. */
 public static Vec3 pick(){
  var mc=Minecraft.getInstance();if(!active())return null;var player=mc.player;var camera=mc.gameRenderer.getMainCamera();Vec3 start=camera.getPosition();
  Vector3f look=camera.getLookVector();Vec3 direction=new Vec3(look.x,look.y,look.z).normalize();Vec3 end=start.add(direction.scale(160));
  Vec3 hit=mc.level.clip(new ClipContext(start,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player)).getLocation();double distance=start.distanceToSqr(hit);
  var search=new AABB(start,hit).inflate(1);
  for(var entity:mc.level.getEntities(player,search,e->e.isPickable()&&!e.isSpectator()&&!e.isPassengerOfSameVehicle(player))){var result=entity.getBoundingBox().inflate(entity.getPickRadius()).clip(start,hit);if(result.isPresent()&&start.distanceToSqr(result.get())<distance){hit=result.get();distance=start.distanceToSqr(hit);}}
  desired=hit;
  Vec3 eye=player.getEyePosition();Vec3 actual=mc.level.clip(new ClipContext(eye,hit,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player)).getLocation();target=actual;return desired;
 }
 public static float[] prepareShot(){
  if(!active())return null;var mc=Minecraft.getInstance();var p=mc.player;Vec3 hit=pick();if(hit==null)return null;
  Vec3 ray=hit.subtract(p.getEyePosition());if(ray.lengthSqr()<1e-6)return null;float[] restore={p.getXRot(),p.getYRot()};
  p.setYRot((float)(Math.toDegrees(Math.atan2(ray.z,ray.x))-90));p.setXRot((float)-Math.toDegrees(Math.atan2(ray.y,Math.hypot(ray.x,ray.z))));return restore;
 }
 public static void restore(float[] rotations){var p=Minecraft.getInstance().player;if(rotations!=null&&p!=null){p.setXRot(rotations[0]);p.setYRot(rotations[1]);}}
 public static boolean drawEvent(Object raw){
  if(!(raw instanceof RenderGuiLayerEvent.Pre event)||!event.getName().equals(VanillaGuiLayers.CROSSHAIR)||!active())return false;
  var mc=Minecraft.getInstance();if(mc.options.hideGui)return false;event.setCanceled(true);pick();GuiGraphics g=event.getGuiGraphics();int x=g.guiWidth()/2,y=g.guiHeight()/2;
  if(target!=null&&projection!=null){var cam=mc.gameRenderer.getMainCamera();Vec3 rel=target.subtract(cam.getPosition());var v=new Vector3f((float)rel.x,(float)rel.y,(float)rel.z);new Quaternionf(cam.rotation()).conjugate().transform(v);var clip=new Vector4f(v,1);projection.transform(clip);if(clip.w>0.001f){x=(int)((clip.x/clip.w+1)*g.guiWidth()/2);y=(int)((1-clip.y/clip.w)*g.guiHeight()/2);}}
  if(x<1||y<1||x>=g.guiWidth()-1||y>=g.guiHeight()-1)return true;
  int edge=0xAA151515,white=0xFFF4F4F4;
  g.fill(x-8,y-2,x-3,y+2,edge);g.fill(x+3,y-2,x+8,y+2,edge);g.fill(x-2,y-8,x+2,y-3,edge);g.fill(x-2,y+3,x+2,y+8,edge);
  g.fill(x-7,y-1,x-4,y+1,white);g.fill(x+4,y-1,x+7,y+1,white);g.fill(x-1,y-7,x+1,y-4,white);g.fill(x-1,y+4,x+1,y+7,white);g.fill(x,y,x+1,y+1,white);
  dev.zomboid.taczfeedback.mixin.CrosshairAccess.hitMarker(g,mc.getWindow());return true;
 }
}
