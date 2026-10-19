package dev.zomboid.gore;
import java.util.*;
public final class Body48 {
 static final Map<Object,Object> OWNERS=Collections.synchronizedMap(new WeakHashMap<>());
 public static boolean duplicate(Object corpse,Object source){Object old=OWNERS.get(source);if(old==corpse)return true;if(old!=null&&!R.yes(R.call(old,"isRemoved")))return true;return false;}
 public static void attached(Object corpse,Object source){OWNERS.put(source,corpse);int sourceId=((Number)R.call(source,"getId")).intValue();if(sourceId!=((Number)R.call(corpse,"getId")).intValue())R.call(R.type(Linked45.MANAGER),"remove",sourceId);}
 public static void client(Object rag){String s=(String)R.get(rag,"medical45");if(s==null||s.isEmpty())return;int source;try{source=Math.abs(Integer.parseInt(s.split(",",2)[0]));}catch(NumberFormatException e){return;}int current=((Number)R.call(rag,"getOriginalEntityId")).intValue();if(source==current)return;R.call(R.type(Linked45.CLIENT),"requestRemoveRagdoll",source);Object world=R.get(R.call(R.type("net.minecraft.client.Minecraft"),"getInstance"),"level");if(world!=null){Linked45.DEAD_IDS.computeIfAbsent(world,w->new HashSet<>()).add(source);R.call(R.type(Linked45.CLIENT),"setEntityHidden",source,true);}}
}
