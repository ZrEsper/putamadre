import dev.zomboid.hordes.*;import net.minecraft.world.entity.*;import net.minecraft.world.entity.monster.Zombie;import net.minecraft.core.Holder;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.entity.ai.attributes.*;
public class NativeChecks{
 static int count;static void eq(double a,double b,String s){count++;if(Math.abs(a-b)>1e-6)throw new AssertionError(s+" "+a);}
 public static void main(String[] args)throws Exception{
  WorldTuning.reset();Mob mob=new Zombie();ResourceLocation id=new ResourceLocation("damage");
  var stat=Class.forName("FixtureZI").getMethod("stat",Mob.class,Holder.class,ResourceLocation.class,double.class);
  stat.invoke(null,mob,Attributes.ATTACK_DAMAGE,id,-.5);eq(mob.damage.value(),3,"native negative modifier halves damage");
  stat.invoke(null,mob,Attributes.ATTACK_DAMAGE,id,-1.);eq(mob.damage.value(),0,"native zero damage modifier");
  stat.invoke(null,mob,Attributes.ATTACK_DAMAGE,id,0.);eq(mob.damage.value(),6,"native reset removes reduction");
  stat.invoke(null,mob,Attributes.ATTACK_DAMAGE,id,1.);eq(mob.damage.value(),12,"native double damage");
  stat.invoke(null,mob,Attributes.ATTACK_DAMAGE,id,1.);eq(mob.damage.value(),12,"native repeated updates do not stack");
  var speed=Class.forName("FixtureSpeed").getMethod("speed",int.class,Object.class);var cap=Class.forName("FixtureSpeed").getMethod("cap",float.class,int.class,boolean.class,Object.class);
  WorldTuning.values.put("velocidad_tontos",2.);eq(((Number)speed.invoke(null,0,mob)).doubleValue(),.56,"actual native speed doubled");
  eq(((Number)cap.invoke(null,1f,0,true,mob)).doubleValue(),.56,"actual navigation cap permits doubled speed");
  WorldTuning.values.put("velocidad_tontos",0.);eq(((Number)cap.invoke(null,1f,0,true,mob)).doubleValue(),0,"actual navigation cap supports stationary zombie");
  eq(dev.zomboid.reactive.Rules.memory(1),180,"actual patched memory default");WorldTuning.values.put("ia_agresividad",2.);eq(dev.zomboid.reactive.Rules.cooldown(1),11,"actual patched attack cadence");
  System.out.println("Passed "+count+" checks executing selected patched attribute/navigation/AI methods with API doubles.");
 }
}
