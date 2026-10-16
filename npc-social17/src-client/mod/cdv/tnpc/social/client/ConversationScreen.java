package mod.cdv.tnpc.social.client;
import mod.cdv.tnpc.tactical.SocialR;import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.client.gui.components.Button;import net.minecraft.client.gui.screens.Screen;import net.minecraft.network.chat.Component;import java.util.*;
/** Three tones, six choices at a time, and an opaque dialogue panel without background blur. */
public final class ConversationScreen extends Screen {
 final String[] data;int tone,offset;static int lastTone,lastOffset;
 static final String[][] CHOICES={{"greet","promise","joke","feeling","favor_ask","favor_give","gift","heal","invite"},{"weather","day","time","past","horde","rumor","favor_help","favor_clean","inventory","orders","depot"},{"insult","threaten","leave","dismiss"},{"follow","stay","patrol","farm","scavenge","clean","craft","cook","deliver","free"}};
 public ConversationScreen(String value){super(Component.translatable("soc.screen.title"));data=value.split("\n",-1);tone=lastTone;offset=lastOffset;}
 public static void open(String value){if(value.length()>8192||value.split("\n",-1).length!=12)return;Minecraft mc=Minecraft.getInstance();if(mc.player==null)return;if(mc.screen instanceof ConversationScreen old){lastTone=old.tone;lastOffset=old.offset;}else{lastTone=0;lastOffset=0;}mc.setScreen(new ConversationScreen(value));}
 static Component tr(String key,Object...args){return Component.translatable(key,args);}void page(int t,int o){lastTone=t;lastOffset=o;Minecraft.getInstance().setScreen(new ConversationScreen(String.join("\n",data)));}
 void action(String key){if(key.equals("orders")){page(3,0);return;}if(key.equals("leave")){onClose();return;}Object player=Minecraft.getInstance().player;if(player==null)return;SocialR.call(SocialR.get(player,"connection"),"sendCommand","superviviente "+key);if(key.equals("inventory"))Minecraft.getInstance().setScreen(null);}
 int left(){return (width-panelWidth())/2;}int panelWidth(){return Math.max(240,Math.min(480,width-12));}int top(){return Math.max(4,(height-240)/2);}
 void add(String label,int x,int y,int w,Button.OnPress press){addRenderableWidget(Button.builder(tr(label),press).bounds(x,y,w,20).build());}
 protected void init(){int x=left()+8,y=top(),w=panelWidth()-16;for(int i=0;i<3;i++){final int t=i;add("soc.tone."+i,x+i*(w/3),y+29,w/3-3,b->page(t,0));}
  String[] choices=CHOICES[tone];offset=Math.max(0,Math.min(offset,((choices.length-1)/6)*6));for(int i=offset;i<Math.min(offset+6,choices.length);i++){String a=choices[i];int row=i-offset;Button b=addRenderableWidget(Button.builder(tr("soc.button."+a),v->action(a)).bounds(x+(row%2)*(w/2),y+126+(row/2)*22,w/2-3,20).build());if(Set.of("inventory","orders","depot","dismiss","follow","stay","patrol","farm","scavenge","clean","craft","cook","deliver","free").contains(a))b.active=data[6].equals("ally");}
  if(offset>0)add("soc.previous",x,y+204,w/3-3,b->page(tone,offset-6));if(offset+6<choices.length)add("soc.next",x+w/3,y+204,w/3-3,b->page(tone,offset+6));add("soc.close",x+2*w/3,y+204,w/3-3,b->onClose());}
 public void renderBackground(GuiGraphics g,int x,int y,float p){}protected void renderBlurredBackground(float p){}
 public void render(GuiGraphics g,int x,int y,float p){int l=left(),t=top(),w=panelWidth();g.fill(l-2,t-4,l+w+2,t+232,0xff88917b);g.fill(l,t-2,l+w,t+230,0xff101610);g.fill(l+7,t+55,l+w-7,t+121,0xff202820);super.render(g,x,y,p);
  g.drawCenteredString(font,Component.literal(data[1]),width/2,t+2,0xfff1e6c7);g.drawCenteredString(font,tr("soc.simple_status",data[4],data[8]),width/2,t+15,0xffd2dcc7);
  Component speech=tr(data[9],data[10],data[11]);Object lines=SocialR.call(font,"split",speech,w-28);int sy=t+59;for(Object line:(Iterable<?>)lines){SocialR.call(g,"drawCenteredString",font,line,width/2,sy,0xfff4f4e6);sy+=10;if(sy>=t+119)break;}
 }
 public boolean isPauseScreen(){return false;}public boolean shouldCloseOnEsc(){return true;}
}
