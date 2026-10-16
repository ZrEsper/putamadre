# Doomsday .6: necesidades y baños

[Descargar ZIP directo](../distribuciones/Zombies-Poldo-Doomsday-6-Necesidades-Banos.zip) · [Guía sencilla y recetas](GUIA.txt).

Basado en el .5 enviado (hash en input.json). Añade contador de diez minutos en supervivencia, aviso traducido, X contextual, aplazamiento único de dos minutos y uso automático al acercarse al inodoro. Acción de 200 ticks, partículas gravitatorias que siguen el cuerpo, sonidos y Regeneración II de 200 ticks solo al terminar junto al mismo baño. El servidor valida el estado; el cliente envía un comando propio, sin permiso administrativo. Los latidos de estado mantienen la postura mientras dure realmente la acción del servidor, con liberación de seguridad tras cinco segundos sin sincronización.

El WC reutiliza la clase nativa VendingMachineBlockItem, ya presente, que coloca su controlador un bloque más alto: el modelo tiene mínimo Y=-16 y antes chocaba con el suelo. Requiere espacio de 3x3x3. El colchón `mat` se incorpora al helper de camas, con receta de tres lanas y protección del respawn existente. El alambre usa el contacto nativo entityInside, comprueba la intersección de su forma real y limita el daño a una vez cada 20 ticks por entidad; FirstAid recibe sangrado de pierna solo tras daño aceptado en jugadores.

Los sonidos nuevos son Foley procedural mono (agua, golpes suaves, descarga y roce), generados de forma reproducible; no se sustituyen audios originales. Los recursos y clases de almacenamiento, clima, generadores y refuerzos permanecen idénticos. No se incluye ningún stub de Minecraft en el JAR.

Construcción: Java 21, Python 3 y ffmpeg/ffprobe; herramientas ECJ/ASM fijadas por hash y reutilizadas del repositorio. Desde la raíz:

```
python doomsday-needs6/build.py
python doomsday-needs6/test.py
python doomsday-needs6/package.py
```

Las pruebas ejecutan Cycle, Needs, Toilets, Wire, Beds, el codec y la lógica cliente reales contra APIs simuladas; se verifica también el bytecode nativo modificado y los filtros de lado CLIENT. Cubren duración, aplazamiento único, persistencia, línea de visión, urinal frente a inodoro, posición elevada de WC, regeneración condicionada, daño letal en criaturas, sangrado inferior, tecla X, colas del cliente y convivencia con menús. validation.json enumera todos los archivos cambiados. test-results.json separa las comprobaciones de una prueba real en el juego, que sigue pendiente.
