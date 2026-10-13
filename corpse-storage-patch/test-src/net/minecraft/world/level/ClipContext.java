package net.minecraft.world.level;public record ClipContext(Object from,Object to,Block block,Fluid fluid,Object entity){public enum Block{COLLIDER}public enum Fluid{NONE}}
