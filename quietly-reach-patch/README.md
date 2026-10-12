# Quietly v11: alcance de interacción de cinco bloques

[Descargar JAR](../mods/quietly-1_0_0-cooldown-v11-five-block-reach.jar).

Reemplaza Quietly v10 en cliente y servidor; deja una sola versión instalada. Construido sobre el JAR enviado por el usuario. Mantiene búsqueda, tiempos, Scavenging, rareza, HUD y sincronización de almacenamiento.

Añade un mínimo de 5 bloques al alcance de interacción con bloques de Minecraft, compartido por la selección del cliente y las validaciones del servidor. Así permite seleccionar y abrir cofres y barriles desde más lejos. Como usa el alcance general de bloques, también permite interactuar y romper/colocar otros bloques hasta esa distancia. Conserva alcances superiores dados por creativo u otros mods; no amplía el alcance de entidades ni de ataques a zombis.

No atraviesa paredes y conserva comprobaciones de distancia, permisos, bloqueo y cancelación de la búsqueda por movimiento. La permanencia del inventario abierto conserva el comportamiento original del contenedor.

Validación: cuatro comprobaciones de alcance y callback con APIs simuladas, verificación ASM del mixin e integridad ZIP. Solo se modifica la lista de mixins; todas las clases y recursos originales permanecen intactos. No se ejecutó Minecraft.

```sh
python -B quietly-reach-patch/build.py /ruta/quietly-v10.jar /ruta/herramientas
```

Requiere Java 21, ECJ y ASM. Los dobles de prueba no se empaquetan.
