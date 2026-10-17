# Ambiente 47

Hordes .47 and Quietly .18 replacements for NeoForge 1.21.1. The environment panel controls newly generated container/body supplies and local population. Native textures, draggable sliders, numeric inputs and a scroll viewport keep save/close controls fixed. Server heat is shown above/below the vanilla inventory only when it fits without covering widgets.

`python ambiente47/build.py` reconstructs the two JARs from the pinned originals already tracked under `mods/`. Java 21, ECJ, ASM and Gson are verified against existing pinned hashes. `python ambiente47/test.py` runs server/settings, loot, native Quietly, population/heat, panel and bytecode checks. `python ambiente47/package.py` publishes local reviewable ZIP artifacts; Git publishing is separate.

The attached Mosslorn .13 datapack assigns lazy `LootTable` entries, so the existing Hordes `LootTable.fill` hook remains the generation boundary. No map reset is required. Existing inventories are preserved; a new search of an empty container without a table uses the same controls. Zombie Remains .15 already delegates corpse creation to Hordes and stays unchanged.

Caps govern new admissions, not deletion of existing entities or a global server memory budget. Saved zombies and infected players carrying belongings are retained. Existing groups can converge after spawning. See [GUIA.txt](GUIA.txt) for behavior and installation. Minecraft runtime validation is pending.
