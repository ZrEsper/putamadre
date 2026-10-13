package dev.zomboid.taczfeedback.mixin;
import com.tacz.guns.client.event.CameraSetupEvent;
import dev.zomboid.taczfeedback.ThirdPersonAimClient;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** The gun may rotate the shooter, but its first-person camera effects do not own Iso. */
@Mixin(CameraSetupEvent.class)
public class IsoGunCameraMixin {
 @Inject(method="applyLevelCameraAnimation",at=@At("HEAD"),cancellable=true)
 private static void isoAnimation(ViewportEvent.ComputeCameraAngles event,CallbackInfo ci){if(ThirdPersonAimClient.isIso())ci.cancel();}
 @Inject(method="applyScopeMagnification",at=@At("HEAD"),cancellable=true)
 private static void isoScope(ViewportEvent.ComputeFov event,CallbackInfo ci){if(ThirdPersonAimClient.isIso())ci.cancel();}
}
