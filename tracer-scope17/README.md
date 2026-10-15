# TaCZ visual / declared-scope limits .17

Inputs: exact user-provided TaCZ .16 reconstructed from all three 7z volumes, exact Tactical scope-range64 JAR in base/, and reviewed taczaddon SHA for provenance. inputs.sha256.json pins the unpacked TaCZ and Tactical. TaCZ base is supplied as a build argument rather than duplicated in this project. Requires Java 21, Python + Pillow, and the pinned ECJ/ASM caches from hordes-melee-interiors-patch/build/tools.

```bash
python tracer-scope17/build.py /path/to/tacz-...16-first-person-scope-ratio.jar
python tracer-scope17/test.py /path/to/tacz-...16-first-person-scope-ratio.jar
python tracer-scope17/package.py
```

Eight asserted bytecode edits: move the ammo model's PoseStack.pushPose ahead of both rotations in EntityBulletRenderer.lambda$render$0; initialize xRotO/yRotO after readSpawnData; select per-ammunition particle sprites at BulletHoleParticle constructor return; redirect Tactical ScopeZoom.scale/scroll; preserve category-specific quadSize in ImpactMarks.configure/size; use RenderLevelStageEvent.getPoseStack().last().pose() for camera-relative hitscan vertices. Modified methods pass ASM BasicVerifier. Native tracer lambda remains bytecode-structurally identical and retains all required WorldTracerMixin injection sites. No method renaming.

The confirmed atlas bug: HoleSpriteMixin injected into getSprite and returned a BLOCK_ATLAS sprite; native render binds PARTICLE_SHEET_TRANSLUCENT. Only HoleSpriteMixin is removed from the Tactical client mixin list. Explicit particle-atlas single sources are appended while preserving all original sources. Four generated transparent particle PNGs use distinct category silhouettes and sizes. Client resource and category fallback limitations are described in GUIA.md.

ScopeLimits gets raw attachment bounds from the exact ClientAttachmentIndex.zoom field. It uses an exception-safe ThreadLocal raw-read guard when deriving the selected native base zoom; this avoids recursive scaling and allows zooming out immediately after reaching the cap. A bounded, world-scoped factor map stores player wheel adjustments; every returned mode is clamped to the raw minimum/maximum without modifying pack arrays. Existing native index switching, integrated scopes, inventory/hotbar handling and ADS checks remain. TaCZ's .16 FOV flow is preserved with its hidden 1.35 multiplier and artificial caps removed.

No ballistics, damage, hit selection, precision, network codecs or ammo packs are modified. Tactical's persistent impacts, sounds, scope sensitivity and world tracer mixins remain active. taczaddon was inspected and retained unchanged. Original mod metadata and protocol are byte-identical; files identify the replacement revisions. TaCZ .17 and the patched Tactical must be installed together.

Tests execute real ScopeZoom.scroll/scale, ScopeLimits, Visual17 and the native camera magnification helper with API doubles. Bytecode tests reproduce the original leaked transform order and check corrected isolation across 325 symbolic yaw/pitch combinations, spawn-angle initialization, unchanged tracer methods and retained mixin sites. They do not run Minecraft or prove GPU output. Manual multiplayer render verification is still required.
