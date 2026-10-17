package dev.zomboidmelee.boxing;
import net.minecraft.client.model.geom.*;import net.minecraft.client.model.geom.builders.*;import com.mojang.blaze3d.vertex.PoseStack;import com.mojang.math.Axis;
/** Separate thigh, shin and foot meshes. Does not mutate the renderer or 3D armor models. */
public final class StompMesh21{
 static final ModelPart THIGH,SHIN,FOOT,PANTS_THIGH,PANTS_SHIN,BOOT;
 static{MeshDefinition m=new MeshDefinition();PartDefinition r=m.getRoot();part(r,"thigh",0,16,-2,0,-2,4,7,4,0);part(r,"shin",0,23,-2,0,-2,4,5,4,0);part(r,"foot",0,25,-2,0,-3,4,3,5,0);part(r,"pants_thigh",0,32,-2,0,-2,4,7,4,.15f);part(r,"pants_shin",0,39,-2,0,-2,4,5,4,.15f);ModelPart baked=LayerDefinition.create(m,64,64).bakeRoot();THIGH=baked.getChild("thigh");SHIN=baked.getChild("shin");FOOT=baked.getChild("foot");PANTS_THIGH=baked.getChild("pants_thigh");PANTS_SHIN=baked.getChild("pants_shin");
 MeshDefinition b=new MeshDefinition();CubeListBuilder c=CubeListBuilder.create().texOffs(0,0).addBox(-2.3f,0,-3.5f,4.6f,2.8f,5.8f).texOffs(0,11).addBox(-2,.25f,-4.3f,4,2.45f,1).texOffs(0,17).addBox(-2.45f,2.8f,-4.4f,4.9f,.9f,6.9f);for(int i=0;i<5;i++)c.texOffs(22,17).addBox(-2.2f,3.7f,-4.1f+i*1.3f,4.4f,.20f,.6f);b.getRoot().addOrReplaceChild("boot",c,PartPose.ZERO);BOOT=LayerDefinition.create(b,32,32).bakeRoot().getChild("boot");}
 static void part(PartDefinition r,String n,int u,int v,float x,float y,float z,float w,float h,float d,float inflate){r.addOrReplaceChild(n,CubeListBuilder.create().texOffs(u,v).addBox(x,y,z,w,h,d,new CubeDeformation(inflate)),PartPose.ZERO);}
 static Object consumer(Object b,Object texture){return R.call(b,"getBuffer",R.call(R.type("net.minecraft.client.renderer.RenderType"),"entityCutoutNoCull",texture));}
 static void drawPart(ModelPart m,PoseStack p,Object c,int light){R.call(m,"render",p,c,light,655360);}
 public static void draw(Object renderer,Object stack,Object buffers,int light,Object player,double phase){Foot24.draw(renderer,stack,buffers,light,player,phase);}
}
