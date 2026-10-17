# Action v20, directional Gore 1.0.10, global population panel .49

Patches the three user-supplied JARs; their hashes and tool hashes are checked. Builds only the changed classes, preserves unrelated binary classes and excludes compile-only Minecraft API fixtures. No Epic Fight runtime is included.

Accepted bare-hand attacks narrowly clamp the target zombie's remaining hurt immunity to 10 before damage, resolving the 8-tick punch / 10-tick immunity conflict. The existing eight-tick server cooldown and stamina costs remain authoritative. Confirmed damage sends tracking cues; failed stamina gets a localized action-bar notice. Shared cooldowns prevent alternating-button bypass. Heavy and stomp retain ray, wall, player-state and stamina checks. Stomp accepts a fallen torso (10 base damage) as well as a precise lethal head strike; it has no random success roll.

Original viewport tracks distinguish straight jabs, lateral heavy hooks, two-arm shoves and a vertical foot stamp. Camera movement adds bounded visual pitch/roll and a damped contact impulse without changing player aim or position. First-person skin leg rendering restores its original part rotations and pose stack. Typed HumanoidModel arm access bypasses shadowing GeoBones. The old overlapping body shove is disabled; native firearm stock animation remains, while unrelated LR items no longer get the bat proxy. Server menu transitions synchronize searching poses, without changing container contents.

Gore reactions encode direction and intensity in existing entity event hooks, separating light/heavy/shove/stomp momentum and restoring part offsets at setup. Existing temporary fall state and v19 recovery gates remain. No new world entity scans or corpse entities are introduced.

`ambiente_limite_global` defaults to 100 (integer range 1–500), saved through the existing WorldTuning/admin packet path. Native simple-panel key, label and icon arrays gain an aligned slider/number field. ZoneReserve room checks, all new zombie admissions, pending reserve processing and population generation respect the global index count across loaded dimensions. Excess saved combat/protected entities are retained temporarily; idle excess enters the existing NBT reserve. Player zombies remain excluded by the original supported predicate. Existing local/group limits and stored loot are preserved. A small per-thread set reuses count bookkeeping rather than allocating a set on every check.

```sh
python action20/build.py
python action20/test.py
python action20/package.py
```

Tests exercise combat, native leg rendering/restoration, network registration/encoding/queueing, directional reactions, actual settings initialization and patched bytecode wiring for V, panel, reserve and population. [Spanish installation and controls](GUIA.txt). [Redacted Spark findings](spark-report.json).

Validation uses API fixtures and actual generated classes, not a Minecraft session. Visual quality, full-modpack compatibility and post-change TPS/FPS must still be checked in game. The shipped limit does not instantly delete 511 saved combat zombies or guarantee a performance number.
