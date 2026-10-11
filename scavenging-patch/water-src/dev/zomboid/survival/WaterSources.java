package dev.zomboid.survival;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
@EventBusSubscriber(modid="zomboid_survival")
public final class WaterSources {
 @SubscribeEvent(priority=EventPriority.LOWEST) public static void fill(PlayerInteractEvent.RightClickBlock event){try{
 Object player=WaterReflect.call(event,"getEntity"),level=WaterReflect.call(event,"getLevel"),pos=WaterReflect.call(event,"getPos"),held=WaterReflect.call(event,"getItemStack");
 if(!WaterReflect.yes(WaterReflect.call(held,"is",Survival.EMPTY.get()))&&!WaterReflect.yes(WaterReflect.call(held,"is",WaterReflect.get(WaterReflect.type("net.minecraft.world.item.Items"),"GLASS_BOTTLE"))))return;
 Object state=WaterReflect.call(level,"getBlockState",pos),block=WaterReflect.call(state,"getBlock"),registry=WaterReflect.get(WaterReflect.type("net.minecraft.core.registries.BuiltInRegistries"),"BLOCK");
 if(!WaterReflect.call(registry,"getKey",block).toString().equals("minecraft:water_cauldron"))return;
 if(!WaterReflect.yes(WaterReflect.call(player,"mayInteract",level,pos)))return;
 if(!WaterReflect.yes(WaterReflect.get(level,"isClientSide"))){
 Object property=null;for(Object p:(Iterable<?>)WaterReflect.call(state,"getProperties"))if(WaterReflect.call(p,"getName").equals("level"))property=p;
 if(property==null)return;int servings=((Number)WaterReflect.call(state,"getValue",property)).intValue();if(servings<=0)return;
 Object next=servings==1?WaterReflect.call(WaterReflect.call(registry,"get",WaterReflect.call(WaterReflect.type("net.minecraft.resources.ResourceLocation"),"parse","minecraft:cauldron")),"defaultBlockState"):WaterReflect.call(state,"setValue",property,servings-1);
 // Creative keeps the source and bottle; survival exchanges exactly one serving.
 if(!WaterReflect.yes(WaterReflect.get(WaterReflect.call(player,"getAbilities"),"instabuild"))){if(!WaterReflect.yes(WaterReflect.call(level,"setBlock",pos,next,3)))return;WaterReflect.call(held,"shrink",1);Object bottle=WaterReflect.make("net.minecraft.world.item.ItemStack",Survival.WATER.get());if(WaterReflect.yes(WaterReflect.call(held,"isEmpty")))WaterReflect.call(player,"setItemInHand",WaterReflect.call(event,"getHand"),bottle);else if(!WaterReflect.yes(WaterReflect.call(WaterReflect.call(player,"getInventory"),"add",bottle)))WaterReflect.call(player,"drop",bottle,false);}
 WaterReflect.call(player,"displayClientMessage",WaterReflect.call(WaterReflect.type("net.minecraft.network.chat.Component"),"literal","Botella rellenada: agua de depósito / lluvia. El agua de río necesita hervirse."),true);
 }
 WaterReflect.call(event,"setCancellationResult",WaterReflect.get(WaterReflect.type("net.minecraft.world.InteractionResult"),"SUCCESS"));WaterReflect.call(event,"setCanceled",true);
 }catch(Throwable e){System.err.println("[Water sources] "+e);}}
}
