package dev.zomboid.clans;
/** Only the finishing damage attributed to a real player qualifies for wave corpse loot. */
public final class WaveLoot {
 static boolean wave(Object mob){return !R.call(Pve.tag(mob),"getString","FactionPveWave").toString().isEmpty();}
 static boolean player(Object source){Object killer=R.call(source,"getEntity");if(killer==null||!R.type("net.minecraft.server.level.ServerPlayer").isInstance(killer))return false;for(Class<?> type=killer.getClass();type!=null;type=type.getSuperclass())if(type.getName().equals("net.neoforged.neoforge.common.util.FakePlayer"))return false;return true;}
 static boolean block(Object mob,Object source){return wave(mob)&&(R.yes(R.call(Pve.tag(mob),"getBoolean","FactionPveCleanupDeath"))||!player(source));}
 public static void death(Object event){Object mob=R.call(event,"getEntity");if(R.yes(R.call(R.call(mob,"level"),"isClientSide"))||!wave(mob))return;R.call(Pve.tag(mob),"putBoolean","FactionPveNoLoot",block(mob,R.call(event,"getSource")));R.call(Pve.tag(mob),"putBoolean","FactionPveDeathRecorded",true);}
 public static void drops(Object event){Object mob=R.call(event,"getEntity");if(R.yes(R.call(R.call(mob,"level"),"isClientSide"))||!wave(mob))return;Object tag=Pve.tag(mob);boolean denied=R.yes(R.call(tag,"getBoolean","FactionPveCleanupDeath"))||(R.yes(R.call(tag,"getBoolean","FactionPveDeathRecorded"))?R.yes(R.call(tag,"getBoolean","FactionPveNoLoot")):!player(R.call(event,"getSource")));if(!denied)return;R.call(Pve.tag(mob),"putBoolean","FactionPveNoLoot",true);R.call(R.call(event,"getDrops"),"clear");R.call(event,"setCanceled",true);}
 public static void experience(Object event){Object mob=R.call(event,"getEntity");if(!R.yes(R.call(R.call(mob,"level"),"isClientSide"))&&wave(mob)&&R.yes(R.call(Pve.tag(mob),"getBoolean","FactionPveNoLoot")))R.call(event,"setDroppedExperience",0);}
}
