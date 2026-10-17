package dev.zomboidmelee.boxing;
import java.util.*;import net.minecraft.client.model.HumanoidModel;import net.minecraft.client.model.geom.ModelPart;
/** Keyframed feet-first glide. The physical low pose is not rendered as swimming or sitting. */
public final class SlidePose28 {
 static final Map<Object,float[]> BASE=new WeakHashMap<>();
 static ModelPart[] parts(HumanoidModel<?> m){return new ModelPart[]{m.body,m.head,m.rightArm,m.leftArm,m.rightLeg,m.leftLeg,m.hat};}
 public static void restore(Object model){float[] b=BASE.remove(model);if(b==null||!(model instanceof HumanoidModel<?>m))return;ModelPart[]parts=parts(m);for(int i=0;i<parts.length;i++){ModelPart p=parts[i];int k=i*6;if(p==null||Float.isNaN(b[k]))continue;p.x=b[k];p.y=b[k+1];p.z=b[k+2];p.xRot=b[k+3];p.yRot=b[k+4];p.zRot=b[k+5];}}
 static void save(HumanoidModel<?>m){restore(m);float[] b=new float[42];ModelPart[]parts=parts(m);for(int i=0;i<parts.length;i++){ModelPart p=parts[i];int k=i*6;if(p==null){b[k]=Float.NaN;continue;}b[k]=p.x;b[k+1]=p.y;b[k+2]=p.z;b[k+3]=p.xRot;b[k+4]=p.yRot;b[k+5]=p.zRot;}BASE.put(m,b);}
 static float mix(float a,double b,double w){return (float)(a+(b-a)*w);}static void place(ModelPart p,double y,double z,double pitch,double roll,double w){if(p==null)return;p.y=mix(p.y,y,w);p.z=mix(p.z,z,w);p.xRot=mix(p.xRot,pitch,w);p.zRot=mix(p.zRot,roll,w);}
 public static boolean apply(Object model,Object player,double weight){if(!(model instanceof HumanoidModel<?>m)||weight<=0)return false;save(m);double w=Motion20.smooth(Math.min(1,weight));place(m.body,18,1,-1.22,-.10,w);place(m.head,18.5,2,.12,.05,w);place(m.rightLeg,22.1,-10.3,-1.47,.08,w);place(m.leftLeg,22.1,-10.3,-1.15,-.18,w);if(Boxing16.empty(player)){place(m.rightArm,18.5,1,-.55,.30,w);place(m.leftArm,18.5,1,-.88,-.20,w);}else{if(m.rightArm!=null){m.rightArm.y=mix(m.rightArm.y,18.5,w);m.rightArm.z=mix(m.rightArm.z,1,w);}if(m.leftArm!=null){m.leftArm.y=mix(m.leftArm.y,18.5,w);m.leftArm.z=mix(m.leftArm.z,1,w);}}if(m.hat!=null&&m.head!=null)m.hat.copyFrom(m.head);MoveClient25.layers(model);return true;}
}
