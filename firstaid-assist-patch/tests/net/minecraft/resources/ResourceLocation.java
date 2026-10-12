package net.minecraft.resources;public record ResourceLocation(String name){public static ResourceLocation parse(String s){return new ResourceLocation(s);}}
