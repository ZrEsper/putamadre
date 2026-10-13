package com.locknar.taczammocheck.tactical;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class DoorCompat {
    public static final String DOOMSDAY_DECORATION_NAMESPACE = "doomsday_decoration";

    private DoorCompat() {
    }

    public static boolean isSupportedDoor(BlockState state) {
        if (state == null || !(state.getBlock() instanceof DoorBlock)) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (id == null) {
            return false;
        }
        if ("minecraft".equals(id.getNamespace())) {
            return !"iron_door".equals(id.getPath());
        }
        return DOOMSDAY_DECORATION_NAMESPACE.equals(id.getNamespace());
    }
}
