package dev.zomboid.storage;

public final class CorpseOutline {
    public static boolean remainsId(String id) { return id.startsWith("doomsday_decoration:remains_") || id.equals("zombie_remains_loot:skeletal_remains"); }
    public static boolean block(Object object) {
        if (object == null || !Reflect.type("net.minecraft.world.level.block.Block").isInstance(object)) return false;
        Object registry=Reflect.get(Reflect.type("net.minecraft.core.registries.BuiltInRegistries"),"BLOCK");
        return remainsId(Reflect.call(registry,"getKey",object).toString());
    }
}
