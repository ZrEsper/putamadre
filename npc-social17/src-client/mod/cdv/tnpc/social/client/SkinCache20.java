package mod.cdv.tnpc.social.client;
import mod.cdv.tnpc.tactical.SocialR;
public final class SkinCache20 {
 public static void refresh(Object fake){try{Object old=SocialR.get(fake,"skin"),link=SocialR.get(fake,"link");if(old==null||SocialR.call(old,"texture").equals(SocialR.call(link,"getPlayerSkin")))return;java.lang.reflect.Field f=fake.getClass().getDeclaredField("skin");f.setAccessible(true);f.set(fake,null);}catch(ReflectiveOperationException e){throw new IllegalStateException(e);}}
}
