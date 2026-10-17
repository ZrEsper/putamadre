# Hybrid thermal vision .6

Adds `fracturepoint:hybrid_thermal_goggles` to the uploaded digital-exposure .5 JAR. A separate `thermal_hybrid` / `htvg` mode preserves existing goggles and shaders, manual visor activation and the 900-second battery algorithm.

Standard living-entity mesh buffers receive an opaque white data texel and a narrow chroma marker, with vanilla depth testing retained. One post pass maps those fragments to red and the scene to cyan-blue, followed by a blit. Non-entity vertex formats retain their original buffers. No second world render or per-tick entity scan is added. This is a lightweight chroma encoding, not a dedicated mask framebuffer: matching external emissive materials can theoretically collide; nonstandard render pipelines may need compatibility work.

Build: `python hybrid-thermal6/build.py`. API and native hook checks: `python hybrid-thermal6/test.py`. Real GLSL render checks: `PYTHONPATH=/tmp/shader-gl python hybrid-thermal6/shader_test.py` using ModernGL 5.13.0 and glcontext 3.1.0 (`python -m pip install --target /tmp/shader-gl -r hybrid-thermal6/requirements-shader.txt`). The build uses the original tracked FracturePoint .5 and verified ECJ/ASM tool hashes. Minecraft runtime validation remains pending.

[Installation and item ID](GUIA.txt).
