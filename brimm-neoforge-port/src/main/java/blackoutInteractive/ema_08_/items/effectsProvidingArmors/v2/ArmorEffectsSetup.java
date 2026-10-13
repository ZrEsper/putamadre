package blackoutInteractive.ema_08_.items.effectsProvidingArmors.v2;

import blackoutInteractive.ema_08_.misc.ReadonlySetView;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import net.minecraft.world.effect.MobEffect;

public record ArmorEffectsSetup(ReadonlySetView<AmplifiedEffect> addOnWear, ReadonlySetView<MobEffect> remOnWear) {
    public static final ArmorEffectsSetup EMPTY;

    public ArmorEffectsSetup {
        Objects.requireNonNull(addOnWear, "Missing addOnWear");
        Objects.requireNonNull(remOnWear, "Missing remOnWear");
    }

    static { EMPTY=new ArmorEffectsSetup(ReadonlySetView.of(java.util.Set.<AmplifiedEffect>of()),ReadonlySetView.of(java.util.Set.<MobEffect>of())); }

    public record AmplifiedEffect(MobEffect effect, int amplifier) {
        public AmplifiedEffect {
            Objects.requireNonNull(effect, "Missing effect");
            if (amplifier < 0 || amplifier > 4) {
                throw new IllegalArgumentException("Invalid amplifier " + amplifier);
            }
        }
    }
}

