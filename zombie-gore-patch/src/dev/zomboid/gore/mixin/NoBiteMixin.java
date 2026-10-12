package dev.zomboid.gore.mixin;
import dev.zomboid.gore.*;import org.spongepowered.asm.mixin.*;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo @Mixin(targets="ichttt.mods.firstaid.survival.CriticalCare",remap=false) public abstract class NoBiteMixin {
 @Inject(method="bite(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",at=@At("HEAD"),cancellable=true,require=0,remap=false) private static void zg$noBite(Object body,Object player,Object source,Object old,CallbackInfo ci){if(Gore.noJaw(R.call(source,"getDirectEntity")))ci.cancel();}
}
