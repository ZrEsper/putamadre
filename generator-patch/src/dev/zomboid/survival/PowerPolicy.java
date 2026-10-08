package dev.zomboid.survival;

/** Device balance is shared by ticking, capabilities, persistence and the GUI. */
public final class PowerPolicy {
    public static int capacity(MachineEntity machine) {
        String id = machine.id().replace("_", "").toLowerCase(java.util.Locale.ROOT);
        if (id.contains("trailergenerator")) return 384000;
        if (id.equals("fixedgenerator") || id.equals("stationarygenerator")) return 256000;
        return 64000;
    }
    public static int demand(MachineEntity machine) {
        if (MachineIds.dispenser(machine.id())) return machine.zsRawWater + machine.zsCleanWater > 0 ? 1 : 0;
        return machine.freezer() ? 2 : machine.oven() ? 30 : 1;
    }
}
