package dev.zomboid.remains.hygiene;
import dev.zomboid.remains.delivery.Reflect;import java.util.*;
public final class Hydration{
 static boolean checked;static Class<?> homeo,tan;static boolean warned;
 static Object c(Object o,String n,Object...a){return Reflect.call(o,n,a);}
 static Class<?> optional(String name){try{return Class.forName(name);}catch(ClassNotFoundException e){return null;}}
 public static boolean drain(Object player){if(!checked){checked=true;homeo=optional("homeostatic.common.attachments.WaterData");tan=optional("toughasnails.api.thirst.ThirstHelper");}
  try{if(homeo!=null){Optional<?> data=(Optional<?>)c(homeo,"getData",player);if(data.isPresent()){Object water=data.get();int level=Math.max(0,((Number)c(water,"getWaterLevel")).intValue()-4);float saturation=Math.min(level,((Number)c(water,"getWaterSaturationLevel")).floatValue());c(water,"setWaterLevel",level);c(water,"setWaterSaturationLevel",saturation);Object info=Reflect.make("homeostatic.common.water.WaterInfo",level,saturation,((Number)c(water,"getWaterExhaustionLevel")).floatValue());c(Reflect.get(Reflect.type("homeostatic.platform.Services"),"PLATFORM"),"syncWaterData",player,info);return true;}}
   if(tan!=null&&Boolean.TRUE.equals(c(tan,"isThirstEnabled"))){Object thirst=c(tan,"getThirst",player);int level=Math.max(0,((Number)c(thirst,"getThirst")).intValue()-4);c(thirst,"setThirst",level);c(thirst,"setHydration",Math.min(level,((Number)c(thirst,"getHydration")).floatValue()));return true;}return false;
  }catch(RuntimeException e){if(!warned){warned=true;System.err.println("[Corpse hygiene] Hydration integration failed: "+e.getMessage());}return false;}
 }
}
