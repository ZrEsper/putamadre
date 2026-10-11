package dev.zomboid.reactive;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public final class R {
 private static final Map<String,Method> METHODS=new ConcurrentHashMap<>();
 public static Class<?> type(String n){try{return Class.forName(n);}catch(Exception e){throw new IllegalStateException(n,e);}}
 public static Object call(Object o,String n,Object... a){try{
  Class<?> c=o instanceof Class<?>?(Class<?>)o:o.getClass();
  String key=c.getName()+"#"+n+Arrays.toString(Arrays.stream(a).map(x->x==null?null:x.getClass()).toArray());
  Method m=METHODS.get(key);
  if(m==null){List<Method> all=new ArrayList<>(Arrays.asList(c.getMethods()));for(Class<?> k=c;k!=null;k=k.getSuperclass())all.addAll(Arrays.asList(k.getDeclaredMethods()));
   outer:for(Method p:all){if(!p.getName().equals(n)||p.getParameterCount()!=a.length)continue;Class<?>[] ts=p.getParameterTypes();for(int i=0;i<a.length;i++){Class<?> t=ts[i];if(t.isPrimitive())t=box(t);if(a[i]==null?ts[i].isPrimitive():!t.isInstance(a[i]))continue outer;}m=p;m.setAccessible(true);METHODS.put(key,m);break;}
   if(m==null)throw new NoSuchMethodException(key);
  }return m.invoke(o instanceof Class<?>?null:o,a);
 }catch(InvocationTargetException e){throw new IllegalStateException(n,e.getCause());}catch(Exception e){throw new IllegalStateException(n,e);}}
 private static Class<?> box(Class<?> c){if(c==int.class)return Integer.class;if(c==long.class)return Long.class;if(c==float.class)return Float.class;if(c==double.class)return Double.class;if(c==boolean.class)return Boolean.class;return c;}
 public static Object get(Object o,String n){try{Class<?> c=o instanceof Class<?>?(Class<?>)o:o.getClass();for(;c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(n);f.setAccessible(true);return f.get(o instanceof Class<?>?null:o);}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(n);}catch(Exception e){throw new IllegalStateException(n,e);}}
 public static Object make(String n,Object...a){try{for(Constructor<?> c:type(n).getDeclaredConstructors()){if(c.getParameterCount()!=a.length)continue;Class<?>[]ts=c.getParameterTypes();boolean ok=true;for(int i=0;i<a.length;i++)if(a[i]==null?ts[i].isPrimitive():!box(ts[i]).isInstance(a[i]))ok=false;if(ok){c.setAccessible(true);return c.newInstance(a);}}throw new NoSuchMethodException(n);}catch(Exception e){throw new IllegalStateException(n,e);}}
 public static void set(Object o,String n,Object value){try{for(Class<?> c=o.getClass();c!=null;c=c.getSuperclass())try{Field f=c.getDeclaredField(n);f.setAccessible(true);f.set(o,value);return;}catch(NoSuchFieldException ignored){}throw new NoSuchFieldException(n);}catch(Exception e){throw new IllegalStateException(n,e);}}
 public static double num(Object o){return ((Number)o).doubleValue();}public static long lng(Object o){return ((Number)o).longValue();}public static boolean yes(Object o){return Boolean.TRUE.equals(o);}public static double coord(Object o,String a){return num(call(o,"get"+a));}
 public static void warn(String msg,Throwable t){System.err.println("[zomboid-clans] "+msg+": "+t);t.printStackTrace(System.err);}
}
