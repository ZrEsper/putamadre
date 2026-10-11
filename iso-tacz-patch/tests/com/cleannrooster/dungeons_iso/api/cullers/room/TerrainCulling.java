package com.cleannrooster.dungeons_iso.api.cullers.room;
public class TerrainCulling {public static java.util.Set<Object> hidden=new java.util.HashSet<>();public static boolean shouldRemove(Object state,int x,int y,int z){return hidden.contains(new net.minecraft.core.BlockPos(x,y,z));}}
