package dev.zomboid.survival;

import net.minecraft.world.inventory.ContainerData;

/** Each full integer uses two unsigned halves because menu data travels as signed shorts. */
public final class GeneratorData implements ContainerData {
    private final MachineEntity machine;
    public GeneratorData(MachineEntity machine) { this.machine = machine; }
    public int getCount() { return 7; }
    public int get(int index) {
        int value = switch (index / 2) {
            case 0 -> machine.zsGetBurn();
            case 1 -> machine.zsGetEnergy();
            case 2 -> machine.zsFuelTotal;
            default -> machine.zsFuelCarry;
        };
        return index % 2 == 0 ? value & 65535 : value >>> 16;
    }
    public void set(int index, int value) { /* The server is authoritative. */ }
}
