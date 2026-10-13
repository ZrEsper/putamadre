package net.minecraft.core.registries;
import net.minecraft.world.level.block.Block;
public class BuiltInRegistries {
    public static final Registry BLOCK=new Registry(), ENTITY_TYPE=new Registry();
    public static class Registry { public Object getKey(Object object){return object instanceof Block block?block.id:object.toString();} }
}
