# Fracture Point .9: color original del láser

Parche incremental del JAR .8 aportado: sólo cambia ocho archivos GLSL. Conserva todas las clases Java, modelos, texturas, sonidos y cadenas de postprocesado byte por byte.

Lee exclusivamente la etiqueta alpha 251/255 del renderizador de láser de Global Melee v32. Devuelve su RGB original y alpha 1, antes de la paleta nocturna/térmica. La etiqueta térmica de entidades 253/255 sigue siendo independiente. La reducción de brillo de escena no cambia.

`python nvg-laser9/build.py` verifica el SHA256 de la entrada; `python nvg-laser9/test.py` compila y renderiza los ocho shaders en EGL con colores RGBA8. Dependencia de prueba: moderngl. Minecraft no se ejecutó en este entorno.
