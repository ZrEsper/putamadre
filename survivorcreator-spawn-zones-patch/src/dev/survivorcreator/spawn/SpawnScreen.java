package dev.survivorcreator.spawn;
import java.util.*;import dev.survivorcreator.net.Net;import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.client.gui.screens.Screen;import net.minecraft.network.chat.Component;import net.minecraft.network.protocol.common.custom.CustomPacketPayload;import net.minecraft.resources.ResourceLocation;import net.neoforged.neoforge.network.PacketDistributor;
public final class SpawnScreen extends Screen{
 final Screen parent;final Net.Confirm confirm;int selected=0;boolean sent=false;
 int first=0;
 public SpawnScreen(Screen p,Net.Confirm c){super(Component.literal("Elige dónde sobrevivir"));parent=p;confirm=c;}
 public static void open(CustomPacketPayload p,CustomPacketPayload[] ignored){if(p instanceof Net.Confirm c){Minecraft mc=(Minecraft)R.mc();mc.setScreen(new SpawnScreen(mc.screen,c));}else PacketDistributor.sendToServer(p,ignored);}
 public boolean isPauseScreen(){return false;}public boolean shouldCloseOnEsc(){return false;}
 public static boolean isMap(){return R.field(R.mc(),"screen") instanceof SpawnScreen;}
 int rowHeight(){return 24;}
 int visible(){return Math.max(2,Math.min(9,(height-153)/24));}
 void choose(int n){selected=Math.max(0,Math.min(Zones.NAMES.length-1,n));if(selected<first)first=selected;if(selected>=first+visible())first=selected-visible()+1;}
 public void render(GuiGraphics g,int mx,int my,float pt){
  g.fill(0,0,width,height,0xff111512);g.drawString(font,"M O S S L O R N  /  ELIGE TU ZONA",16,12,0xffded7b6,false);
  int mapW=Math.min(width/2-24,(height-72)*2/3),mapH=mapW*3/2;int mapX=16,mapY=35;
  g.fill(mapX,mapY,mapX+mapW,mapY+mapH,0xffb7b09a);
  Object pose=g.pose();boolean pushed=false;try{R.call(pose,"pushPose");pushed=true;R.call(pose,"translate",mapX+0.0,mapY+0.0,0.0);R.call(pose,"scale",mapW/512f,mapH/768f,1f);Object texture=R.call(R.type("net.minecraft.resources.ResourceLocation"),"parse","survivorcreator:textures/gui/mosslorn_spawn_map.png");R.call(g,"blit",texture,0,0,0f,0f,512,768,512,768);}catch(Exception e){g.drawString(font,"Mapa no disponible",mapX+4,mapY+4,0xff202820,false);}finally{if(pushed)R.call(pose,"popPose");}
  int[][] markers=Zones.MARKERS;
  for(int i=0;i<Zones.NAMES.length;i++){int x=mapX+mapW*markers[i][0]/100,y=mapY+mapH*markers[i][1]/100;g.fill(x-4,y-4,x+5,y+5,i==selected?0xffc54337:0xff343e32);g.drawString(font,""+(i+1),x+7,y-4,0xff101610,false);}
  int x=width/2+8;
  for(int row=0;row<visible()&&first+row<Zones.NAMES.length;row++){int i=first+row,y=40+row*rowHeight();g.fill(x,y,width-16,y+rowHeight()-4,i==selected?0xff425244:0xff252c27);g.drawString(font,font.plainSubstrByWidth((i+1)+". "+Zones.NAMES[i],width-x-28),x+7,y+7,0xffece6cc,false);}
  int lineY=40+visible()*rowHeight()+3;for(var line:font.split(Component.literal("Peligro: "+Zones.DANGER[selected]+" / Loot: "+Zones.LOOT[selected]),width-x-20)){g.drawString(font,line,x,lineY,0xffdbc783,false);lineY+=10;}
  for(var line:font.split(Component.literal(Zones.INFO[selected]),width-x-20)){if(lineY>height-65)break;g.drawString(font,line,x,lineY,0xffb8c2ad,false);lineY+=10;}
  g.fill(x,height-42,width-16,height-16,0xff7e302a);g.drawString(font,"COMENZAR EN ESTA ZONA",x+7,height-33,0xfff2e9d2,false);
  g.drawString(font,"1–9 / rueda: zona   Enter: comenzar",16,height-19,0xffb8c2ad,false);
 }
 void submit(){if(sent)return;sent=true;List<ResourceLocation> perks=new ArrayList<>(confirm.perks());perks.add((ResourceLocation)R.call(R.type("net.minecraft.resources.ResourceLocation"),"parse",Spawns.PREFIX+selected));((Minecraft)R.mc()).setScreen(parent);PacketDistributor.sendToServer(new Net.Confirm(confirm.profession(),List.copyOf(perks)));}
 public boolean mouseClicked(double x,double y,int button){if(button!=0)return false;if(x>=width/2+8&&x<=width-16){for(int row=0;row<visible()&&first+row<Zones.NAMES.length;row++)if(y>=40+row*rowHeight()&&y<=40+row*rowHeight()+rowHeight()-4){choose(first+row);return true;}if(y>=height-42&&y<=height-16){submit();return true;}}int mw=Math.min(width/2-24,(height-72)*2/3),mh=mw*3/2;int[][] markers=Zones.MARKERS;for(int i=0;i<Zones.NAMES.length;i++){double dx=x-(16+mw*markers[i][0]/100),dy=y-(35+mh*markers[i][1]/100);if(dx*dx+dy*dy<=225){choose(i);return true;}}return true;}
 public boolean mouseScrolled(double x,double y,double sx,double sy){if(sy!=0)choose(selected+(sy>0?-1:1));return true;}
 public boolean keyPressed(int key,int scan,int mod){if(key>=49&&key<=57){choose(key-49);return true;}if(key==264){choose(selected+1);return true;}if(key==265){choose(selected-1);return true;}if(key==257||key==335){submit();return true;}return true;}
}
