package ichttt.mods.firstaid.common.util;
public class CommonUtils {public static Object getDamageModel(Object p){return ((net.minecraft.world.entity.player.Player)p).model;}public static void syncDamageModel(Object p){}public static void killPlayerDirectly(Object p,Object source){((net.minecraft.world.entity.player.Player)p).alive=false;}}
