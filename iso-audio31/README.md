# Escucha desde el jugador y compatibilidad con Sound Physics

Reemplazo de Iso .30 por .31. La version anterior ya movia el listener de OpenAL, pero Sound Physics Remastered 1.5.1 seguia trazando oclusion/reflexiones desde `gameRenderer.getMainCamera().getPosition()`. Con la camara fuera de una casa, sonidos de la misma habitacion podian amortiguarse como si el oyente estuviera fuera.

`PlayerAudio31` publica un snapshot inmutable de posicion de ojos y orientacion, usado por el listener y por la compatibilidad acustica. El audio conserva la cola del ejecutor y la orientacion del jugador; rechaza snapshots de otro mundo. Fuera del modo isometrico se conserva la operacion original. No mueve la camara visual.

El mixin opcional de MixinExtras sustituye exclusivamente esa lectura dentro de `SoundPhysics.evaluateEnvironment`. El plugin original activa `.compat.sound_physics_remastered` solo si esta instalado ese mod. Se verificaron el descriptor y la unica llamada de la clase exacta enviada. La clase de prueba extraida de ese JAR se guarda en `tests/fixtures`; sus SHA256 y el del JAR constan en input.json. No se incluye en el mod distribuido. SoundAttract y Sounds permanecen sin cambios.

Se conservan las clases de curaciones, entrada, desfibrilador y apuntado de .30. El ZIP incluye ademas [Tactical .4](../far-shots4/README.md) y [Doomsday .5](../doomsday-board5/README.md). [Guia sencilla](GUIA.txt).

Construccion: `python iso-audio31/build.py`; pruebas: `python iso-audio31/test.py`. Herramientas ECJ/ASM fijadas por SHA256. 380 regresiones, 23 comprobaciones de snapshot/posicion y 7 del objetivo acustico/plugin nativos. APIs simuladas, verificacion ASM/JVM; no se ejecuto Minecraft/OpenAL ni una prueba auditiva real. Paquete: `python iso-audio31/package.py` despues de construir y probar los tres proyectos.
