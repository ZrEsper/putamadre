package dev.zomboid.tacz;
import java.util.Optional;
/** Positional playback also works when the distant shooter is outside entity tracking. */
public final class RemoteAudio {
 public static boolean play(Object message){String raw=R.call(message,"getSoundName").toString();if(!raw.startsWith("pzpos;"))return false;try{String[] data=raw.split(";",5);if(data.length!=5||!Audio.shot(data[4]))return true;Object level=R.get(Bridge.mc(),"level");if(level==null)return true;Object entity=R.call(level,"getEntity",R.call(message,"getEntityId"));boolean tracked=entity!=null;if(!tracked){double x=Double.parseDouble(data[1]),y=Double.parseDouble(data[2]),z=Double.parseDouble(data[3]);if(!Double.isFinite(x)||!Double.isFinite(y)||!Double.isFinite(z))return true;entity=R.make("net.minecraft.world.entity.decoration.ArmorStand",level,x,y,z);}
 Optional<?> display=(Optional<?>)R.call(R.type("com.tacz.guns.api.TimelessAPI"),"getGunDisplay",R.call(message,"getGunDisplayId"),R.call(message,"getGunId"));if(display.isEmpty())return true;Object sound=R.call(display.get(),"getSounds",data[4]);if(sound==null)return true;float volume=Audio.volume(((Number)R.call(message,"getVolume")).floatValue(),entity);R.call(R.type("com.tacz.guns.client.sound.SoundPlayManager"),"playClientSound",entity,sound,volume,R.call(message,"getPitch"),R.call(message,"getDistance"),true,tracked?8:0,tracked,false);return true;
 }catch(Throwable e){R.warn("positional gunshot playback",e);return true;}}
}
