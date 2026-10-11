package dev.zomboid.reactive.mixin;
import dev.zomboid.reactive.Reactive;import net.minecraft.world.entity.Mob;import net.minecraft.world.entity.Entity;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.Inject;import org.spongepowered.asm.mixin.injection.At;import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Mob.class)
public abstract class MutantBiteMixin {
 @Inject(method="doHurtTarget(Lnet/minecraft/world/entity/Entity;)Z",at=@At("HEAD"),cancellable=true)
 private void zpr$mutantBite(Entity target,CallbackInfoReturnable<Boolean> callback){Object self=this;
  if(!dev.zomboid.reactive.R.type("net.minecraft.world.entity.monster.Zombie").isInstance(self)&&Reactive.controlled(self)&&!Reactive.allowAttack(self,target))callback.setReturnValue(false);
 }
}
