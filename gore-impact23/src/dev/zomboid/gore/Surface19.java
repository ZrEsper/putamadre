package dev.zomboid.gore;
import java.util.*;
/** Cut original UV-mapped polygons, never regenerate skin as solid cuboid fragments. */
public final class Surface19 {
 record P(float x,float y,float z,float u,float v){float axis(int a){return a==0?x:a==1?y:z;}P mix(P b,float t){return new P(x+(b.x-x)*t,y+(b.y-y)*t,z+(b.z-z)*t,u+(b.u-u)*t,v+(b.v-v)*t);}}
 static boolean destroyed(int region,int details){return Trauma12.stage(details,region)==7&&(region!=6||Trauma12.stage(details,7)==7);}
 static final Map<Object,Boolean>TRUSTED=new WeakHashMap<>();
 public static boolean render(Object part,Object pose,Object vertex,int light,int overlay,int color){
  Object context=((ThreadLocal<?>)R.get(R.type("dev.zomboid.gore.Skin12"),"CURRENT")).get();if(context==null)return false;
  Object assigned=((Map<?,?>)R.get(context,"parts")).get(part);if(!(assigned instanceof Integer index))return false;int region=index>=8?index-8:index;
  int state=((Number)R.get(context,"state")).intValue();if(destroyed(region,((Number)R.get(context,"details")).intValue()))return true;if(region<6&&(state&(1<<region))!=0)return true;
  // Decorative descendants retain native rendering. They do not each become a new body cavity.
  if(!Model14.ROOTS.containsKey(part))return false;
  if(!readable(part)){if(Math.max(Trauma12.stage(((Number)R.get(context,"details")).intValue(),region),region==6?Trauma12.stage(((Number)R.get(context,"details")).intValue(),7):0)>0){Object children=R.get(part,"children");if(children instanceof Map<?,?> m&&!m.isEmpty()){Model14.children(part,pose,vertex,light,overlay,color);return true;}}return false;}
  if(index<8)return draw(part,pose,vertex,light,overlay,color);
  // Clip clothing with the same wound; do not hide the entire layer or leave it covering the hole.
  @SuppressWarnings("unchecked") Map<Object,Integer> parts=(Map<Object,Integer>)R.get(context,"parts");
  parts.put(part,region);try{return draw(part,pose,vertex,light,overlay,color);}finally{parts.put(part,index);}
 }
 static boolean draw(Object part,Object pose,Object vertex,int light,int overlay,int color){Object visible=null;try{visible=R.get(part,"visible");if(Boolean.FALSE.equals(visible))R.set(part,"visible",true);return R.yes(R.call(R.type("dev.zomboid.gore.Skin12"),"renderSurface19",part,pose,vertex,light,overlay,color));}finally{if(Boolean.FALSE.equals(visible))R.set(part,"visible",false);}}
 static boolean readable(Object part){try{Object raw=R.get(part,"cubes");if(!(raw instanceof List<?>cubes)||cubes.isEmpty())return false;for(Object cube:cubes){Object faces=R.get(cube,"polygons");if(!(faces instanceof Object[]fs)||fs.length==0)return false;for(String bound:new String[]{"minX","minY","minZ","maxX","maxY","maxZ"})if(!Double.isFinite(R.num(R.get(cube,bound))))return false;for(Object f:fs){Object vertices=R.get(f,"vertices");if(!(vertices instanceof Object[]vs)||vs.length!=4)return false;for(Object vertex:vs){Object position=R.get(vertex,"pos");for(String axis:new String[]{"x","y","z"})if(!Double.isFinite(R.num(R.get(position,axis))))return false;for(String uv:new String[]{"u","v"})if(!Double.isFinite(R.num(R.get(vertex,uv))))return false;}Object normal=R.get(f,"normal");for(String axis:new String[]{"x","y","z"})if(!Double.isFinite(R.num(R.get(normal,axis))))return false;}}return true;}catch(RuntimeException unsupported){return false;}}
 static List<P> clip(List<P>p,int axis,float edge,boolean less){if(p.isEmpty())return List.of();List<P>out=new ArrayList<>();P previous=p.getLast();boolean before=less?previous.axis(axis)<=edge:previous.axis(axis)>=edge;for(P current:p){boolean after=less?current.axis(axis)<=edge:current.axis(axis)>=edge;if(before!=after){float t=(edge-previous.axis(axis))/(current.axis(axis)-previous.axis(axis));out.add(previous.mix(current,t));}if(after)out.add(current);previous=current;before=after;}return out;}
 static List<List<P>> subtract(List<P>p,Mesh12.Box b){List<List<P>>outside=new ArrayList<>();List<P>remaining=p;float[]edges={b.x0(),b.x1(),b.y0(),b.y1(),b.z0(),b.z1()};
  // A face outside the cutting volume stays byte-for-byte in the original polygon order.
  for(int a=0;a<3;a++){float min=Float.MAX_VALUE,max=-min;for(P v:p){min=Math.min(min,v.axis(a));max=Math.max(max,v.axis(a));}if(max<=edges[a*2]||min>=edges[a*2+1])return List.of(p);}
  for(int i=0;i<6&&!remaining.isEmpty();i++){int axis=i/2;boolean lower=i%2==0;List<P>piece=clip(remaining,axis,edges[i],lower);if(piece.size()>=3)outside.add(piece);remaining=clip(remaining,axis,edges[i],!lower);}return outside;
 }
 static void emit(List<Mesh12.Quad>out,List<P>p,int material,float nx,float ny,float nz){if(p.size()<3)return;if(p.size()==4){out.add(quad(p.get(0),p.get(1),p.get(2),p.get(3),material,nx,ny,nz));return;}for(int i=1;i<p.size()-1;i++)out.add(quad(p.get(0),p.get(i),p.get(i+1),p.get(i+1),material,nx,ny,nz));}
 static Mesh12.Quad quad(P a,P b,P c,P d,int material,float nx,float ny,float nz){Mesh12.Vertex[]vs=new Mesh12.Vertex[4];P[]ps={a,b,c,d};for(int i=0;i<4;i++){P p=ps[i];vs[i]=new Mesh12.Vertex(p.x/16,p.y/16,p.z/16,p.u,p.v);}return new Mesh12.Quad(vs,material,nx,ny,nz);}
 static void cuts(List<Mesh12.Box>out,Mesh12.Box b,int level,float lo,float hi){if(level<2)return;if(level>=7){out.add(Anatomy14.scale(new Mesh12.Box(lo-.001f,-.001f,-.001f,hi+.001f,1.001f,1.001f),b));return;}
  float cx=(lo+hi)/2,rx=level==2?.065f:level==3?.15f:level==4?.27f:level==5?.39f:.48f,ry=level==2?.075f:level==3?.18f:level==4?.33f:level==5?.43f:.49f;
  float[]profile={.45f,.85f,1,.7f,.4f};for(int i=0;i<5;i++){float x=rx*profile[i]*(hi-lo),y=.5f-ry+2*ry*i/5,Y=.5f-ry+2*ry*(i+1)/5;out.add(Anatomy14.scale(new Mesh12.Box(cx-x,y,-.001f,cx+x,Y,1.001f),b));}
 }
 static void mark(List<Mesh12.Quad>out,Mesh12.Box b,int level,float lo,float hi){if(level!=1)return;float x=(lo+hi)/2,y=.5f;float[]widths={.018f,.045f,.032f};for(int i=0;i<3;i++){float rx=widths[i]*(hi-lo),Y=y-.045f+i*.03f;float z=b.z0()-.02f;float a=b.x0()+(x-rx)*(b.x1()-b.x0()),X=b.x0()+(x+rx)*(b.x1()-b.x0()),v=b.y0()+Y*(b.y1()-b.y0()),V=b.y0()+(Y+.03f)*(b.y1()-b.y0());emit(out,List.of(new P(X,v,z,.45f,.05f),new P(a,v,z,.05f,.05f),new P(a,V,z,.05f,.45f),new P(X,V,z,.45f,.45f)),1,0,0,-1);}}
 static void internals(List<Mesh12.Quad>out,List<Anatomy14.Piece>pieces){
 for(int i=0;i<pieces.size();i++){var piece=pieces.get(i);List<Mesh12.Quad>faces=new ArrayList<>();Mesh12.box(faces,piece.box(),piece.material(),null);
  for(var face:faces){int axis=face.nx()!=0?0:face.ny()!=0?1:2;float normal=axis==0?face.nx():axis==1?face.ny():face.nz();List<P>p=new ArrayList<>();for(var v:face.vertices())p.add(new P(v.x()*16,v.y()*16,v.z()*16,v.u(),v.v()));float plane=p.getFirst().axis(axis);List<List<P>>remaining=List.of(p);
   for(int j=0;j<pieces.size()&&!remaining.isEmpty();j++){if(i==j)continue;var b=pieces.get(j).box();float low=axis==0?b.x0():axis==1?b.y0():b.z0(),high=axis==0?b.x1():axis==1?b.y1():b.z1();if(plane<low-.00001f||plane>high+.00001f)continue;
    boolean extendsOut=normal>0?high>plane+.00001f||Math.abs(high-plane)<.00001f&&j<i:low<plane-.00001f||Math.abs(low-plane)<.00001f&&j<i;if(!extendsOut)continue;
    var occluder=new Mesh12.Box(b.x0()-.00001f,b.y0()-.00001f,b.z0()-.00001f,b.x1()+.00001f,b.y1()+.00001f,b.z1()+.00001f);List<List<P>>next=new ArrayList<>();for(var polygon:remaining)next.addAll(subtract(polygon,occluder));remaining=next;
   }
   for(var polygon:remaining)emit(out,polygon,piece.material(),face.nx(),face.ny(),face.nz());
  }
 }
 }
 public static List<Mesh12.Quad> build(List<Mesh12.Cube>cubes,int region,int stage){if(cubes.isEmpty())return List.of();float x=Float.MAX_VALUE,y=x,z=x,X=-x,Y=-x,Z=-x;boolean faces=false;
  for(var c:cubes)for(var f:c.faces())for(float[]p:f.xyz()){faces=true;x=Math.min(x,p[0]);y=Math.min(y,p[1]);z=Math.min(z,p[2]);X=Math.max(X,p[0]);Y=Math.max(Y,p[1]);Z=Math.max(Z,p[2]);}
  if(!faces){var b=cubes.getFirst().bounds();x=b.x0();y=b.y0();z=b.z0();X=b.x1();Y=b.y1();Z=b.z1();}
  Mesh12.Box bounds=new Mesh12.Box(x,y,z,X,Y,Z);List<Mesh12.Box>holes=new ArrayList<>();if(region==6){cuts(holes,bounds,stage&7,.5f,1);cuts(holes,bounds,(stage>>5)&7,0,.5f);}else cuts(holes,bounds,stage&7,0,1);
  List<Mesh12.Quad>out=new ArrayList<>();
  if(Model14.skin())for(var cube:cubes)for(var f:cube.faces()){List<P>p=new ArrayList<>();for(int i=0;i<4;i++)p.add(new P(f.xyz()[i][0],f.xyz()[i][1],f.xyz()[i][2],f.u()[i],f.v()[i]));List<List<P>>remain=List.of(p);for(var hole:holes){List<List<P>>next=new ArrayList<>();for(var polygon:remain)next.addAll(subtract(polygon,hole));remain=next;}for(var polygon:remain)emit(out,polygon,0,f.nx(),f.ny(),f.nz());}
  if(Model14.internal()){List<Anatomy14.Piece>pieces=new ArrayList<>();for(var part:Anatomy14.damaged(region,stage)){// Skin aperture exposes underlying anatomy; it must not drill the same void through it.
 pieces.add(new Anatomy14.Piece(part.name(),Anatomy14.scale(part.box(),bounds),part.material()));}internals(out,pieces);}
  if(Model14.skin()&&faces){if(region==6){mark(out,bounds,stage&7,.5f,1);mark(out,bounds,(stage>>5)&7,0,.5f);}else mark(out,bounds,stage&7,0,1);}
  return List.copyOf(out);
 }
}
