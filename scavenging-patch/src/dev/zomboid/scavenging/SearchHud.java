package dev.zomboid.scavenging;
import java.util.regex.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
@EventBusSubscriber(modid="quietly",value=Dist.CLIENT)
public final class SearchHud {
 static final Pattern PROGRESS=Pattern.compile("^Buscando (\\d{1,3})%.*");
 static int progress(String text){Matcher m=PROGRESS.matcher(text);return m.matches()?Math.max(0,Math.min(100,Integer.parseInt(m.group(1)))):-1;}
 @SubscribeEvent public static void render(RenderGuiEvent.Post event){try{Object mc=R.call(R.type("net.minecraft.client.Minecraft"),"getInstance");if(R.get(mc,"screen")!=null||R.yes(R.get(R.get(mc,"options"),"hideGui")))return;Object gui=R.get(mc,"gui"),message=R.get(gui,"overlayMessage");if(message==null||((Number)R.get(gui,"overlayMessageTime")).intValue()<=0)return;int percent=progress((String)R.call(message,"getString"));if(percent<0)return;draw(R.call(event,"getGuiGraphics"),percent);}catch(Throwable e){Search.warn("search progress HUD",e);}}
 static void draw(Object graphics,int percent){int width=((Number)R.call(graphics,"guiWidth")).intValue(),height=((Number)R.call(graphics,"guiHeight")).intValue();int left=width/2-61,top=height-85;R.call(graphics,"fill",left-1,top-1,left+123,top+8,0xCC090C09);R.call(graphics,"fill",left,top,left+122,top+7,0xFF7A806C);R.call(graphics,"fill",left+1,top+1,left+121,top+6,0xFF242B23);int fill=120*Math.max(0,Math.min(100,percent))/100;if(fill>0)R.call(graphics,"fill",left+1,top+1,left+1+fill,top+6,0xFFD5C875);}
}
