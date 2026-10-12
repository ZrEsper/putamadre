package dev.zomboid.isotacz;
/** Move the visual cutaway focus, never the player's interaction origin. */
public final class PanRecut {
 static Object shift(Object box,double x,double z){return x==0&&z==0?box:R.call(box,"move",x,0d,z);}
 public static Object box(Object box){return Bridge.edgeX==0&&Bridge.edgeZ==0||!Bridge.active()?box:shift(box,Bridge.edgeX,Bridge.edgeZ);}
 public static Object position(Object entity){Object pos=R.call(entity,"blockPosition");double x=Bridge.edgeX,z=Bridge.edgeZ;if(x==0&&z==0||!Bridge.active())return pos;return R.make("net.minecraft.core.BlockPos",(int)Math.floor(R.num(R.call(entity,"getX"))+x),(int)Math.floor(R.num(R.call(entity,"getY"))),(int)Math.floor(R.num(R.call(entity,"getZ"))+z));}
 public static Object camera(){Object mc=Bridge.mc();return R.call(R.call(R.get(mc,"gameRenderer"),"getMainCamera"),"getPosition");}
}
