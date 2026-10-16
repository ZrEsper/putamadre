package dev.zomboid.firstaid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
/** Opaque, non-pausing view; revival is still validated and completed by the existing server code. */
public final class UnconsciousScreen extends Screen{
 private Button rise;private boolean requested,waiting;private int requestTicks;
 public UnconsciousScreen(){super(Component.translatable("firstaid.blackout.title"));}
 protected void init(){
  rise=addRenderableWidget(Button.builder(Component.translatable("firstaid.blackout.rise"),b->request()).bounds(width/2-110,height/2+12,220,20).build());
  addRenderableWidget(Button.builder(Component.translatable("firstaid.blackout.wait"),b->{waiting=true;}).bounds(width/2-110,height/2+38,220,20).build());
  refresh();
 }
 void refresh(){if(rise!=null){rise.visible=DownedSupport.canSelf(Minecraft.getInstance().player);rise.active=rise.visible&&!requested;}}
 public void tick(){UnconsciousClient.tick();if(requested&&++requestTicks>=100){requested=false;waiting=true;}refresh();}
 private void request(){Object p=Minecraft.getInstance().player;if(requested||!DownedSupport.canSelf(p))return;requested=true;requestTicks=0;waiting=false;rise.active=false;
  Object packet=R.make("ichttt.mods.firstaid.common.network.MessageClientRequest",R.get(R.type("ichttt.mods.firstaid.common.network.MessageClientRequest$RequestType"),"ATTEMPT_SELF_DEFIB"));
  R.call(R.get(R.type("ichttt.mods.firstaid.FirstAid"),"NETWORKING"),"sendToServer",packet);
 }
 public void render(GuiGraphics g,int x,int y,float partial){g.fill(0,0,width,height,0xff000000);g.drawCenteredString(font,title,width/2,height/2-52,0xffdddddd);
  String key=requested?"firstaid.blackout.reviving":waiting?"firstaid.blackout.waiting":"firstaid.blackout.help";
  g.drawCenteredString(font,Component.translatable(key),width/2,height/2-30,0xffaaaaaa);
  g.drawCenteredString(font,Component.translatable("firstaid.blackout.time",DownedSupport.seconds(Minecraft.getInstance().player)),width/2,height/2-14,0xff888888);
  g.drawCenteredString(font,Component.translatable("firstaid.blackout.controls"),width/2,height/2+68,0xffcccccc);super.render(g,x,y,partial);
 }
 public void renderBackground(GuiGraphics g,int x,int y,float p){}protected void renderBlurredBackground(float p){}
 public boolean keyPressed(int key,int scan,int mods){Minecraft mc=Minecraft.getInstance();if(key==256){mc.setScreen((Screen)R.make("net.minecraft.client.gui.screens.PauseScreen",true));return true;}Object options=R.get(mc,"options");if(Boolean.TRUE.equals(R.call(R.get(options,"keyChat"),"matches",key,scan))||Boolean.TRUE.equals(R.call(R.get(options,"keyCommand"),"matches",key,scan))){boolean command=Boolean.TRUE.equals(R.call(R.get(options,"keyCommand"),"matches",key,scan));mc.setScreen((Screen)R.make("net.minecraft.client.gui.screens.ChatScreen",command?"/":""));return true;}return super.keyPressed(key,scan,mods);}
 public boolean isPauseScreen(){return false;}
 public boolean shouldCloseOnEsc(){return false;}
}
