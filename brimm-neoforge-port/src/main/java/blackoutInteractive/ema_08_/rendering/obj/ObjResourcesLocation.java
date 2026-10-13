package blackoutInteractive.ema_08_.rendering.obj;

import blackoutInteractive.ema_08_.rendering.obj.ArmorModelType;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public record ObjResourcesLocation(String name, ResourceLocation obj, ResourceLocation mtl, ResourceLocation png) {
    public ObjResourcesLocation {
        Objects.requireNonNull(obj, "Missing obj location");
        Objects.requireNonNull(mtl, "Missing mtl location");
        Objects.requireNonNull(png, "Missing png location");
    }

    private static ResourceLocation rl(StringBuilder path) {
        return ResourceLocation.fromNamespaceAndPath("brimm", path.toString());
    }

    private static StringBuilder objl() {
        return new StringBuilder("models/obj/");
    }

    private static StringBuilder mtll() {
        return new StringBuilder("models/mtl/");
    }

    private static StringBuilder pngl() {
        return new StringBuilder("textures/models/obj/");
    }

    public static ObjResourcesLocation ofBlock(String name) {
        return new ObjResourcesLocation(name, ObjResourcesLocation.rl(ObjResourcesLocation.objl().append("blocks/").append(name).append(".obj")), ObjResourcesLocation.rl(ObjResourcesLocation.mtll().append("blocks/").append(name).append(".mtl")), ObjResourcesLocation.rl(ObjResourcesLocation.pngl().append("blocks/").append(name).append(".png")));
    }

    public static ObjResourcesLocation ofArmor(ArmorModelType type, String name) {
        return new ObjResourcesLocation(name, ObjResourcesLocation.rl(ObjResourcesLocation.objl().append("armors/").append(type.locFolder).append(name).append(".obj")), ObjResourcesLocation.rl(ObjResourcesLocation.mtll().append("armors/").append(type.locFolder).append(name).append(".mtl")), ObjResourcesLocation.rl(ObjResourcesLocation.pngl().append("armors/").append(type.locFolder).append(name).append(".png")));
    }
}

