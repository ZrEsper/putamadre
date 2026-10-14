package dev.survivorcreator.hudscale;
import java.lang.reflect.*;import java.util.*;
public final class ScreenScale {
 private static final Map<Object,Double> ACTIVE=new WeakHashMap<>();
 private static Object call(Object target,String name,Object...args){try{Class<?> c=target instanceof Class<?> k?k:target.getClass();for(Method m:c.getMethods())if(m.getName().equals(name)&&m.getParameterCount()==args.length)return m.invoke(target instanceof Class<?>?null:target,args);throw new NoSuchMethodException(name);}catch(ReflectiveOperationException e){throw new IllegalStateException(name,e);}}
 private static Object mc(){try{return call(Class.forName("net.minecraft.client.Minecraft"),"getInstance");}catch(ClassNotFoundException e){throw new IllegalStateException(e);}}
 private static void field(Object target,String name,Object value){try{Field f=target.getClass().getField(name);f.set(target,value);}catch(ReflectiveOperationException e){throw new IllegalStateException(name,e);}}
 public static double input(double coordinate){Object win=call(mc(),"getWindow");double current=((Number)call(win,"getGuiScale")).doubleValue();double target=((Number)call(win,"calculateScale",3,false)).doubleValue();return coordinate*current/target;}
 public static void enter(Object screen){Object win=call(mc(),"getWindow");double target=((Number)call(win,"calculateScale",3,false)).doubleValue();field(screen,"width",(int)Math.ceil(((Number)call(win,"getWidth")).doubleValue()/target));field(screen,"height",(int)Math.ceil(((Number)call(win,"getHeight")).doubleValue()/target));}
 public static void scissor(Object gui,int x,int y,int right,int bottom){double factor=1/input(1);call(gui,"enableScissor",(int)Math.floor(x*factor),(int)Math.floor(y*factor),(int)Math.ceil(right*factor),(int)Math.ceil(bottom*factor));}
 public static void exit(Object screen){}
}
