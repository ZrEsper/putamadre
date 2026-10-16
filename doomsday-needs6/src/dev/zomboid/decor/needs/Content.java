package dev.zomboid.decor.needs;
import dev.zomboid.decor.R;import java.util.*;import java.util.function.Supplier;
public final class Content {
 public static Object PEE,POOP;public static final Map<String,Object> SOUNDS=new HashMap<>();
 public static Object id(String n){return R.call(R.type("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","doomsday_decoration",n);}
 public static void register(Object event){Object key=R.call(event,"getRegistryKey");Class<?> regs=R.type("net.minecraft.core.registries.Registries");
  if(key.equals(R.get(regs,"PARTICLE_TYPE")))for(String n:List.of("pee_drop","feces_drop")){Object v=R.make("net.minecraft.core.particles.SimpleParticleType",false);if(n.equals("pee_drop"))PEE=v;else POOP=v;R.call(event,"register",key,id(n),(Supplier<Object>)()->v);}
  if(key.equals(R.get(regs,"SOUND_EVENT")))for(String n:List.of("needs_urinate","needs_defecate","toilet_flush","wire_cut")){Object v=R.call(R.type("net.minecraft.sounds.SoundEvent"),"createVariableRangeEvent",id(n));SOUNDS.put(n,v);R.call(event,"register",key,id(n),(Supplier<Object>)()->v);}
 }
 public static void sound(Object world,Object pos,String name,float volume){Object s=SOUNDS.get(name);if(s!=null)R.call(world,"playSound",null,pos,s,R.get(R.type("net.minecraft.sounds.SoundSource"),"PLAYERS"),volume,1f);}
}
