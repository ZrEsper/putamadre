# Electronic scrap and turret sounds .2

Based on Turrets Doomsday .1, the exact uploaded Remains .12 (SHA matches tracked file), and Hordes .43. [Player guide](GUIA.txt). The package also contains the independent [impact replay fix](../impact-once3/README.md).

- Registers `tacz_turrets:electronic_scrap`, stack size 64, with a new transparent 16px item texture, generated-item model, ES/EN/DE names and TaCZ creative tab entry. Shapeless recipe: one each lapis, redstone, copper ingot, iron ingot → 2. Turret shaped recipe retains its existing ingredients and adds two scrap.
- Registers items and all three sounds using a concrete `Consumer<RegisterEvent>` attached directly in the original mod constructor. The old generated automatic sound subscriber annotation is removed to prevent duplicate registry calls. The listener preserves its generic event type and bridge method for event-bus inference.
- Rotation sound follows actual `getYHeadRot` / `getXRot`, with wrapped yaw, ≥0.8° movement and 8-tick rate limit. Existing idle becomes audible at .35 volume; movement .7; target-recognition alert 1.1. Acquisitions during the 60-tick alert cooldown remain pending and can be announced once the cooldown ends. All sounds require server-side power, a gun and an enabled turret; recognition also requires a living valid target and direct LOS.
- Corpse addition runs after the native `CorpseBridge35.body` completes, so the existing balancing path cannot erase it. It inserts one stack in a random empty slot, never replaces equipment, and does not touch player-zombie exclusion. Base chance 15%, one scrap.
- Barrel addition runs after the original `LootProfiles.container` returns its generated list. It limits additions to available capacity, recognizes barrel subclasses, excludes chests and adds one stack at 30% base chance (75% one / 25% two). This is loot-table unpacking, not repeated inventory access or free loot for crafted barrels.
- Both chances scale down by `min(1, SimpleSettings.mean(...))`, and become zero when materials or quantity are disabled. The providers are optional reflective bridges: absent Turrets .2 does not introduce missing item IDs or crash Remains/Hordes. Only one existing Remains class and one existing Hordes class change. The exact firing/energy/range/damage classes from Turrets .1 remain byte-identical.

Build in the existing checkout, without new worktrees:

```sh
python turret-scrap2/build.py
python turret-scrap2/test.py
python impact-once3/build.py
python impact-once3/test.py
python turret-scrap2/package.py
```

Java 21, Python 3 and the pinned ECJ/ASM files from `hordes-melee-interiors-patch/build/tools` are required. Inputs are pinned in `inputs.json`; outputs and exact changes are in `validation.json`. Audio and texture assets are included, so builds need no generation dependency. The mono audio was synthesized with FFmpeg; the new icon was drawn as a pixel bitmap.

37 checks run production item/audio/loot helpers with API doubles, the real generated typed listener, the real patched Remains bridge, the extracted native container wrapper and the actual Hordes `SimpleSettings.mean/value` methods. They cover capacity, preserved equipment, quantity/material disablement, scarce profiles, client exclusion, movement, yaw wrap, pending detection, occlusion and power loss. Recipes and retained firing bytes are checked separately. ASM BasicVerifier checks modified native methods. The icon, audio and full modpack have not been tested in a running Minecraft client.
