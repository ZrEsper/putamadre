package dev.zomboidmelee.boxing;
import java.util.*;
/** Only collider occlusion counts: grass cannot hide a prone zombie from the exact aim ray. */
public final class StompTarget21{
 public static Object target(Object m){Object old=BoxingClient16.target(m),p=R.get(m,"player");if(p==null)return old;if(old!=null&&"ENTITY".equals(R.call(old,"getType").toString())){return old;}
 Object start=R.call(p,"getEyePosition"),end=R.call(start,"add",R.call(R.call(p,"getLookAngle"),"scale",3d)),area=R.call(R.call(p,"getBoundingBox"),"inflate",3d);Object level=R.get(m,"level");if(level==null)return old;Object best=null,point=null;double distance=Double.MAX_VALUE;
 for(Object e:(List<?>)R.call(level,"getEntitiesOfClass",R.type("net.minecraft.world.entity.monster.Zombie"),area)){if(!R.yes(R.call(e,"isAlive"))||!Stomp18.down(e))continue;Optional<?> q=(Optional<?>)R.call(R.call(e,"getBoundingBox"),"clip",start,end);if(q.isEmpty()||!Combat18.clearRay(p,q.get()))continue;double d=R.num(R.call(start,"distanceToSqr",q.get()));if(d<distance){distance=d;best=e;point=q.get();}}
 return best==null?old:R.make("net.minecraft.world.phys.EntityHitResult",best,point);
 }
}
