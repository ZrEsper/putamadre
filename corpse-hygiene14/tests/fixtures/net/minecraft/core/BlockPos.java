package net.minecraft.core;public record BlockPos(int x,int y,int z){public BlockPos below(){return new BlockPos(x,y-1,z);}}
