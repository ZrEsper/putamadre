package dev.zomboid.gore;
import java.util.*;
/** Pellets from one short burst cannot skip the visible tissue layers. Health damage is unchanged. */
public final class Severity23 {
 static final Map<Object,Window[]> WINDOWS=Collections.synchronizedMap(new WeakHashMap<>());
 static final class Window{long tick;int base;Window(long t,int b){tick=t;base=b;}}
 public static int limit(Object target,int region,int previous,int requested,long tick){synchronized(WINDOWS){Window[] ws=WINDOWS.computeIfAbsent(target,k->new Window[8]);Window w=ws[region];if(w==null||tick-w.tick>=4||tick<w.tick)ws[region]=w=new Window(tick,previous);return Math.max(previous,Math.min(requested,Math.min(7,w.base+2)));}}
}
