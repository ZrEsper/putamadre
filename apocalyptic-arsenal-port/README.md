# Apocalyptic Arsenal — NeoForge / Minecraft 1.21.1

Install `../mods/apocalypticarsenal-1.21.1-1.0.1-neoforge.1.jar` in client and server `mods`. Requires Minecraft 1.21.1, NeoForge 21.1.255+, TACZ 1.1.8 and LRTactical 0.4.3. Keep the supplied LesRaisins isometric-melee.1; neither engine JAR is replaced. Remove the original Forge 1.20.1 Arsenal JAR and avoid duplicate engine versions.

Three melees: `lrtactical:fire_axe`, `lrtactical:hardened_katana`, `lrtactical:wooden_baseball_bat`. Available in LesRaisins' melee creative tab and smith table. Original attack timing and artwork retained. Assets are isolated under `apocalypticarsenal` to avoid overriding shared base animations, scripts and item models. Removed the duplicate recipe for the existing LesRaisins baseball bat. Recipes use 1.21.1 paths, components and `c:` tags.

TACZ exports the pack automatically as `tacz/apocalyptic_1211`. If an older manual Apocalyptic Arsenal export is installed, remove only that older addon folder first. Keep the default TACZ and LesRaisins packs.

Build: `python run-gradle.py build`. Native server tests: `python run-gradle.py runGameTestServer`. Uses the existing repository engine JARs and the official AtlasLib 1.21-1.1.14 dependency in `libs`, verified against Modrinth SHA-512 `386fde446c14ed0885f4c76190752c03419c76b2938afcdffdc7362851ecf406eef4ef0b24179971194eb27c4d60cf19b32327ad666e2d3d987de3dea8848b5f`.

Native Minecraft test passes: all three indexes, stack IDs, damage values, smithing outputs and ingredient tags. Also loads the BRIMM port, TACZ, your LesRaisins base, FirstAid, The Hordes and AtlasLib. Graphical client unavailable: model appearance and isometric input still require in-game confirmation. Test classes/structures excluded from release.

Original authors: ChaosCraft_HD / Admany. Original license included in exported pack. Artifact hashes in `validation.json`.
