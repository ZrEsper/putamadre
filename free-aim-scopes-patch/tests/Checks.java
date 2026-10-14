import dev.zomboid.isotacz.FreeAim;
import dev.zomboid.tacz.ScopeZoom;
import dev.zomboid.tacz.ShotPose;
import dev.zomboid.taczfeedback.GlareRules;
public final class Checks {
 static int count;
 static void check(boolean b,String s){count++;if(!b)throw new AssertionError(s);}
 static boolean near(double a,double b){return Math.abs(a-b)<.0001;}
 public static void main(String[] args){
  check(near(ScopeZoom.magnification(1),1),"iron sights unchanged");
  check(near(ScopeZoom.magnification(2),2.7),"2x optic boosted");
  check(near(ScopeZoom.magnification(8),10.8),"8x optic boosted");
  check(ScopeZoom.magnification(Double.NaN)==1,"invalid optic safe");
  check(ScopeZoom.magnification(100)==32,"extreme optic bounded");
  check(near(ScopeZoom.effective(8,0),1),"hip fire no zoom");
  check(near(ScopeZoom.effective(8,.5),5.9),"ADS transition continuous");
  check(near(ScopeZoom.fov(90,1,1),90),"unmagnified field of view");
  check(near(ScopeZoom.fov(90,2,1),53.130102354),"optical tangent projection");
  check(ScopeZoom.fov(90,10.8,1)<ScopeZoom.fov(90,2.7,1),"larger scope zooms further");
  check(near(ScopeZoom.fov(90,8,0),90),"no ADS no FOV change");
  check(near(FreeAim.recoil(0,8,0),8),"recoil not pinned to former 4 degree cap");
  check(near(FreeAim.recoil(8,0,.1),8*Math.exp(-.7)),"recoil naturally recovers");
  double recoil=0;for(int i=0;i<60;i++)recoil=FreeAim.recoil(recoil,.7,1./60);
  check(recoil>4&&Double.isFinite(recoil),"held burst retains recoil without old cap");
  for(int i=0;i<120;i++)recoil=FreeAim.recoil(recoil,0,1./60);
  check(recoil<.0001,"release recovers rather than abrupt snap");
  check(FreeAim.recoil(Double.NaN,Double.NaN,Double.NaN)==0,"invalid recoil safe");
  float[] pose=ShotPose.restored(new float[]{10,20,35,170},37,175);
  check(near(pose[0],12)&&near(pose[1],25),"parallax restore preserves shot recoil");
  pose=ShotPose.restored(new float[]{10,20,35,179},35,-179);
  check(near(pose[1],22),"yaw wrap preserves small recoil");
  check(ShotPose.restored(null,0,0)==null,"inactive pose remains inactive");
  check(GlareRules.visible(1,1,true,true,true,true),"near sniper visible");
  check(GlareRules.visible(Math.cos(Math.toRadians(30)),10,true,true,true,true),"near wider observation cone");
  check(!GlareRules.visible(Math.cos(Math.toRadians(30)),70,true,true,true,true),"distant scope direction retained");
  check(!GlareRules.visible(1,10,true,true,true,false),"no glare through walls");
  check(!GlareRules.visible(1,10,false,true,true,true),"not aiming no glare");
  check(!GlareRules.visible(1,10,true,false,true,true),"non sniper excluded");
  check(!GlareRules.visible(1,10,true,true,false,true),"no scope excluded");
  check(!GlareRules.visible(-1,10,true,true,true,true),"back of sniper excluded");
  check(!GlareRules.visible(1,161,true,true,true,true),"range retained");
  check(!GlareRules.visible(Double.NaN,10,true,true,true,true),"invalid direction excluded");
  check(GlareRules.alpha(1,10)==255,"head on fully luminous");
  check(GlareRules.alpha(Math.cos(Math.toRadians(34)),10)>=216,"edge remains bright");
  check(GlareRules.size(10)>3*(.09+10*.0035),"near glare much larger");
  check(GlareRules.size(160)<=1.5,"far sprite bounded");
  System.out.println("Passed "+count+" behavioral checks (math and pose logic; no Minecraft runtime).");
 }
}
