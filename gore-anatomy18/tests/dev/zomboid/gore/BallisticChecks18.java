package dev.zomboid.gore;
import java.util.*;
public class BallisticChecks18 {
 static int n;static void ok(boolean v,String why){n++;if(!v)throw new AssertionError(why);}
 static double volume(Mesh12.Box b){return (b.x1()-b.x0())*(b.y1()-b.y0())*(b.z1()-b.z0());}
 public static class Bullet{public Object position(){return this;}public float getDamage(Object pos){return 20;}}
 public static void main(String[]args){
  var unit=new Mesh12.Box(0,0,0,1,1,1);var cuts=new ArrayList<Mesh12.Box>();Anatomy14.skinCut(cuts,unit,1,0,1);ok(volume(cuts.getFirst())<.001,"minor injury removes less than 0.1 percent of volume");ok(cuts.getFirst().z1()<.05,"minor mark is shallow");
  cuts.clear();Anatomy14.skinCut(cuts,unit,2,0,1);ok(cuts.getFirst().z1()==1,"perforation passes through both surfaces");ok(volume(cuts.getFirst())<.03,"perforation preserves over 97 percent of surrounding body");
  cuts.clear();Anatomy14.skinCut(cuts,unit,3,0,1);ok(volume(cuts.getFirst())<.15,"internal trauma retains surrounding skin");
  ok(Wounds18.stage(.2,6)==1&&Wounds18.stage(1,6)==1,"tiny positive damage leaves a mark");ok(Wounds18.stage(5,6)==2,"moderate damage perforates");ok(Wounds18.stage(14,6)==3,"high damage opens local trauma");ok(Wounds18.stage(100,6)==7,"extreme cumulative damage still destroys everything");
  ok(Wounds18.stage(0,0)==0&&Wounds18.stage(Double.NaN,0)==0,"invalid or zero damage creates no wound");
  int low=Wounds18.stage(.2,6),high=Wounds18.stage(14,6);ok(low<high,"damage amount changes injury severity independent of hit count");
  Object projectile=new Object();ok(Collateral18.target(projectile,1)==1,"primary target full damage");ok(Collateral18.target(projectile,1)==1,"armor split does not consume another body");ok(Math.abs(Collateral18.target(projectile,2)-.65)<1e-9,"second body gets 65 percent");ok(Math.abs(Collateral18.target(projectile,3)-.4225)<1e-9,"third body gets 42.25 percent");
  Bullet bullet=new Bullet();ok(Collateral18.corpseDamage(bullet,10)==20,"corpse uses native damage, not hardcoded eight");ok(Collateral18.corpseDamage(bullet,10)==20,"same body's corpse retains its existing projectile factor");ok(Math.abs(Collateral18.corpseDamage(bullet,11)-13)<1e-5,"next corpse loses damage");ok(Math.abs(Collateral18.target(bullet,12)-.4225)<1e-9,"living target after two corpses uses remaining damage");
  var before=Layers15.hit(0,0,0,6,1,0,.5);var untouched=Wounds18.corpse(0,0,0,6,1,0,.5,new Object(),new Object(),2);ok(before.equals(untouched),"player melee retains existing damage route");
  Object packet=new Object();var first=Wounds18.corpse(0,0,0,6,15,0,.5,packet,null,.2f);ok(Trauma12.stage(first.details(),6)==1,"corpse low-damage shot cannot strip skin even with high legacy hit count");
  var next=Wounds18.corpse(first.state(),first.details(),first.surfaces(),6,15,0,.5,packet,null,5);ok(Trauma12.stage(next.details(),6)==2,"corpse damage accumulates by actual damage");
  System.out.println("DAMAGE_SCALED_BALLISTICS_CHECKS="+n);
 }
}
