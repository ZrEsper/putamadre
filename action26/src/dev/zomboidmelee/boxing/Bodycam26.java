package dev.zomboidmelee.boxing;
/** Continuous donor bodycam, composed with the accepted combat camera. No player rotation edits. */
public final class Bodycam26 {
 static Object owner,world;static double lastX,lastZ,distance,motion;
 static void tick(Object p,Object level){if(p!=owner||level!=world){owner=p;world=level;distance=motion=0;if(p!=null){lastX=R.coord(p,"X");lastZ=R.coord(p,"Z");}return;}if(p==null)return;double x=R.coord(p,"X"),z=R.coord(p,"Z"),delta=Math.hypot(x-lastX,z-lastZ);lastX=x;lastZ=z;motion=delta>2?0:Math.min(.8,delta);distance+=motion;}
 static double[] offset(double seconds,double stride,double motion,boolean sprint,boolean aim,double slide,double climb){double amp=(sprint?.42:.22)*(aim?.22:1),weight=Math.min(1,motion/.15);return new double[]{Math.sin(seconds*1.8)*.025+(Math.sin(stride*2)*.65+Math.sin(stride*1.1)*.15)*amp*weight+slide*.45-climb,Math.sin(seconds*1.1)*.025+Math.sin(stride)*amp*.7*weight+slide*1.2};}
 public static void camera(Object e){Object m=BoxingClient16.mc(),p=R.get(m,"player");if(p==null||p!=owner||R.get(m,"screen")!=null||!R.yes(R.call(p,"isAlive"))||R.yes(R.call(p,"isSleeping"))||!Action19.firstPerson(m))return;boolean aim=R.yes(R.call(R.get(R.get(m,"options"),"keyUse"),"isDown"));double[]a=offset(BoxingClient16.CLOCK.getAsLong()/1e9,distance*8,motion,R.yes(R.call(p,"isSprinting")),aim,MoveClient25.slide(p),MoveClient25.climb(p));R.call(e,"setPitch",(float)(R.num(R.call(e,"getPitch"))+a[0]));R.call(e,"setRoll",(float)(R.num(R.call(e,"getRoll"))+a[1]));}
}
