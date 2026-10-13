package dev.zomboid.taczfeedback.mixin;
import dev.zomboid.taczfeedback.ThirdPersonAimClient;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/** Optional MTS integration: do not run its seat/cockpit camera over the Iso camera. */
@Pseudo
@Mixin(targets="mcinterface1211.InterfaceEventsEntityRendering",remap=false)
public class IsoVehicleCameraMixin {
 @Inject(method="onIVCameraSetup",at=@At("HEAD"),cancellable=true,require=0,remap=false)
 private static void isoVehicle(ViewportEvent.ComputeCameraAngles event,CallbackInfo ci){
  if(!ThirdPersonAimClient.isIso())return;
  try{Class.forName("mcinterface1211.InterfaceEventsEntityRendering").getField("adjustedCamera").setBoolean(null,false);}catch(ReflectiveOperationException ignored){}
  ci.cancel();
 }
}
