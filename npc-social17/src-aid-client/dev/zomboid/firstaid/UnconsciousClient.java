package dev.zomboid.firstaid;
import net.minecraft.client.Minecraft;
/** Other screens (chat, pause and settings) remain usable during unconsciousness. */
public final class UnconsciousClient{
 static Object lastPlayer;static boolean wasDown;
 public static void tick(){Minecraft mc=Minecraft.getInstance();if(mc.player==null||mc.level==null){wasDown=false;lastPlayer=null;if(mc.screen instanceof UnconsciousScreen)mc.setScreen(null);return;}boolean down=DownedSupport.down(mc.player);boolean entered=down&&(!wasDown||lastPlayer!=mc.player);wasDown=down;lastPlayer=mc.player;if(down&&(entered||mc.screen==null))mc.setScreen(new UnconsciousScreen());else if(!down&&mc.screen instanceof UnconsciousScreen)mc.setScreen(null);}
}
