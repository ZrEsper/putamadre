package dev.survivorcreator.hudscale;
import java.lang.reflect.*;import java.util.*;
public final class ScreenScale {
 private static final Map<Object,Double> ACTIVE=new WeakHashMap<>();
 private static Object call(Object target,String name,Object...args){try{Class<?> c=target instanceof Class<?> k?k:target.getClass();for(Method m:c.getMethods())if(m.getName().equals(name)&&m.getParameterCount()==args.length)return m.invoke(target instanceof Class<?>?null:target,args);throw new NoSuchMethodException(name);}catch(ReflectiveOperationException e){throw new IllegalStateException(name,e);}}
 private static Object mc(){try{return call(Class.forName("net.minecraft.client.Minecraft"),"getInstance");}catch(ClassNotFoundException e){throw new IllegalStateException(e);}}
 private static void field(Object target,String name,Object value){try{Field f=target.getClass().getField(name);f.set(target,value);}catch(ReflectiveOperationException e){throw new IllegalStateException(name,e);}}
 public static void enter(Object screen){Object win=call(mc(),"getWindow");ACTIVE.putIfAbsent(screen,((Number)call(win,"getGuiScale")).doubleValue());double target=((Number)call(win,"calculateScale",3,false)).doubleValue();call(win,"setGuiScale",target);field(screen,"width",((Number)call(win,"getGuiScaledWidth")).intValue());field(screen,"height",((Number)call(win,"getGuiScaledHeight")).intValue());}
 public static void exit(Object screen){Double previous=ACTIVE.remove(screen);if(previous==null||!ACTIVE.isEmpty())return;Object client=mc(),win=call(client,"getWindow");try{Object options=client.getClass().getField("options").get(client);int selected=((Number)call(call(options,"guiScale"),"get")).intValue();previous=((Number)call(win,"calculateScale",selected,false)).doubleValue();}catch(ReflectiveOperationException|RuntimeException ignored){previous=((Number)call(win,"calculateScale",previous.intValue(),false)).doubleValue();}call(win,"setGuiScale",previous);}
}
