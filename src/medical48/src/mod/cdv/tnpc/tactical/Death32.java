package mod.cdv.tnpc.tactical;
import java.util.*;
public final class Death32 {
 static final Set<Object> BUSY=Collections.newSetFromMap(new WeakHashMap<>());
 public static synchronized boolean claim(Object npc){if(BUSY.contains(npc)||Social.yes(Social.c(Social.tag(npc),"getBoolean","soc_corpse20")))return false;BUSY.add(npc);return true;}
 public static synchronized void release(Object npc){BUSY.remove(npc);}
}
