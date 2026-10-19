package dev.zomboid.gore;
import java.util.*;
/** Distinguish anatomical roots from decorative descendants in EMF hierarchies. */
public final class Model14 {
 static final Map<Object,Boolean> ROOTS=new WeakHashMap<>(),OVERLAYS=new WeakHashMap<>(),BOUND=new WeakHashMap<>();static final ThreadLocal<Object> PART=new ThreadLocal<>();
 static final Map<Object,Long> REFRESH=new WeakHashMap<>();
 public static void bind(Object model){long now=System.nanoTime();Long last=REFRESH.get(model);if(last!=null&&now-last<500_000_000L)return;REFRESH.put(model,now);Skin12.MODELS.remove(model);for(String key:new String[]{"head","body","leftArm","rightArm","leftLeg","rightLeg","hat","jacket","leftSleeve","rightSleeve","leftPants","rightPants"})try{Object p=R.get(model,key);if(p!=null){boolean overlay=key.equals("hat")||key.equals("jacket")||key.endsWith("Sleeve")||key.endsWith("Pants");ROOTS.put(p,true);if(overlay)OVERLAYS.put(p,true);String anatomical=key.equals("hat")?"head":key.equals("jacket")?"body":key.replace("Sleeve","Arm").replace("Pants","Leg");Object owner=Owner20.resolve(p,anatomical);if(owner!=null){ROOTS.put(owner,true);if(overlay)OVERLAYS.put(owner,true);}}}catch(RuntimeException optional){}}
 public static void part(Object part){PART.set(part);}public static void end(){PART.remove();}
 static boolean skin(){Object p=PART.get();if(p==null)return true;try{Object cubes=R.get(p,"cubes");return cubes instanceof List<?> l&&!l.isEmpty();}catch(RuntimeException missing){return false;}}
 static boolean internal(){Object p=PART.get();return p==null||ROOTS.containsKey(p)&&!OVERLAYS.containsKey(p);}
 public static void children(Object part,Object pose,Object vertex,int light,int overlay,int color){Object children;try{children=R.get(part,"children");}catch(RuntimeException absent){return;}if(!(children instanceof Map<?,?>map)||map.isEmpty())return;R.call(pose,"pushPose");try{R.call(part,"translateAndRotate",pose);for(Object child:map.values())R.call(child,"render",pose,vertex,light,overlay,color);}finally{R.call(pose,"popPose");}}
}
