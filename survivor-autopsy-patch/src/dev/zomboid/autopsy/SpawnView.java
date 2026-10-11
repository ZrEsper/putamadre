package dev.zomboid.autopsy;
public final class SpawnView {
 static Object lastPlayer;
 public static void tick(Object event){try{Object mc=R.call(R.type("net.minecraft.client.Minecraft"),"getInstance"),player=R.get(mc,"player");if(player==null||R.get(mc,"level")==null){lastPlayer=null;return;}if(player==lastPlayer)return;boolean iso=false;try{Object mod=R.type("com.cleannrooster.dungeons_iso.mod.Mod");iso=R.yes(R.get(mod,"enabled"));R.set(mod,"enabled",false);}catch(IllegalStateException e){if(!(e.getCause() instanceof ClassNotFoundException))throw e;}R.call(R.get(mc,"options"),"setCameraType",R.get(R.type("net.minecraft.client.CameraType"),"FIRST_PERSON"));if(iso&&R.get(mc,"screen")==null){Object mouse=R.get(mc,"mouseHandler");R.call(mouse,"releaseMouse");R.call(mouse,"grabMouse");}lastPlayer=player;}catch(Throwable e){R.warn("first person on spawn",e);}}
}
