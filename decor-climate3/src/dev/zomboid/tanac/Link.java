package dev.zomboid.tanac;
import java.lang.reflect.*;import java.util.*;
public final class Link {
 static final Map<String,Method> methods=new HashMap<>();static boolean reported;
 static boolean invoke(String name,Object...a){try{Method m=methods.get(name);if(m==null){Class<?>[] sig=new Class<?>[a.length];Arrays.fill(sig,Object.class);m=Class.forName("dev.zomboid.decor.Climate").getMethod(name,sig);methods.put(name,m);}return Boolean.TRUE.equals(m.invoke(null,a));}catch(ClassNotFoundException absent){return false;}catch(ReflectiveOperationException|LinkageError error){if(!reported){reported=true;System.err.println("[Doomsday AC] Temperature bridge failed: "+error);}return false;}}
 public static boolean player(Object player){return invoke("tan",player);}
}
