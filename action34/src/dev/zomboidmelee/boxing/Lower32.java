package dev.zomboidmelee.boxing;
import java.util.*;import com.mojang.blaze3d.vertex.PoseStack;
/** Lower high-ready in outer view coordinates before the native wall/high-ready rotation. */
public final class Lower32{
 static final ThreadLocal<Deque<Boolean>> STACK=ThreadLocal.withInitial(ArrayDeque::new);
 public static void before(Object pose,float partial){boolean pushed=false;if(pose instanceof PoseStack p&&Bodycam27.active()){double down=.15*Equipment29.raised(partial)*(1-Bodycam27.aiming(BoxingClient16.mc(),R.get(BoxingClient16.mc(),"player"),partial));if(down>.0001){p.pushPose();p.translate(0,-down,0);pushed=true;}}STACK.get().push(pushed);}
 public static void after(Object pose){var s=STACK.get();if(!s.isEmpty()&&s.pop())((PoseStack)pose).popPose();if(s.isEmpty())STACK.remove();}
}
