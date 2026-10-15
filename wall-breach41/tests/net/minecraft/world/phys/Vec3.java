package net.minecraft.world.phys;public record Vec3(double x,double y,double z){public Vec3 add(double x,double y,double z){return new Vec3(this.x+x,this.y+y,this.z+z);}}
