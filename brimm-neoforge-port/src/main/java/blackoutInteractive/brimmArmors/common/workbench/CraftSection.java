package blackoutInteractive.brimmArmors.common.workbench;

import java.util.Objects;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;

public enum CraftSection {
    HELMETS("helmets"),
    CHESTPLATES("chestplates"),
    PLATES("plates"),
    LEGGINGS("leggings"),
    BOOTS("boots"),
    PATCHES("patches");

    private final String name;

    public static CraftSection ofArmor(ArmorItem.Type type) {
        Objects.requireNonNull(type);
        switch (type) {
            case HELMET: {
                return HELMETS;
            }
            case CHESTPLATE: {
                return CHESTPLATES;
            }
            case LEGGINGS: {
                return LEGGINGS;
            }
            case BOOTS: {
                return BOOTS;
            }
        }
        throw new IllegalArgumentException("Unexpected armor type " + String.valueOf(type));
    }

    private CraftSection(String name) {
        this.name = name;
    }

    public String localisedName() {
        return Component.literal((String)I18n.get((String)("screen.brimm.workbench.section." + this.name), (Object[])new Object[0])).getString();
    }
}

