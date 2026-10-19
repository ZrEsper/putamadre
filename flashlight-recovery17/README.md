# Flashlight .17: native capture and recoverable updates

Build/test: `python flashlight-recovery17/build.py`, `python flashlight-recovery17/test.py`.
GPU fixtures: iris_test.py, shader_test.py, camera_test.py, temporal_test.py in the same directory.
Uses the SHA-pinned .8 base in flashlight-hands16/base; batteries, inventory X and server item IDs stay unchanged.

Confirmed code defects corrected:
- Light6.safeTick permanently disabled the whole cone after one RuntimeException. It now retries with bounded 0.2–1s backoff, rate-limited diagnostics and reset after success. Production fixture throws a real pose error, then restores a working light source.
- Iris12 applied its shaderpack proxy to VanillaRenderingPipeline even with shaders disabled. That pipeline now stays untouched; active shaderpacks retain the scoped capture and restoration checks.
- Native albedo/depth capture used half resolution, creating edge mismatches against full-resolution scene depth. Without an active shaderpack it now captures at native resolution. Active packs retain half resolution. Native capture costs additional texture memory and fill rate (about 12 MB extra at 1080p for RGBA8+depth24 storage rounded to 4 bytes). No FPS improvement is claimed.
- On foreground entities, the cone used a second exposure fallback on top of native entity lighting. Foreground pixels now retain their own rendered color, and the mismatch fallback no longer multiplies exposure. Native entity block-light contribution is capped at 12 rather than 14; player hands keep the existing minimum 6.
- If the shared entity ray budget is exhausted, a recent geometrically valid cached visibility result can be reused for up to ten ticks. Expired or moved samples are rejected, so it cannot create unbounded stale illumination. Budgets and cache caps stay fixed.

Passed fixtures include real production tick recovery, native inventory/batteries/NPC sources, bounded shadows/cache, Iris vanilla/active-pack isolation, real EGL GLSL compilation/render, entity color preservation in first/third/isometric cameras, Darkness shader compatibility and moving-shadow regression. These do not run Minecraft, Sodium or the user's modpack. The uploaded videos establish symptoms, not a confirmed full runtime stack trace. The original request for latest.log remains useful if capture still fails.
