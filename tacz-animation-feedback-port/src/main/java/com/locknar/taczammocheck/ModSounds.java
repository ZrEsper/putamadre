package com.locknar.taczammocheck;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import net.neoforged.neoforge.registries.DeferredHolder;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, (String)"locknar_doorkick");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHECK = SOUND_EVENTS.register("check", () -> SoundEvent.createVariableRangeEvent((ResourceLocation)ResourceLocation.fromNamespaceAndPath("locknar_doorkick", "check")));

    private ModSounds() {
    }
}
