# Gore .15: Minecraft anatomy and complete destruction

Build: `python gore-anatomy15/build.py`
Test: `python gore-anatomy15/test.py`
Technical preview: `python gore-anatomy15/preview.py`
Package and validate consumer dependency TOML: `python gore-anatomy15/compat.py`
Uses SHA-pinned original inputs from gore-anatomy14/base and the existing ECJ/ASM toolchain.

Changes from .14:
- Facial bones, eye sockets, eyeballs, nose bridge and jaw; voxel anatomy retained.
- Pectoral and abdominal muscle cover organs. Eyes move outside sockets on optic tissue; bowel protrudes while attached. These are posed model pieces, not independently simulated physics objects.
- Damage13.hit now routes through Layers15. Arms use the same progressive tiers as head, legs and torso. Stages 1–2 open skin; stage 3 removes muscle; stage 4 removes organs; stages 5–6 consume bones; stage 7 removes every rendered piece. Torso halves progress separately. Each tier takes two ordinary impacts; thirteen counted hits reach terminal destruction. Existing weapon/amputation effects can bypass geometry earlier.
- Existing three-bit synchronized fields and corpse counters remain unchanged. No new unbounded entity/particle lists. Old injuries can map to different visuals; no world data format change.
- Existing .14 arterial audio and finite pressure spray, flashlight lighting bridge, particle caps and physical corpse loot integration retained. Dead bodies do not gain permanent cardiac pressure.
- Complete geometry destruction intentionally leaves the existing corpse loot object available. Armor and third-party decorative renderers are outside this anatomical geometry.

Tests exercise full destruction for each region, torso-half isolation, monotonic damage, diminishing bone volume, finite mesh geometry, eye/bowel displacement, particle/audio budgets and model hierarchy. This environment cannot run Minecraft; visual behavior with the complete modpack and detached limb physics needs in-game verification.

Install all four packaged mods, one version each. Mob Dismemberment is client-only; Gore, Ragdollified and Remains go on client/server. Keep Splatter blood-budget.2.
