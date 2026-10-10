package dev.survivorcreator.spawn;
import java.lang.reflect.*;import java.util.function.Supplier;
public final class EndingAudio{
 static Object track,owner;static long began;
 public static boolean ending(){Object screen=R.field(R.mc(),"screen");if(screen==null||!screen.getClass().getName().equals("dev.survivorcreator.client.EndingScreen"))return false;return !(Boolean)R.call(screen,"survivorcreator$isDeath");}
 public static boolean start(){if(!ending())return false;Object mc=R.mc(),screen=R.field(mc,"screen"),sounds=R.call(mc,"getSoundManager");
  if(owner!=screen){owner=screen;try{Method stop=R.type("dev.survivorcreator.hearing.CreationAudio").getDeclaredMethod("stop");stop.setAccessible(true);stop.invoke(null);}catch(Exception e){throw new IllegalStateException(e);}if(track!=null)R.call(sounds,"stop",track);track=null;}
  if(track==null||(!(Boolean)R.call(sounds,"isActive",track)&&System.currentTimeMillis()-began>1500)){
   R.call(R.call(mc,"getMusicManager"),"stopPlaying");Object sound=((Supplier<?>)R.field(R.type("dev.survivorcreator.ModSounds"),"ENDING_MUSIC")).get();track=R.call(R.type("net.minecraft.client.resources.sounds.SimpleSoundInstance"),"forMusic",sound);began=System.currentTimeMillis();R.call(sounds,"play",track);
  }return true;
 }
 public static Object track(){start();return track;}
}
