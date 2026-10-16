# Corrección .46: cierre al generar cadáveres

[Descargar ZIP corregido](../distribuciones/Zombies-Poldo-Loot-46-Correccion-Crash.zip).

El log del usuario revela `IllegalAccessException` en `ZoneReflection.set` al escribir `profession31` de `ZombieRemainsLoot$Corpse`: el campo es público pero su clase contenedora no lo es. El setter ahora habilita acceso al campo antes de escribir, como ya hace el getter nativo. No cambia la visibilidad de la clase ni el balance de loot.

La regresión usa una clase no pública de otro paquete: carga el setter original y reproduce exactamente `IllegalAccessException`, después verifica el setter corregido para profesión y ambas marcas de finalización/propiedad. Las pruebas anteriores usaban una clase pública y no cubrían esa restricción. Total: 422 comprobaciones; sin ejecución real de Minecraft. Reemplazar Hordes .45 por .46 en cliente y servidor; los otros tres JAR del ZIP son idénticos a la entrega anterior.

# Loot profesional .45 y tres niveles de torreta

[Descargar ZIP directo](../distribuciones/Zombies-Poldo-Loot-45-Torretas-3-Niveles.zip) · [Guía sencilla y recetas](GUIA.txt) · [Análisis de rendimiento](RENDIMIENTO.txt).

Cuatro reemplazos sobre los JAR enviados: Hordes .45, Remains .15, Quietly .17 y Turrets .4. `inputs.json` fija los SHA256 de los originales. No se publican los archivos de diagnóstico originales, solo agregados de rendimiento sin datos de conexión o jugador.

Se encontró una ruta de generación del panel que retorna antes de asegurar ropa, además de premios posteriores de Quietly que vuelven a introducir herramientas. El nuevo pase final aplica las garantías tras ambas rutas y tras la chatarra, distingue familias de herramienta sin depender del desgaste y dispersa objetos. Quietly filtra antes de insertar y vuelve a normalizar al terminar. Los cadáveres anteriores se reparan una sola vez; la marca se guarda en NBT. Los inventarios de jugadores, almacenes propios y carga de NPC conservan sus protecciones. La ropa usa la profesión reconocida, catálogo configurable y alternativas teñidas para las prendas ausentes. La identificación se basa en datos/nombre/equipo, sin inferir visualmente texturas de cliente.

Policías con arma corta/bate real LR y mochila, munición compatible opcional; peso inicial 5 frente a militar 3. La migración tiene marcador por mundo para que no deshaga futuros ajustes del administrador. Los mutantes reciben suministros médicos y materiales adicionales una sola vez en la creación, escalados por cantidad, perfil y categorías del panel (cero desactiva el extra), sin alterar el porcentaje de cura de infección. Se reconoce el addon L4D2 solo en la conversión a cadáver, conservando las clases nativas que controlan vida e IA.

Los tres niveles usan la misma entidad nativa, con entero sincronizado, persistencia NBT y recogida del ítem correcto. Vida 50/100/160, radio 7/13/20 y disparo cada 60/32/16 ticks como mínimo. Se conservan adquisición de 16 ticks, energía real de generador y validación de visibilidad. Industrial y Tactical reutilizan sus propios modelos/texturas nativos para todos los estados; no se recolorean mapas UV ni se cambian los recursos originales.

Java 21 y Python 3, usando ECJ/ASM ya fijados por hash en el repositorio. Desde la raíz:

```
python loot-turrets45/build.py
python loot-turrets45/test.py
python loot-turrets45/package.py
```

`test-results.json` registra 418 controles: 37 profesiones, 100 policías, no reposición tras saqueo, ropa en cadáveres anteriores, guardado nativo de metadatos, protección de inventarios, niveles/intervalos y puentes nativos. Una carga aislada del Quietly original ejecuta su bonus real y reproduce 12 herramientas; la misma entrada en el método parcheado termina con un hacha y un pico, sin cambiar otros objetos. Los proveedores de Minecraft/TaCZ/LR y mundo son simulados. Se comprueban bytecode, recetas y preservación exacta de higiene, protección y recursos. No equivale a una partida real: esa validación sigue pendiente.

`performance-analysis.json` resume el perfil de Render thread: Xaero ~21,7 %, EMF ~4,6 %; servidor 20 TPS en las métricas adjuntas. Los porcentajes de CPU no predicen incremento de FPS. Los JAR de este paquete no cambian Xaero.
