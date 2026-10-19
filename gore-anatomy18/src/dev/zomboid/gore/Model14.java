package dev.zomboid.gore;
import java.util.*;
/** Distinguish anatomical roots from decorative descendants in EMF hierarchies. */
public final class Model14 {
 static final Map<Object,Boolean> ROOTS=new WeakHashMap<>(),BOUND=new WeakHashMap<>();static final ThreadLocal<Object> PART=new ThreadLocal<>();
 public static void bind(Object model){if(BOUND.putIfAbsent(model,true)!=null)return;for(String key:new String[]{"head","body","leftArm","rightArm","leftLeg","rightLeg"})try{Object p=R.get(model,key);if(p!=null)ROOTS.put(p,true);}catch(RuntimeException optional){}}
 public static void part(Object part){PART.set(part);}public static void end(){PART.remove();}
 static boolean skin(){Object p=PART.get();if(p==null)return true;try{Object cubes=R.get(p,"cubes");return cubes instanceof List<?> l&&!l.isEmpty();}catch(RuntimeException missing){return false;}}
 static boolean internal(){Object p=PART.get();return p==null||ROOTS.containsKey(p);}
 public static void children(Object part,Object pose,Object vertex,int light,int overlay,int color){Object children;try{children=R.get(part,"children");}catch(RuntimeException absent){return;}if(!(children instanceof Map<?,?>map)||map.isEmpty())return;R.call(pose,"pushPose");try{R.call(part,"translateAndRotate",pose);for(Object child:map.values())R.call(child,"render",pose,vertex,light,overlay,color);}finally{R.call(pose,"popPose");}}
}
