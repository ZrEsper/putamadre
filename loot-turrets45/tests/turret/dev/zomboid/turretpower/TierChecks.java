package dev.zomboid.turretpower;
import java.util.*;import dev.zomboid.turretpower.Checks.*;import dev.zomboid.survival.MachineEntity;import com.entropy.tacz_turrets.turret.TurretEntity;
public class TierChecks{
 static int checks;static void ok(boolean b,String s){checks++;if(!b)throw new AssertionError(s);}
 public static class Data{public int tier;public Object get(Object key){return tier;}public void set(Object key,Object value){tier=(int)value;}public void define(Object key,Object value){tier=(int)value;}}
 public static class Attribute{double base;public Attribute(double b){base=b;}public double getBaseValue(){return base;}public void setBaseValue(double v){base=v;}}
 public static class Tag{int tier;public void putInt(String s,int n){tier=n;}public int getInt(String s){return tier;}}
 public record Stack(Object getItem){}
 public record Context(Stack getItemInHand){}
 public static class Event{public Map<String,Object> items=new HashMap<>();public Object getRegistryKey(){return net.minecraft.core.registries.Registries.ITEM;}public void register(Object k,Object id,java.util.function.Supplier<?> s){items.put(id.toString(),s.get());}}
 public static void main(String[]args){Event registry=new Event();Tier4.register(registry);ok(registry.items.size()==2,"two upgrade items registered alongside original");for(int level=0;level<3;level++){
  W w=new W();TurretEntity t=new TurretEntity(w);Tier4.define(t.data);Tier4.place(t,new Context(new Stack("tacz_turrets:"+Tier4.IDS[level])));ok(Tier4.tier(t)==level,"placement resolves crafted item tier "+level);ok(t.health==Tier4.HEALTH[level]&&t.getMaxHealth()==Tier4.HEALTH[level],"tier maximum and initial health "+level);ok(Tier4.range(t)==Tier4.RANGES[level],"tier range "+level);ok(Tier4.item(t)==(level==0?"base":registry.items.get("tacz_turrets:"+Tier4.IDS[level])),"pickup returns same tier "+level);
  Tag tag=new Tag();Tier4.save(t,tag);TurretEntity loaded=new TurretEntity(w);Tier4.load(loaded,tag);ok(Tier4.tier(loaded)==level&&loaded.getMaxHealth()==Tier4.HEALTH[level],"saved tier and attributes restored "+level);t.health=Tier4.HEALTH[level]/2;Tier4.stats(t);ok(t.health==Tier4.HEALTH[level]/2,"attribute sync cannot refill damage "+level);
  T target=new T(w);t.target=target;MachineEntity gen=new MachineEntity();P origin=new P(0,0,0);w.machines.put(origin,gen);w.blocks.put(origin,new S("doomsday_decoration:generator"));MachineEntity.GENERATORS.put(w,Set.of(origin));int energy=gen.energy.stored;
  ok(!Link.allowed(t),"first acquisition delay "+level);w.time=16;Fire shot=new Fire(t);Link.fire(shot);ok(!shot.canceled&&gen.energy.stored==energy-100,"real round costs generator energy "+level);
  int interval=Tier4.INTERVALS[level];for(int dt=1;dt<interval;dt++){w.time=16+dt;if(Link.allowed(t))throw new AssertionError("early shot at tier "+level+" dt="+dt);}checks++;w.time=16+interval;ok(Link.allowed(t),"exact tier cooldown "+level);Link.fire(new Fire(t));ok(gen.energy.stored==energy-200,"one energy charge per tier shot "+level);
  t.distance=Tier4.RANGES[level]*Tier4.RANGES[level]+.01;ok(!Link.allowed(t),"outside tier range blocked "+level);t.distance=4;t.visible=false;ok(!Link.allowed(t),"walls block each tier "+level);t.visible=true;gen.energy.stored=0;ok(!Link.allowed(t),"no generator blocks each tier "+level);
  Object original="original";ok(Tier4.model(original,t).equals(level==0?original:level==1?"industrial-model":"tactical-model"),"native per-tier model "+level);ok(Tier4.texture(original,t).equals(level==0?original:level==1?"industrial-active":"tactical-active"),"matching per-tier texture "+level);
 }
 Tag invalid=new Tag();invalid.tier=999;TurretEntity corrupt=new TurretEntity(new W());Tier4.load(corrupt,invalid);ok(Tier4.tier(corrupt)==2,"NBT tier clamped");System.out.println("TURRET_THREE_TIER_CHECKS="+checks);
 }
}
