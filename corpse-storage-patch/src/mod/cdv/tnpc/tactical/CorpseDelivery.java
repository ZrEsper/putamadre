package mod.cdv.tnpc.tactical;
public final class CorpseDelivery {
    public static void prepare(Object mob) {
        // With the matching Remains patch, cargo stays on the NPC until LivingDropsEvent.
        // Never spawn loose items before NeoForge begins capturing death drops.
        try { Class.forName("dev.zomboid.remains.delivery.NpcCorpses"); }
        catch(ClassNotFoundException missing) { SurvivorPatch.deathOriginal14(mob); }
    }
}
