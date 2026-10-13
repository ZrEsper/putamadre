package blackoutInteractive.brimmArmors.common.items;

import blackoutInteractive.ema_08_.rendering.twoToThreeD.IDefaultPatchesRenderablePngProvider;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ArmorPatch
extends Item
implements IDefaultPatchesRenderablePngProvider {
    private final String unlocName;

    public ArmorPatch(String unlocName) {
        super(new Item.Properties());
        this.unlocName = unlocName;
    }

    public String getPatchName() {
        return this.unlocName;
    }

    @Override
    public String getTextureName() {
        return this.unlocName;
    }

    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltipList, @NotNull TooltipFlag flag) {
        String tooltipRaw = I18n.get((String)("tooltip.brimm." + this.unlocName), (Object[])new Object[0]);
        if (!tooltipRaw.isBlank()) {
            tooltipList.add((Component)Component.literal((String)tooltipRaw));
        }
    }
}

