package cn.kafei.mixin;
import dev.zomboid.storage.ChestMenus;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Preserve overflow when a player deliberately breaks the chest, just like its native slots. */
@Mixin(targets="net.minecraft.world.Containers",remap=false)
public abstract class ExpandedChestDropsMixin {
    @ModifyVariable(method="dropContents(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/Container;)V",at=@At("HEAD"),argsOnly=true)
    private static Container quietly$allSlots(Container container) { return ChestMenus.drops(container); }
    @Inject(method="dropContents(Lnet/minecraft/world/level/Level;DDDLnet/minecraft/world/Container;)V",at=@At("RETURN"))
    private static void quietly$saveDrained(Level level,double x,double y,double z,Container container,CallbackInfo ci) { container.setChanged(); }
}
