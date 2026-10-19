package net.minecraft.core.registries;
public class BuiltInRegistries{public static final Registry BLOCK=new Registry();public static class Registry{public boolean containsKey(Object key){return true;}public Object get(Object key){return new Block(key);} }public record Block(Object key){public Object defaultBlockState(){return new dev.zomboid.gore.Check48.State(false,true);}}}
