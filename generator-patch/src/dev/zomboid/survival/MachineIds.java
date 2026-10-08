package dev.zomboid.survival;

import java.util.Set;
import java.util.stream.Stream;
import net.mcreator.doomsdaydecoration.DoomsdayDecorationMod;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

public final class MachineIds {
    public static boolean dispenser(String id) {
        String compact = id.replace("_", "").replace("-", "").toLowerCase(java.util.Locale.ROOT);
        return compact.startsWith("waterdispenser") || compact.startsWith("watercooler");
    }
    public static boolean contains(Set<?> machines, Object id) {
        return machines.contains(id) || id instanceof String name && dispenser(name);
    }
    /** Find actual registered IDs; never add speculative block IDs to the block-entity type. */
    public static Stream<String> registered(Set<String> machines) {
        return Stream.concat(machines.stream(), DoomsdayDecorationMod.BLOCKS_BY_ID.keySet().stream().filter(MachineIds::dispenser)).distinct();
    }
    public static Block dispenserBlock() {
        return DoomsdayDecorationMod.BLOCKS_BY_ID.entrySet().stream().filter(e -> dispenser(e.getKey()))
                .sorted(java.util.Map.Entry.comparingByKey()).map(e -> (Block)((DeferredBlock<?>)e.getValue()).get()).findFirst().orElse(null);
    }
}
