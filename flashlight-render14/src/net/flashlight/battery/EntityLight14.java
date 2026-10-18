package net.flashlight.battery;
import java.util.*;
/** Give native entity/hand shaders real block-light input; do not redraw or invent a flat albedo. */
public final class EntityLight14 {
 static Object world;static long tick=Long.MIN_VALUE;static int rays;static boolean playerLit;static final ArrayList<Light6.Lamp> active=new ArrayList<>(2);static final int BUDGET=24,MAX=128;
 static final LinkedHashMap<Object,Map<Object,Hit>> CACHE=new LinkedHashMap<>(16,.75f,true);
 record Hit(long tick,double[]from,double[]to,boolean clear){}
 public static void reset(Object level){if(world!=level){world=level;CACHE.clear();tick=Long.MIN_VALUE;active.clear();playerLit=false;}}
 public static void invalidate(){tick=Long.MIN_VALUE;}
 static double d(double[]a,double[]b){double s=0;for(int i=0;i<3;i++)s+=(a[i]-b[i])*(a[i]-b[i]);return Math.sqrt(s);}
 static double smooth(double x){x=Math.max(0,Math.min(1,x));return x*x*(3-2*x);}
 static double strength(Light6.Lamp l,double[]p,double radius){double[]v={p[0]-l.origin[0],p[1]-l.origin[1],p[2]-l.origin[2]};double n=Math.sqrt(Light6.dot(v,v)),z=Light6.dot(v,l.forward);if(n>l.range||z<=0)return 0;double radial=Math.sqrt(Math.max(0,n*n-z*z)),aperture=z*l.tan+Math.min(.45,radius);double spot=1-smooth((radial-aperture*.6)/Math.max(.01,aperture*.4));return spot*Math.pow(Math.max(0,1-n/l.range),1.1);}
 static int pack(int original,double power){if(power<=.002)return original;int block=Math.min(15,(int)Math.ceil(14*Math.pow(Math.min(1,power),.35)));return(original&~240)|(Math.max((original>>4)&15,block)<<4);}
 static boolean clear(Object entity,Light6.Lamp lamp,double[]p,long time){Map<Object,Hit>hits=CACHE.get(entity);if(hits==null){hits=new IdentityHashMap<>();CACHE.put(entity,hits);if(CACHE.size()>MAX)CACHE.remove(CACHE.keySet().iterator().next());}Hit old=hits.get(lamp.owner);if(old!=null&&time-old.tick()<3&&d(old.from(),lamp.origin)<.2&&d(old.to(),p)<.35)return old.clear();if(rays>=BUDGET)return false;rays++;double distance=d(lamp.origin,p);double[]dir={p[0]-lamp.origin[0],p[1]-lamp.origin[1],p[2]-lamp.origin[2]};boolean visible=distance<.6||Light6.trace(lamp,lamp.origin,Light6.normal(dir))>=distance-.1;hits.put(lamp.owner,new Hit(time,lamp.origin.clone(),p.clone(),visible));return visible;}
 public static int light(Object entity,float partial,int original){try{
  Object m=Light6.mc(),level=R.get(m,"level");if(level==null){world=null;CACHE.clear();active.clear();playerLit=false;return original;}if(entity==null)return original;if(world!=level){world=level;CACHE.clear();tick=Long.MIN_VALUE;}long now=((Number)R.call(level,"getGameTime")).longValue();Object player=R.get(m,"player");if(now!=tick){tick=now;rays=0;active.clear();playerLit=player!=null&&Power.active(player)!=null;if(Light6.world==level)for(Light6.Lamp l:Light6.cache.values())if(Power.active(l.owner)!=null)active.add(l);}
  if(entity==player&&playerLit)return(original&~240)|240;
  if(Light6.world!=level||active.isEmpty()){CACHE.clear();return original;}double[]p=Matrix6.xyz(R.call(R.call(entity,"getBoundingBox"),"getCenter"));double radius=((Number)R.call(entity,"getBbWidth")).doubleValue()*.5;double power=0;
  for(Light6.Lamp lamp:active){double s=strength(lamp,p,radius);if(s>power&&s>.002&&clear(entity,lamp,p,now))power=s;}
  return pack(original,power);
 }catch(RuntimeException unavailable){return original;}}
}
