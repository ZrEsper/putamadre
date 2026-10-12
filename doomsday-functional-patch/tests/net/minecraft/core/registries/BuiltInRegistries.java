package net.minecraft.core.registries;
import dev.zomboid.decor.*;
public class BuiltInRegistries {public static Registry BLOCK=new Registry(),ITEM=new Registry();public static class Registry {public String getKey(Object o){return R.get(o,"id").toString();}}}
