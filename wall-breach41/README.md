# Wall breach 41

Fixes the exact Hordes .40 artifact (SHA256 pinned in build.py), retaining its simple loot panel and all earlier changes. BreachAI, SiegeZombie and ZombieIndividual change; ScreamerStats is added. No resources, mixins, network protocol or loot classes change.

`python wall-breach41/build.py` uses the pinned ECJ/ASM caches in hordes-melee-interiors-patch/build/tools. Minimal Minecraft API fixtures compile the replacement helper; fixtures never enter the mod. The patch asserts exactly four hooks: replace the horizontal-collision read with collision/navigation/no-progress detection guard breakBlock with NeoForge's grief hook, intercept generic individual stats for screamers and preserve their dedicated stats before the siege lottery. ASM BasicVerifier checks the modified SiegeZombie methods. Fixed geometry cases run against the compiled helper with collision sampling fixtures. These do not establish in-game pathfinding or compatibility with every protection mod.

The original two-second stalled-step clock is now sampled consistently while pursuing, not only when considering a single-block jump. The candidate scan covers body width and feet/chest/crown instead of three center rays. Only first ray hits qualify; a cached coordinate cannot tunnel through another obstacle. Existing timers, hardness limits, target memory, building, routing and break-speed formula remain native.

Install and manual scenarios: [GUIA.md](GUIA.md).
