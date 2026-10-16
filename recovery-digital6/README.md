# Torretas, inconsciencia y exposición digital

[Descarga directa del paquete de cuatro mods](../distribuciones/Zombies-Poldo-Torretas-Inconsciente-Digital-Higiene.zip). [Instalación y comportamiento](GUIA.txt).

Fuentes y parches reproducibles sobre los JAR enviados: `python recovery-digital6/build.py` y `python recovery-digital6/test.py`, desde la raíz, con Java 21 y Python 3. Los hashes de entrada/herramientas están en inputs.json. Los tipos en api/ son exclusivamente de compilación; no se incluyen en los JAR.

La torreta usa adquisición de 16 ticks e intervalo mínimo de 10 ticks, conservando energía, visibilidad, sonidos y recursos. FirstAid añade pantalla negra y solicitud nativa de desfibrilador con comprobación del inventario; conserva validación y tratamiento del servidor. Fracture Point modifica únicamente el shader digital blanco.

`test-results.json` registra 120 controles de torreta y 21 de pantalla/puente nativo de inventario. `shader-test-results.json` registra compilación y render real de ambos shaders sobre EGL/llvmpipe. Para repetirlo, instala moderngl y ejecuta `python recovery-digital6/tests/shader_check.py`. La escena sintética conserva el orden sombra/pared/cielo y reduce la luminancia media verde de 0,484 a 0,149; no equivale a una prueba visual en Minecraft.

Los perfiles Spark adjuntos incluyen espera/pausa. En el perfil de cliente, Corpse ocupa aproximadamente 0,5%, partículas 9%, EMF 6,6% y Xaero 8,1%; no permiten atribuir por sí solos la caída de TPS a los cadáveres ni garantizar una mejora de FPS.

El paquete combinado se genera con `python recovery-digital6/package.py`, después de construir y probar también corpse-hygiene14. Prueba dentro de Minecraft pendiente.
