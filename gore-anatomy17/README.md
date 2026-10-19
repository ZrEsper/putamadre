# Gore .17: reference skeleton rebuild

Build/test/preview/package in order:
`python gore-anatomy17/build.py`
`python gore-anatomy17/test.py`
`python gore-anatomy17/preview.py`
`python gore-anatomy17/compat.py`

Skeleton17 replaces only anatomical bone pieces, retaining organic meshes and .16 eye/organ physics. Skull silhouette uses a broad cuboid forehead and shell, two real rectangular apertures with recessed dark socket liners, a separate U jaw and two tooth rows. Three thick paired thoracic bands replace six thin ribs. Narrow straight limb bones replace large end caps and fingers. Short shoulder bridges connect arm bones to the clavicle. Pelvis has an open U silhouette. Bone and teeth tints are neutral gray-white; actual game lighting/shader color remains effective.

The user's reference image determines proportions and silhouette; this is an implementation using cuboids, not an imported original model. preview.py renders the actual generated stage-4 geometry, not a promotional render or Minecraft screenshot. Destruction cuts are adjusted so the narrower replacement limb bones still lose volume at each late tier.

Regression checks cover rectangular eye openings and jaw gap, filled forehead, three thick paired ribs and removal of broad limb end caps. Existing damage, physical organ simulation, lifetime/caps, interpolation and audio/hierarchy checks remain. ASM verifies patched class methods. Dependency TOML ranges for all three companion mods are checked against Gore 1.0.17. Minecraft and the full shader/modpack are not available in this environment, so no in-game visual/runtime claim is made.

All .16 physics limits remain: client visual objects only, 24 active pieces, 30 seconds, block VoxelShape collision adapter, sleep, bounded deduplication and world cleanup. Loot and network field formats are unchanged. Install all four packaged JARs without duplicates. Mob Dismemberment is client-only. Keep Splatter blood-budget.2.
