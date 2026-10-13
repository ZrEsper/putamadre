package dev.zomboid.taczfeedback.mixin;
import dev.zomboid.taczfeedback.ThirdPersonAimClient;
import com.tacz.guns.client.gameplay.LocalPlayerShoot;
import com.tacz.guns.api.entity.ShootResult;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(value=LocalPlayerShoot.class,remap=false)
public class ThirdPersonShotMixin {
 @Unique private float[] feedback$restore;
 @Inject(method="shoot",at=@At("HEAD")) private void feedback$pick(CallbackInfoReturnable<ShootResult> ci){feedback$restore=ThirdPersonAimClient.prepareShot();}
 @Inject(method="shoot",at=@At("RETURN")) private void feedback$restore(CallbackInfoReturnable<ShootResult> ci){ThirdPersonAimClient.restore(feedback$restore);feedback$restore=null;}
}
