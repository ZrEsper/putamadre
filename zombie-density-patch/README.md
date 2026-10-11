# Hordes: límite local de acumulación .6

[Descargar addon](../mods/zomboid-hordes-1.8.3-neoforge-1.21.1-reactive-density.6.jar).

Reemplaza el addon `zomboid-hordes` .5 o anterior en cliente y servidor. Conserva The Hordes, MutantsZombies y las demás dependencias. Se ha reconstruido sobre el .5 del repositorio, conservando sus zonas, ruido, sigilo y esquiva.

Al añadirse un zombi o mutante nuevo, cuenta los zombis compatibles vivos a 24 bloques de radio horizontal y ±20 de altura. Si ya hay 24, cancela esa incorporación. No fuerza chunks ni recorre todas las entidades del mundo. Se conservan los que cargan desde disco, los huevos y comandos cuyo origen haya registrado el addon. Otros mobs no cuentan. Los spawns de otros mods que no registren su origen también quedan sujetos al límite.

No elimina zombis guardados ni impide que grupos existentes se junten al perseguir sonidos/jugadores. Tampoco borra la acumulación ya presente en la captura. Puede reducir oleadas en zonas saturadas; no limita un grupo que llega desde otra zona.

13 comprobaciones de límites y eventos con dobles de API aprobadas. Verificación ASM e integridad ZIP; solo cambia `ChunkPopulation.class` y se añaden los helpers. No probado en Minecraft.

```sh
python -B zombie-density-patch/build.py /ruta/zomboid-hordes-reactive-noise-dodge.5.jar /ruta/herramientas
```
