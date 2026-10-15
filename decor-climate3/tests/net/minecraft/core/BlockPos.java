package net.minecraft.core;
import dev.zomboid.decor.Checks.P;
public class BlockPos {public static P of(long value){return new P((int)(value>>38),(int)(value<<52>>52),(int)(value<<26>>38));}}
