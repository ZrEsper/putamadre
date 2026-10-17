package dev.zomboidmelee.boxing;
import java.util.*;
/** Called from the method actually declared by TaCZ's gun renderer, not an inherited optional hook. */
public final class Equipment29 {
 static final ThreadLocal<Deque<Boolean>> NATIVE=ThreadLocal.withInitial(ArrayDeque::new);
 public static double raised(float partial){if(Owned31.local())return 0;double value=0;try{value=Math.max(value,R.num(R.call(R.type("com.locknar.taczammocheck.tactical.client.TaczHighReadyClient"),"getLocalHighReadyProgress",partial)));}catch(IllegalStateException absent){}try{value=Math.max(value,R.num(R.call(R.type("com.locknar.taczammocheck.tactical.client.TaczKickRaiseClient"),"getKickRaiseProgress")));}catch(IllegalStateException absent){}try{value=Math.max(value,R.num(R.get(R.type("com.locknar.taczammocheck.tactical.client.TaczWallObstructionClient"),"currentProgress")));}catch(IllegalStateException absent){}return Math.max(0,Math.min(1,value));}
 public static void before(Object item,Object context,Object pose,float partial){Sight34.begin();Lower32.before(pose,partial);boolean wrapped=false;try{R.call(R.type("com.locknar.taczammocheck.tactical.client.TaczFirstPersonGunPoseHooks"),"beforeRenderFirstPerson",item,context,pose,partial);wrapped=true;}catch(IllegalStateException unavailable){}try{Weapon27.before(item,context,pose,partial);NATIVE.get().push(wrapped);}catch(RuntimeException failure){try{if(wrapped)nativeAfter(pose);}finally{Lower32.after(pose);}throw failure;}}
 static void nativeAfter(Object pose){R.call(R.type("com.locknar.taczammocheck.tactical.client.TaczFirstPersonGunPoseHooks"),"afterRenderFirstPerson",pose);}
 public static void after(Object pose){Sight34.end();Deque<Boolean> frames=NATIVE.get();boolean wrapped=!frames.isEmpty()&&frames.pop();try{Weapon27.after(pose);}finally{try{if(wrapped)nativeAfter(pose);}finally{Lower32.after(pose);if(frames.isEmpty())NATIVE.remove();}}}
}
