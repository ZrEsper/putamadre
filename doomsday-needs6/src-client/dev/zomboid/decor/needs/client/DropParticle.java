package dev.zomboid.decor.needs.client;
import net.minecraft.client.particle.*;import net.minecraft.client.multiplayer.ClientLevel;import net.minecraft.core.particles.SimpleParticleType;
public final class DropParticle extends TextureSheetParticle{
 public DropParticle(ClientLevel w,double x,double y,double z,double vx,double vy,double vz,SpriteSet sprites,boolean poop){super(w,x,y,z,vx,vy,vz);xd=vx;yd=vy;zd=vz;gravity=.8f;hasPhysics=true;quadSize=poop?.075f:.025f;lifetime=poop?30:22;setSpriteFromAge(sprites);}
 public ParticleRenderType getRenderType(){return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;}
 public record Provider(SpriteSet sprites,boolean poop)implements ParticleProvider<SimpleParticleType>{public Particle createParticle(SimpleParticleType t,ClientLevel w,double x,double y,double z,double vx,double vy,double vz){return new DropParticle(w,x,y,z,vx,vy,vz,sprites,poop);}}
}
