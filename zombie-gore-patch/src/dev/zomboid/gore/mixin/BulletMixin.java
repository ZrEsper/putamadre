package dev.zomboid.gore.mixin;
import dev.zomboid.gore.Gore;import org.spongepowered.asm.mixin.*;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;import net.minecraft.world.phys.Vec3;
@Pseudo @Mixin(targets="com.tacz.guns.entity.EntityKineticBullet",remap=false) public abstract class BulletMixin {
 @Inject(method="onHitEntity(Lcom/tacz/guns/util/TacHitResult;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)V",at=@At("HEAD"),require=0,remap=false) private void zg$hit(@Coerce Object hit,Vec3 from,Vec3 to,CallbackInfo ci){Gore.begin(this,hit);}
 @Inject(method="onHitEntity(Lcom/tacz/guns/util/TacHitResult;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/Vec3;)V",at=@At("RETURN"),require=0,remap=false) private void zg$end(@Coerce Object hit,Vec3 from,Vec3 to,CallbackInfo ci){Gore.end();}
 @Inject(method="<init>",at=@At("RETURN"),require=0,remap=false) private void zg$pierce(CallbackInfo ci){Gore.pierce(this);}
}
