package me.xjqsh.lesraisinsarmor.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {
    public static ModConfigSpec.BooleanValue enableArmorSetEffect;
    public static ModConfigSpec.BooleanValue enableArmorAttribute;

    public static ModConfigSpec init() {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("general");
        enableArmorSetEffect = builder.comment("Enable armor set effect").define("enableArmorSetEffect", true);
        enableArmorAttribute = builder.comment("Enable armor attribute").define("enableArmorAttribute", true);
        builder.pop();
        return builder.build();
    }
}
