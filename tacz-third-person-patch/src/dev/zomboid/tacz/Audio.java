package dev.zomboid.tacz;
import java.util.List;
/** Shared shot policy: server packet range and local playback use the same scale. */
public final class Audio {
 public static String name(String raw){return raw.startsWith("pzpos;")?raw.substring(raw.lastIndexOf(';')+1):raw;}
 public static String positioned(String name,Object e){if(!shot(name))return name;return "pzpos;"+R.num(R.call(e,"getX"))+";"+R.num(R.call(e,"getY"))+";"+R.num(R.call(e,"getZ"))+";"+name;}
 public static boolean shot(String name){name=name(name);return "shoot".equals(name)||"shoot_3p".equals(name)||"silence".equals(name)||"silence_3p".equals(name);}
 public static int range(int original,String name){if(!shot(name))return original;double scale=name.startsWith("silence")?2.5:3;return (int)Math.min(32767,Math.max(0,original*scale));}
 public static float volume(float original,Object entity){float gain=1.45f;try{if(!R.yes(R.call(R.call(entity,"level"),"canSeeSky",R.call(entity,"blockPosition"))))gain*=1.08f;}catch(Throwable ignored){}return original*gain;}
 public static List<?> listeners(List<?> tracked,Object entity,String name){if(!shot(name))return tracked;return (List<?>)R.call(R.call(entity,"level"),"players");}
}
