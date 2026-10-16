package dev.zomboid.hordes;
public final class ScrapBridge2 {
 static boolean reported;
 static Object invoke(String method,Object a,Object b,Object c){try{return Class.forName("dev.zomboid.turretpower.ScrapLoot").getMethod(method,Object.class,Object.class,Object.class).invoke(null,a,b,c);}catch(ClassNotFoundException absent){return a;}catch(ReflectiveOperationException|LinkageError failure){if(!reported){reported=true;System.err.println("[Electronic scrap] Optional loot bridge unavailable: "+failure);}return a;}}
 public static void body(Object corpse,Object level,Object event){invoke("corpse",corpse,level,event);}
 public static Object container(Object list,Object block,Object params){return invoke("container",list,block,params);}
}
