package dev.zomboid.remains;
import java.util.*;
public final class CorpseBridge35 {
 static boolean invoke(String name,Class<?>[]types,Object...args){try{Class.forName("dev.zomboid.hordes.CorpseBalance35").getMethod(name,types).invoke(null,args);return true;}catch(ClassNotFoundException e){return false;}catch(ReflectiveOperationException e){throw new IllegalStateException("CorpseBalance35 "+name,e);}}
 public static void capture(Object event){Object entity=dev.zomboid.remains.delivery.Reflect.call(event,"getEntity");invoke("capture",new Class<?>[]{Object.class},entity);}
 public static void clear(Collection<?>drops,Object event){Object entity=dev.zomboid.remains.delivery.Reflect.call(event,"getEntity");invoke("captureDrops",new Class<?>[]{Object.class,Collection.class},entity,drops);drops.clear();}
 public static void scatter(Object corpse,Object level){invoke("scatter",new Class<?>[]{Object.class,Object.class},corpse,level);}
 public static void body(Object corpse,Object level,Object event){if(!invoke("body",new Class<?>[]{Object.class,Object.class,Object.class},corpse,level,event))ProfileCompat.corpse(corpse,level,event);}
}
