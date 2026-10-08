package dev.zomboid.survival;

import net.minecraft.nbt.CompoundTag;

/** Exact furnace-fuel conversion: 64 coal (1600 ticks each) = 36000 active ticks. */
public final class GeneratorFuel {
    public static int consume(MachineEntity machine, int furnaceTicks) {
        long scaled = (long) furnaceTicks * 45 + machine.zsFuelCarry;
        machine.zsFuelCarry = (int) (scaled % 128);
        machine.zsFuelTotal = (int) Math.min(Integer.MAX_VALUE, scaled / 128);
        return machine.zsFuelTotal;
    }

    public static void save(MachineEntity machine, CompoundTag tag) {
        tag.putInt("GeneratorFuelVersion", 1);
        tag.putInt("GeneratorFuelCarry", machine.zsFuelCarry);
        tag.putInt("GeneratorFuelTotal", machine.zsFuelTotal);
        WaterGameplay.save(machine,tag);
    }

    public static void load(MachineEntity machine, CompoundTag tag) {
        machine.zsFuelCarry = Math.max(0, Math.min(127, tag.getInt("GeneratorFuelCarry")));
        if (machine.generator() && !tag.contains("GeneratorFuelVersion")) {
            // Migrate already-lit generators from the old furnace-duration scale once.
            machine.zsSetBurn((int) Math.max(0, (long) machine.zsGetBurn() * 45 / 128));
        }
        machine.zsFuelTotal = Math.max(machine.zsGetBurn(), tag.getInt("GeneratorFuelTotal"));
        WaterGameplay.load(machine,tag);
    }
}
