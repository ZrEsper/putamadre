package dev.zomboid.remains.hygiene.client;
import net.minecraft.client.particle.*;import net.minecraft.client.multiplayer.ClientLevel;import net.minecraft.core.particles.SimpleParticleType;
public final class FlyParticle extends TextureSheetParticle{
 private final SpriteSet sprites;
 public FlyParticle(ClientLevel level,double x,double y,double z,double vx,double vy,double vz,SpriteSet sprites){super(level,x,y,z,vx,vy,vz);this.sprites=sprites;quadSize=.055f;lifetime=35+(int)(Math.random()*25);hasPhysics=false;rCol=.55f;gCol=.55f;bCol=.55f;setSpriteFromAge(sprites);}
 public void tick(){xd=Math.sin(age*.85)*.012;yd=Math.cos(age*.65)*.004;zd=Math.cos(age*.75)*.012;super.tick();setSpriteFromAge(sprites);}
 public ParticleRenderType getRenderType(){return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;}
 public record Provider(SpriteSet sprites)implements ParticleProvider<SimpleParticleType>{public Particle createParticle(SimpleParticleType type,ClientLevel level,double x,double y,double z,double vx,double vy,double vz){return new FlyParticle(level,x,y,z,vx,vy,vz,sprites);}}
}
