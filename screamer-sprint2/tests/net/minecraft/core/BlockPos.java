package net.minecraft.core;public record BlockPos(int x,int y,int z){public long asLong(){return ((long)x&0x3ffffff)<<38|((long)z&0x3ffffff)<<12|((long)y&0xfff);}}
