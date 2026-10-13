package dev.zomboid.wardrobebudget;
import java.lang.reflect.*;import java.util.*;
public final class Lookup {
 private static final ClassValue<Map<String,Method>> M=new ClassValue<>(){protected Map<String,Method> computeValue(Class<?>c){return new HashMap<>();}};
 private static final ClassValue<Map<String,Field>> F=new ClassValue<>(){protected Map<String,Field> computeValue(Class<?>c){return new HashMap<>();}};
 public static Object call(Object target,String n,Object...a){Class<?> c=target instanceof Class<?>t?t:target.getClass();Method m=M.get(c).computeIfAbsent(n+":"+a.length,k->{for(Class<?>p=c;p!=null;p=p.getSuperclass())for(Method v:p.getDeclaredMethods())if(v.getName().equals(n)&&v.getParameterCount()==a.length){v.setAccessible(true);return v;}throw new IllegalStateException(c+"."+n);});try{return m.invoke(target instanceof Class<?>?null:target,a);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}}
 public static Object get(Object target,String n){Field f=F.get(target.getClass()).computeIfAbsent(n,k->{for(Class<?>c=target.getClass();c!=null;c=c.getSuperclass())try{Field v=c.getDeclaredField(n);v.setAccessible(true);return v;}catch(NoSuchFieldException ignored){}throw new IllegalStateException(n);});try{return f.get(target);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}}
}
