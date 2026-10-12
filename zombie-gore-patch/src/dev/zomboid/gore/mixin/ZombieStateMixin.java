package dev.zomboid.gore.mixin;
import dev.zomboid.gore.Gore;import net.minecraft.world.entity.monster.Zombie;import net.minecraft.network.syncher.SynchedEntityData;import net.minecraft.nbt.CompoundTag;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.Inject;import org.spongepowered.asm.mixin.injection.At;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(Zombie.class) public abstract class ZombieStateMixin {
 @Inject(method="defineSynchedData(Lnet/minecraft/network/syncher/SynchedEntityData$Builder;)V",at=@At("TAIL")) private void zg$define(SynchedEntityData.Builder builder,CallbackInfo ci){Gore.define(builder);}
 @Inject(method="addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",at=@At("TAIL")) private void zg$save(CompoundTag tag,CallbackInfo ci){Gore.save(this,tag);}
 @Inject(method="readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",at=@At("TAIL")) private void zg$load(CompoundTag tag,CallbackInfo ci){Gore.restore(this,tag);}
}
