package dev.zomboid.survival;

import net.minecraft.world.inventory.ContainerData;

/** Each full integer uses two unsigned halves because menu data travels as signed shorts. */
public final class GeneratorData implements ContainerData {
    private final MachineEntity machine;
    public GeneratorData(MachineEntity machine) { this.machine = machine; }
    public int getCount() { return 9; }
    public int get(int index) {
        if (index >= 7) return index == 7 ? PowerPolicy.capacity(machine) & 65535 : PowerPolicy.capacity(machine) >>> 16;
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
