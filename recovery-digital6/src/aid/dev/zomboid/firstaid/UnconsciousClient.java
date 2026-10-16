package dev.zomboid.firstaid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
public final class UnconsciousClient{
 public static void tick(){Minecraft mc=Minecraft.getInstance();if(mc.player==null||mc.level==null){if(mc.screen instanceof UnconsciousScreen)mc.setScreen(null);return;}boolean down=DownedSupport.down(mc.player);if(down&&!(mc.screen instanceof UnconsciousScreen))mc.setScreen(new UnconsciousScreen());else if(!down&&mc.screen instanceof UnconsciousScreen)mc.setScreen(null);}
}
