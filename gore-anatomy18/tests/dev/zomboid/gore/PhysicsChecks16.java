package dev.zomboid.gore;
public class PhysicsChecks16 {
 static int n;static void ok(boolean v,String why){n++;if(!v)throw new AssertionError(why);}
 static double clamp(double v,double lo,double hi){return Math.max(lo,Math.min(hi,v));}
 public static void main(String[]args){
 // Swept floor/slab and wall fixture. The runtime supplies Minecraft VoxelShape collisions.
 for(double floor:new double[]{0,.5,1}){
  var b=new OrganMotion16.Body(0,3,0,.06);b.vx=.16;b.vz=.08;b.spin=.3;
  OrganMotion16.Collision resolver=(x,y,z,r,dx,dy,dz)->new double[]{Math.min(dx,1-r-x),Math.max(dy,floor+r-y),dz};
  boolean bounced=false;double before=0;
  for(int tick=0;tick<250;tick++){before=b.vy;ok(OrganMotion16.step(b,resolver),"piece survives initial physics");if(before<-.1&&b.vy>0)bounced=true;ok(b.y>=floor+b.radius-1e-8,"does not fall through floor or slab");ok(b.x<=1-b.radius+1e-8,"cannot cross wall");}
  ok(bounced,"low restitution rebound");ok(b.sleeping,"settles and sleeps");ok(b.vx==0&&b.vz==0&&b.vy==0,"sleep has no residual motion");
  while(b.age<600)ok(OrganMotion16.step(b,resolver),"retained until expiry");ok(!OrganMotion16.step(b,resolver),"expires at 30 seconds");
 }
 var air=new OrganMotion16.Body(0,10,0,.05);air.vy=-.6;OrganMotion16.step(air,(x,y,z,r,dx,dy,dz)->new double[]{dx,dy,dz});ok(air.y<10,"gravity operates");ok(air.py==10,"previous position retained for interpolation");
 ok(!OrganMotion16.step(new OrganMotion16.Body(0,0,0,.05),(x,y,z,r,dx,dy,dz)->null),"unloaded collision space removes piece");
 ok(!OrganMotion16.step(new OrganMotion16.Body(0,0,0,.05),(x,y,z,r,dx,dy,dz)->new double[]{Double.NaN,0,0}),"reject invalid physics displacement");
 System.out.println("ORGAN_PHYSICS_CHECKS="+n);
 }
}
