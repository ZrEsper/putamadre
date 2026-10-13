package dev.zomboid.sounds;
import java.util.*;
import dev.zomboid.hordes.fast.FastReflection;
import dev.zomboid.reactive.Reactive;
public final class ActionSounds {
 static final Map<Object,Map<Long,Long>> barriers=new WeakHashMap<>();
 static final Map<Object,Long> bites=new WeakHashMap<>();
 static final Map<String,Object> sounds=new HashMap<>();
 static Object call(Object o,String name,Object...args){return FastReflection.call(o,name,args);}
 static boolean yes(Object o){return Boolean.TRUE.equals(o);}
 static long number(Object o){return ((Number)o).longValue();}
 static boolean permit(Map<Object,Long> cache,Object key,long now,int duration){Long until=cache.get(key);if(until!=null&&until>now&&until-now<=duration)return false;cache.put(key,now+duration);return true;}
 static Object sound(String name){return sounds.computeIfAbsent(name,k->call(FastReflection.type("net.minecraft.sounds.SoundEvent"),"createVariableRangeEvent",call(FastReflection.type("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","zomboid_hordes",k)));}
 static void play(Object level,Object pos,String name,float volume){call(level,"playSound",null,pos,sound(name),FastReflection.get(FastReflection.type("net.minecraft.sounds.SoundSource"),"HOSTILE"),volume,1.0f);}
 public static void barrier(Object mob,Object pos,boolean glass){if(glass)return;barrierSound(mob,pos,false);}
 public static boolean window(boolean destroyed,Object mob,Object pos){if(destroyed)barrierSound(mob,pos,true);return destroyed;}
 static void barrierSound(Object mob,Object pos,boolean glass){if(mob==null||pos==null)return;Object level=call(mob,"level");if(yes(call(level,"isClientSide")))return;long now=number(call(level,"getGameTime")),key=number(call(pos,"asLong"));Map<Long,Long> recent=barriers.computeIfAbsent(level,k->new HashMap<>());recent.entrySet().removeIf(e->e.getValue()<=now||e.getValue()-now>100);Long until=recent.get(key);if(until!=null&&until>now)return;if(recent.size()>=256)return;recent.put(key,now+(glass?100:20));play(level,pos,glass?"window_break":"door_break_start",0.9f);}
 public static void damaged(Object event){Object victim=call(event,"getEntity"),level=call(victim,"level");if(yes(call(level,"isClientSide"))||!FastReflection.type("net.minecraft.world.entity.player.Player").isInstance(victim)||((Number)call(event,"getNewDamage")).floatValue()<=0)return;Object source=call(event,"getSource"),attacker=call(source,"getEntity");if(attacker==null||attacker!=call(source,"getDirectEntity"))return;if(!FastReflection.type("net.minecraft.world.entity.monster.Zombie").isInstance(attacker)&&!Reactive.controlled(attacker))return;long now=number(call(level,"getGameTime"));if(permit(bites,victim,now,162))play(level,call(victim,"blockPosition"),"bite_cloth",0.85f);}
}
