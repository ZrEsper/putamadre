package cn.kafei.interact;
import java.lang.reflect.*;
public final class R{
 public static Class<?> type(String n){try{return Class.forName(n);}catch(Exception e){throw new IllegalStateException(n,e);}}
 static boolean match(Class<?> p,Object a){if(a==null)return !p.isPrimitive();return p.isInstance(a)||p==int.class&&a instanceof Integer||p==double.class&&a instanceof Double||p==float.class&&a instanceof Float||p==boolean.class&&a instanceof Boolean||p==long.class&&a instanceof Long;}
 public static Object call(Object o,String name,Object...a){Class<?> c=o instanceof Class<?> t?t:o.getClass();for(Method m:c.getMethods()){Class<?>[] p=m.getParameterTypes();if(!m.getName().equals(name)||p.length!=a.length)continue;boolean ok=true;for(int i=0;i<p.length;i++)ok&=match(p[i],a[i]);if(ok)try{return m.invoke(o instanceof Class<?>?null:o,a);}catch(Exception e){throw new IllegalStateException(c+"."+name,e);}}throw new IllegalStateException("Missing method "+c+"."+name);}
 public static Object make(String name,Object...a){for(Constructor<?> c:type(name).getConstructors()){Class<?>[] p=c.getParameterTypes();if(p.length!=a.length)continue;boolean ok=true;for(int i=0;i<p.length;i++)ok&=match(p[i],a[i]);if(ok)try{return c.newInstance(a);}catch(Exception e){throw new IllegalStateException(e);}}throw new IllegalStateException("Missing constructor "+name);}
 public static Object field(Object o,String n){try{return (o instanceof Class<?> c?c:o.getClass()).getField(n).get(o instanceof Class<?>?null:o);}catch(Exception e){throw new IllegalStateException(n,e);}}
 public static Object mc(){return call(type("net.minecraft.client.Minecraft"),"getInstance");}
}
