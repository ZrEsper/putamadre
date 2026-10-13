package dev.zomboidmelee.mixin;
@org.spongepowered.asm.mixin.Mixin(targets="net.minecraft.client.model.HumanoidModel",priority=900)
public class BodyPushMixin {
 @org.spongepowered.asm.mixin.injection.Inject(method="setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",at=@org.spongepowered.asm.mixin.injection.At("TAIL"),require=1)
 private void zpush$body(net.minecraft.world.entity.LivingEntity entity,float limb,float amount,float age,float yaw,float pitch,org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci){dev.zomboidmelee.motion.BodyPush.apply(this,entity);}
}
