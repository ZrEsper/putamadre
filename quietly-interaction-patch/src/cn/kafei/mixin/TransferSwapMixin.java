package cn.kafei.mixin;
import cn.kafei.interact.TransferGuard;import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;import org.spongepowered.asm.mixin.Mixin;import org.spongepowered.asm.mixin.injection.*;import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(targets="net.minecraft.server.network.ServerGamePacketListenerImpl",remap=false)
public abstract class TransferSwapMixin{
 @Inject(method="handlePlayerAction",at=@At("HEAD"),cancellable=true)
 private void quietly$swap(ServerboundPlayerActionPacket packet,CallbackInfo ci){if(TransferGuard.swap(this,packet))ci.cancel();}
}
