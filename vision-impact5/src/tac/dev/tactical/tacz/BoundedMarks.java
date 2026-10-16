package dev.tactical.tacz;
import java.util.*;
/** Insertion order means order of the latest physical hit, including repeated coordinates. */
public final class BoundedMarks {
 public static final int LIMIT=200;
 public static Object orderedPut(Map<Object,Object> marks,Object key,Object value)throws Exception{
  Object previous=marks.remove(key);if(previous!=null)TimedMarks.born.remove(previous);
  marks.put(key,value);
  while(marks.size()>LIMIT){Iterator<Object> it=marks.values().iterator();Object oldest=it.next();Reflect.call(Reflect.field(oldest,"particle"),"remove");it.remove();TimedMarks.born.remove(oldest);}
  return previous;
 }
}
