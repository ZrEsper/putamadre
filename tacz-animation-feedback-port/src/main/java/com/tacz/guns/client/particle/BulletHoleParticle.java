package com.tacz.guns.client.particle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.config.client.RenderConfig;
import com.tacz.guns.init.ModBlocks;
import com.tacz.guns.particles.BulletHoleOption;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
public class BulletHoleParticle extends TextureSheetParticle {
 private final Direction direction;private final BlockPos pos;
 public BulletHoleParticle(ClientLevel world,double x,double y,double z,Direction direction,BlockPos pos,String ammoId,String gunId,String gunDisplayId){
  super(world,x,y,z);this.direction=direction;this.pos=pos;this.hasPhysics=false;this.gravity=0;this.alpha=.96f;
  int life=RenderConfig.BULLET_HOLE_PARTICLE_LIFE.get();this.lifetime=life<=1?life:life+world.random.nextInt(Math.max(1,life/2));
  this.quadSize=TimelessAPI.getCommonGunIndex(ResourceLocation.parse(gunId)).map(index->index.getType().equals("sniper")?.135f:index.getType().equals("shotgun")?.12f:.1f).orElse(.1f);
  setSprite(((net.minecraft.client.renderer.texture.TextureAtlas)Minecraft.getInstance().getTextureManager().getTexture(ResourceLocation.fromNamespaceAndPath("minecraft","textures/atlas/particles.png"))).getSprite(ResourceLocation.fromNamespaceAndPath("tacz","bullet_hole")));
  if(world.getBlockState(pos).is(ModBlocks.TARGET.get())||shouldRemove())remove();
 }
 public void tick(){super.tick();if(shouldRemove())remove();}
 private boolean shouldRemove(){var state=level.getBlockState(pos);if(state.isAir())return true;var shape=state.getCollisionShape(level,pos);if(shape.isEmpty())return true;return !shape.bounds().move(pos).intersects(x-.12,y-.12,z-.12,x+.12,y+.12,z+.12);}
 public void render(VertexConsumer buffer,Camera camera,float partial){
  Vec3 view=camera.getPosition();float px=(float)(Mth.lerp(partial,xo,x)-view.x),py=(float)(Mth.lerp(partial,yo,y)-view.y),pz=(float)(Mth.lerp(partial,zo,z)-view.z);
  float scale=getQuadSize(partial);var rotation=direction.getRotation();var points=new Vector3f[]{new Vector3f(-1,.045f,-1),new Vector3f(-1,.045f,1),new Vector3f(1,.045f,1),new Vector3f(1,.045f,-1)};
  for(var point:points)point.rotate(rotation).mul(scale).add(px,py,pz);
  int light=LevelRenderer.getLightColor(level,pos.relative(direction));if(age<5)light=LightTexture.FULL_BRIGHT;
  double threshold=RenderConfig.BULLET_HOLE_PARTICLE_FADE_THRESHOLD.get()*lifetime;float fade=1-(float)(Math.max(age-threshold,0)/Math.max(1,lifetime-threshold));float a=alpha*Math.max(0,fade);
  float[][] uv={{getU1(),getV1()},{getU1(),getV0()},{getU0(),getV0()},{getU0(),getV1()}};
  for(int i=0;i<4;i++)buffer.addVertex(points[i].x,points[i].y,points[i].z).setUv(uv[i][0],uv[i][1]).setColor(1,1,1,a).setLight(light);
 }
 public ParticleRenderType getRenderType(){return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;}
 public static class Provider implements ParticleProvider<BulletHoleOption> {
  public BulletHoleParticle createParticle(BulletHoleOption o,ClientLevel world,double x,double y,double z,double vx,double vy,double vz){return new BulletHoleParticle(world,x,y,z,o.getDirection(),o.getPos(),o.getAmmoId(),o.getGunId(),o.getGunDisplayId());}
 }
}
