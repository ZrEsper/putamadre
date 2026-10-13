package dev.zomboid.taczfeedback.mixin;
import com.tacz.guns.client.event.RenderCrosshairEvent;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.platform.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
@Mixin(value=RenderCrosshairEvent.class,remap=false)
public interface CrosshairAccess {
 @Invoker("renderHitMarker") static void hitMarker(GuiGraphics graphics,Window window){throw new AssertionError();}
}
