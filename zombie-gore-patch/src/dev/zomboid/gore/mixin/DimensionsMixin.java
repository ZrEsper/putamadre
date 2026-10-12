package dev.zomboid.gore.mixin;
import dev.zomboid.gore.Gore;import net.minecraft.world.entity.Entity;import net.minecraft.world.entity.LivingEntity;import net.minecraft.world.entity.Pose;import net.minecraft.world.entity.EntityDimensions;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin({Entity.class,LivingEntity.class}) public abstract class DimensionsMixin {
 @Inject(method="getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;",at=@At("RETURN"),cancellable=true,require=0) private void zg$size(Pose pose,CallbackInfoReturnable<EntityDimensions> ci){ci.setReturnValue((EntityDimensions)Gore.dimensions(this,ci.getReturnValue()));}
}
