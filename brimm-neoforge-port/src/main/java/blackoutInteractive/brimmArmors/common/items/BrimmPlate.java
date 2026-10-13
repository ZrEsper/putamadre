package blackoutInteractive.brimmArmors.common.items;

import blackoutInteractive.brimmArmors.common.items.BrimmRarity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BrimmPlate
extends Item {
    private final BrimmRarity rarity;

    public BrimmPlate(BrimmRarity rarity) {
        super(new Item.Properties());
        this.rarity = rarity;
    }

    @NotNull
    public Component getName(@NotNull ItemStack stack) {
        return Component.literal((String)this.rarity.applyFormatting(super.getName(stack).getString()));
    }
}

