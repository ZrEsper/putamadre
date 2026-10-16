package dev.zomboid.decor.needs;
import dev.zomboid.decor.*;import java.util.*;
public final class Toilets {
 public static boolean toilet(String id,int kind){return id.equals("wc")||id.equals("closestool")||kind==1&&id.equals("urinepool");}
 public static Object root(Object w,Object p){Object b=R.call(w,"getBlockEntity",p);Object root=b==null?null:Footprint.root(b);return root==null?p:root;}
 public static boolean visible(Object player,Object pos){Object w=R.call(player,"level"),end=R.make("net.minecraft.world.phys.Vec3",R.coord(pos,"X")+.5,R.coord(pos,"Y")+.5,R.coord(pos,"Z")+.5);Object ctx=R.make("net.minecraft.world.level.ClipContext",R.call(player,"getEyePosition"),end,R.get(R.type("net.minecraft.world.level.ClipContext$Block"),"COLLIDER"),R.get(R.type("net.minecraft.world.level.ClipContext$Fluid"),"NONE"),player);Object hit=R.call(w,"clip",ctx);return R.call(hit,"getType").toString().equals("MISS")||root(w,R.call(hit,"getBlockPos")).equals(pos);}
 public static Object nearby(Object player,int kind){Object w=R.call(player,"level"),base=R.call(player,"blockPosition");Set<Object> seen=new HashSet<>();double best=Double.MAX_VALUE;Object result=null;
  for(int x=-2;x<=2;x++)for(int y=-1;y<=2;y++)for(int z=-2;z<=2;z++){Object p=R.call(base,"offset",x,y,z);if(!R.yes(R.call(w,"hasChunkAt",p)))continue;p=root(w,p);if(!seen.add(p))continue;String id=Storage.id(R.call(w,"getBlockState",p));if(!toilet(id,kind))continue;double dx=R.coord(p,"X")+.5-R.coord(player,"X"),dy=R.coord(p,"Y")-(id.equals("wc")?1:0)-R.coord(player,"Y"),dz=R.coord(p,"Z")+.5-R.coord(player,"Z");double dist=dx*dx+dz*dz;if(Math.abs(dy)<=1&&dist<=2.56&&dist<best&&visible(player,p)){result=p;best=dist;}}
  return result;
 }
 public static boolean use(Object state,Object world,Object pos,Object player){String id=Storage.id(state);if(!toilet(id,1))return false;if(R.yes(R.call(world,"isClientSide")))return true;if(Needs.disabled(player)||Needs.busy(player))return true;Cycle cycle=Needs.get(player);if(cycle.kind==0)Needs.text(player,"not_due",false);else if(!toilet(id,cycle.kind))Needs.text(player,"urinal_only",false);else if(nearby(player,cycle.kind)!=null)Needs.start(player,cycle,true,root(world,pos));return true;}
}
