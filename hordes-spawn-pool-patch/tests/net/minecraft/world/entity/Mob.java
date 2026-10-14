package net.minecraft.world.entity;
import net.minecraft.core.Holder;import net.minecraft.world.entity.ai.attributes.*;import net.minecraft.nbt.CompoundTag;
public class Mob{
 public final AttributeInstance damage=new AttributeInstance(6),speed=new AttributeInstance(.2),health=new AttributeInstance(20);
 public final CompoundTag tag=new CompoundTag();public float hp=20;
 public AttributeInstance getAttribute(Holder<Attribute> key){return key==Attributes.ATTACK_DAMAGE?damage:key==Attributes.MOVEMENT_SPEED?speed:health;}
 public float getMaxHealth(){return (float)health.value();}public float getHealth(){return hp;}public void setHealth(float value){hp=value;}
 public CompoundTag getPersistentData(){return tag;}
}
