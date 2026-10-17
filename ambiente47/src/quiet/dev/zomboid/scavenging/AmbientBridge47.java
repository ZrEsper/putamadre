package dev.zomboid.scavenging;
public final class AmbientBridge47 {
 static Boolean available;
 public static boolean bonus(Search.Target target,Object player){if(PlayerStorage.owned(target))return true;Class<?>type;try{type=Class.forName("dev.zomboid.hordes.EnvironmentLoot47");}catch(ClassNotFoundException absent){return false;}if(target.corpse!=null||target.entity!=null)return R.yes(R.call(R.type("dev.zomboid.hordes.WorldTuning"),"flag","cadaveres_loot_todos"));return R.yes(R.call(type,"searched",target.be,target.level));}
}
