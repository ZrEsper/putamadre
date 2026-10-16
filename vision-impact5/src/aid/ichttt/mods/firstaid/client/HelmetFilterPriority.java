package ichttt.mods.firstaid.client;
import java.lang.reflect.Method;
/** Optional Fracture Point bridge: reserve the post-effect slot for a lowered, powered visor. */
public final class HelmetFilterPriority {
 private static boolean resolved;private static Method owns;
 public static boolean defer(){
  if(!resolved){resolved=true;try{owns=Class.forName("com.raiiiden.warborn.client.util.NvgClient2").getMethod("ownsPostEffect");}catch(ClassNotFoundException|NoSuchMethodException ignored){}}
  if(owns==null)return false;
  try{return Boolean.TRUE.equals(owns.invoke(null));}catch(ReflectiveOperationException e){throw new IllegalStateException("Cannot coordinate helmet and medical filters",e);}
 }
}
