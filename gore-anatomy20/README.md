# Gore .20: visible wounds below empty EMF model containers

Run build.py, test.py, compat.py in this directory using Python from the repository root. compat.py also requires the built flashlight-recovery17 output and creates one five-mod ZIP.

The .19 render guard only allowed anatomical root nodes with their own readable cubes. An EMF container can be empty while its actual head/body/limb mesh is in a child. That path kept native appearance and suppressed detailed wounds. Model14.bind now also registers a selected real geometry owner below an empty container. Owner20 prefers a readable volume close to the standard anatomical part size, excludes named hair/hat/ear/helmet/overlay/accessory branches and planar cubes, and bounds traversal to 128 nodes/16 levels with identity cycle detection. Native nonempty roots keep their existing route. This is a structural compatibility fix, not an EMF-wide guarantee for arbitrary nonhumanoid/procedural models.

Original UV surface clipping, interior overlap removal, damage-scaled wounds, .17 skeleton, .16 organ physics and .18 collateral logic remain. A model hierarchy fixture verifies that the selected child actually emits anatomical bone geometry, while decorative children do not become extra skulls. Existing .19 UV/area and other regressions run unchanged. Unreadable unsupported models still fall back to native appearance. Wound coordinates remain coarse region templates.

The combined ZIP contains Gore20, compatible Ragdollified/Mob Dismemberment/Remains and Flashlight17. Their dependency TOML is parsed against the packaged Gore version. Mob Dismemberment client-only; other four replace their prior client/server copies. Keep Splatter blood-budget.2. Install one version of each and compare on new zombies; prior injuries are not healed.

No Minecraft/EMF/MakeUp runtime was available. Tests demonstrate code/geometry and standalone OpenGL behavior, not verified visuals in the user's complete installation. See flashlight-recovery17/README.md for native capture memory tradeoff and diagnostic limits.
