package dev.zomboidmelee.boxing;
/** Registered on the client MOD bus. The binding remains remappable in Controls. */
public final class Keys26 {
 static Object slide;
 public static void register(Object event){slide=R.make("net.minecraft.client.KeyMapping","key.zomboid_global_melee.slide",67,"key.categories.movement");R.call(event,"register",slide);}
 static boolean down(){return slide!=null&&R.yes(R.call(slide,"isDown"));}
}
