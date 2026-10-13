# BRIMM Armors — NeoForge / Minecraft 1.21.1

Install `../mods/brimm-1.21.1-3.0.1-neoforge.1.jar` in client and server `mods`. Remove original Forge 1.20.1 `brimm-3.0.1.jar`. Requires Minecraft 1.21.1, NeoForge 21.1.255+ and Java 21. BRIMM is a standalone armor mod, compatible with TACZ and LesRaisins through vanilla armor attributes. Do not put it in `tacz/`. No GeckoLib dependency introduced.

Preserves 153 armor pieces, 30 patches, three plates, creative tabs, original OBJ models/textures, armor effects, XML overrides and workbench recipes. Workbench opens on right click; supported armors accept patches in the crafting grid and removing a patch returns it. Migrated registered armor materials, item components, recipes, packets, registries, client rendering extensions, vertex rendering and GUI input to 1.21.1. Resource reload clears baked model/patch caches. The external version-check notifier is omitted.

Build: `python run-gradle.py build`. Native server tests: `python run-gradle.py runGameTestServer`. Requires a full JDK 21; helper respects JAVA_HOME or the retained cloud JDK/cache. Uses NeoForge 21.1.255 and ModDevGradle 2.0.148.

Three native server tests pass: armor statistics/durability; patch addition/removal and recovery; vanilla and workbench recipes. The release JAR also loaded alongside Apocalyptic Arsenal, TACZ, LRTactical, FirstAid, The Hordes and AtlasLib. No graphical client was available: OBJ appearance, GUI rendering and client/server clicks have not been exercised visually. Tests and test structures excluded from release.

Original authors: LIRAY, Concord, strubium, ema_08_. Original `BrimmArmorsLicense.txt` retained. Artifact hashes in `validation.json`.
