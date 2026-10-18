# Linterna .8: evitar luz duplicada y cortes al girar

El vídeo y log aportados muestran un render que completa sin errores pero se superpone a la iluminación persistente por bloques de `Lamps.beam`. Ese método añade luz en los ojos, un bloque por encima y cada dos bloques del rayo. Ahora retorna inmediatamente si su entidad es un jugador. No se altera el resto del método para las linternas arrojadas ni el control de teclas de .7.

El shader conserva el mapa de sombras de colisiones como respaldo para geometría no visible, añade contacto mediante profundidad actual y mezcla el límite del mapa anterior. El área aún no cubierta deja de interpretarse como oclusión total. No aumenta el presupuesto de rayos del cliente ni escribe bloques.

Construcción: `python flashlight-render8/build.py`. Herramientas: Java 21, ECJ y ASM existentes en hordes-melee-interiors-patch/build/tools; las bases están fijadas por SHA-256 en inputs.json.
Pruebas: `python flashlight-render8/test.py`; reutiliza las fixtures de `flashlight-render6/test.py` y `flashlight-render7/test.py`. `shader_test.py` necesita moderngl, numpy y EGL. Incluye dos controles negativos: el código anterior de Integration permite la ruta duplicada y el shader anterior apaga el haz fuera de su mapa viejo. No se afirma ejecución de Minecraft.
