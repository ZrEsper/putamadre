package dev.zomboidmelee.boxing;
import java.util.*;import net.minecraft.client.model.*;import net.minecraft.client.model.geom.*;import net.minecraft.client.model.geom.builders.*;import com.mojang.blaze3d.vertex.PoseStack;import com.mojang.math.Axis;
/** Elbow articulation without replacing vanilla model fields or changing armor model types. */
public final class Arms23{
 public record Joint(double upper,double elbow,double yaw,double roll){}
 record Binding(int side,boolean sleeve,boolean slim,double elbow){}
 static final Map<Object,Binding> BINDINGS=new WeakHashMap<>();
 static final ModelPart[][][] UPPER=new ModelPart[2][2][2],LOWER=new ModelPart[2][2][2];
 static{for(int side=0;side<2;side++)for(int sleeve=0;sleeve<2;sleeve++)for(int slim=0;slim<2;slim++){int u=side==0?40:sleeve==0?32:48,v=side==0?(sleeve==0?16:32):48;float x=side==0?(slim==1?-2:-3):-1,width=slim==1?3:4;MeshDefinition mesh=new MeshDefinition();PartDefinition root=mesh.getRoot();CubeDeformation inflate=new CubeDeformation(sleeve==1?.25f:0);root.addOrReplaceChild("upper",CubeListBuilder.create().texOffs(u,v).addBox(x,-2,-2,width,6,4,inflate),PartPose.ZERO);root.addOrReplaceChild("lower",CubeListBuilder.create().texOffs(u,v+6).addBox(x,0,-2,width,6,4,inflate),PartPose.ZERO);ModelPart m=LayerDefinition.create(mesh,64,64).bakeRoot();UPPER[side][sleeve][slim]=m.getChild("upper");LOWER[side][sleeve][slim]=m.getChild("lower");}}
 public static Joint joint(int kind,int side,int combo,double phase){int sign=side==0?1:-1;double a=Motion20.strike(kind,phase),hit=Math.max(0,a),load=Math.max(0,-a);boolean active=side==Math.floorMod(combo,2);double upper=side==0?-1.35:-1.50,elbow=side==0?-1.60:-1.52,yaw=-sign*.14,roll=sign*.10;
 if(kind==Motion20.LIGHT){if(active){upper-=.76*hit;upper+=.16*load;elbow+=1.80*hit;elbow-=.10*load;yaw+=sign*.10*hit;roll+=sign*.035*hit;}else{upper-=.10*hit;elbow-=.12*hit;}}
 else if(kind==Motion20.HEAVY){if(active){upper-=.64*hit;upper+=.24*load;elbow+=1.25*hit;elbow-=.12*load;yaw+=sign*(.68*hit-.40*load);roll+=sign*.22*hit;}else{upper-=.10*hit;elbow-=.15*hit;}}
 else if(kind==Motion20.SHOVE){upper=-.90-.65*hit;elbow=-1.75+1.65*hit;}
 else if(kind==Motion20.INTERACT){double reach=Motion20.track(phase,new double[]{0,.17,.39,.58,1},new double[]{0,.2,1,.82,0});if(side==0){upper-=.70*reach;elbow+=1.65*reach;yaw+=.18*reach;}}
 else if(kind==Motion20.SEARCH){double wave=(1+Math.sin(phase*Math.PI*2+side*Math.PI))*.5;upper=-1.1-.25*wave;elbow=-.75+.55*wave;}
 else if(kind==Motion20.STOMP){double lift=StompMotion21.lift(phase);upper+=.12*lift;elbow+=.18*lift;roll+=sign*.1*lift;}
 return new Joint(upper,elbow,yaw,roll);}
 static boolean slim(Object player){try{return "slim".equalsIgnoreCase(R.call(R.call(player,"getSkin"),"model").toString());}catch(IllegalStateException absent){return false;}}
 static void bind(ModelPart part,int side,boolean sleeve,boolean slim,double elbow){if(part!=null)BINDINGS.put(part,new Binding(side,sleeve,slim,elbow));}
 public static void clear(Object model){if(model instanceof HumanoidModel<?>m){BINDINGS.remove(m.rightArm);BINDINGS.remove(m.leftArm);if(model instanceof PlayerModel<?>p){BINDINGS.remove(p.rightSleeve);BINDINGS.remove(p.leftSleeve);}}}
 public static void body(Object model,Object player,int kind,int combo,double phase){if(!(model instanceof PlayerModel<?>m))return;boolean slim=slim(player);for(int side=0;side<2;side++){if(side==1&&!Hands21.offEmpty(player))continue;Joint j=joint(kind,side,combo,phase);double aim=(kind==Motion20.LIGHT||kind==Motion20.HEAVY)&&side==Math.floorMod(combo,2)?Math.max(0,Motion20.strike(kind,phase)):0;float pitch=m.head==null?0:m.head.xRot,yaw=m.head==null?0:m.head.yRot;Render20.pose(model,side,(float)(j.upper+Math.max(-.9,Math.min(.9,pitch))*.75*aim),(float)(j.yaw+Math.max(-.8,Math.min(.8,yaw))*.75*aim),(float)j.roll);bind(side==0?m.rightArm:m.leftArm,side,false,slim,j.elbow);bind(side==0?m.rightSleeve:m.leftSleeve,side,true,slim,j.elbow);}}
 static void segment(ModelPart m,PoseStack p,Object buffer,int light,int overlay,int color){R.call(m,"render",p,buffer,light,overlay,color);}
 static void chain(PoseStack p,Object buffer,int light,int overlay,int color,int side,boolean sleeve,boolean slim,double elbow){int l=sleeve?1:0,s=slim?1:0;segment(UPPER[side][l][s],p,buffer,light,overlay,color);p.translate(0,.25,0);p.mulPose(Axis.XP.rotationDegrees((float)Math.toDegrees(elbow)));segment(LOWER[side][l][s],p,buffer,light,overlay,color);}
 /** Called by ModelPart's five-argument render overload, before its original transform. */
 public static boolean render(Object part,Object pose,Object buffer,int light,int overlay,int color){Binding b=BINDINGS.get(part);if(b==null)return false;ModelPart original=(ModelPart)part;if(!original.visible)return true;PoseStack p=(PoseStack)pose;p.pushPose();try{original.translateAndRotate(p);if(!original.skipDraw){p.pushPose();try{chain(p,buffer,light,overlay,color,b.side,b.sleeve,b.slim,b.elbow);}finally{p.popPose();}}for(Object child:((Map<?,?>)R.get(original,"children")).values())R.call(child,"render",p,buffer,light,overlay,color);}finally{p.popPose();}return true;}
 public static void first(Object renderer,Object stack,Object buffers,int light,Object player,int side,int kind,int combo,double phase){First24.draw(stack,buffers,light,player,side,kind,combo,phase);}
}
