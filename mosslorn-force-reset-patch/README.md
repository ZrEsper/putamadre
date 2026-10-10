# Mosslorn v9: reiniciar todos los cofres y barriles

El comando anterior respetaba los inventarios ocupados y recorría 61.793 entradas a cuatro por medio segundo: una vuelta podía tardar más de dos horas. Esta revisión vacía también los ocupados y restaura loot pendiente por chunks, con un catálogo completo del Mosslorn enviado. No requiere ejecutar otro script sobre el mundo.

## Instalar y usar

Cerrar Minecraft y reemplazar el datapack anterior de Mosslorn por `mosslorn-expanded.9-reinicio-total.zip` en datapacks del mundo que realmente se juega. Dejar solo una versión activa. Mantener los JAR Jade pending-loot y Quietly v7 de la revisión anterior; esos JAR impiden que la vista previa lea loot pendiente.

Después de abrir el mundo:

```
/reload
/function mosslorn:force/reiniciar
/function mosslorn:force/estado
```

`/function mosslorn:loot/reiniciar` también apunta al reinicio total. Este reinicio elimina los objetos de todos los cofres, cofres trampa y barriles vanilla catalogados o registrados, incluidos los renombrados y los puestos por jugadores que figuren en el registro. Conserva CustomName, Lock y demás datos del bloque. Restaura la tabla pendiente: no inserta ni genera loot. La generación sigue siendo al abrir, salvo sistemas externos como tolvas u otros mods.

El catálogo contiene las 61.793 posiciones y categorías del mapa enviado; por eso funciona también si el registro del mundo quedó vacío o incompleto. Las asignaciones del registro actual sustituyen las del catálogo para la misma posición. Si el bloque aún tiene una LootTable, se conserva esa tabla al reiniciarlo; si ya fue abierta, se restaura la asignación registrada o del catálogo.

La preparación añade hasta 64 registros actuales por tick: 61.793 registros requieren unos 48 segundos a 20 TPS, más el coste inicial del catálogo. El chat muestra progreso cada cinco segundos. Cuando aparece «Índice listo», se reinician hasta ocho contenedores por tick en el chunk de cada jugador y sus ocho vecinos cargados. El comando de estado muestra el número realmente reiniciado y los chunks completados.

Los chunks descargados quedan pendientes y se procesan al acercarse un jugador. Si un chunk se descarga a mitad de trabajo, se conserva su cola restante y se continúa al volver. Cada chunk se reinicia una vez por solicitud; no se vuelve a vaciar después de abrirlo. Una nueva solicitud permite reiniciar otra vez todo el mapa. El estado persiste entre guardados y reinicios del servidor.

Los bloques eliminados o sustituidos por máquinas de otros mods se omiten y se cuentan como ausentes. Los contenedores especiales de mods no son objetivo de esta revisión. Los contenedores nuevos que no figuren ni en el catálogo ni en el registro requieren registro; el catálogo corresponde al mundo Mosslorn subido, no a un mundo arbitrario.

El escaneo automático antiguo se desactiva al solicitar el reinicio para evitar que interfiera. La reposición periódica anterior conserva su comportamiento de respetar ocupados y no llama al reinicio destructivo. No abras contenedores mientras se reinicia su zona.

## Validación

Se analizaron con nbtlib todos los NBT del catálogo, se verificaron 61.793 posiciones únicas, las tablas y las referencias internas de funciones. Cuatro casos aplican las instrucciones reales de borrado/asignación a NBT con inventarios ocupados, vacíos y tablas existentes: conservan nombre y bloqueo y terminan con LootTable pendiente. Once casos comprueban límites de chunks negativos y uno el reemplazo de una asignación personalizada. Las comprobaciones estructurales cubren suspensión de colas al descargar, macros y ausencia de generación inmediata.

No se ejecutó Minecraft: no se ha demostrado en el juego el rendimiento ni la ejecución completa de los comandos. La descarga del servidor de Mojang para probarlo fue bloqueada por la red del entorno. La revisión usa el log real para corregir el comportamiento y la sintaxis conocidos; requiere comprobar el progreso en el modpack.

## Reconstruir

Python 3: `python build.py`. Usa el catálogo derivado del mundo enviado y el datapack 8.1 del repositorio. `python validate.py` requiere nbtlib 2.0.4. El catálogo publica únicamente posiciones y categorías de contenedores; no incluye el mundo, level.dat ni datos personales de jugadores.
