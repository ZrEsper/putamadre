package dev.zomboid.gore;
import java.util.*;
public class SurfaceChecks19 {
 static int n;static void ok(boolean v,String why){n++;if(!v)throw new AssertionError(why);}
 static Mesh12.Cube cube(){float[][][]p={{{0,0,0},{0,0,1},{0,1,1},{0,1,0}},{{1,0,1},{1,0,0},{1,1,0},{1,1,1}},{{0,0,1},{0,0,0},{1,0,0},{1,0,1}},{{0,1,0},{0,1,1},{1,1,1},{1,1,0}},{{1,0,0},{0,0,0},{0,1,0},{1,1,0}},{{0,0,1},{1,0,1},{1,1,1},{0,1,1}}};float[][]ns={{-1,0,0},{1,0,0},{0,-1,0},{0,1,0},{0,0,-1},{0,0,1}};List<Mesh12.Face>faces=new ArrayList<>();for(int i=0;i<6;i++){float[]u=new float[4],v=new float[4];for(int j=0;j<4;j++){u[j]=.2f+.2f*(i<2?p[i][j][2]:p[i][j][0]);v[j]=.6f+.1f*(i==2||i==3?p[i][j][2]:p[i][j][1]);}faces.add(new Mesh12.Face(p[i],u,v,ns[i][0],ns[i][1],ns[i][2]));}return new Mesh12.Cube(new Mesh12.Box(-100,-100,-100,100,100,100),faces);}
 static double triangle(Mesh12.Vertex a,Mesh12.Vertex b,Mesh12.Vertex c){double x=b.x()-a.x(),y=b.y()-a.y(),z=b.z()-a.z(),X=c.x()-a.x(),Y=c.y()-a.y(),Z=c.z()-a.z();return Math.sqrt(Math.pow(y*Z-z*Y,2)+Math.pow(z*X-x*Z,2)+Math.pow(x*Y-y*X,2))/2*256;}
 static double area(List<Mesh12.Quad>qs){double result=0;for(var q:qs){var v=q.vertices();result+=triangle(v[0],v[1],v[2])+triangle(v[0],v[2],v[3]);}return result;}
 public static void main(String[]args){var c=cube();var light=Surface19.build(List.of(c),2,1).stream().filter(q->q.material()==0).toList();ok(light.size()==6,"minor hit preserves all original skin faces");ok(Math.abs(area(light)-6)<1e-6,"minor hit removes no skin area");
  for(int i=0;i<6;i++)for(int j=0;j<4;j++){var a=c.faces().get(i);var b=light.get(i).vertices()[j];ok(b.u()==a.u()[j]&&b.v()==a.v()[j]&&b.x()*16==a.xyz()[j][0]&&b.y()*16==a.xyz()[j][1]&&b.z()*16==a.xyz()[j][2],"exact original vertex and UV preserved");}
  var perforated=Surface19.build(List.of(c),2,2).stream().filter(q->q.material()==0).toList();ok(Math.abs(area(perforated)-(6-2*.13*.15*.68))<.00001,"only irregular entry and exit apertures removed");
  for(var q:perforated){int axis=q.nx()!=0?0:q.ny()!=0?1:2;float plane=axis==0?q.vertices()[0].x()*16:axis==1?q.vertices()[0].y()*16:q.vertices()[0].z()*16;ok(plane==0||plane==1,"no invented interior skin walls");for(var v:q.vertices()){ok(v.u()>=.19999f&&v.u()<=.40001f&&v.v()>=.59999f&&v.v()<=.70001f,"no sampling outside original texture island");}}
  var full=Surface19.build(List.of(c),2,7);ok(full.isEmpty(),"complete destruction still removes all geometry");
  List<Mesh12.Quad>union=new ArrayList<>();Surface19.internals(union,List.of(new Anatomy14.Piece("a",new Mesh12.Box(0,0,0,1,1,1),2),new Anatomy14.Piece("b",new Mesh12.Box(1,0,0,2,1,1),2)));ok(Math.abs(area(union)-10)<.0002,"shared internal faces removed");
  union.clear();Surface19.internals(union,List.of(new Anatomy14.Piece("a",new Mesh12.Box(0,0,0,1,1,1),2),new Anatomy14.Piece("b",new Mesh12.Box(0,0,0,1,1,1),2)));ok(Math.abs(area(union)-6)<.0002,"coplanar duplicate bones rendered once");
  ok(Surface19.destroyed(0,Trauma12.withStage(0,0,7)),"destroyed head and clothing are hidden");int half=Trauma12.withStage(0,6,7);ok(!Surface19.destroyed(6,half),"one destroyed torso half cannot hide the other half");ok(Surface19.destroyed(6,Trauma12.withStage(half,7,7)),"completely destroyed torso hides decorative layers");
  ok(!Surface19.readable(new Object()),"unsupported model keeps native rendering");System.out.println("UV_SURFACE_AND_OVERLAP_CHECKS="+n);
 }
}
