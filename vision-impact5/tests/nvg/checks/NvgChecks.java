package checks;
import java.util.*;import net.minecraft.world.item.*;import net.minecraft.server.level.ServerPlayer;import net.minecraft.client.Minecraft;import com.raiiiden.warborn.common.item.*;import com.raiiiden.warborn.common.util.*;import com.raiiiden.warborn.client.util.NvgClient2;import com.raiiiden.warborn.client.shader.*;import com.raiiiden.warborn.client.network.ClientBatteryUpdateHandler;import com.raiiiden.warborn.common.network.*;import com.raiiiden.warborn.common.event.HelmetBatteryTickHandler;
public class NvgChecks {
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 public static class Tag{public Map<String,Object> values=new HashMap<>();public int getInt(String s){return ((Number)values.getOrDefault(s,0)).intValue();}public void putInt(String s,int n){values.put(s,n);}public void remove(String s){values.remove(s);}public boolean getBoolean(String s){return Boolean.TRUE.equals(values.get(s));}public void putBoolean(String s,boolean b){values.put(s,b);}public String getString(String s){return (String)values.getOrDefault(s,"");}public void putString(String s,String v){values.put(s,v);}}
 public static class World{public long getGameTime(){return 1;}public boolean isClientSide(){return false;}}
 public static class Renderer{public Object effect;public Object currentEffect(){return effect;}}
 static ItemStack helmet(WBArmorItem item,String mode,int energy){ItemStack s=new ItemStack(item);s.goggles=new ItemStack(new GogglesItem(mode));s.battery=new ItemStack(new NVGBatteryItem());s.battery.tag.putInt("BatteryEnergy",energy);return s;}
 static String active(ServerPlayer p){return ClientVisionState.getActive(p.uuid);}
 static void tick(){NvgClient2.tick(null);}
 public static void lowered(ServerPlayer p){NvgLink2.wear(p);((WBArmorItem)p.helmet.item).setTopOpen(p.helmet,false);}
 public static void main(String[]a){
  Minecraft mc=Minecraft.getInstance();ServerPlayer p=new ServerPlayer();mc.player=p;mc.level=p.world;WBArmorItem item=new WBArmorItem();p.helmet=helmet(item,"nvg",50000);
  tick();ok(active(p).isEmpty()&&NvgLink2.topOpen(p.helmet),"equip starts up and off");NvgClient2.key(new Object());ok(active(p).isEmpty(),"N starts animation without prematurely enabling filter");lowered(p);tick();ok(active(p).equals("nvg"),"physical lowering activates matching filter");ok(NvgClient2.ownsPostEffect(),"lowered powered visor reserves native post-filter");
  int packets=ModNetworking.packets.size(),changes=ShaderRegistry.INSTANCE.calls;for(int i=0;i<50;i++)tick();ok(ModNetworking.packets.size()==packets&&ShaderRegistry.INSTANCE.calls==changes,"stable closed visor does not spam");
  ShaderRegistry.INSTANCE.enabled.put("nvg",false);tick();ok(ShaderRegistry.INSTANCE.enabled.get("nvg"),"lost flag repaired");
  ((WBArmorItem)p.helmet.item).setTopOpen(p.helmet,true);tick();ok(active(p).isEmpty(),"raising disables");ok(!NvgClient2.ownsPostEffect(),"raised visor releases medical filter");lowered(p);tick();ok(active(p).equals("nvg"),"lowering restores");
  p.helmet=helmet(item,"thermal_white",30000);tick();ok(active(p).isEmpty(),"new helmet stays off until lowered manually");lowered(p);tick();ok(active(p).equals("thermal_white")&&ShaderRegistry.INSTANCE.enabled.get("twvg")&&!ShaderRegistry.INSTANCE.enabled.get("nvg"),"correct new filter replaces old");
  for(String mode:new String[]{"nvg","simple_nvg","digital","thermal","thermal_white","thermal_black"}){p.helmet=helmet(item,mode,30000);tick();ok(active(p).isEmpty(),"each helmet starts off");lowered(p);tick();ok(active(p).equals(mode),"each mode follows lowering");}
  ClientBatteryUpdateHandler.handle(0);NvgClient2.empty();for(int i=0;i<25;i++)tick();ok(active(p).isEmpty(),"empty battery cannot reactivate");p.helmet=helmet(item,"nvg",50000);tick();lowered(p);tick();ok(active(p).equals("nvg"),"replacement powered helmet works after manual lowering");
  ShaderRegistry registry=ShaderRegistry.INSTANCE;registry.active("nvg");mc.gameRenderer.effect=new Object();Object expected=mc.gameRenderer.effect;NvgClient2.loaded(registry);NvgClient2.recover(registry);ok(registry.active().equals("nvg"),"healthy shader retained");mc.gameRenderer.effect=null;NvgClient2.recover(registry);ok(registry.active()==null,"lost chain can reload");registry.active("nvg");mc.gameRenderer.effect=expected;NvgClient2.loaded(registry);mc.gameRenderer.effect=new Object();NvgClient2.recover(registry);ok(registry.active()==null,"damage replacement chain can reload");
  int frames=registry.frames;NvgClient2.frame();ok(registry.frames==frames+1&&active(p).equals("nvg"),"frame hook drives filter independently of level-stage callback");
  NvgLink2.server(p);HelmetBatteryTickHandler.batteryEnergyCache.put(p.uuid,50000);NvgSupport33.player(p);ok(p.effect!=null&&p.effect.duration==-1,"native infinite night vision remains applied");p.effect=null;NvgSupport33.player(p);ok(p.effect!=null&&p.effect.duration==-1,"effect removed by damage is repaired");
  ((WBArmorItem)p.helmet.item).setTopOpen(p.helmet,true);NvgLink2.server(p);NvgSupport33.player(p);ok(p.effect==null&&!HelmetBatteryTickHandler.nvgActiveMap.containsKey(p.uuid),"raised visor stops server effect and drain");ok(NvgLink2.authorize("nvg",p).isEmpty(),"server refuses raised visor");
  int energy=50000;for(int second=1;second<=900;second++){energy-=NvgSupport33.drain(999,p);if(second<900)ok(energy>0,"battery active before 15 minutes");}ok(energy==0,"900 active seconds");
  p.helmet.visor=false;tick();NvgClient2.key(new Object());tick();ok(active(p).isEmpty(),"no independent manual fallback without visor");
  p.helmet=helmet(item,"digital",30000);tick();lowered(p);tick();mc.level=new World();tick();ok(active(p).isEmpty(),"reconnect starts off");
  p.helmet=helmet(item,"nvg",30000);p.helmet.goggles=null;NvgLink2.wear(p);ok(!NvgLink2.topOpen(p.helmet),"non NVG visor keeps original default");ok(!p.helmet.tag.values.containsKey("WB_NVG3Initialized"),"do not rewrite non NVG helmets");
  System.out.println("NVG4_CHECKS="+checks);
 }
}
