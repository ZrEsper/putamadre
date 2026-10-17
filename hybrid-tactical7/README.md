# Hybrid thermal tactical .7

Fixes the .6 thermal mesh encoder: Minecraft 1.21 and GeckoLib submit packed vertices through interface default methods. The previous reflective proxy invoked those methods on its delegate, bypassing the color/overlay/light overrides. `HeatVertex7` implements the native consumer primitives directly, so default/bulk submissions remain on the wrapper and no reflection runs per vertex. The negative-control regression fails on the published .6 and passes on .7.

Adds a concrete four-tube inventory model and a head-mounted four-tube renderer, taking the actual helmet's manual visor position and battery state. The existing native humanoid layer is intercepted for hybrid goggles; single-lens armor bones are hidden only for this mode. Mounted optics use the generated metal/rubber/glass atlas directly; inventory geometry uses native mipmapped materials, avoiding a non-power-of-two sprite lowering the terrain atlas mip level. No new armor renderer registration is required.

The hybrid post pass preserves cold blue scenery, maps the mesh signature to light red, and adds bounded fine grain/scanlines. It preserves depth and does not add a second world render or entity scan. Chroma encoding is not a dedicated mask framebuffer; external materials with the same signature or custom rendering pipelines may require compatibility work. Existing thermal shaders remain byte-identical.

Client sounds are driven by physical visor/battery state transitions, independently of shader recreation. User-supplied audio is trimmed to 1.4 s on and 0.94 s off, mono Vorbis, with short fades and reduced volume. World/player changes initialize the state silently. Tooltips use translation keys.

Build and validate:

```sh
python hybrid-tactical7/build.py
python hybrid-tactical7/test.py
PYTHONPATH=/tmp/shader-gl python hybrid-tactical7/shader_test.py
python hybrid-tactical7/assets_test.py
python hybrid-tactical7/package.py
```

Uses the tracked original .5 input, preserving .6 registration IDs and battery logic. ECJ/ASM hashes are checked against `spas-reload18/inputs.json`; compile-only Minecraft API fixtures are never shipped. ModernGL dependencies are pinned in `requirements-shader.txt`. Test fixtures are not a Minecraft client: a modpack runtime/visual/performance test remains pending.

See [player guide](GUIA.txt), `validation.json`, `test-results.json`, `shader-test-results.json`, `asset-test-results.json` and `assets/provenance.json`.
