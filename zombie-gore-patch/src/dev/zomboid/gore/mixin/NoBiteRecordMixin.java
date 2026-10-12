package dev.zomboid.gore.mixin;
import dev.zomboid.gore.*;import org.spongepowered.asm.mixin.*;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Pseudo @Mixin(targets="ichttt.mods.firstaid.compat.lrtactical.TreatmentV33",remap=false) public abstract class NoBiteRecordMixin {
 @Inject(method="recordBite(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V",at=@At("HEAD"),cancellable=true,require=0,remap=false) private static void zg$noBite(Object body,Object player,Object source,Object old,CallbackInfo ci){if(Gore.noJaw(R.call(source,"getDirectEntity")))ci.cancel();}
}
