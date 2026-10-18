# Flashlight .12: captura aislada de Iris

Construir con `python flashlight-render12/build.py`. Reutiliza Java 21 y ECJ/ASM del repositorio; base .8 fijada por SHA. No requiere nuevas variables ni servicios.

El log del usuario muestra una llamada desde AFTER_LEVEL a renderSectionLayer después de que Iris haya puesto LevelRenderer.pipeline a null. La excepción desactivaba la linterna permanentemente. Ahora AFTER_TRANSLUCENT_BLOCKS captura el terreno y AFTER_LEVEL solamente compone el cono. Un fallo transitorio se reintenta con espera de un segundo, sin inundar el log.

Con Iris, Iris12 sustituye temporalmente sus dos referencias de pipeline por un proxy de WorldRenderingPipeline. Ese proxy no es ShaderRenderingPipeline ni IrisRenderingPipeline: los hooks oficiales seleccionan el programa de terreno normal de Sodium y no enlazan los MRT del shaderpack. El formato extendido conserva los cuatro atributos base de Sodium. No se construye VanillaRenderingPipeline ni se cambian formatos, ajustes globales o mallas. La fase del pack y su bloqueo de mezcla se conservan con try/finally. El estado GL también restaura las mezclas independientes de los MRT en OpenGL 4.0. Los contratos inspeccionados y sus commits están en upstream-contracts.json.

Se conserva .11: un solo cono amplio, recarga, X desde inventario, batería, sombras, origen en el jugador y matrices de las tres cámaras. No se vuelve a renderizar entidades ni muebles en la captura. La composición ocurre sobre el resultado final del shaderpack; no intenta modificar sus buffers internos ni adivinar cuál contiene albedo.

Comprobaciones: `test.py`, `iris_test.py`, `mrt_test.py`, `shader_test.py`, `camera_test.py`, `appearance_test.py`, `beam_shadow_test.py`. Las pruebas Iris usan fixtures y las pruebas GLSL/MRT usan OpenGL real por EGL. Ninguna ejecuta Minecraft, Iris transformado por Mixin ni un shaderpack real. La compatibilidad visual debe verificarse en el juego con un pack válido, en primera, tercera e isométrica; no se garantiza cada shaderpack existente. Los efectos de postproceso extremos pueden necesitar ajustes específicos.
