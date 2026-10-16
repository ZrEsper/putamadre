# TaCZ Tactical .3 — impact effects only on actual hits

The `.2` persistent-mark tracker recreates a dead `BulletHoleParticle` every time restoration is needed. Its constructor is also the trigger for `ImpactMixin → ClientEffects.impact`: every restoration emits four electric sparks and a dynamic-light pulse. In addition, the new particle starts at age zero; TaCZ's native render gives it full brightness while `age < 5`. This causes repeated glitter and light after shooting has stopped, in every camera.

Two native changes fix those distinct paths:

1. `ClientEffects.impact` returns immediately while `ImpactMarks.replay` is true. A new real hit keeps its original sparks and light duration.
2. `ImpactMarks.configure` uses `OnceEffects.configure`, preserving the `.2` lifetime/alpha configuration and restoring the original mark age from `TimedMarks.born` during replay, with a minimum of five ticks to skip the fresh fullbright phase. Replayed particles keep ammo-category size. The original 3600-tick deadline is unchanged.

Only those two classes and one new helper differ. There is no camera-specific branch, no changes to Dungeons Iso .30, no new mixins and no TaCZ core replacement. The uploaded Iso .30 matches the tracked build; its medical, audio-position and interaction behavior is preserved. Muzzle effects, gunplay, tracers, scope limits and decals remain otherwise unchanged.

```sh
python impact-once3/build.py
python impact-once3/test.py
```

Inputs and tools are SHA-256 pinned. Java 21 and Python 3 use the existing checkout and pinned ECJ/ASM tool directory; no additional worktree is needed.

Tests execute actual native `ClientEffects` and `ImpactMarks` with API doubles and a particle fixture that reproduces constructor mixin callbacks. Six baseline checks reproduce `.2` re-emitting light/sparks and resetting age. The `.3` suite passes 209 checks across 100 restorations, original expiry and another genuine hit. The previous 3607-check expiry suite also passes. ASM BasicVerifier checks modified methods. These are logic/bytecode checks; no GPU/client Minecraft runtime was available to verify the rendered result.
