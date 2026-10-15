package dev.tactical.tacz;
import java.util.*;
/** One deadline per original mark, measured in client maintenance ticks. */
public final class TimedMarks {
 public static final int LIFE=3600;
 static long clock;
 static final Map<Object,Long> born=new WeakHashMap<>();
 public static void stamp(Object mark){born.put(mark,clock);}
 public static void configure(Object particle)throws Exception{ShotAlignment.set(particle,"lifetime",LIFE);ShotAlignment.set(particle,"alpha",1f);}
 public static void tick()throws Exception{
  clock++;
  Map<?,?> marks=(Map<?,?>)Reflect.field(Reflect.type("dev.tactical.tacz.ImpactMarks"),"marks");
  if(marks.isEmpty()){born.clear();return;}
  Iterator<?> it=marks.values().iterator();
  while(it.hasNext()){
   Object mark=it.next();Long start=born.get(mark);if(start==null){born.put(mark,clock);continue;}
   if(clock-start>=LIFE){Reflect.call(Reflect.field(mark,"particle"),"remove");it.remove();born.remove(mark);}
  }
 }
}
