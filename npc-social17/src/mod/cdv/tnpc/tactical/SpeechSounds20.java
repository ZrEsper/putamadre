package mod.cdv.tnpc.tactical;
import java.util.*;import java.util.function.Supplier;
public final class SpeechSounds20 {
 static final Map<String,Object> SOUNDS=new HashMap<>();
 public static void register(Object event){Object key=SocialR.get(Social.t("net.minecraft.core.registries.Registries"),"SOUND_EVENT");if(!key.equals(Social.c(event,"getRegistryKey")))return;for(String mood:List.of("calm","happy","angry","hostile","afraid"))for(String gender:List.of("male","female")){String eventName=mood+"_"+gender;Object id=Social.c(Social.t("net.minecraft.resources.ResourceLocation"),"parse","tacznpcs:voice_"+eventName),sound=Social.c(Social.t("net.minecraft.sounds.SoundEvent"),"createVariableRangeEvent",id);SOUNDS.put(eventName,sound);Social.c(event,"register",key,id,(Supplier<Object>)()->sound);}}
 static void play(Object n,String mood){Object sound=SOUNDS.get(mood+"_"+Social.s(Social.tag(n),"soc_gender20"));if(sound==null)return;float pitch=(float)(.92+Math.floorMod(Social.uuid(n).hashCode(),17)/100.0-(Identity20.age(n)>55?.06:0));Social.c(Social.world(n),"playSound",null,Social.c(n,"blockPosition"),sound,SocialR.get(Social.t("net.minecraft.sounds.SoundSource"),"NEUTRAL"),.16f,pitch);}
}
