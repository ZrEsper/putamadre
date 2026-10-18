# Global Melee v36: láser sin profundidad sólida

Construir con `python laser-color36/build.py`; Java 21, ECJ y los API de compilación existentes de action34. Base v35 fijada por SHA. Solo se cambia Laser32 y se añade LaserType36; combate, locomoción y demás recursos se conservan.

En el video del 18 de octubre aparecen trazos negros repetidos alrededor del láser bajo la linterna. El RenderType nativo de TaCZ usa COLOR_DEPTH_WRITE: el propio haz escribe profundidad como un sólido. El trazado de sombras de pantalla puede leer ese haz como una barrera y producir falsas sombras repetidas. El nuevo tipo conserva shader, luz plena, transparencia aditiva, textura, destino y prueba de profundidad, pero usa COLOR_WRITE. Así las paredes siguen ocultando el haz y el haz no escribe un obstáculo nuevo.

`test.py` ejecuta el constructor real con estados fixture y las regresiones de trayectoria física, distancia, vegetación, lean, armaduras y locomoción. `depth_test.py` usa OpenGL real: reproduce la modificación de profundidad con el estado antiguo, confirma profundidad intacta con el nuevo y comprueba que la pared siga ocultando el láser. El estampado 251 de color, clip al bloque y los transforms del arma no cambian. No se ejecutó Minecraft ni un shaderpack real aquí.
