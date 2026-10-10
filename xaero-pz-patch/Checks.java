package xaero.pz;
import java.nio.file.*;import java.util.*;
public class Checks {
 static int cases;
 static void check(boolean condition,String name){if(!condition)throw new AssertionError(name);cases++;}
 static boolean known(int x,int z){var r=Fog.rows.get(Math.floorDiv(z,2));return r!=null&&r.contains(Math.floorDiv(x,2));}
 public static void main(String[] args)throws Exception {
  Path dir=Files.createTempDirectory("pz-fog-checks");Fog.activate(dir.resolve("world1/overworld.bin"));Fog.walk(0,0);
  check(known(0,0)&&known(-1,-1),"negative coordinates");check(!known(15,0),"does not unlock chunk");
  Fog.walk(8,0);check(known(4,0)&&known(8,0),"continuous route");Fog.walk(100,0);check(!known(50,0)&&known(100,0),"teleport gap");
  Fog.flush();int count=Fog.rows.values().stream().mapToInt(Set::size).sum();
  Fog.activate(dir.resolve("world2/overworld.bin"));check(Fog.rows.isEmpty(),"world isolation");Fog.walk(30,30);Fog.flush();
  Fog.activate(dir.resolve("world1/nether.bin"));check(Fog.rows.isEmpty(),"dimension isolation");
  Fog.activate(dir.resolve("world1/overworld.bin"));check(Fog.rows.values().stream().mapToInt(Set::size).sum()==count&&known(100,0),"persisted discovery");
  boolean[][] fog=new boolean[40][140];Fog.mask(-20,-20,120,20,(x,z,r,b)->{for(int j=z;j<b;j++)for(int i=x;i<r;i++){check(!fog[j+20][i+20],"non overlapping mask");fog[j+20][i+20]=true;}});
  for(int z=-20;z<20;z++)for(int x=-20;x<120;x++)check(fog[z+20][x+20]!=known(x,z),"mask exactly matches discovery");
  final int[] rectangles={0};Fog.mask(-1_000_000,-1_000_000,1_000_000,1_000_000,(x,z,r,b)->rectangles[0]++);check(rectangles[0]<500,"zoom out does not scan millions of cells");
  check(Fog.clock(0).equals("06:00")&&Fog.clock(18000).equals("00:00")&&Fog.clock(23999).equals("05:59"),"Minecraft clock");
  check(Fog.season("WINTER").equals("Invierno")&&Fog.season("AUTUMN").equals("Otoño"),"real season names");check(Fog.smaller(130)==104&&Fog.smaller(55)==55,"20 percent smaller");
  Object context=Class.forName("xaero.hud.render.module.ModuleRenderContext").getConstructor(int.class,int.class,double.class).newInstance(960,540,2d);
  Fog.set(context,"w",112);Fog.set(context,"h",112);Fog.layout(context);
  check((int)Fog.field(context,"x")==840&&(int)Fog.field(context,"y")==8,"actual Xaero context aligns to top right");
  check(Boolean.TRUE.equals(Fog.field(context,"flippedHorizontally"))&&Boolean.FALSE.equals(Fog.field(context,"flippedVertically")),"correct HUD orientation");
  int[] rgb={200,210,220};Fog.safeColor(rgb,50,0);check(Arrays.equals(rgb,new int[]{24,28,22}),"safe mode hides unknown pixels");
  rgb=new int[]{200,210,220};Fog.safeColor(rgb,0,0);check(rgb[0]==200,"safe mode preserves discovered pixels");
  System.out.println("Exploration / persistence / exact mask / clock / seasons: "+cases+" assertions passed");
 }
}
