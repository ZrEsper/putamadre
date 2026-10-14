package net.minecraft.world.entity.ai.attributes;
import java.util.*;import net.minecraft.resources.ResourceLocation;
public class AttributeInstance{
 public final double base;public final Map<ResourceLocation,AttributeModifier> modifiers=new HashMap<>();
 public AttributeInstance(double base){this.base=base;}public double getBaseValue(){return base;}
 public AttributeModifier getModifier(ResourceLocation id){return modifiers.get(id);}public boolean removeModifier(ResourceLocation id){return modifiers.remove(id)!=null;}public void addPermanentModifier(AttributeModifier modifier){modifiers.put(modifier.id(),modifier);}
 public double value(){double v=base;for(var m:modifiers.values())v*=1+m.amount();return v;}
}
