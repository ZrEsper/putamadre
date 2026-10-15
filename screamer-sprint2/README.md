# Screamer sprint 2

Build: `python screamer-sprint2/build.py` after `python wall-breach41/build.py`. Input is the SHA256-pinned horror.1 artifact. Tool hashes are pinned. Only Screamers and Screamers$Screamer change; Rally is added. Audio, egg, translations, renderer, events, dependencies and mod metadata remain byte-identical. No fixture classes enter the artifact.

Five asserted edits: native health 48 -> 40, base movement .48 -> .50, navigation 1.35 -> 1.50, stopping distance squared 9 -> 1.44, and replace player-centered HordeScent.alert with a caller-centered Rally invocation. ASM BasicVerifier validates modified methods.

Hordes 41 is required for the ordinary-zombie stat override fix: ZombieIndividual.stats delegates recognized screamers to ScreamerStats, removing stale individual pace/health and world-aggression speed modifiers; SiegeZombie.tick applies the same exception before its special siege lottery. Other mobs remain unchanged. This preserves generic stat tuning for ordinary zombies and gives the screamer its own 40 HP and movement base.

Rally only visits loaded infected mobs, excludes screamers/dead/distant/currently-targeting mobs, picks the nearest 128, records the caller position in native noise memory and initiates a throttled 16-block waypoint. It never sets the caller as an attack target. Native Hordes noise goals continue the investigation. Twelve-second cries refresh the 24-second point memory. No entities are spawned and no chunks are force-loaded.

Tests execute the actual patched Screamer.tick, the actual Rally helper and Hordes ScreamerStats with API doubles. They verify native sound and pose behavior, movement requests, position origin, filtering, cap, old HP clamping without healing, stale modifier removal and ordinary-zombie exclusion. They do not measure actual Minecraft movement speed or guarantee routing in a modded world. Manual test instructions are in GUIA.md.
