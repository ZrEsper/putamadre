package com.raiiiden.warborn.hybrid;
import com.raiiiden.warborn.common.util.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
/** Four physical optical tubes, mounted on the animated vanilla head bone. */
public final class Tactical7 {
 static final ResourceLocation ATLAS=ResourceLocation.fromNamespaceAndPath("fracturepoint","textures/item/hybrid_tactical_atlas.png");
 static Object soundWorld,soundPlayer;static boolean soundReady,lastOn;static long lastSound;
 static Object c(Object o,String n,Object...a){return NvgReflection.call(o,n,a);}
 public static boolean hybrid(Object stack){return stack!=null&&Hybrid6.MODE.equals(NvgLink2.mode(stack));}
 public static boolean hideBuiltIn(Object renderer){return hybrid(c(renderer,"getCurrentStack"));}
 public static boolean render(Object layer,Object pose,Object source,int light,Object entity){
  Object helmet=NvgLink2.head(entity);if(!hybrid(helmet))return false;
  boolean down=NvgLink2.down(helmet),on=down&&NvgLink2.energy(helmet)>0;
  Object model=c(layer,"getParentModel"),head=NvgReflection.get(model,"head");
  PoseStack p=(PoseStack)pose;p.pushPose();c(head,"translateAndRotate",p);
  VertexConsumer vc=((MultiBufferSource)source).getBuffer(RenderType.entityCutoutNoCull(ATLAS));
  draw(p,vc,light,down,on);p.popPose();return true;
 }
 public static void draw(PoseStack p,VertexConsumer v,int light,boolean down,boolean on){
  box(p,v,light,down,-5.9f,-6.8f,-5.5f,5.9f,-5.1f,-4.1f,0,false);
  box(p,v,light,down,-.65f,-8.3f,-5.1f,.65f,-5.3f,-3.8f,1,false);
  for(float center:new float[]{-4.35f,-1.45f,1.45f,4.35f}){
   box(p,v,light,down,center-1.25f,-6.5f,-8.15f,center+1.25f,-4.0f,-4.8f,0,false);
   box(p,v,light,down,center-1.38f,-6.63f,-8.45f,center+1.38f,-3.87f,-7.75f,1,false);
   // Independent recessed glass, lower light when powered off.
   box(p,v,on?0xF000F0:light,down,center-1.03f,-6.28f,-8.47f,center+1.03f,-4.22f,-8.43f,on?3:2,true);
  }
 }
 static void box(PoseStack p,VertexConsumer v,int light,boolean down,float x0,float y0,float z0,float x1,float y1,float z1,int material,boolean glass){
  float[][]q={{x0,y0,z0},{x1,y0,z0},{x1,y1,z0},{x0,y1,z0},{x0,y0,z1},{x1,y0,z1},{x1,y1,z1},{x0,y1,z1}};
  int[][]faces={{0,3,2,1},{5,6,7,4},{4,7,3,0},{1,2,6,5},{0,1,5,4},{3,7,6,2}};
  float[][]normals={{0,0,-1},{0,0,1},{-1,0,0},{1,0,0},{0,-1,0},{0,1,0}};
  float u0=(material%2)*.5f+.005f,v0=(material/2)*.5f+.005f,u1=u0+.49f,v1=v0+.49f;
  for(int f=0;f<6;f++){if(glass&&f!=0)continue;float nx=normals[f][0],ny=normals[f][1],nz=normals[f][2];
   for(int i=0;i<4;i++){float[]a=q[faces[f][i]];float x=a[0],y=a[1],z=a[2];float nY=ny,nZ=nz;
    if(!down){double angle=Math.toRadians(-110);float dy=y+7,dz=z+4;y=-7+(float)(dy*Math.cos(angle)-dz*Math.sin(angle));z=-4+(float)(dy*Math.sin(angle)+dz*Math.cos(angle));nY=(float)(ny*Math.cos(angle)-nz*Math.sin(angle));nZ=(float)(ny*Math.sin(angle)+nz*Math.cos(angle));}
    v.addVertex(p.last(),x/16,y/16,z/16).setColor(255,255,255,255).setUv(i==0||i==3?u0:u1,i<2?v0:v1).setUv1(0,10).setUv2(light&65535,light>>>16&65535).setNormal(p.last(),nx,nY,nZ);
   }
  }
 }
 /** Transition-driven, never tied to shader reloads or damage effects. */
 public static void tick(){
  Object mc=c(NvgReflection.type("net.minecraft.client.Minecraft"),"getInstance");Object player=NvgReflection.get(mc,"player"),world=NvgReflection.get(mc,"level");
  if(player==null||world==null){soundWorld=null;soundPlayer=null;soundReady=false;lastOn=false;return;}
  Object helmet=NvgLink2.head(player);boolean on=!NvgLink2.mode(helmet).isEmpty()&&NvgLink2.down(helmet)&&NvgLink2.energy(helmet)>0;
  if(world!=soundWorld||player!=soundPlayer){soundWorld=world;soundPlayer=player;soundReady=true;lastOn=on;return;}
  if(on==lastOn)return;lastOn=on;long now=System.nanoTime();if(now-lastSound<150_000_000L)return;lastSound=now;
  Object id=c(NvgReflection.type("net.minecraft.resources.ResourceLocation"),"fromNamespaceAndPath","fracturepoint",on?"nvg_tactical_on":"nvg_tactical_off");Object event=c(NvgReflection.type("net.minecraft.sounds.SoundEvent"),"createVariableRangeEvent",id);
  Object sound=c(NvgReflection.type("net.minecraft.client.resources.sounds.SimpleSoundInstance"),"forUI",event,1.0f,.45f);c(c(mc,"getSoundManager"),"play",sound);
 }
}
