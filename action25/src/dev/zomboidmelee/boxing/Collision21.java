package dev.zomboidmelee.boxing;
/** Ignore normal entities before the optional Gore lookup; no world scans or collision caches. */
public final class Collision21{
 public static boolean down(Object entity){return entity instanceof net.minecraft.world.entity.monster.Zombie&&Stomp18.down(entity);}
 public static boolean cancel(Object a,Object b){return down(a)||down(b);}
}
