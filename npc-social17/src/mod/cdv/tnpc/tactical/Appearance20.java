package mod.cdv.tnpc.tactical;
public final class Appearance20 {
 static void assign(Object n,int index){Object skin=SocialR.make("net.minecraft.nbt.CompoundTag");Social.c(skin,"putString","texture","tacznpcs:entity/survivors/survivor_"+String.format(java.util.Locale.ROOT,"%02d",index)+".png");Social.c(skin,"putBoolean","slim",false);Object key=SocialR.get(Social.t(Social.NPC),"SYNCED_SKIN");Social.c(Social.c(n,"getEntityData"),"set",key,skin);Social.c(Social.tag(n),"putInt","soc_skin20",index);}
}
