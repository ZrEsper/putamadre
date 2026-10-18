# Flashlight 6

Adaptación NeoForge 1.21.1 del mod suministrado, con iluminación por cono y recarga de baterías. Véase LEEME.txt para instalación, controles y límites.

Construcción reproducible: `python flashlight-render6/build.py` desde la raíz. Usa Java 21, ECJ y ASM del directorio de herramientas existente; verifica SHA-256 del archivo base con inputs.json.

Validación: `python flashlight-render6/test.py`, `python flashlight-render6/shader_test.py` y `python darkness-light4/test.py`. Los shaders usan moderngl, numpy y EGL. No necesita servicios de red para construir o probar. No sustituye una prueba en Minecraft.
