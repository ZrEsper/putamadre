package net.flashlight.battery;
import java.nio.*;import java.util.*;
/** LWJGL bridge kept client-only; saves/restores the state touched by the extra terrain pass. */
public final class G6{
 static Object gl(int v,String method,Object...args){return R.call(R.type("org.lwjgl.opengl.GL"+v),method,args);}static int i(Object v){return ((Number)v).intValue();}static int get(int p){return i(gl(11,"glGetInteger",p));}static void bind(int unit,int texture){R.call(R.type("com.mojang.blaze3d.platform.GlStateManager"),"_activeTexture",33984+unit);R.call(R.type("com.mojang.blaze3d.platform.GlStateManager"),"_bindTexture",texture);}static int texture(){return i(gl(11,"glGenTextures"));}
 static int image(int width,int height,int internal,int format,int kind){int id=texture();R.call(R.type("com.mojang.blaze3d.platform.GlStateManager"),"_bindTexture",id);gl(11,"glTexImage2D",3553,0,internal,width,height,0,format,kind,0L);gl(11,"glTexParameteri",3553,10241,9728);gl(11,"glTexParameteri",3553,10240,9728);gl(11,"glTexParameteri",3553,10242,33071);gl(11,"glTexParameteri",3553,10243,33071);return id;}
 static int shader(int type,String text){int s=i(gl(20,"glCreateShader",type));gl(20,"glShaderSource",s,text);gl(20,"glCompileShader",s);if(i(gl(20,"glGetShaderi",s,35713))==0)throw new IllegalStateException(String.valueOf(gl(20,"glGetShaderInfoLog",s)));return s;}
 static int program(String vertex,String fragment){int v=shader(35633,vertex),f=shader(35632,fragment),p=i(gl(20,"glCreateProgram"));gl(20,"glAttachShader",p,v);gl(20,"glAttachShader",p,f);gl(20,"glLinkProgram",p);gl(20,"glDeleteShader",v);gl(20,"glDeleteShader",f);if(i(gl(20,"glGetProgrami",p,35714))==0)throw new IllegalStateException(String.valueOf(gl(20,"glGetProgramInfoLog",p)));return p;}
 static int loc(int p,String n){return i(gl(20,"glGetUniformLocation",p,n));}static void scalar(int p,String n,float f){gl(20,"glUniform1f",loc(p,n),f);}static void integer(int p,String n,int i){gl(20,"glUniform1i",loc(p,n),i);}static void vec(int p,String n,double[]v){gl(20,"glUniform3f",loc(p,n),(float)v[0],(float)v[1],(float)v[2]);}static void matrix(int p,String n,double[]v){float[]f=new float[16];for(int i=0;i<16;i++)f[i]=(float)v[i];gl(20,"glUniformMatrix4fv",loc(p,n),false,f);}
 static Object rs(){return R.type("com.mojang.blaze3d.systems.RenderSystem");}static void flag(int cap,boolean on){gl(11,on?"glEnable":"glDisable",cap);}static void cacheReset(String type,String name){try{var f=R.type(type).getDeclaredField(name);f.setAccessible(true);if(f.getType()==int.class)f.setInt(null,-1);else f.set(null,null);}catch(ReflectiveOperationException|IllegalStateException absent){}}
 static final class State implements AutoCloseable{int draw=get(36006),read=get(36010),program=get(35725),vao=get(34229),active=get(34016),array=get(34964),renderbuffer=get(36007),unpack=get(35055),depthFunc=get(2932),srcRGB=get(32969),dstRGB=get(32968),srcA=get(32971),dstA=get(32970);int[]viewport=new int[4],scissor=new int[4],textures=new int[4],shaderTextures=new int[4];boolean blend,depth,cull,sciss,depthWrite;ByteBuffer mask=ByteBuffer.allocateDirect(4);Object oldShader,projection,sorting;float fogStart,fogEnd;float[]colour,clearColour=new float[4];
 State(){gl(11,"glGetFloatv",3106,clearColour);gl(11,"glGetIntegerv",2978,viewport);gl(11,"glGetIntegerv",3088,scissor);blend=(Boolean)gl(11,"glIsEnabled",3042);depth=(Boolean)gl(11,"glIsEnabled",2929);cull=(Boolean)gl(11,"glIsEnabled",2884);sciss=(Boolean)gl(11,"glIsEnabled",3089);depthWrite=(Boolean)gl(11,"glGetBoolean",2930);gl(11,"glGetBooleanv",3107,mask);for(int i=0;i<4;i++){gl(13,"glActiveTexture",33984+i);textures[i]=get(32873);shaderTextures[i]=G6.i(R.call(rs(),"getShaderTexture",i));}gl(13,"glActiveTexture",active);oldShader=R.call(rs(),"getShader");projection=R.make("org.joml.Matrix4f",R.call(rs(),"getProjectionMatrix"));sorting=R.call(rs(),"getVertexSorting");fogStart=((Number)R.call(rs(),"getShaderFogStart")).floatValue();fogEnd=((Number)R.call(rs(),"getShaderFogEnd")).floatValue();colour=((float[])R.call(rs(),"getShaderColor")).clone();}
 static void sync(String method,Object...args){try{R.call(rs(),method,args);}catch(IllegalStateException unavailable){}}
 public void close(){
 Engine6.capturing=false;
 // Restore native state first: an optional compatibility wrapper must not interrupt cleanup.
 gl(11,"glClearColor",clearColour[0],clearColour[1],clearColour[2],clearColour[3]);
 gl(30,"glBindFramebuffer",36009,draw);gl(30,"glBindFramebuffer",36008,read);
 for(int i=0;i<4;i++)bind(i,textures[i]);gl(13,"glActiveTexture",active);
 gl(30,"glBindRenderbuffer",36161,renderbuffer);gl(30,"glBindVertexArray",vao);
 gl(15,"glBindBuffer",34962,array);gl(15,"glBindBuffer",35052,unpack);gl(20,"glUseProgram",program);
 flag(3042,blend);flag(2929,depth);flag(2884,cull);flag(3089,sciss);
 gl(11,"glDepthFunc",depthFunc);gl(11,"glDepthMask",depthWrite);gl(14,"glBlendFuncSeparate",srcRGB,dstRGB,srcA,dstA);
 gl(11,"glColorMask",mask.get(0)!=0,mask.get(1)!=0,mask.get(2)!=0,mask.get(3)!=0);
 gl(11,"glViewport",viewport[0],viewport[1],viewport[2],viewport[3]);gl(11,"glScissor",scissor[0],scissor[1],scissor[2],scissor[3]);
 sync("setProjectionMatrix",projection,sorting);sync("setShaderFogStart",fogStart);sync("setShaderFogEnd",fogEnd);sync("setShaderColor",colour[0],colour[1],colour[2],colour[3]);
 for(int i=0;i<4;i++){sync("setShaderTexture",i,shaderTextures[i]);sync("activeTexture",33984+i);sync("bindTexture",textures[i]);}sync("activeTexture",active);
 sync("setShader",(java.util.function.Supplier<Object>)(()->oldShader));
 sync(blend?"enableBlend":"disableBlend");sync(depth?"enableDepthTest":"disableDepthTest");sync(cull?"enableCull":"disableCull");
 sync("depthFunc",depthFunc);sync("depthMask",depthWrite);sync("blendFuncSeparate",srcRGB,dstRGB,srcA,dstA);
 sync("colorMask",mask.get(0)!=0,mask.get(1)!=0,mask.get(2)!=0,mask.get(3)!=0);sync("viewport",viewport[0],viewport[1],viewport[2],viewport[3]);
 if(sciss)sync("enableScissor",scissor[0],scissor[1],scissor[2],scissor[3]);else sync("disableScissor");
 R.call(R.type("com.mojang.blaze3d.platform.GlStateManager"),"_activeTexture",active);
 try{R.call(R.type("com.mojang.blaze3d.vertex.BufferUploader"),"invalidate");}catch(IllegalStateException absent){try{R.call(R.type("com.mojang.blaze3d.vertex.BufferUploader"),"reset");}catch(IllegalStateException legacy){}}
 cacheReset("net.minecraft.client.renderer.ShaderInstance","lastProgramId");cacheReset("net.minecraft.client.renderer.ShaderInstance","lastAppliedShader");cacheReset("com.mojang.blaze3d.shaders.BlendMode","lastApplied");
 }

 }
}
