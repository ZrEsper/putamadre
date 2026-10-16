package dev.zomboid.survival;
public final class PowerPolicy {
 static int tier(MachineEntity m){String id=m.id().replace("_","").toLowerCase(java.util.Locale.ROOT);return id.contains("trailergenerator")?2:id.equals("fixedgenerator")||id.equals("stationarygenerator")?1:0;}
 public static int capacity(MachineEntity m){return m.generator()?switch(tier(m)){case 2->768000;case 1->512000;default->128000;}:64000;}
 public static int output(MachineEntity m){return switch(tier(m)){case 2->240;case 1->160;default->80;};}
 public static int demand(MachineEntity m){if(MachineIds.dispenser(m.id()))return m.zsRawWater+m.zsCleanWater>0?1:0;return m.freezer()?2:m.oven()?30:1;}
 public static int fuel(MachineEntity m,int ticks){return (int)Math.min(Integer.MAX_VALUE,(long)ticks*(tier(m)+1));}
}
