# Gore .16: independent visual organ physics

Run `python gore-anatomy16/build.py`, `python gore-anatomy16/test.py`,
`python gore-anatomy16/preview.py`, then `python gore-anatomy16/compat.py`.
Reuses SHA-pinned gore-anatomy14/base inputs and existing ECJ/ASM tools.

Eyes, heart, liver and stomach detach at injury tier 3. Attached eye pose at tier 2 and bowel remain; released anatomy is excluded from the body mesh. Torso organ release uses the affected half (heart/liver left, stomach right). The client observes synchronized living damage and corpse updates; original entity IDs prevent duplicate release during the living-to-corpse transition. The deduplication cache is bounded to 512 observed bodies per client world.

OrganMotion16 runs at 20 Hz: gravity, damped spin, low restitution, floor friction, sleep and 600-tick expiry. Runtime collision uses Minecraft Entity.collideBoundingBox with swept AABB and block VoxelShapes, including slabs and walls. Unloaded destination chunks remove a moving piece without forcing chunk loads. Each eye has a white voxel shell and pupil; organ meshes reuse the anatomical geometry. Fixed conservative bounds encompass the rotating geometry.

At most 24 pieces exist globally per client. They are visual objects, not server entities, collectible items, saved world objects or mutually colliding rigid bodies. Different clients can see different scatter. No new server packets, persistence fields or inventory changes. The existing shaded Shards13 renderer is preserved and supplied with interpolated coordinates; its old generic debris simulation is replaced. One quiet wet impact sound per initial floor landing, shared 10-tick sound cooldown. Objects stop rotating when settled and are cleared on world change.

Tests: simulation against floor/slab/wall fixtures, bounce/sleep/expiry, interpolation, all four mesh types, 24-object limit and world-reset cleanup. Renderer-container fixture is test-only and never packaged. Tests do not execute Minecraft's collision solver or renderer; actual modpack/shader integration still needs in-game verification. ASM verifies all patched method bodies. Existing layered damage, audio/particle limits and model-hierarchy checks also run.

The four-mod ZIP includes dependency ranges checked against the actual Gore 1.0.16 TOML. Install one version of each. Mob Dismemberment client-only; Gore, Ragdollified and Remains on client/server. Keep Splatter blood-budget.2. Loot remains accessible after geometry destruction.
