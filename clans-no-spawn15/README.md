# Clans .15 — no new creatures in claimed chunks

Based on the exact uploaded Clans `.14-expanded-territory` JAR; preserves expanded territory, UI, claims, ownership, progression, waves and existing configuration. [Player guide](LEEME.txt).

The original `Pve.join` checks a small zombie ID list, reads the raw chunk map without the dimension-aware API, and returns early for active waves. The patch replaces its public entry point with `SpawnClaims15.join`; it preserves the original method under `zp15OriginalJoin` solely for the native orphan-wave validation. All fresh `Mob` additions are rejected inside `FactionManager.getFactionByChunk(level, chunk)` territories, including direct additions by mods. `SurfaceSpawns.at` rejects claimed locations before ground scanning, so PvE attempts choose outside locations and cannot bypass the filter.

Entities read from disk and previously admitted entities retain their persistent `zp15_existing_creature` marker. Movement is never intercepted. No scanning, despawning, entity removal, chunk force-loading or mob-cap changes are introduced. Client events and other mods' canceled join events are left untouched. New spawns outside claims are admitted, and unclaiming takes effect without a stale cache. Native abandoned-wave cleanup still runs for wave-tagged entities.

Native conversions replace an existing creature and must not delete it when its initial insertion is rejected. A weak rejection set records only entities denied by this policy. `LivingConversionEvent.Post` may re-admit that exact outcome only when its source was already admitted, the outcome is not removed/added, and its initial rejection was ours. The new admission repeats the event pipeline and respects other mods; a refused retry loses the marker. This preserves underwater zombies, cured villagers and other native conversions without exempting unrelated new creatures.

Placed TaCZ turrets are manufactured defenses, and are exempt despite extending `Mob`. The Hordes `PlayerZombie` interface is exempt as an existing player's transformation; this preserves the inventory/corpse ownership added by Hordes .43. Players, corpses, items, vehicles and bullets do not extend `Mob` and remain available.

`ProtectionEventHandler.onMobGriefing` now yields for normal zombies and the mutant namespaces, removing FactionFriction's territory veto. It does not force the global gamerule or override grief restrictions set by other mods. Its original logic still handles other entity types. The player protection handlers are unchanged.

## Build and verify

From the existing checkout (no new worktree):

```sh
python clans-no-spawn15/build.py
python clans-no-spawn15/test.py
python clans-no-spawn15/package.py
```

Uses Java 21, Python 3 and the SHA-256-pinned ECJ/ASM artifacts from `hordes-melee-interiors-patch/build/tools`. The uploaded input is pinned by `input.json` and kept in `inputs`. `validation.json` lists the exact three modified classes and the helper/conversion subscriber; the original mod metadata and every other archive entry remain identical.

Tests execute the exact native `.14` and patched `.15` join/grief method bodies with their reachable helper methods, a minimal wave-map initializer and API doubles. They reproduce three original behaviors (animal spawn, wave bypass, zombie grief veto), then pass 69 checks for the fix: fresh hostile/friendly mobs, spawn origins, waves, saved mobs, reattachment, movement, dimension rules, negative chunk coordinates, unclaim, manufacturing defenses, infected players, non-mob entities, orphan cleanup and other grief restrictions. ASM BasicVerifier checks modified methods. These checks do not constitute a full Minecraft/modpack runtime test.
