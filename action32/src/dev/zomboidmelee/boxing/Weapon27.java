package dev.zomboidmelee.boxing;
import java.util.*;import com.mojang.blaze3d.vertex.PoseStack;
/** Outer push/pop around the uploaded TaCZ .19 wrapper, keeping reload, recoil and wall poses intact. */
public final class Weapon27 {
 static final ThreadLocal<Deque<Boolean>> STACK=ThreadLocal.withInitial(ArrayDeque::new);
 public static void before(Object item,Object context,Object pose,float partial){Deque<Boolean> stack=STACK.get();boolean pushed=false;try{if(pose instanceof PoseStack p&&!stack.contains(Boolean.TRUE)&&Bodycam27.active()&&(context==null||R.yes(R.call(context,"firstPerson")))&&R.call(R.type("com.locknar.taczammocheck.tactical.TaczGunCompat"),"getGunInfo",item)!=null){p.pushPose();pushed=true;Bodycam27.weapon(p,partial);}}catch(RuntimeException failure){if(pushed){((PoseStack)pose).popPose();pushed=false;}if(!(failure instanceof IllegalStateException))throw failure;}stack.push(pushed);}
 public static void after(Object pose){Deque<Boolean> stack=STACK.get();if(!stack.isEmpty()&&stack.pop())((PoseStack)pose).popPose();if(stack.isEmpty())STACK.remove();}
}
