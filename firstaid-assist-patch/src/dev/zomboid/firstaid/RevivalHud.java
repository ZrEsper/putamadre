package dev.zomboid.firstaid;
public final class RevivalHud {
 public static void render(Object g){try{Object mc=R.call(R.type("net.minecraft.client.Minecraft"),"getInstance"),p=R.get(mc,"player");if(p==null)return;Object m=Assist.model(p);if(m==null||!Revival.active(m))return;int w=((Number)R.call(g,"guiWidth")).intValue(),ticks=((Number)R.call(m,"getUnconsciousTicks")).intValue();Object f=R.get(mc,"font");R.call(g,"drawCenteredString",f,"DERRIBADO · "+Math.max(0,(ticks+19)/20)+" s",w/2,16,0xFFE2C778);R.call(g,"drawCenteredString",f,"Ayuda: clic derecho · 8 s. Solo: desfibrilador en mano.",w/2,30,0xFFCCCCCC);R.call(g,"fill",w/2-90,44,w/2+90,47,0xFF303C32);R.call(g,"fill",w/2-90,44,w/2-90+180*Math.max(0,Math.min(2000,ticks))/2000,47,0xFFC6AC6A);}catch(Exception ignored){}}
}
