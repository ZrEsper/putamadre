package com.raiiiden.warborn.hybrid;
import java.util.function.LongSupplier;import com.raiiiden.warborn.common.util.*;
/** Immediate local key sound, with one confirmation instead of a second sound after the animation. */
public final class Sound10{
 static LongSupplier clock=System::nanoTime;static Object owner,world,lastSound;static boolean initialized,lastOn;static Boolean pending;static long deadline,requested;
 static Object c(Object o,String n,Object...a){return NvgReflection.call(o,n,a);}static Class<?>t(String n){return NvgReflection.type(n);}static Object mc(){return c(t("net.minecraft.client.Minecraft"),"getInstance");}
 static boolean powered(Object helmet){return !NvgLink2.mode(helmet).isEmpty()&&NvgLink2.down(helmet)&&NvgLink2.energy(helmet)>0;}
 static boolean context(){Object m=mc(),p=NvgReflection.get(m,"player"),w=NvgReflection.get(m,"level");if(p==null||w==null){owner=world=null;initialized=false;pending=null;lastSound=null;return false;}if(p!=owner||w!=world){owner=p;world=w;initialized=true;lastOn=powered(NvgLink2.head(p));pending=null;lastSound=null;}return true;}
 static void play(boolean on){Object m=mc(),manager=c(m,"getSoundManager");if(lastSound!=null)try{c(manager,"stop",lastSound);}catch(IllegalStateException absent){}Object id=c(t("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","fracturepoint",on?"nvg_tactical_on":"nvg_tactical_off"),event=c(t("net.minecraft.sounds.SoundEvent"),"createVariableRangeEvent",id);lastSound=c(t("net.minecraft.client.resources.sounds.SimpleSoundInstance"),"forUI",event,1f,.5f);c(manager,"play",lastSound);}
 public static void request(){if(!context())return;Object h=NvgLink2.head(owner);if(NvgLink2.mode(h).isEmpty())return;boolean on=!NvgLink2.down(h);if(on&&NvgLink2.energy(h)<=0)return;long now=clock.getAsLong();if(pending!=null&&pending==on&&now-requested<200_000_000L)return;pending=on;requested=now;deadline=now+5_000_000_000L;play(on);}
 public static void tick(){if(!context())return;boolean on=powered(NvgLink2.head(owner));long now=clock.getAsLong();if(on==lastOn){if(pending!=null&&now>deadline)pending=null;return;}lastOn=on;if(pending!=null&&pending==on&&now<=deadline){pending=null;return;}pending=null;play(on);}
}
