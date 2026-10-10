package cn.kafei.interact;
import snownee.jade.api.*;import snownee.jade.api.config.IPluginConfig;import net.minecraft.nbt.CompoundTag;import net.minecraft.resources.ResourceLocation;import net.minecraft.network.chat.Component;
@WailaPlugin("quietly")
public final class JadePlugin implements IWailaPlugin{
 static final Provider P=new Provider();
 public void register(IWailaCommonRegistration r){r.registerBlockDataProvider((IServerDataProvider)P,net.minecraft.world.level.block.entity.BlockEntity.class);r.registerEntityDataProvider((IServerDataProvider)P,net.minecraft.world.entity.Entity.class);}
 public void registerClient(IWailaClientRegistration r){r.registerBlockComponent((IComponentProvider)P,net.minecraft.world.level.block.Block.class);r.registerEntityComponent((IComponentProvider)P,net.minecraft.world.entity.Entity.class);}
 public static final class Provider implements IComponentProvider<Accessor<?>>,IServerDataProvider<Accessor<?>>{
  public ResourceLocation getUid(){return (ResourceLocation)R.call(R.type("net.minecraft.resources.ResourceLocation"),"parse","quietly:interaction_hint");}
  public void appendServerData(CompoundTag tag,Accessor<?> a){Object t=a.getTarget();if(t==null&&a instanceof BlockAccessor b)t=b.getBlock();if(!Interaction.supported(t))return;R.call(tag,"putBoolean","quietly_interactive",true);try{String s=Interaction.preview(t,a.getPlayer());R.call(tag,"putString","quietly_preview",s);}catch(Exception e){R.call(tag,"putString","quietly_preview","Interactuar para revisar");}}
  public void appendTooltip(ITooltip tip,Accessor<?> a,IPluginConfig config){Object t=a.getTarget();if(t==null&&a instanceof BlockAccessor b)t=b.getBlock();if(!Interaction.supported(t))return;tip.add(Component.literal("§e["+Interaction.key()+"] "+Interaction.action(t)));String s=R.call(a.getServerData(),"getString","quietly_preview").toString();if(!s.isBlank())tip.add(Component.literal("§7"+s));}
 }
}
