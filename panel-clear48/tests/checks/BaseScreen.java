package checks;import net.minecraft.client.gui.GuiGraphics;
/** Parent render sequence: background/blur precedes widget rendering. */
public class BaseScreen{public void render(GuiGraphics g,int x,int y,float p){renderBackground(g,x,y,p);Trace.widgets++;}public void renderBackground(GuiGraphics g,int x,int y,float p){renderBlurredBackground(p);}protected void renderBlurredBackground(float p){Trace.labelsBlurred=Trace.labelsDrawn;}}
