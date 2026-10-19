package dev.zomboid.gore;
import java.util.*;import java.nio.file.*;
public class LayersChecks15{
 static int checks;static void ok(boolean value,String message){checks++;if(!value)throw new AssertionError(message);}
 static double volume(List<Anatomy14.Piece>p){double v=0;for(var q:p){var b=q.box();v+=(b.x1()-b.x0())*(b.y1()-b.y0())*(b.z1()-b.z0());}return v;}
 public static void main(String[]args)throws Exception{
 StringBuilder json=new StringBuilder("[");boolean comma=false;
 for(int region:new int[]{0,2,3,4,5,6}){
  double previous=Double.MAX_VALUE;
  for(int stage=0;stage<=7;stage++){
   int packed=region==6?stage|(stage<<5):stage;
   var pieces=Anatomy14.damaged(region,packed);
   var mesh=Anatomy14.build(List.of(new Mesh12.Cube(Mesh12.canonical(region),List.of())),region,packed);
   ok(mesh.size()<1800,"geometry budget");
   ok(mesh.stream().flatMap(q->Arrays.stream(q.vertices())).allMatch(v->Float.isFinite(v.x())&&Float.isFinite(v.y())&&Float.isFinite(v.z())),"finite geometry");
   if(stage==4)ok(pieces.stream().allMatch(Anatomy14::bone),"skeleton revealed only after muscle and organ removal");
   if(stage>=4){double v=volume(pieces);ok(v<previous,"bone volume strictly decreases through complete destruction");previous=v;}
   if(stage==7){ok(pieces.isEmpty(),"every anatomical piece destroyed");ok(mesh.isEmpty(),"no skin or permanent anatomy at terminal damage");}
   if(stage==2&&region==0)ok(pieces.stream().anyMatch(p->p.name().equals("eyeball")&&p.box().z0()<0),"eye displaced out of socket");
   if(stage==3&&region==6)ok(pieces.stream().anyMatch(p->p.name().equals("bowel_loop")&&p.box().z0()==.1f),"exposed bowel protrudes");
   for(var p:pieces){if(comma)json.append(',');comma=true;var b=p.box();json.append(String.format(Locale.ROOT,"{\"stage\":%d,\"region\":%d,\"name\":\"%s\",\"material\":%d,\"box\":[%f,%f,%f,%f,%f,%f]}",stage,region,p.name(),p.material(),b.x0(),b.y0(),b.z0(),b.x1(),b.y1(),b.z1()));}
  }
 }
 for(int region:new int[]{0,2,3,4,5,6,7}){int details=0,stage=0;for(int hit=1;hit<=30;hit++){var result=Damage13.hit(0,details,0,region,hit,0,.5);details=result.details();int next=Trauma12.stage(details,region);ok(next>=stage,"damage never reverses");stage=next;}ok(stage==7,"all hit regions reach total destruction");}
 var half=Anatomy14.damaged(6,7);ok(half.stream().allMatch(p->p.box().x1()<=.5f),"destroying one torso half preserves only other half");
 ok(Artery14.arterial(0,.78,1),"neck arterial pressure retained");
 Files.writeString(Path.of(args[0]),json.append(']').toString());System.out.println("LAYERED_DESTRUCTION_CHECKS="+checks);
 }
}
