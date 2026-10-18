# Flashlight .16: iluminación moderada de manos

`python flashlight-hands16/build.py` y `python flashlight-hands16/test.py`. Conserva el cono, baterías, X en inventario, fuentes NPC y compatibilidad Iris de .15.

EntityLight14 deja de forzar bloque15 sobre el jugador y aporta un mínimo de6 conservando la luz ambiental superior y el cielo. Power.range devuelve0 para la ruta antigua de linternas: deja de colocar puntos de luz15 junto al jugador que volvían a sobreexponer sus brazos y producían parches cuadrados. El alcance del cono sigue dependiendo de Power.RANGE y no cambia; faroles y cascos con luz conservan su ruta original. Los puntos ya colocados se eliminan mediante su ciclo nativo.

Al restaurar estado GPU, comprueba las texturas guardadas antes de volver a enlazarlas, pues el log contiene GL_INVALID_OPERATION de nombres de textura inválidos durante cambios de renderizado. Esta protección no demuestra que sea la única fuente del error.

Pasan batería/inventario/teclas, sombras, fuentes NPC, iluminación nativa, cache Iris y bytecode. La fixture del antiguo alcance espera ahora cero bloques, mientras las pruebas de cono verifican el alcance real intacto. Sin validación visual dentro de Minecraft/MakeUp. No se promete una exposición idéntica entre shaderpacks.
