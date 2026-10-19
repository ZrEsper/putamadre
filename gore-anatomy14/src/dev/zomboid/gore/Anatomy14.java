package dev.zomboid.gore;
import java.util.*;
/** Anatomy stays in the same local frame as the damaged model part. */
public final class Anatomy14 {
 public record Piece(String name,Mesh12.Box box,int material){}
 static void add(List<Piece>p,String name,int mat,float a,float b,float c,float d,float e,float f){if(Set.of("proximal_muscle","distal_muscle","brain_lobe","left_lung","right_lung","heart","liver","stomach").contains(name)){float midX=(a+d)/2,midZ=(c+f)/2;float[]profile=name.equals("heart")?new float[]{.72f,1,.9f,.65f,.3f}:new float[]{.55f,.86f,1,.86f,.55f};for(int i=0;i<5;i++){float k=profile[i],lo=b+(e-b)*i/5,hi=Math.min(e,b+(e-b)*(i+1)/5+.0001f);p.add(new Piece(name,new Mesh12.Box(midX-(d-a)*k/2,lo,midZ-(f-c)*k/2,midX+(d-a)*k/2,hi,midZ+(f-c)*k/2),mat));}}else p.add(new Piece(name,new Mesh12.Box(a,b,c,d,e,f),mat));}
 public static List<Piece> anatomy(int region){List<Piece>p=new ArrayList<>();
 if(region==0){
 add(p,"occipital",2,.24f,.08f,.68f,.76f,.85f,.88f);add(p,"cranial_roof",2,.16f,.06f,.18f,.84f,.18f,.84f);
 for(float x:new float[]{.13f,.77f}){add(p,"temporal",2,x,.15f,.2f,x+.1f,.76f,.82f);add(p,"jaw_ramus",2,x,.67f,.18f,x+.11f,.98f,.74f);}
 add(p,"jaw_chin",2,.15f,.86f,.12f,.85f,.98f,.28f);add(p,"palate",2,.22f,.69f,.18f,.78f,.77f,.72f);add(p,"cervical",2,.41f,.75f,.6f,.59f,1,.8f);
 for(float x:new float[]{.26f,.53f}){add(p,"brain_lobe",3,x,.19f,.32f,x+.22f,.58f,.72f);add(p,"optic_stalk",1,x+.06f,.42f,.21f,x+.12f,.52f,.7f);add(p,"orbit",2,x-.02f,.33f,.16f,x+.25f,.4f,.35f);add(p,"eye_socket",4,x+.02f,.4f,.16f,x+.19f,.54f,.29f);}
 for(int i=0;i<6;i++)add(p,"tooth",5,.25f+i*.08f,.76f,.14f,.30f+i*.08f,.88f,.22f);
 }else if(region==6){
 add(p,"spine",2,.45f,0,.65f,.55f,1,.86f);add(p,"sternum",2,.46f,.13f,.16f,.54f,.62f,.3f);add(p,"clavicle",2,0,.055f,.22f,1,.14f,.73f);
 for(int i=0;i<6;i++){float y=.18f+i*.074f;for(boolean left:new boolean[]{true,false}){float x=left?.08f:.84f;add(p,"rib_posterior",2,left?.08f:.5f,y,.67f,left?.5f:.92f,y+.034f,.76f);add(p,"rib_lateral",2,x,y,.48f,x+.08f,y+.034f,.74f);add(p,"rib_curve",2,left?.11f:.81f,y,.30f,left?.19f:.89f,y+.034f,.53f);add(p,"rib_curve",2,left?.16f:.76f,y,.23f,left?.24f:.84f,y+.034f,.36f);add(p,"rib_anterior",2,left?.18f:.5f,y,.22f,left?.5f:.82f,y+.034f,.3f);}}
 add(p,"pelvis_back",2,.15f,.86f,.58f,.85f,1,.84f);add(p,"pelvis_left",2,.12f,.79f,.25f,.28f,1,.76f);add(p,"pelvis_right",2,.72f,.79f,.25f,.88f,1,.76f);add(p,"pubic_arch",2,.24f,.95f,.24f,.76f,1,.4f);
 add(p,"mediastinum",1,.43f,.2f,.28f,.57f,.69f,.73f);add(p,"heart",1,.38f,.34f,.22f,.57f,.53f,.54f);
 add(p,"left_lung",3,.17f,.21f,.3f,.45f,.56f,.67f);add(p,"right_lung",3,.55f,.21f,.3f,.83f,.56f,.67f);
 add(p,"diaphragm",1,.16f,.55f,.25f,.84f,.6f,.71f);add(p,"liver",1,.22f,.59f,.27f,.69f,.69f,.65f);add(p,"stomach",3,.64f,.61f,.29f,.83f,.75f,.64f);
 add(p,"mesentery",1,.36f,.64f,.48f,.65f,.93f,.73f);
 for(int i=0;i<4;i++){float y=.71f+i*.055f;add(p,"bowel_loop",3,.24f,y,.27f,.77f,y+.04f,.5f);add(p,"bowel_join",3,i%2==0?.72f:.24f,y,.3f,i%2==0?.77f:.29f,y+.09f,.5f);}
 }else{
 boolean arm=region==2||region==3;
 add(p,"proximal_joint",2,0,0,.23f,1,.12f,.77f);add(p,"upper_bone",2,.41f,.08f,.43f,.59f,.51f,.62f);add(p,"joint",2,.34f,.46f,.34f,.66f,.57f,.7f);
 for(float x:new float[]{.32f,.55f})add(p,arm?"radius_ulna":"tibia_fibula",2,x,.53f,.43f,x+.13f,.93f,.59f);
 add(p,"distal_joint",2,.26f,.88f,.26f,.74f,1,.75f);
 add(p,"tendon",1,.4f,0,.26f,.6f,1,.47f);
 add(p,"proximal_muscle",1,.23f,.12f,.22f,.77f,.43f,.5f);add(p,"distal_muscle",1,.27f,.57f,.22f,.73f,.85f,.47f);
 if(arm)for(int i=0;i<4;i++)add(p,"metacarpal",2,.28f+i*.12f,.93f,.21f,.35f+i*.12f,1,.59f);
 }
 add(p,"posterior_fascia",1,.45f,0,.72f,.55f,1,1);return List.copyOf(p);}
 public static List<Piece> damaged(int region,int stage){List<Piece>out=new ArrayList<>();int level=region==6?Math.max(stage&7,(stage>>5)&7):stage&7;for(Piece p:anatomy(region)){Mesh12.Box b=p.box();if(region==0&&level>=6&&p.name().equals("cranial_roof"))continue;if(region==0&&level>=4&&p.name().equals("temporal")&&b.x0()<.5)continue;if(region>=2&&region<=5&&level>=4&&p.name().equals("upper_bone")){out.add(new Piece("fracture_proximal",new Mesh12.Box(b.x0(),b.y0(),b.z0(),b.x1(),.24f,b.z1()),2));out.add(new Piece("fracture_distal",new Mesh12.Box(b.x0(),.29f,b.z0(),b.x1(),b.y1(),b.z1()),2));continue;}if(region==6&&level>=5&&p.name().equals("rib_anterior")&&b.x0()<.5)continue;out.add(p);}return out;}
 static Mesh12.Box scale(Mesh12.Box p,Mesh12.Box b){float w=b.x1()-b.x0(),h=b.y1()-b.y0(),d=b.z1()-b.z0();return new Mesh12.Box(b.x0()+p.x0()*w,b.y0()+p.y0()*h,b.z0()+p.z0()*d,b.x0()+p.x1()*w,b.y0()+p.y1()*h,b.z0()+p.z1()*d);}
 public static List<Mesh12.Quad> build(List<Mesh12.Cube> cubes,int region,int stage){if(cubes.isEmpty())return List.of();List<Mesh12.Quad>out=new ArrayList<>();float x=Float.MAX_VALUE,y=x,z=x,X=-x,Y=-x,Z=-x;for(var c:cubes){var b=c.bounds();x=Math.min(x,b.x0());y=Math.min(y,b.y0());z=Math.min(z,b.z0());X=Math.max(X,b.x1());Y=Math.max(Y,b.y1());Z=Math.max(Z,b.z1());}Mesh12.Box bounds=new Mesh12.Box(x,y,z,X,Y,Z);List<Mesh12.Box>cuts=new ArrayList<>();if(region==6){int l=stage&31,r=(stage>>5)&31;if(l!=0)Mesh13.cut(cuts,bounds,6,l);if(r!=0)Mesh13.cut(cuts,bounds,7,r);}else Mesh13.cut(cuts,bounds,region,stage);
 if(Model14.skin())for(var cube:cubes){List<Mesh12.Box>remain=List.of(cube.bounds());for(var cut:cuts){List<Mesh12.Box>next=new ArrayList<>();for(var box:remain)next.addAll(Mesh12.subtract(box,cut));remain=next;}for(var box:remain)Mesh12.box(out,box,0,cube);}
 // All anatomical pieces form one connected local structure; intact skin naturally occludes it.
 if(Model14.internal())for(var part:damaged(region,stage))Mesh12.box(out,scale(part.box(),bounds),part.material(),null);
 return List.copyOf(out);}
}
