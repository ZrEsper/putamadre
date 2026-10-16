package dev.zomboid.clans;
/** Only fresh creature insertion is blocked. No entity scanning or removal. */
public final class SpawnClaims15 {
 static final String SEEN="zp15_existing_creature";
 public static boolean claimed(Object world,Object chunk){Object manager=R.call(R.type("me.danialisntcool.factionfriction.data.FactionManager"),"get",R.call(world,"getServer"));return manager!=null&&R.call(manager,"getFactionByChunk",world,chunk)!=null;}
 public static boolean spot(Object world,int x,int z){return !claimed(world,R.make("net.minecraft.world.level.ChunkPos",Math.floorDiv(x,16),Math.floorDiv(z,16)));}
 static boolean playerZombie(Object entity){try{return R.type("net.smileycorp.hordes.common.entities.PlayerZombie").isInstance(entity);}catch(IllegalStateException absent){return false;}}
 static boolean machine(Object entity){String id=R.call(R.get(R.type("net.minecraft.core.registries.BuiltInRegistries"),"ENTITY_TYPE"),"getKey",R.call(entity,"getType")).toString();return id.startsWith("tacz_turrets:");}
 public static void join(Object event){Object world=R.call(event,"getLevel");if(R.yes(R.call(world,"isClientSide"))||R.yes(R.call(event,"isCanceled")))return;Object entity=R.call(event,"getEntity");if(!R.type("net.minecraft.world.entity.Mob").isInstance(entity))return;Object tag=R.call(entity,"getPersistentData");
  // Keep native orphan-wave cleanup; active waves receive no spawn exemption.
  if(!R.call(tag,"getString","FactionPveWave").toString().isEmpty()){R.call(R.type("dev.zomboid.clans.Pve"),"zp15OriginalJoin",event);if(R.yes(R.call(event,"isCanceled")))return;}
  boolean existing=R.yes(R.call(event,"loadedFromDisk"))||R.yes(R.call(tag,"getBoolean",SEEN))||playerZombie(entity);
  if(!existing&&!machine(entity)&&claimed(world,R.call(entity,"chunkPosition"))){R.call(event,"setCanceled",true);return;}
  R.call(tag,"putBoolean",SEEN,true);
 }
 /** Removes this mod's claim veto, without enabling the global mobGriefing rule. */
 public static boolean zombieGrief(Object event){Object entity=R.call(event,"getEntity"),world=R.call(entity,"level");if(R.yes(R.call(world,"isClientSide")))return false;String id=R.call(R.get(R.type("net.minecraft.core.registries.BuiltInRegistries"),"ENTITY_TYPE"),"getKey",R.call(entity,"getType")).toString();return id.contains("zombie")||id.equals("minecraft:husk")||id.equals("minecraft:drowned")||id.startsWith("mutantszombies:")||id.startsWith("mutants_l4d2:");}
}
