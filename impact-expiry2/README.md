# Tactical impact expiry .2

Pinned input: previous user-supplied Tactical tracers-impacts-scope-limits.1. Run
`python impact-expiry2/build.py`, `python impact-expiry2/test.py`, then
`python impact-expiry2/package.py`. Java 21 and the shared SHA-pinned ECJ/ASM cache
are required. No changes to TaCZ .18 are required.

The old configure path set Integer.MAX_VALUE; ImpactMarks kept records and restored
removed particles indefinitely. This patch stamps each native Mark constructor with
its original client-maintenance tick, configures 3600 particle ticks and prunes the
actual native map before restoration. Expiry precedes all chunk/distance/player checks,
so unloading cannot postpone it. Replacing the particle on an existing mark leaves
its original deadline intact. Removing records prevents native restoration after expiry.
The existing Minecraft.tick TAIL maintenance hook still supplies the clock.

Only ImpactMarks and its Mark constructor change; one new TimedMarks class is added.
ASM BasicVerifier checks modified native methods. Existing limits, replacement keys,
block-change checks, sprites, sizes, native fade ratio and other Tactical logic remain.
The physical TaCZ tracer lifetime is untouched.

3607 checks execute the patched native Mark constructor, native tracking map and native
configure against simulated particles, including the 3599/3600 boundary, replacement,
staggered creation, absent chunks/player and all 2048 records. They do not run Minecraft
or prove visible GPU rendering. At normal 20 client ticks per second, 3600 ticks are
180 seconds; scheduling pauses/slow clients can affect wall-clock timing.
