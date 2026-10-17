package dev.zomboidmelee.boxing;
import java.util.*;
/** Client and integrated-server copies compare equal by ID, so they must use separate buckets. */
public final class SideMap25<V> extends AbstractMap<Object,V>{
 static final List<SideMap25<?>> ALL=new java.util.concurrent.CopyOnWriteArrayList<>();final Map<Object,V> server=Collections.synchronizedMap(new WeakHashMap<>()),client=Collections.synchronizedMap(new WeakHashMap<>());
 public SideMap25(){ALL.add(this);}Map<Object,V>bucket(Object p){return Move25.server(p)?server:client;}
 public V get(Object p){return p==null?null:bucket(p).get(p);}public V put(Object p,V v){return bucket(p).put(p,v);}public V remove(Object p){return p==null?null:bucket(p).remove(p);}public boolean containsKey(Object p){return p!=null&&bucket(p).containsKey(p);}public Set<Entry<Object,V>>entrySet(){throw new UnsupportedOperationException("Choose a logical side");}
 public static void clearClient(){for(SideMap25<?>m:ALL)m.client.clear();}
}
