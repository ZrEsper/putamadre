package fixture;
import dev.zomboid.hordes.ZoneReflection;
import java.net.*;import java.nio.file.*;import java.lang.reflect.*;
public class CorpseAccessChecks {
 static final class HiddenCorpse {public String profession31="";public boolean lootOwned15,lootFinal15;}
 public static void main(String[] args)throws Exception{
  HiddenCorpse corpse=new HiddenCorpse();
  try(URLClassLoader old=new URLClassLoader(new URL[]{Path.of(args[0]).toUri().toURL()},ClassLoader.getPlatformClassLoader())){
   Method setter=old.loadClass("dev.zomboid.hordes.ZoneReflection").getMethod("set",Object.class,String.class,Object.class);
   try{setter.invoke(null,corpse,"profession31","police_officer");throw new AssertionError("Original must reproduce uploaded IllegalAccessException");}
   catch(InvocationTargetException expected){Throwable cause=expected;while(cause.getCause()!=null)cause=cause.getCause();if(!(cause instanceof IllegalAccessException))throw expected;}
  }
  ZoneReflection.set(corpse,"profession31","police_officer");ZoneReflection.set(corpse,"lootOwned15",true);ZoneReflection.set(corpse,"lootFinal15",true);
  if(!corpse.profession31.equals("police_officer")||!corpse.lootOwned15||!corpse.lootFinal15)throw new AssertionError("Fields not saved");
  System.out.println("NONPUBLIC_CORPSE_ACCESS_CHECKS=4 ORIGINAL_CRASH_REPRODUCED=true");
 }
}
