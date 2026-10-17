package dev.zomboidmelee.boxing;
import java.util.Optional;
/** Exact ray against the fallen body and its actual directional head, never a cone. */
public final class StompRay24{
 public static Optional<?> point(Object p,Object v){if(!Stomp18.down(v)||R.num(R.call(p,"distanceToSqr",v))>9)return Optional.empty();Object start=R.call(p,"getEyePosition"),end=R.call(start,"add",R.call(R.call(p,"getLookAngle"),"scale",3d));Optional<?> body=(Optional<?>)R.call(R.call(v,"getBoundingBox"),"clip",start,end);Object h=Stomp18.head(v);double x=R.num(R.get(h,"x")),y=R.num(R.get(h,"y")),z=R.num(R.get(h,"z"));Object box=R.make("net.minecraft.world.phys.AABB",x-.4,y-.3,z-.4,x+.4,y+.3,z+.4);Optional<?> head=R.num(R.call(p,"distanceToSqr",h))<=6.25?(Optional<?>)R.call(box,"clip",start,end):Optional.empty();Optional<?> result=body;if(head.isPresent()&&(result.isEmpty()||R.num(R.call(start,"distanceToSqr",head.get()))<R.num(R.call(start,"distanceToSqr",result.get()))))result=head;return result.isPresent()&&Combat18.clearRay(p,result.get())?result:Optional.empty();}
 public static boolean aim(Object p,Object v){return point(p,v).isPresent();}
}
