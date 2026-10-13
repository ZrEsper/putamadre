package blackoutInteractive.brimmArmors.common.configurations;

import blackoutInteractive.brimmArmors.common.configurations.MaterialOverrides;
import blackoutInteractive.brimmArmors.common.items.BrimmRarity;
import java.util.Optional;
import javax.annotation.Nullable;

public record ArmorConfig(@Nullable MaterialOverrides materialOverrides, @Nullable Optional<BrimmRarity> rarityOverride) {
    public static final ArmorConfig EMPTY = new ArmorConfig(null, null);
}

