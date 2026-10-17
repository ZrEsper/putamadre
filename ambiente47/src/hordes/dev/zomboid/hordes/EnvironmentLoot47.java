package dev.zomboid.hordes;
import java.util.*;
public final class EnvironmentLoot47 {
 static Object c(Object o,String n,Object...a){return ZoneReflection.call(o,n,a);}static Object tag(Object o){return c(o,"getPersistentData");}
 public static List<Object> halves(Object o){if(o==null)return List.of();if(o.getClass().getSimpleName().equals("CompoundContainer")){List<Object> a=new ArrayList<>();a.addAll(halves(ZoneReflection.get(o,"container1")));a.addAll(halves(ZoneReflection.get(o,"container2")));return a;}return List.of(o);}
 public static boolean supported(Object o){for(Object half:halves(o))for(Class<?>type=half.getClass();type!=null;type=type.getSuperclass()){String n=type.getSimpleName().toLowerCase(Locale.ROOT);if(n.contains("chest")||n.contains("barrel"))return true;}return false;}
 public static boolean owned(Object o){for(Object h:halves(o))if(Boolean.TRUE.equals(c(tag(h),"getBoolean","QuietlyPlayerPlaced")))return true;return false;}
 public static void mark(Object o){for(Object h:halves(o)){c(tag(h),"putBoolean","EnvironmentGenerated47",true);c(h,"setChanged");}}
 public static boolean generated(Object o){for(Object h:halves(o))if(Boolean.TRUE.equals(c(tag(h),"getBoolean","EnvironmentGenerated47")))return true;return false;}
 /** Called by Quietly only once after a successful search. Existing items are never replaced. */
 public static boolean searched(Object block,Object level){if(!WorldTuning.flag("cofres_loot_todos"))return false;if(block==null||!supported(block))return false;if(owned(block)||generated(block))return true;int size=((Number)c(block,"getContainerSize")).intValue();for(int i=0;i<size;i++)if(!CorpseBalance35.empty(c(block,"getItem",i)))return true;
  List<Object> result=SimpleLoot.generate(WorldTuning.snapshot(),"cofres",c(LootProfiles.class,"params",level,c(c(block,"getBlockPos"),"getCenter")),level,2);List<Integer> slots=new ArrayList<>();for(int i=0;i<size;i++)slots.add(i);Collections.shuffle(slots,new Random(((Number)c(ZoneReflection.get(level,"random"),"nextLong")).longValue()));for(int i=0;i<Math.min(size,result.size());i++)c(block,"setItem",slots.get(i),result.get(i));mark(block);return true;
 }
}
