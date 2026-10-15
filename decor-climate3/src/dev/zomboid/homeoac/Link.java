package dev.zomboid.homeoac;
import java.lang.reflect.*;import java.util.*;
public final class Link {
 static final Map<String,Method> methods=new HashMap<>();static boolean reported;
 static boolean invoke(String name,Object...a){try{Method m=methods.get(name);if(m==null){Class<?>[] sig=new Class<?>[a.length];Arrays.fill(sig,Object.class);m=Class.forName("dev.zomboid.decor.Climate").getMethod(name,sig);methods.put(name,m);}return Boolean.TRUE.equals(m.invoke(null,a));}catch(ClassNotFoundException absent){return false;}catch(ReflectiveOperationException|LinkageError error){if(!reported){reported=true;System.err.println("[Doomsday AC] Temperature bridge failed: "+error);}return false;}}
 public static void body(Object player,Object body){invoke("homeoBody",player,body);}public static void environment(Object player,Object env){invoke("homeoEnvironment",player,env);}public static boolean data(Object player,Object data){return invoke("homeoData",player,data);}
}
