package dev.zomboid.isotacz;
import java.util.*;
public final class Impact {
 record Mark(Object position,long time){}
 static final Deque<Mark> marks=new ArrayDeque<>();static Object world;
 static void add(double x,double y,double z){try{Object mc=Bridge.mc(),level=R.get(mc,"level");if(world!=level){marks.clear();world=level;}if(!Bridge.active())return;marks.addLast(new Mark(Bridge.vec(x,y,z),System.nanoTime()));while(marks.size()>48)marks.removeFirst();}catch(Throwable e){Bridge.warn("bullet impacts",e);}}
 static void draw(Object mc,Object g){long now=System.nanoTime();if(world!=R.get(mc,"level")){marks.clear();return;}while(!marks.isEmpty()&&now-marks.peekFirst().time()>800_000_000L)marks.removeFirst();for(Mark mark:marks){double[] p=Bridge.screenPoint(mc,g,mark.position());if(p!=null){int x=(int)Math.round(p[0]),y=(int)Math.round(p[1]);R.call(g,"fill",x-3,y,x+4,y+1,0xFFFFA340);R.call(g,"fill",x,y-3,x+1,y+4,0xFFFFA340);}}}
}
