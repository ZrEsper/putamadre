package dev.zomboid.gibbudget;
import java.util.*;import java.lang.ref.WeakReference;
public final class GibBudget {
 private record Entry(WeakReference<Object> entity,long born){}
 private static WeakReference<Object> world=new WeakReference<>(null);private static final ArrayDeque<Entry> order=new ArrayDeque<>();private static final Map<Object,Entry> active=new WeakHashMap<>();
 public static boolean keep(Object entity){return keep(entity,System.nanoTime());}
 public static boolean keep(Object entity,long now){Object w=Lookup.call(entity,"level");if(w!=world.get()){world=new WeakReference<>(w);order.clear();active.clear();}if(Boolean.TRUE.equals(Lookup.call(entity,"isRemoved")))return false;
  Entry e=active.get(entity);if(e==null){while(!order.isEmpty()){Object first=order.peek().entity.get();if(first!=null&&!Boolean.TRUE.equals(Lookup.call(first,"isRemoved")))break;order.remove();if(first!=null)active.remove(first);}while(order.size()>=128){Object old=order.remove().entity.get();if(old!=null){Lookup.call(old,"discard");active.remove(old);}}e=new Entry(new WeakReference<>(entity),now);order.add(e);active.put(entity,e);}
  if(now-e.born>=10_000_000_000L){Lookup.call(entity,"discard");active.remove(entity);return false;}return true;
 }
}
