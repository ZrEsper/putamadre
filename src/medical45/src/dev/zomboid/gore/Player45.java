package dev.zomboid.gore;
import java.util.*;
/** Surface marks share the current player model's transforms; no replacement player model. */
public final class Player45 {
 static final ThreadLocal<Context> CURRENT=new ThreadLocal<>();
 static final String[] PARTS={"head","leftArm","leftLeg","leftLeg","body","rightArm","rightLeg","rightLeg"};
 static final String[] ROOT={"head","body","leftArm","rightArm","leftLeg","rightLeg"};
 static final int[] REGIONS={0,6,2,3,4,5};
 static final Map<Object,Map<Object,Integer>> MODELS=new WeakHashMap<>();
 static class Context {int[] marks;Object buffer;Map<Object,Integer>parts;Context(int[]m,Object b){marks=m;buffer=b;}}
 static Object c(Object o,String n,Object...a){return R.call(o,n,a);}
 public static void begin(Object renderer,Object entity,Object buffer){CURRENT.remove();if(entity==null||!instance(entity,"net.minecraft.world.entity.player.Player"))return;try{int[] a=(int[])c(R.type("dev.zomboid.firstaid.Wounds45"),"snapshot",entity);if(a.length>0){Context ctx=new Context(a,buffer);ctx.parts=parts(c(renderer,"getModel"));CURRENT.set(ctx);}}catch(IllegalStateException e){if(!(e.getCause()instanceof ClassNotFoundException))throw e;}}
 static boolean instance(Object o,String type){for(Class<?>c=o.getClass();c!=null;c=c.getSuperclass())if(c.getName().equals(type))return true;return false;}
 static Map<Object,Integer> parts(Object model){return MODELS.computeIfAbsent(model,m->{Map<Object,Integer>p=new IdentityHashMap<>();for(int i=0;i<ROOT.length;i++)p.put(R.get(m,ROOT[i]),REGIONS[i]);String[] layers={"hat","jacket","leftSleeve","rightSleeve","leftPants","rightPants"};for(int i=0;i<layers.length;i++)try{p.put(R.get(m,layers[i]),REGIONS[i]);}catch(IllegalStateException ignored){}return p;});}
 public static void rag(Object rag,Object buffer){CURRENT.remove();int[] a=Linked45.medical(rag);if(a.length>0)CURRENT.set(new Context(a,buffer));}
 public static void model(Object m){Context ctx=CURRENT.get();if(ctx!=null)ctx.parts=parts(m);}
 public static void end(){CURRENT.remove();}
 public static void draw(Object part,Object pose,int light,int overlay){Context ctx=CURRENT.get();if(ctx==null||ctx.parts==null||!Boolean.TRUE.equals(R.get(part,"visible"))||Boolean.TRUE.equals(R.get(part,"skipDraw")))return;Integer region=ctx.parts.get(part);if(region==null)return;List<Mesh12.Cube> cubes=Model25.cubes(part,region);if(cubes.isEmpty())return;
  float[] b={Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE,-Float.MAX_VALUE};for(Mesh12.Cube cube:cubes){Mesh12.Box q=cube.bounds();b[0]=Math.min(b[0],q.x0());b[1]=Math.min(b[1],q.y0());b[2]=Math.min(b[2],q.z0());b[3]=Math.max(b[3],q.x1());b[4]=Math.max(b[4],q.y1());b[5]=Math.max(b[5],q.z1());}
  Object texture=c(R.type("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","zomboid_gore","textures/entity/medical45.png"),vc=c(ctx.buffer,"getBuffer",c(R.type("net.minecraft.client.renderer.RenderType"),"entityCutoutNoCull",texture));
  c(pose,"pushPose");try{c(part,"translateAndRotate",pose);Object p=c(pose,"last");for(int k=0;k+6<ctx.marks.length;k+=7){int clinical=ctx.marks[k];if(clinical<0||clinical>7)continue;int r=switch(clinical){case 0->0;case 1->2;case 2,3->4;case 4->6;case 5->3;default->5;};if(r!=region)continue;double[] q={ctx.marks[k+1]/255d,ctx.marks[k+2]/255d,ctx.marks[k+3]/255d};if(clinical==3||clinical==7)q[1]=.75+q[1]*.25;else if(clinical==2||clinical==6)q[1]*=.75;int axis=0;double edge=1;boolean high=false;for(int a=0;a<3;a++){if(q[a]<edge){edge=q[a];axis=a;high=false;}if(1-q[a]<edge){edge=1-q[a];axis=a;high=true;}}
   boolean bandage=(ctx.marks[k+6]&1)!=0;float size=bandage?1.6f:ctx.marks[k+4]==2?1.05f:.7f;size*=Math.max(.12,Math.min(1,ctx.marks[k+5]/12000f));float[] center=new float[3];for(int a=0;a<3;a++)center[a]=(float)(b[a]+q[a]*(b[a+3]-b[a]));center[axis]=b[axis+(high?3:0)]+(high?.018f:-.018f);
   int u=(axis+1)%3,v=(axis+2)%3,color=bandage?((ctx.marks[k+6]&2)!=0?0xffa99477:0xffe8dfc5):0xffb7343c;float[][] verts=new float[4][3];int[][] d={{-1,-1},{1,-1},{1,1},{-1,1}};for(int j=0;j<4;j++){verts[j]=center.clone();verts[j][u]=Math.max(b[u],Math.min(b[u+3],center[u]+d[j][0]*size));verts[j][v]=Math.max(b[v],Math.min(b[v+3],center[v]+d[j][1]*size));Object vv=c(vc,"addVertex",p,verts[j][0]/16,verts[j][1]/16,verts[j][2]/16);c(vv,"setColor",color);c(vv,"setUv",(bandage?.5f:0f)+(j==0||j==3?0f:.5f),j<2?0f:1f);c(vv,"setOverlay",overlay);c(vv,"setLight",light);float[]normal=new float[3];normal[axis]=high?1:-1;c(vv,"setNormal",p,normal[0],normal[1],normal[2]);}
  }}finally{c(pose,"popPose");}
 }
}
