package dev.zomboidmelee.boxing;
import com.mojang.blaze3d.vertex.PoseStack;
/** Optional Carry On 1.21.1 API. Never caches mutable carrying state across client/server. */
public final class Carry30 {
 static Class<?> manager;static boolean resolved;
 public static boolean active(Object player){if(player==null)return false;if(!resolved){resolved=true;try{manager=R.type("tschipp.carryon.common.carry.CarryOnDataManager");}catch(IllegalStateException absent){}}if(manager==null)return false;try{return R.yes(R.call(R.call(manager,"getCarryData",player),"isCarrying"));}catch(IllegalStateException incompatible){return false;}}
 static boolean show(Object player,int side){try{Object data=R.call(manager,"getCarryData",player);java.util.Optional<?> script=(java.util.Optional<?>)R.call(data,"getActiveScript");return script.isEmpty()||R.yes(R.call(R.call(script.get(),"scriptRender"),side==0?"renderRightArm":"renderLeftArm"));}catch(IllegalStateException unavailable){return true;}}
 public static First24.Frame frame(Object player,int side){int sign=side==0?1:-1;double gait=Bodycam26.distance*2.7,w=Math.min(1,Bodycam26.motion/.18),sway=Math.sin(gait)*.007*w,breath=Math.sin(BoxingClient16.CLOCK.getAsLong()/1e9*1.6)*.002;return new First24.Frame(sign*.34+sway,-.48+Math.abs(Math.sin(gait))*.008*w+breath,-.43,-103+Math.sin(gait)*w,sign*13,sign*8,sign*8);}
 /** Called after Carry On draws its carried object; native object rendering and placement are preserved. */
 public static void first(Object player,Object buffer,Object pose,int light,float partial){if(!active(player)||!Action19.firstPerson(BoxingClient16.mc()))return;for(int side=0;side<2;side++)if(show(player,side))First24.drawFrame((PoseStack)pose,buffer,light,player,side,frame(player,side));}
}
