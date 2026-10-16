package net.minecraft.resources;public record ResourceLocation(String value){public static ResourceLocation parse(String v){return new ResourceLocation(v);}public String toString(){return value;}}
