package dev.zomboid.hordes;
import java.util.WeakHashMap;
public final class PathThrottle {
 private static final WeakHashMap<Object,State> PATHS=new WeakHashMap<>();private static final WeakHashMap<Object,Budget> WORLDS=new WeakHashMap<>();
 public static Object call(Object nav,String method,Object[] args,Object mob){if(!method.equals("moveTo")||args.length!=4)return ChunkReflection.call(nav,method,args);
 Object level=ChunkReflection.call(mob,"level");long now=((Number)ChunkReflection.call(level,"getGameTime")).longValue();double x=((Number)args[0]).doubleValue(),y=((Number)args[1]).doubleValue(),z=((Number)args[2]).doubleValue();State s=PATHS.get(nav);
 if(s!=null){double dx=x-s.x,dy=y-s.y,dz=z-s.z;boolean moved=dx*dx+dy*dy+dz*dz>=16;boolean done=s.success&&Boolean.TRUE.equals(ChunkReflection.call(nav,"isDone"));
 // Finished paths must not bypass the minimum retry interval; failures back off too.
 if(now>=s.last&&now-s.last<(s.success?20:40))return s.success;
 if(!moved&&!done&&now<s.next)return s.success;
 }
 Budget b=WORLDS.computeIfAbsent(level,k->new Budget());if(b.tick!=now){b.tick=now;b.spent=0;}if(b.spent>=4)return s!=null&&s.success;b.spent++;
 Object result=ChunkReflection.call(nav,method,args);if(s==null)s=new State();s.x=x;s.y=y;s.z=z;s.last=now;s.success=Boolean.TRUE.equals(result);s.next=now+(s.success?80:40)+Math.floorMod(System.identityHashCode(nav),20);PATHS.put(nav,s);return result;
 }
 static final class State{double x,y,z;long last,next;boolean success;}static final class Budget{long tick=Long.MIN_VALUE;int spent;}
}
