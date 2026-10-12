# Vista isométrica de vehículos (.20)

Entrada: Iso `.19-vehicles-food` aportado por el usuario. Salida: `.20-vehicle-visibility`.

La protección de entrada nativa de .19 terminaba `tickXIVHEAD` cuando el jugador estaba montado o comiendo, antes de `SodiumCompat.run()`. .20 conserva esa protección, pero ejecuta el mantenimiento visual antes de retornar. El recorte de líneas de visión usa el volumen del coche completo cuando el jugador va sentado; CTRL sigue desplazando ese foco. No se modifican las coordenadas de alcance del jugador ni la interacción de mochilas.

Mientras está activo el modo isométrico se mantienen visibles las entidades auxiliares `mcinterface1211.BuilderEntity*` cercanas. Si EntityCulling aporta sus métodos, se restablecen sus indicadores de ocultación y se solicita visibilidad forzada. No se cambian los indicadores de zombis u otras entidades; sin EntityCulling, las llamadas opcionales inexistentes se omiten.

Pruebas: 12 casos simulados de foco del coche, foco desplazado con CTRL, actualización visual montado y exclusión de entidades ajenas a MTS. Se analizaron 886 métodos con ASM y se verificó la integridad del JAR. No se ejecutó Minecraft: el aspecto del recorte y la compatibilidad con el conjunto real de optimizadores requieren prueba en el juego.

```sh
python iso-vehicle-view-patch/build.py ISO_19 TOOLS
```

## .21: conservar el suelo

El foco montado conserva como mínimo el Y inferior del jugador y `floor(vehicle.position.y)+1`. Una caja envolvente inclinada ya no reduce ese mínimo bajo la carretera. Se conservan las correcciones de visibilidad y de actualización al conducir. Doce pruebas simuladas y análisis de 886 métodos; sin ejecución del juego.

## Versión .22: interacción nativa para Carry y modelos originales

Se construye desde el JAR .21 aportado por el usuario, cuyo SHA coincide con la copia .21 publicada. Reemplazar solo Dungeons Iso .21 por `dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.22-carry-model-textures.jar`; mantener los otros mods.

Los clics de interacción ya no saltan directamente a gameMode.useItemOn/interact. Bridge.open coloca el objetivo isométrico real en hitResult y crosshairPickEntity y llama a Minecraft.startUseItem, conservando el evento NeoForge onClickInput de cada mano y la secuencia normal de bloques, entidades y uso de objetos. Esto permite a Carry On y otros mods recibir el clic. Además, agacharse con ambas manos vacías permite elegir bloques y entidades bajo el cursor sin exigir que sean contenedores/cadáveres: se comprueban rango nativo, línea de visión y estado de conducción/comida. El mod de Carry conserva sus restricciones y validación de servidor; no se crean objetos ni se modifica su capacidad de transporte.

La compatibilidad Sodium desactivaba hasTransform durante reconstrucciones isométricas. El mixin de esa sobreescritura ahora deja intacto el resultado original, permitiendo que los modelos con transformaciones conserven sus quads y texturas nativas. No se sustituyen bloques del mundo ni se aplica la textura de un vecino arbitrario. El bloque visible utiliza su propio modelo y recursos originales. Se conservan el recorte direccional, CTRL, el suelo de vehículos, visibilidad de coches y las clases de mochilas y acciones nativas de .21.

Validación: 18 comprobaciones simuladas (12 de cámara/vehículos y 6 de interacción), análisis ASM de 890 métodos, integridad ZIP y comparación de clases. Comprobación del flujo de startUseItem con el parche público de NeoForge (rama 1.21.x: onClickInput(1, keyUse, mano) antes del uso de cada mano). No se ejecutó Minecraft ni se dispone aquí del JAR específico de Carry On del usuario: queda pendiente verificar su gesto configurado y la apariencia de los bloques de la captura con los optimizadores/resource packs reales.

```sh
python iso-vehicle-view-patch/build.py ISO_21 TOOLS
```
