package dev.zomboid.gore;
import java.util.*;
public class ReferenceChecks17{
 static int n;static void ok(boolean b,String why){n++;if(!b)throw new AssertionError(why);}
 static boolean solid(List<Anatomy14.Piece>ps,double x,double y,double z){return ps.stream().anyMatch(p->{var b=p.box();return x>b.x0()&&x<b.x1()&&y>b.y0()&&y<b.y1()&&z>b.z0()&&z<b.z1();});}
 public static void main(String[]args){
  var skull=Anatomy14.damaged(0,4);
  ok(solid(skull,.3,.25,.12)&&solid(skull,.7,.25,.12),"broad filled cuboid forehead");
  ok(!solid(skull,.30,.47,.12)&&!solid(skull,.70,.47,.12),"two actual rectangular eye openings");
  ok(solid(skull,.5,.47,.12),"nose bridge between eye openings");
  ok(!solid(skull,.5,.79,.12),"jaw opening is visibly separate");
  ok(solid(skull,.5,.90,.12),"broad lower jaw remains");
  var ribs=Anatomy14.anatomy(6).stream().filter(p->p.name().startsWith("rib_anterior")).toList();
  ok(ribs.size()==6,"three ribs per side, not a fine grille");
  ok(ribs.stream().allMatch(p->p.box().y1()-p.box().y0()>=.089f),"thick rectangular ribs");
  ok(ribs.stream().map(p->p.box().y0()).distinct().count()==3,"three separated thoracic bands");
  for(int region:new int[]{2,3,4,5}){var bones=Anatomy14.damaged(region,4);ok(bones.stream().filter(p->!p.name().equals("shoulder_link")).noneMatch(p->p.box().x1()-p.box().x0()>.5f),"no oversized limb end caps");}
  System.out.println("REFERENCE_SILHOUETTE_CHECKS="+n);
 }
}
