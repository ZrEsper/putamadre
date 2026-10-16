package dev.zomboid.remains.hygiene;
import dev.zomboid.remains.delivery.Reflect;
public final class HygieneContent{
 public static Object VOMIT,FLY,SOUND;
 static Object c(Object o,String n,Object...a){return Reflect.call(o,n,a);}static Class<?> t(String n){return Reflect.type(n);}static Object id(String name){return c(t("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","zombie_remains_loot",name);}
 public static void register(Object event){Object key=c(event,"getRegistryKey");Object regs=t("net.minecraft.core.registries.Registries");String type=null;Object entry=null;
  if(key.equals(Reflect.get(regs,"BLOCK"))){Object p=c(t("net.minecraft.world.level.block.state.BlockBehaviour$Properties"),"of");p=c(p,"strength",.05f);p=c(p,"noCollission");p=c(p,"noOcclusion");p=c(p,"sound",Reflect.get(t("net.minecraft.world.level.block.SoundType"),"SLIME_BLOCK"));entry=VOMIT=Reflect.make("net.minecraft.world.level.block.SnowLayerBlock",p);type="vomit";}
  else if(key.equals(Reflect.get(regs,"PARTICLE_TYPE"))){entry=FLY=Reflect.make("net.minecraft.core.particles.SimpleParticleType",false);type="corpse_fly";}
  else if(key.equals(Reflect.get(regs,"SOUND_EVENT"))){entry=SOUND=c(t("net.minecraft.sounds.SoundEvent"),"createVariableRangeEvent",id("vomit"));type="vomit";}
  if(entry!=null){Object value=entry;c(event,"register",key,id(type),(java.util.function.Supplier<Object>)()->value);}
 }
}
