# Darkness .3: emisión del láser

Parche incremental del `.2-config-lifecycle-fix` aportado. Se modifica únicamente final_darkness.fsh para conservar los píxeles etiquetados con alpha 251/255 por Global Melee. No se alteran la oscuridad normal, las clases Java ni los arreglos de carga de configuración.

Prueba OpenGL real: con intensidad 1, el shader original devuelve rojo 0.000078 para un píxel de láser de 0.078431; el resultado cae por debajo de medio nivel de una imagen de 8 bits. El parche mantiene 0.078431. Los píxeles ordinarios coinciden con el original en las cuatro intensidades probadas. No se ha ejecutado Minecraft.

`python darkness-laser3/build.py` verifica el hash de entrada y que sólo cambia un recurso. `python darkness-laser3/test.py` requiere moderngl y EGL.
