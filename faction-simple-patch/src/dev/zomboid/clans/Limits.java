package dev.zomboid.clans;
public final class Limits {
 public static int rule(String name,int fallback){try{return ((Number)R.call(R.get(R.type("me.danialisntcool.factionfriction.config.FFConfig"),name),"get")).intValue();}catch(Exception missing){return fallback;}}
 public static int level(Object tier){return switch(tier.toString()){case "COLONY"->2;case "COUNTRY"->3;default->1;};}
 public static int maximum(Object tier){return 4+2*(level(tier)-1);}
 public static boolean room(Object faction,int additional){return ((Number)R.call(faction,"getClaimCount")).intValue()+additional<=maximum(R.call(faction,"getTier"));}
 public static boolean launch(Object player){Object m=Clans.manager(R.call(player,"getServer")),f=Clans.mine(m,player);if(f==null)return false;if(!room(f,1)){Clans.say(player,"Límite de territorio alcanzado. Libera un chunk o sube de nivel.");return false;}return true;}
 public static boolean attack(Object attack,Object attacker,Object defender){long chunk=((Number)R.call(attack,"chunkLong")).longValue();boolean capital=((java.util.Set<?>)R.call(defender,"getCapitalChunks")).contains(chunk);return room(attacker,capital?((Number)R.call(defender,"getClaimCount")).intValue():1);}
}
