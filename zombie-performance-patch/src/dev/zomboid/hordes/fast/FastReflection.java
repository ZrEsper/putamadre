package dev.zomboid.hordes.fast;
import java.lang.reflect.*;import java.util.*;import java.util.concurrent.ConcurrentHashMap;
public final class FastReflection {
 static final Map<String,Class<?>> TYPES=new ConcurrentHashMap<>();
 record Resolved(Method method,Class<?>[] parameters){};
 static final ClassValue<Cache> CACHE=new ClassValue<>(){protected Cache computeValue(Class<?> c){return new Cache(c);}};
 static final class Cache {final Class<?> type;final Map<String,List<Resolved>> methods=new HashMap<>();final Map<String,Field> fields=new ConcurrentHashMap<>();Cache(Class<?> c){type=c;Set<String> seen=new HashSet<>();for(Class<?> k=c;k!=null;k=k.getSuperclass())for(Method m:k.getDeclaredMethods()){String signature=m.getName()+Arrays.toString(m.getParameterTypes());if(seen.add(signature))methods.computeIfAbsent(m.getName(),s->new ArrayList<>()).add(new Resolved(m,m.getParameterTypes()));}for(Method m:c.getMethods()){String signature=m.getName()+Arrays.toString(m.getParameterTypes());if(seen.add(signature))methods.computeIfAbsent(m.getName(),s->new ArrayList<>()).add(new Resolved(m,m.getParameterTypes()));}}}
 public static Class<?> type(String n){return TYPES.computeIfAbsent(n,s->{try{return Class.forName(s);}catch(ClassNotFoundException e){throw new IllegalStateException(s,e);}});}
 static Class<?> boxed(Class<?> c){if(c==int.class)return Integer.class;if(c==double.class)return Double.class;if(c==float.class)return Float.class;if(c==long.class)return Long.class;if(c==boolean.class)return Boolean.class;if(c==byte.class)return Byte.class;if(c==short.class)return Short.class;if(c==char.class)return Character.class;return c;}
 static boolean matches(Class<?>[] p,Object[] a){if(p.length!=a.length)return false;for(int i=0;i<a.length;i++)if(a[i]==null?p[i].isPrimitive():!boxed(p[i]).isInstance(a[i]))return false;return true;}
 static Class<?> cls(Object o){return o instanceof Class<?>?(Class<?>)o:o.getClass();}
 public static Object call(Object o,String n,Object...a){try{for(Resolved r:CACHE.get(cls(o)).methods.getOrDefault(n,List.of()))if(matches(r.parameters,a)){Method m=r.method;if(!m.canAccess(o instanceof Class<?>?null:o))m.setAccessible(true);return m.invoke(o instanceof Class<?>?null:o,a);}throw new NoSuchMethodException(n);}catch(InvocationTargetException e){throw new IllegalStateException(n,e.getCause());}catch(ReflectiveOperationException e){throw new IllegalStateException(n,e);}}
 static Field field(Class<?> c,String n){return CACHE.get(c).fields.computeIfAbsent(n,s->{for(Class<?> k=c;k!=null;k=k.getSuperclass())try{Field f=k.getDeclaredField(s);f.setAccessible(true);return f;}catch(NoSuchFieldException ignored){}throw new IllegalStateException(s);});}
 public static Object get(Object o,String n){try{return field(cls(o),n).get(o instanceof Class<?>?null:o);}catch(IllegalAccessException e){throw new IllegalStateException(n,e);}}
 public static void set(Object o,String n,Object v){try{field(cls(o),n).set(o instanceof Class<?>?null:o,v);}catch(IllegalAccessException e){throw new IllegalStateException(n,e);}}
 public static Object make(String n,Object...a){try{for(Constructor<?> c:type(n).getDeclaredConstructors())if(matches(c.getParameterTypes(),a)){c.setAccessible(true);return c.newInstance(a);}throw new NoSuchMethodException(n);}catch(ReflectiveOperationException e){throw new IllegalStateException(n,e);}}
}
