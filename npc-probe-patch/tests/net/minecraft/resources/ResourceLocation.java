package net.minecraft.resources;public record ResourceLocation(String id){public String toString(){return id;}public static ResourceLocation parse(String id){return new ResourceLocation(id);}}
