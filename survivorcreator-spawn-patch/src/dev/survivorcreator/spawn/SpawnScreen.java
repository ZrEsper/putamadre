package dev.survivorcreator.spawn;
import java.util.*;import dev.survivorcreator.net.Net;import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.client.gui.screens.Screen;import net.minecraft.network.chat.Component;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;import net.neoforged.neoforge.network.PacketDistributor;
public final class SpawnScreen extends Screen{
 final Screen parent;final Net.Confirm confirm;int selected=0;boolean sent=false;
 final String[] names={"Residencial noroeste","Residencial noreste","Ciudad suroeste","Ciudad sureste"};
 final String[] info={"Viviendas y suministros domésticos","Viviendas cerca del sector oriental","Interiores urbanos: mayor peligro fuera","Interiores de edificios: mayor peligro fuera"};
 public SpawnScreen(Screen p,Net.Confirm c){super(Component.literal("Elige dónde sobrevivir"));parent=p;confirm=c;}
 public static void open(CustomPacketPayload p,CustomPacketPayload[] ignored){if(p instanceof Net.Confirm c){Minecraft mc=(Minecraft)R.mc();mc.setScreen(new SpawnScreen(mc.screen,c));}else PacketDistributor.sendToServer(p,ignored);}
 public boolean isPauseScreen(){return false;}public boolean shouldCloseOnEsc(){return false;}
 public static boolean isMap(){return R.field(R.mc(),"screen") instanceof SpawnScreen;}
 int rowHeight(){return Math.max(24,Math.min(38,(height-130)/4));}
 public void render(GuiGraphics g,int mx,int my,float pt){
  g.fill(0,0,width,height,0xff111512);g.drawString(font,"M O S S L O R N  /  ELIGE TU ZONA",16,12,0xffded7b6,false);
  int mapW=Math.min(width/2-24,(height-72)*2/3),mapH=mapW*3/2;int mapX=16,mapY=35;
  g.fill(mapX,mapY,mapX+mapW,mapY+mapH,0xffb7b09a);
  Object pose=g.pose();boolean pushed=false;try{R.call(pose,"pushPose");pushed=true;R.call(pose,"translate",mapX+0.0,mapY+0.0,0.0);R.call(pose,"scale",mapW/512f,mapH/768f,1f);Object texture=R.call(R.type("net.minecraft.resources.ResourceLocation"),"parse","survivorcreator:textures/gui/mosslorn_spawn_map.png");R.call(g,"blit",texture,0,0,0f,0f,512,768,512,768);}catch(Exception e){g.drawString(font,"Mapa no disponible",mapX+4,mapY+4,0xff202820,false);}finally{if(pushed)R.call(pose,"popPose");}
  int[][] markers={{28,30},{65,38},{30,68},{69,75}};
  for(int i=0;i<4;i++){int x=mapX+mapW*markers[i][0]/100,y=mapY+mapH*markers[i][1]/100;g.fill(x-4,y-4,x+5,y+5,i==selected?0xffc54337:0xff343e32);g.drawString(font,""+(i+1),x+7,y-4,0xff101610,false);}
  int x=width/2+8;
  for(int i=0;i<4;i++){int y=40+i*rowHeight();g.fill(x,y,width-16,y+rowHeight()-4,i==selected?0xff425244:0xff252c27);g.drawString(font,(i+1)+". "+names[i],x+7,y+8,0xffece6cc,false);}
  int infoY=40+4*rowHeight()+3;g.drawString(font,"Interior revisado + provisiones",x,infoY,0xffb8c2ad,false);g.drawString(font,selected<2?"Norte: residencial":"Sur: peligro urbano",x,infoY+12,0xffb8c2ad,false);
  g.fill(x,height-42,width-16,height-16,0xff7e302a);g.drawString(font,"COMENZAR EN ESTA ZONA",x+7,height-33,0xfff2e9d2,false);
  g.drawString(font,"1–4: elegir    Enter: comenzar",16,height-19,0xffb8c2ad,false);
 }
 void submit(){if(sent)return;sent=true;List<ResourceLocation> perks=new ArrayList<>(confirm.perks());perks.add((ResourceLocation)R.call(R.type("net.minecraft.resources.ResourceLocation"),"parse",Spawns.PREFIX+selected));((Minecraft)R.mc()).setScreen(parent);PacketDistributor.sendToServer(new Net.Confirm(confirm.profession(),List.copyOf(perks)));}
 public boolean mouseClicked(double x,double y,int button){if(button!=0)return false;if(x>=width/2+8&&x<=width-16){for(int i=0;i<4;i++)if(y>=40+i*rowHeight()&&y<=40+i*rowHeight()+rowHeight()-4){selected=i;return true;}if(y>=height-42&&y<=height-16){submit();return true;}}int mw=Math.min(width/2-24,(height-72)*2/3),mh=mw*3/2;int[][] markers={{28,30},{65,38},{30,68},{69,75}};for(int i=0;i<4;i++){double dx=x-(16+mw*markers[i][0]/100),dy=y-(35+mh*markers[i][1]/100);if(dx*dx+dy*dy<=225){selected=i;return true;}}return true;}
 public boolean keyPressed(int key,int scan,int mod){if(key>=49&&key<=52){selected=key-49;return true;}if(key==257||key==335){submit();return true;}return true;}
}
