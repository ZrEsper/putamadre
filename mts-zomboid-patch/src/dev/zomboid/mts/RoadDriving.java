package dev.zomboid.mts;
import java.util.*;
public final class RoadDriving {
 static final ThreadLocal<Object> scopedWorld=new ThreadLocal<>();
 static final ThreadLocal<Boolean> collisionContext=new ThreadLocal<>();
 static final Map<Object,Boolean> sent=new WeakHashMap<>();
 public static boolean road(Object v){if(v==null)return false;if(Mechanics.car(v))return true;Object defs=R.get(R.get(v,"definition"),"parts");if(!(defs instanceof List<?>))return false;for(Object slot:(List<?>)defs){Object types=R.get(slot,"types");if(types instanceof Collection<?> c&&c.stream().anyMatch(t->t.toString().equals("engine_car")||t.toString().equals("engine_motorcycle")))return true;}return false;}
 public static boolean obstacle(String id){return id.endsWith("_wall")||id.endsWith("_fence")||id.endsWith("_fence_gate")||id.endsWith(".iron_bars")||id.equals("minecraft:iron_bars");}
 public static void enter(Object v){boolean active=road(v);collisionContext.set(active);if(active){Object world=R.get(v,"world");scopedWorld.set(world);((Set<?>)R.get(world,"knownAirBlocks")).clear();}}
 public static void leave(){Object world=scopedWorld.get();if(world!=null)((Set<?>)R.get(world,"knownAirBlocks")).clear();scopedWorld.remove();collisionContext.remove();}
 public static Object shape(Object shape,Object state){if(!Boolean.TRUE.equals(collisionContext.get()))return shape;String id=R.call(R.call(state,"getBlock"),"getDescriptionId").toString();return obstacle(id)?R.call(R.type("net.minecraft.world.phys.shapes.Shapes"),"empty"):shape;}
 public static void input(Object v){if(!road(v))return;Object mc=R.call(R.type("net.minecraft.client.Minecraft"),"getInstance");long window=R.lng(R.call(R.call(mc,"getWindow"),"getWindow"));boolean down=R.get(mc,"screen")==null&&R.yes(R.call(mc,"isWindowActive"))&&((Number)R.call(R.type("org.lwjgl.glfw.GLFW"),"glfwGetKey",window,32)).intValue()==1;boolean before=sent.getOrDefault(v,false);if(down!=before){Object var=R.call(v,"getOrCreateVariable","pz_drift");R.call(var,"setTo",down?1d:0d,false);R.call(R.get(R.type("minecrafttransportsimulator.mcinterface.InterfaceManager"),"packetInterface"),"sendToServer",R.make("minecrafttransportsimulator.packets.instances.PacketEntityVariableSet",var,down?1d:0d));sent.put(v,down);}}
 public static double grip(double normal,boolean held){return held&&normal>0?Math.max(.03,normal*.12):normal;}
 public static float skid(float normal,Object v){if(!road(v)||R.call(v,"getController")==null)return normal;boolean held=R.num(R.get(R.call(v,"getOrCreateVariable","pz_drift"),"currentValue"))>0;return (float)grip(normal,held);}
 public static void gun(Object v,Object fire,Object select){if(!road(v))R.call(R.type("minecrafttransportsimulator.systems.ControlSystem"),"controlGun",v,fire,select);}
}
