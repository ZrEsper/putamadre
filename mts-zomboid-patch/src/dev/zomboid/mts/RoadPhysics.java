package dev.zomboid.mts;
import java.util.*;
public final class RoadPhysics {
 static Map<Object,Double> contacts=new WeakHashMap<>();
 public static boolean soft(String id){return id.endsWith("_leaves")||id.equals("minecraft:moss_block")||id.equals("minecraft:moss_carpet")||id.equals("minecraft:pale_moss_block")||id.equals("minecraft:pale_moss_carpet");}
 public static double severity(String id,float hardness){return soft(id)?0:hardness<1?.35:hardness<1.5?.65:1;}
 public static float maximumStep(float requested,double radius){return (float)Math.max(0,Math.min(requested,2.5-radius));}
 public static double tilt(double requested,double current,boolean pitch){double limit=pitch?22:14,target=Math.max(-limit,Math.min(limit,current+Math.max(-.75,Math.min(.75,requested))));return Math.max(-.75,Math.min(.75,target-current));}
 public static void begin(Object v){if(Mechanics.car(v))contacts.put(v,0d);}
 public static float hardness(Object world,Object position,Object v){float actual=((Number)R.call(world,"getBlockHardness",position)).floatValue();if(!Mechanics.car(v))return actual;String name=R.call(world,"getBlockName",position).toString();contacts.put(v,Math.max(contacts.getOrDefault(v,0d),severity(name,actual)));return soft(name)?0:actual;}
 public static boolean breakAllowed(boolean original,Object v,Object position){return original||Mechanics.car(v)&&soft(R.call(R.get(v,"world"),"getBlockName",position).toString());}
 public static double factor(Object v){return contacts.getOrDefault(v,1d);}
 public static void groundBox(Object box){Object v=R.get(box,"vehicle");if(!Mechanics.car(v))return;R.set(box,"climbHeight",maximumStep(((Number)R.get(box,"climbHeight")).floatValue(),R.num(R.get(R.get(box,"solidBox"),"heightRadius"))));}
 public static double angle(double requested,Object collection,boolean pitch){Object v=R.get(collection,"vehicle");if(!Mechanics.car(v))return requested;Object orientation=R.get(v,"orientation");R.call(orientation,"convertToAngles");double current=R.num(R.get(R.get(orientation,"angles"),pitch?"x":"z"));return tilt(requested,current,pitch);}
}
