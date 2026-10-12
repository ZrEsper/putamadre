package dev.zomboid.firstaid;
import java.util.*;
public final class ZeroParts {
 static List<Object> parts(Object model){List<Object> a=new ArrayList<>();for(Object p:(Iterable<?>)model)a.add(p);return a;}
 static float[] current(List<Object> parts){float[] a=new float[parts.size()];for(int i=0;i<a.length;i++)a[i]=((Number)R.get(parts.get(i),"currentHealth")).floatValue();return a;}
 public static float rawHealth(Object model,Object player){List<Object> p=parts(model);float[] max=new float[p.size()];for(int i=0;i<max.length;i++)max[i]=((Number)R.call(p.get(i),"getMaxHealth")).floatValue();return Rules.health(current(p),max,player==null?20:((Number)R.call(player,"getMaxHealth")).floatValue());}
 public static float health(Object model,Object player){float value=rawHealth(model,player);return value<=0&&Revival.active(model)?1:value;}
 public static boolean dead(Object model,Object player){return player!=null&&!R.yes(R.call(player,"isAlive"))||health(model,player)<=0;}
 public static void tick(Object model,Object player){Object level=R.call(player,"level");if(R.yes(R.call(level,"isClientSide")))return;
 Revival.tick(model,player);if(R.yes(R.get(model,"criticalConditionActive"))&&!Revival.active(model)){R.set(model,"criticalConditionActive",false);R.call(model,"clearUnconsciousState");R.call(model,"clearUnconsciousPenalties",player);R.call(model,"scheduleResync");}
 Object tag=R.call(player,"getPersistentData");int old=((Number)R.call(tag,"getInt","FirstAidZeroParts")).intValue();int mask=Rules.mask(old,current(parts(model)));if(mask!=old)R.call(tag,"putInt","FirstAidZeroParts",mask);
 attribute(player,"MOVEMENT_SPEED","zero_parts_mobility",Rules.speed(mask));attribute(player,"ATTACK_DAMAGE","zero_parts_strength",Rules.damage(mask));attribute(player,"ATTACK_SPEED","zero_parts_attack_speed",Rules.attackSpeed(mask));
 }
 static void attribute(Object player,String attribute,String name,double penalty){Object value=R.call(player,"getAttribute",R.get(R.type("net.minecraft.world.entity.ai.attributes.Attributes"),attribute));if(value==null)return;Object id=R.call(R.type("net.minecraft.resources.ResourceLocation"),"parse","firstaid:"+name),old=R.call(value,"getModifier",id);if(old!=null&&R.num(R.call(old,"amount"))==penalty)return;if(old!=null)R.call(value,"removeModifier",id);if(penalty!=0)R.call(value,"addTransientModifier",R.make("net.minecraft.world.entity.ai.attributes.AttributeModifier",id,penalty,R.get(R.type("net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation"),"ADD_MULTIPLIED_TOTAL")));}
}
