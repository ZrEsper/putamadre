package net.minecraft.sounds;public record SoundEvent(Object id) {public static Object createVariableRangeEvent(Object id){return new SoundEvent(id);}}
