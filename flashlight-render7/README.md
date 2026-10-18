# Reparación de estado gráfico y controles de linterna

El usuario probó tanto clic derecho como tecla. Se revisaron el video 20.49.15 y latest.log: la versión .6 está instalada y aparecen errores de VAO sin activo. La cadena de teclas pertenece a Zomboid Integration, cuyo Lamps solo reconoce los ítems básicos y utiliza el contador legado BatteryFlashlight. Este parche conserva el resto de Integration, incluida la atención médica.

Construcción: `python flashlight-render7/build.py` (Java 21, ECJ y ASM existentes en hordes-melee-interiors-patch/build/tools). Bases comprobadas por SHA-256 en inputs.json. Pruebas: `python flashlight-render7/test.py`; requieren las fixtures construidas por `python flashlight-render6/test.py`. Regresiones reales de shaders: `python flashlight-render6/shader_test.py` y `python darkness-light4/test.py`.

Las dobles de OpenGL prueban que, al retornar del enlace de textura del cono, un render nativo que confía en su caché efectivamente escribe en su unidad elegida. No se presenta esta comprobación como una ejecución del cliente Minecraft ni de Sodium. El control negativo ejecuta el mismo caso contra el G6 original y reproduce la desincronización.

Ver LEEME.txt para instalación y límites.
