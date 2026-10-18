package dev.zomboidmelee.boxing;
import java.util.*;import java.io.*;import java.nio.charset.StandardCharsets;
/** World-depth snapshot followed by a small color-only pass after Iris and first-person hands. */
public final class LaserLate37{
 static final int MAX=32;static final LinkedHashMap<Object,Laser32.Beam>QUEUE=new LinkedHashMap<>();static java.lang.ref.WeakReference<Object> world=new java.lang.ref.WeakReference<>(null);static double[]view=Matrix32.identity(),vp=Matrix32.identity(),inverse=Matrix32.identity(),camera={0,0,0};static int depth,width,height,program,vao;static boolean ready,warned;static long retry;
 static void warn(RuntimeException e){if(!warned){warned=true;System.err.println("[Laser37] Late world-depth beam: "+e);e.printStackTrace();}}
 static boolean shadow(){try{return R.yes(R.call(R.type("net.irisshaders.iris.shadows.ShadowRenderingState"),"areShadowsCurrentlyBeingRendered"));}catch(IllegalStateException absent){return false;}}
 static void enqueue(Laser32.Beam beam){if(shadow())return;Object player=R.get(BoxingClient16.mc(),"player");if(QUEUE.containsKey(beam.entity())){if(!beam.first()&&QUEUE.get(beam.entity()).first())return;QUEUE.put(beam.entity(),beam);return;}if(QUEUE.size()>=MAX){if(beam.entity()!=player)return;QUEUE.remove(QUEUE.keySet().iterator().next());}QUEUE.put(beam.entity(),beam);}
 static boolean heldGun(Object mc){try{Object p=R.get(mc,"player");return p!=null&&R.type("com.tacz.guns.api.item.IGun").isInstance(R.call(R.call(p,"getMainHandItem"),"getItem"));}catch(IllegalStateException absent){return false;}}
 public static void stage(Object event){String stage=R.call(event,"getStage").toString().toLowerCase(Locale.ROOT);Object mc=BoxingClient16.mc(),level=R.get(mc,"level");if(stage.endsWith("after_sky")||world.get()!=level){QUEUE.clear();ready=false;world=new java.lang.ref.WeakReference<>(level);}if(!stage.endsWith("after_level")||level==null||R.get(mc,"screen")!=null||QUEUE.isEmpty()&&!heldGun(mc))return;if(System.nanoTime()<retry)return;
  try(Gl37.State state=new Gl37.State()){
   view=Matrix32.read(R.call(event,"getModelViewMatrix"));double[]projection=Matrix32.read(R.call(event,"getProjectionMatrix"));vp=Matrix32.mul(projection,view);inverse=Matrix32.inverse(vp);camera=Matrix32.xyz(R.call(R.call(event,"getCamera"),"getPosition"));Object main=R.call(mc,"getMainRenderTarget");int w=Gl37.i(R.get(main,"width")),h=Gl37.i(R.get(main,"height"));if(w<1||h<1||Gl37.i(R.call(main,"getDepthTextureId"))<0)return;
   Gl37.gl(15,"glBindBuffer",35052,0);Gl37.bind(0,0);if(depth==0||width!=w||height!=h){if(depth!=0)Gl37.gl(11,"glDeleteTextures",depth);depth=Gl37.image(w,h,33190,6402,5126);width=w;height=h;}
   Gl37.gl(30,"glBindFramebuffer",36008,state.draw);Gl37.bind(0,depth);Gl37.gl(11,"glCopyTexSubImage2D",3553,0,0,0,0,0,w,h);ready=true;
  }catch(RuntimeException e){ready=false;retry=System.nanoTime()+1_000_000_000L;warn(e);}}
 static String resource(String path){try(InputStream in=LaserLate37.class.getResourceAsStream("/assets/zomboid_global_melee/shaders/"+path)){if(in==null)throw new IOException(path);return new String(in.readAllBytes(),StandardCharsets.UTF_8);}catch(IOException e){throw new IllegalStateException(e);}}
 static void init(){if(program!=0)return;program=Gl37.program(resource("laser37.vert"),resource("laser37.frag"));vao=Gl37.i(Gl37.gl(30,"glGenVertexArrays"));}
 static double[][]segment(double[]from,double[]to,double[]cam,double[]view){double[]a={from[0]-cam[0],from[1]-cam[1],from[2]-cam[2]},b={to[0]-cam[0],to[1]-cam[1],to[2]-cam[2]},eyeA=Matrix32.point(view,a[0],a[1],a[2]),eyeB=Matrix32.point(view,b[0],b[1],b[2]);double plane=-.08;if(eyeA[2]>=plane&&eyeB[2]>=plane)return null;if(eyeA[2]>=plane){double t=(plane-eyeA[2])/(eyeB[2]-eyeA[2]);for(int i=0;i<3;i++)a[i]+=(b[i]-a[i])*t;}if(eyeB[2]>=plane){double t=(plane-eyeB[2])/(eyeA[2]-eyeB[2]);for(int i=0;i<3;i++)b[i]+=(a[i]-b[i])*t;}return new double[][]{a,b};}
 static double distance(double[]a,double[]b){double s=0;for(int i=0;i<3;i++)s+=(a[i]-b[i])*(a[i]-b[i]);return Math.sqrt(s);}
 public static void finish(){try{
  Object mc=BoxingClient16.mc();if(!ready||QUEUE.isEmpty()||R.get(mc,"screen")!=null||world.get()!=R.get(mc,"level"))return;
  try(Gl37.State saved=new Gl37.State()){
   init();R.call(R.call(mc,"getMainRenderTarget"),"bindWrite",true);Gl37.gl(11,"glViewport",0,0,width,height);Gl37.gl(11,"glDisable",3089);Gl37.gl(11,"glDisable",2929);Gl37.gl(11,"glDisable",2884);Gl37.gl(11,"glEnable",3042);Gl37.gl(14,"glBlendFuncSeparate",770,1,1,0);Gl37.gl(11,"glDepthMask",false);Gl37.gl(11,"glColorMask",true,true,true,true);Gl37.gl(20,"glUseProgram",program);Gl37.gl(30,"glBindVertexArray",vao);Gl37.bind(0,depth);Gl37.integer(program,"WorldDepth",0);Gl37.matrix(program,"ViewProjection",vp);Gl37.matrix(program,"InverseViewProjection",inverse);Gl37.gl(20,"glUniform2f",Gl37.loc(program,"Viewport"),(float)width,(float)height);
   for(Laser32.Beam beam:QUEUE.values()){
    double[]end=Laser32.axisEnd(beam);double[][]s=segment(beam.origin(),end,camera,view);if(s==null||distance(s[0],s[1])<.01)continue;Gl37.vec(program,"From",s[0]);Gl37.vec(program,"To",s[1]);int c=beam.color();Gl37.gl(20,"glUniform3f",Gl37.loc(program,"Color"),(c>>16&255)/255f,(c>>8&255)/255f,(c&255)/255f);Gl37.scalar(program,"WidthPixels",beam.night()?.75f:.55f);Gl37.scalar(program,"DotPixels",beam.night()?2.2f:1.6f);Gl37.integer(program,"Shape",0);Gl37.gl(11,"glDrawArrays",4,0,6);if(distance(beam.origin(),end)<255.9){Gl37.integer(program,"Shape",1);Gl37.gl(11,"glDrawArrays",4,0,6);}
   }
  }
 }catch(RuntimeException e){retry=System.nanoTime()+1_000_000_000L;warn(e);}finally{QUEUE.clear();ready=false;}}
}
