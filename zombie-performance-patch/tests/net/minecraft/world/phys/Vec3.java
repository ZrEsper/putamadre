package net.minecraft.world.phys;public record Vec3(double x,double y,double z){public Vec3 add(double a,double b,double c){return new Vec3(x+a,y+b,z+c);}}
