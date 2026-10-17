# Hybrid entity mask .8

The .7 in-game screenshot exposed two independent defects. `ShaderRegistry.isShaderActive` evaluates the registration predicate, which is deliberately false for the manually driven hybrid mode; it does not read `forceEnabled`. Thus the scene filter was active while entity encoding never ran. Its broad RGB detector then classified ordinary brick texture colors as heat. The original fake tests did not reproduce the native registry semantics.

`HybridClient6.active` now reads `isShaderForceEnabled`. A native-bytecode regression extracts both query methods from the shipped registry and demonstrates their different behavior. A separate negative control fails with the published .7 and passes with .8.

The material-color classifier is removed. The scoped living-entity consumer writes UNORM8 alpha 253 into the opaque scene framebuffer. The hybrid pass reads that explicit stamp, maps only stamped fragments to light red, and restores output alpha to one. RGB is used only for tonal shading, never for selecting heat. No extra full-screen framebuffer, second world render, entity scan or per-vertex reflection is added. Vanilla entity depth testing remains enabled.

Encoding is scoped between NeoForge `AFTER_SKY` and `AFTER_LEVEL`, with a frame-start reset. NeoForge 1.21.1's patches dispatch these around world rendering and before the hand. This keeps the marker out of GUI portraits, inventory entity rendering and the first-person hand. Existing goggles IDs, shaders, N activation, 15-minute battery, four optical tubes and transition sounds are retained.

This relies on the standard opaque RGBA8 entity render path. Shader packs or external render pipelines that overwrite alpha may need specific compatibility. It is an alpha metadata mask in the existing scene target, not a dedicated mask framebuffer. Minecraft visual/runtime validation remains pending.

```sh
python hybrid-mask8/build.py
python hybrid-mask8/test.py
PYTHONPATH=/tmp/shader-gl python hybrid-mask8/shader_test.py
python hybrid-mask8/assets_test.py
python panel-clear48/build.py
python panel-clear48/test.py
python hybrid-mask8/package.py
```

The real GLSL tests include identical entity/wall RGB with different stamps, near-neighbor alpha rejection, an RGBA8 framebuffer and depth occlusion. Parent-screen blur is fixed separately in [panel-clear48](../panel-clear48/README.md). [Player guide](GUIA.txt).
