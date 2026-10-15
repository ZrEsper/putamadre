package net.minecraft.resources;public record ResourceLocation(String value){public static ResourceLocation fromNamespaceAndPath(String a,String b){return new ResourceLocation(a+":"+b);}}
