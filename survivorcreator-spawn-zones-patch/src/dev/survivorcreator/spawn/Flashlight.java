package dev.survivorcreator.spawn;
import java.util.function.Supplier;
public final class Flashlight {
 static Object registry(){return R.field(R.type("net.minecraft.core.registries.BuiltInRegistries"),"ITEM");}
 static String id(Object stack){return R.call(registry(),"getKey",R.call(stack,"getItem")).toString();}
 static boolean flashlight(Object stack){String id=id(stack);return id.equals("flashlight:flashlight_off")||id.equals("flashlight:flashlight_on");}
 public static void equip(Object player){try{Object reg=registry(),on=R.call(reg,"get",R.call(R.type("net.minecraft.resources.ResourceLocation"),"parse","flashlight:flashlight_on"));if(on==null||R.call(reg,"getKey",on).toString().equals("minecraft:air")){System.err.println("[SurvivorCreator] flashlight:flashlight_on missing; starter flashlight retained");return;}
 Object inv=R.call(player,"getInventory"),old=R.call(player,"getOffhandItem");if(((Number)R.call(old,"getCount")).intValue()>0&&!id(old).equals("minecraft:air")&&!flashlight(old)){Object remaining=R.call(old,"copy");if(!(Boolean)R.call(inv,"add",remaining))R.call(player,"drop",remaining,false);}
 int count=((Number)R.call(inv,"getContainerSize")).intValue();for(int slot=0;slot<count;slot++){Object s=R.call(inv,"getItem",slot);if(((Number)R.call(s,"getCount")).intValue()>0&&flashlight(s)){R.call(s,"shrink",1);break;}}
 R.call(player,"setItemSlot",R.field(R.type("net.minecraft.world.entity.EquipmentSlot"),"OFFHAND"),R.make("net.minecraft.world.item.ItemStack",on,1));
 try{Object attachment=((Supplier<?>)R.field(R.type("net.flashlight.network.FlashlightModVariables"),"PLAYER_VARIABLES")).get();Object data=R.call(player,"getData",attachment);data.getClass().getField("BatteryFlashlight").setDouble(data,100);R.call(data,"markSyncDirty");}catch(Exception e){System.err.println("[SurvivorCreator] Starter flashlight battery: "+e);}
 }catch(Exception e){System.err.println("[SurvivorCreator] Starter flashlight equipment: "+e);}}
}
