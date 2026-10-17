package dev.zomboidmelee.boxing;
/** Use the same base attribute as a fully charged normal bare-handed attack, doubled once. */
public final class HeavyDamage23{
 public static Float amount(Object player){double base=R.num(R.call(player,"getAttributeValue",R.get(R.type("net.minecraft.world.entity.ai.attributes.Attributes"),"ATTACK_DAMAGE")));if(!Double.isFinite(base)||base<0)base=0;return (float)Math.min(Float.MAX_VALUE,base*2);}
}
