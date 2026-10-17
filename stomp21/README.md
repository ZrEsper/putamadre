# Global Melee v21: pisotón articulado

[Guía de instalación y uso](GUIA.txt). Reemplaza únicamente Global Melee; conserva Gore 1.0.10 y Hordes .49.

El vídeo de 8,75 s mostraba una pierna rígida, entrada/salida brusca y poca lectura del contacto. La nueva cadena muslo/espinilla/pie tiene rodilla y tobillo independientes. El servidor programa el contacto a nueve ticks, lo valida otra vez y comunica impacto o fallo; la pierna espera confirmación antes de recuperar. El enfoque de cámara es temporal y no mueve al jugador.

La mano secundaria conserva su renderer nativo y no bloquea el puño principal. Dos mixins eliminan el empuje entre entidades cuando un zombi está derribado. La selección sigue el rayo exacto, admite vegetación sin colisión y respeta paredes y objetivos interactuables.

El derribo temporal pasa de 80 a 180 ticks. Una recuperación de 1,4 s escalona torso, cabeza y rodillas; la IA espera hasta 210 ticks desde el derribo. Los rastreros por pérdida de piernas no se curan.

Construcción reproducible desde la raíz:

```sh
python stomp21/build.py
python stomp21/test.py
python stomp21/preview.py
```

Base y herramientas fijadas por SHA-256. Las clases de `compile-api` son stubs de compilación y nunca entran en el JAR. Las pruebas reutilizan fixtures de `action20`, ampliadas dentro de `build/` para este cambio. `assets/stomp-geometry.jpg` y `.gif` son una inspección matemática del movimiento, no una prueba dentro del juego. Minecraft y validación visual final pendientes.
