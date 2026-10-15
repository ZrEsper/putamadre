# Fracture Point NVG linked visor .2

Exact user upload is pinned in inputs.sha256.json. Build/test/package from repo root:

```bash
python nvg-linked2/build.py
python nvg-linked2/test.py
python nvg-linked2/package.py
```

Java 21, Python, and SHA256-pinned ECJ/ASM caches from hordes-melee-interiors-patch/build/tools. Metadata and protocol stay unchanged. Only five native classes, three control-label translations and two new helper classes differ from the uploaded .1 artifact. All shader JSON/programs, textures, animations, batteries and NvgSupport33 classes remain byte-identical. Test fixtures never enter the mod.

Ten asserted hooks: default visor key H -> N, independent special-vision key N -> unbound, remove independent key registration, dispatch visor key through the linked controller, replace ClientKeyEvents.onClientTick's stale item-only reconciliation, invalidate stale ShaderRegistry effect cache before processing, record the successfully loaded chain, retain a zero-energy client latch after the native empty packet handler, reconcile real helmet state before server battery ticks, and authorize requested vision against real installed goggles/visor/battery. ASM BasicVerifier checks all modified native methods; existing names and event annotations are preserved.

The original getActiveVisionType ignores its helmet argument and reads UUID-wide ClientVisionState. Native equipment reconciliation compared only Item identity, disabled the old shader and never selected the new goggles. NvgClient2 derives wanted mode from the actual stack and installed goggles every tick; its reference-change handling clears stale battery UI state without reloading healthy shaders. It enforces exactly one NVG force-enabled flag, sends network updates only for desired-mode/stack changes and preserves manual intent for configured third-party helmets without a physical visor. The physical toggle remains native, so activation follows the actual animation-triggered visor change rather than an early key press.

ShaderRegistry previously returned immediately when shader ID matched, even if GameRenderer.currentEffect was null or a different PostChain. The new identity lease invalidates only recognized NVG cached IDs and records only the chain actually loaded by ShaderRegistry. It does not force reloads of a healthy chain or interfere with unrelated registry shader IDs. Native preset conditions and rendering remain in place.

NvgLink2 is common-side and never resolves client classes. It derives authoritative mode from equipped goggles, stops on raised visor or empty battery, flushes a prior helmet reference before discarding its energy cache, and leaves the original drain/persistence/effect-backup implementation intact. Non-visor helmets require an activation request; physical closed visors can reconcile themselves. Requested arbitrary mode strings cannot select another shader than the installed goggles support.

930 checks run the actual helpers and original NvgSupport33.drain with API doubles. Scenarios include legacy key migration, closed/open visor, different and identical helmet items, lost shader flags, null/replaced/stable PostChain identities, unrelated shaders, empty-battery latch, battery-cache separation, server authorization, unequip/reconnect, non-visor helmets and all 900 active battery seconds. These are not Minecraft/GPU/multiplayer integration tests. See GUIA.md for manual validation.
