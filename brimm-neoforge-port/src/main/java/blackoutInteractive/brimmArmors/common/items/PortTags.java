package blackoutInteractive.brimmArmors.common.items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import java.util.function.Consumer;
public final class PortTags {
 public static CompoundTag read(ItemStack stack){return stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();}
 public static void update(ItemStack stack,Consumer<CompoundTag> change){CustomData.update(DataComponents.CUSTOM_DATA,stack,change);}
}
