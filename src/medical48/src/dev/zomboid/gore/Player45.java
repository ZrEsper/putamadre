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
   boolean bandage=(ctx.marks[k+6]&1)!=0;
   // Render the dressing once on the base part, outside its visible clothing shell.
   if(bandage){if(Model25.LAYERS.contains(part))continue;boolean earlier=false;for(int j=0;j<k;j+=7)if(ctx.marks[j]==clinical&&(ctx.marks[j+6]&1)!=0)earlier=true;if(earlier)continue;wrap(vc,p,b,q,region,(ctx.marks[k+6]&2)!=0,light,overlay);continue;}
   float size=ctx.marks[k+4]==2?1.05f:.7f;size*=Math.max(.12,Math.min(1,ctx.marks[k+5]/12000f));float[] center=new float[3];for(int a=0;a<3;a++)center[a]=(float)(b[a]+q[a]*(b[a+3]-b[a]));center[axis]=b[axis+(high?3:0)]+(high?.025f:-.025f);
   int u=(axis+1)%3,v=(axis+2)%3;float[][] verts=new float[4][3];int[][] d={{-1,-1},{1,-1},{1,1},{-1,1}};for(int j=0;j<4;j++){verts[j]=center.clone();verts[j][u]=Math.max(b[u],Math.min(b[u+3],center[u]+d[j][0]*size));verts[j][v]=Math.max(b[v],Math.min(b[v+3],center[v]+d[j][1]*size));}float[]normal=new float[3];normal[axis]=high?1:-1;quad(vc,p,verts,normal,0xffb7343c,0,.5f,light,overlay);

  }}finally{c(pose,"popPose");}
 }
 static void quad(Object vc,Object p,float[][] verts,float[] normal,int color,float u0,float u1,int light,int overlay){for(int j=0;j<4;j++){Object vv=c(vc,"addVertex",p,verts[j][0]/16,verts[j][1]/16,verts[j][2]/16);c(vv,"setColor",color);c(vv,"setUv",j==0||j==3?u0:u1,j<2?0f:1f);c(vv,"setOverlay",overlay);c(vv,"setLight",light);c(vv,"setNormal",p,normal[0],normal[1],normal[2]);}}
 static void wrap(Object vc,Object p,float[] b,double[] q,int region,boolean dirty,int light,int overlay){float half=region==0?.65f:region==6?1.25f:1.1f;float center=(float)(b[1]+q[1]*(b[4]-b[1]));if(region==0)center=b[1]+1.25f;center=Math.max(b[1]+half,Math.min(b[4]-half,center));float y0=center-half,y1=center+half;
  // Skin overlay inflation is up to half a pixel; a small cloth thickness avoids flicker.
  float pad=region==0?.56f:.31f,x0=b[0]-pad,x1=b[3]+pad,z0=b[2]-pad,z1=b[5]+pad;int color=dirty?0xffa99b87:0xffeee9dc;
  quad(vc,p,new float[][]{{x1,y0,z0},{x0,y0,z0},{x0,y1,z0},{x1,y1,z0}},new float[]{0,0,-1},color,.5f,1f,light,overlay);
  quad(vc,p,new float[][]{{x0,y0,z1},{x1,y0,z1},{x1,y1,z1},{x0,y1,z1}},new float[]{0,0,1},color,.5f,1f,light,overlay);
  quad(vc,p,new float[][]{{x0,y0,z0},{x0,y0,z1},{x0,y1,z1},{x0,y1,z0}},new float[]{-1,0,0},color,.5f,1f,light,overlay);
  quad(vc,p,new float[][]{{x1,y0,z1},{x1,y0,z0},{x1,y1,z0},{x1,y1,z1}},new float[]{1,0,0},color,.5f,1f,light,overlay);
 }
}
