# Inventario ampliable de cadáveres

## Instalación

Reemplaza las versiones anteriores por estos dos JAR en cliente y servidor:

- `mods/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.4-expanding-corpse.jar`
- `mods/zomboid-zombie-gore-1.0.4-neoforge-1.21.1-expanding-corpse-drops.jar`

Conserva Hordes 19, ETF, EMF y el pack de lesiones publicado anteriormente. No se necesitan nuevos packs para este menú. No instales dos versiones de Remains ni de Gore.

## Comportamiento

Los cuerpos de Zombie Remains empiezan con sus 27 huecos habituales, o 54 si son mutantes. Al recoger objetos cercanos, primero combina stacks de idéntico tipo y componentes y ocupa los huecos vacíos; solo si se agotan añade otra fila de nueve. La capacidad no se reduce al vaciarlo y queda guardada.

El recogedor de Gore mantiene el radio de tres bloques, espera de cinco segundos, comprobación de paredes y límites de trabajo de la versión anterior. Solo elimina el objeto del suelo después de transferirlo; preserva el resto si no hubo transferencia. Se pausa mientras un jugador del mundo tenga abierto un menú de contenedor, para no reemplazar el inventario mientras se está usando. Corpse conserva su ampliación mediante inventario adicional.

Hasta 54 huecos se usa el menú de cofre vanilla con las filas correspondientes: 3, 4, 5 o 6. Al superar ese tamaño se usa un menú paginado de seis filas. Las primeras cinco filas contienen hasta 45 objetos de la página; la última tiene las flechas «Página anterior / siguiente» y una hoja con el número de página. Las flechas se activan con clic izquierdo. Los controles no se pueden extraer, mover con Shift ni usar como almacén.

Se conserva la validación del cadáver, su existencia, bloque, estado de esqueleto y distancia de ocho bloques para mantener abierto el menú. La ampliación no cambia rarezas, armas, NVG, loot mítico, cosecha ni deterioro del JAR recibido.

## Guardado

`CorpseData.save` registra la capacidad como `ZGCapacity`, además de los objetos usando el formato nativo. `CorpseData.load` restaura el tamaño ANTES de leer los slots; también infiere el tamaño necesario por los índices de objetos si no existe el campo de capacidad. Los mundos anteriores siguen siendo legibles.

Una vez guardados cadáveres mayores, conserva esta versión o una posterior que soporte el inventario ampliado. Volver al Remains original haría que ignorase los slots fuera de su capacidad fija.

## Validación

- 42 comprobaciones de ampliación solo al llenarse, mezcla por componentes, filas, capacidad, retirada de objetos, páginas y controles protegidos.
- 10 comprobaciones usando las clases del JAR parcheado de Remains: constructor real, guardado y carga real con APIs de Minecraft sustituidas por dobles. Se verifican objetos en slots 27, 28 y 70 y capacidad 72.
- 7 comprobaciones con el recogedor real de Gore conectado a ExpandedStorage: conserva el objeto, amplía un cadáver lleno y combina nuevas entregas sin volver a crecer.
- Cuatro puntos de integración comprobados y 340 métodos verificados con ASM. ZIP/JAR íntegros. Solo cambian tres clases originales de Remains; Gore conserva todo salvo el recogedor y su versión.

**No se ejecutó Minecraft.** Las pruebas de persistencia e integración usan el código de los mods y dobles de las APIs del juego; no validan la presentación gráfica ni todos los comportamientos de clic reales. Prueba en un mundo de ensayo: llena un cuerpo, deja objetos a su lado, espera, abre las páginas y vuelve a entrar al mundo para confirmar los contenidos.

## Reconstrucción

Java 21, ECJ/ASM en `/workspace/zombies-only-patch/tools`:

```sh
python remains-expansion-patch/build.py /ruta/a/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.3.jar
```

La reconstrucción usa Gore 1.0.3 publicado en `mods`. Los dobles de API no se incluyen en los JAR; solo se empaquetan `dev/zomboid/remains/expand` y el recogedor de Gore.
