import dev.zomboid.danger.Zones;import dev.zomboid.hordes.ChunkPopulationRules.Pair;
public class Checks{
 static void eq(int a,int b){if(a!=b)throw new AssertionError(a+" != "+b);}
 public static void main(String[] a){
 eq(Zones.multiplier(-550,-618),3);eq(Zones.multiplier(90,-485),2);eq(Zones.multiplier(93,192),1);
 eq(Zones.multiplier(-647,-618),1);eq(Zones.multiplier(-646,-618),3);eq(Zones.multiplier(219,-485),1);
 eq(Zones.missing(0,5,Pair.of(Math.floorDiv(-550,16),Math.floorDiv(-618,16))),15);
 eq(Zones.missing(12,5,Pair.of(Math.floorDiv(-550,16),Math.floorDiv(-618,16))),3);
 eq(Zones.missing(15,5,Pair.of(Math.floorDiv(-550,16),Math.floorDiv(-618,16))),0);
 eq(Zones.missing(0,5,Pair.of(Math.floorDiv(90,16),Math.floorDiv(-485,16))),10);
 eq(Zones.missing(5,5,Pair.of(0,0)),0);
 System.out.println("PASS: 11 real helper cases, zone boundaries, deficits and no duplicate population");
 }
}
