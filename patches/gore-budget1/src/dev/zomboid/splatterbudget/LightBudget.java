package dev.zomboid.splatterbudget;
public final class LightBudget {
 private static java.lang.ref.WeakReference<Object> world=new java.lang.ref.WeakReference<>(null);private static long tick=Long.MIN_VALUE;private static int used;
 public static Object cached(Object cache,Object level,long now,int interval){if(world.get()!=level||tick!=now){world=new java.lang.ref.WeakReference<>(level);tick=now;used=0;}Object values=Lookup.get(cache,"values");long refreshed=((Number)Lookup.get(cache,"refreshedAt")).longValue();if(values!=null&&now-refreshed<Math.max(10,interval))return values;if(values!=null&&used>=32)return values;used++;return null;}
}
