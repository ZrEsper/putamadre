package dev.zomboid.gore;
import java.util.*;
public final class Ray45 {
 static final Map<Object,Cache> CACHE=new WeakHashMap<>();
 static class Cache {int hash;Map<Integer,List<Mesh12.Box>> parts=new HashMap<>();Cache(int h){hash=h;}}
 /** Conservative surviving bone envelope. Never makes a removed crater solid again. */
 public static double trace(double[] start,double[] end,int part,Object packet){int region=Damage13.region(part);int[] previous=Cuts24.CURRENT.get();Cuts24.CURRENT.set(Cuts24.field(packet));try{
  float hx=part==0||part==1?.25f:(part==4||part==5)&&R.get(packet,"medical45") instanceof String slim&&slim.startsWith("-")?.09375f:.125f,hy=part==1?.25f:.375f,hz=part==1?.25f:.125f;
  Mesh12.Box box=new Mesh12.Box(-hx*16,-hy*16,-hz*16,hx*16,hy*16,hz*16);
  // Rendering uses Z-pi: physics local x/y are inverted relative to model coordinates.
  double[] a={-start[0]*16,-start[1]*16,start[2]*16},b={-end[0]*16,-end[1]*16,end[2]*16};double nearest=Double.POSITIVE_INFINITY;
  int hash=Arrays.hashCode(Cuts24.field(packet));Cache cache=CACHE.get(packet);if(cache==null||cache.hash!=hash){cache=new Cache(hash);CACHE.put(packet,cache);}List<Mesh12.Box> pieces=cache.parts.get(part);if(pieces==null){pieces=new ArrayList<>(CSG24.remainder(box,CSG24.holes(box,region,0.0)));for(Anatomy14.Piece piece:Anatomy14.anatomy(region)){if(Organ29.removed(piece.name(),region,piece.box()))continue;double depth=piece.material()==2||piece.name().equals("tooth")?.85:Anatomy27.organ(piece.name())?1.05:.4;pieces.addAll(CSG24.remainder(Anatomy14.scale(piece.box(),box),CSG24.holes(box,region,depth)));}pieces=Connected48.connected(pieces);cache.parts.put(part,List.copyOf(pieces));}for(Mesh12.Box q:pieces){double t=intersect(a,b,q);nearest=Math.min(nearest,t);}return nearest;
 }finally{Cuts24.CURRENT.set(previous);}}
 public static double intersect(double[]a,double[]b,Mesh12.Box q){double lo=0,hi=1;double[] min={q.x0(),q.y0(),q.z0()},max={q.x1(),q.y1(),q.z1()};for(int i=0;i<3;i++){double d=b[i]-a[i];if(Math.abs(d)<1e-10){if(a[i]<min[i]||a[i]>max[i])return Double.POSITIVE_INFINITY;continue;}double u=(min[i]-a[i])/d,v=(max[i]-a[i])/d;if(u>v){double t=u;u=v;v=t;}lo=Math.max(lo,u);hi=Math.min(hi,v);if(lo>hi)return Double.POSITIVE_INFINITY;}return lo;}
}
