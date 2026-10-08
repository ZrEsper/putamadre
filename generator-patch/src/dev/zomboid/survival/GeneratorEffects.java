package dev.zomboid.survival;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = "zomboid_survival", bus = EventBusSubscriber.Bus.MOD)
public final class GeneratorEffects {
    private static final ResourceLocation SOUND_ID = ResourceLocation.fromNamespaceAndPath("zomboid_survival", "generator_running");
    public static final SoundEvent RUNNING = SoundEvent.createVariableRangeEvent(SOUND_ID);
    @SubscribeEvent public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, SOUND_ID, () -> RUNNING);
    }
    /** Called exclusively on a tick that actually generated energy. */
    public static void tick(Level level, BlockPos position, long now) {
        if (now % 20 == 0) level.playSound(null, position, RUNNING, SoundSource.BLOCKS, 0.65f, 1.0f);
        if (now % 10 == 0 && level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.SMOKE, position.getX() + 0.5, position.getY() + 1.15, position.getZ() + 0.5,
                    2, 0.06, 0.05, 0.06, 0.025);
        }
    }
}
