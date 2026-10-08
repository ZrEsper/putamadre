# Generador: interfaz, alcance y combustible

JAR para Minecraft 1.21.1 / NeoForge: `../mods/zomboid-survival-1.21.1-1.0.3-water-power.jar`.
Reemplazar el JAR anterior de Zomboid Survival, sin cargar dos versiones simultáneamente.
First Aid conserva su interfaz: la interceptación retirada anteriormente sigue desactivada.

- Menú registrado propio para los tres generadores, con dibujo del motor, entrada de combustible, reserva, barras de energía y combustión, estado y autonomía.
- Conserva y expone los 54 espacios existentes. La entrada y reserva aceptan combustible; los objetos anteriores se pueden retirar. Shift-click funciona en ambas direcciones.
- Alcance esférico de 25 bloques, incluyendo distancia vertical; requiere que el generador esté en un chunk cargado. Refrigeradoras, freezer y hornos conservan su menú anterior.
- 64 carbones o carbones vegetales duran exactamente 36 000 ticks de generación, 30 minutos a 20 TPS. Las demás fuentes de combustible usan la misma proporción respecto de su duración de horno. Se alternan 562 y 563 ticks por carbón mediante un residuo persistente.
- Salida de 80 FE/t y depósitos de 64 000 FE (normal), 256 000 FE (fijo) y 384 000 FE (remolque). El combustible se pausa cuando no cabe otro tick completo de energía, y se reanuda al extraer energía.
- Persistencia de combustible y residuo; conversión única de la carga encendida de versiones anteriores. Inventarios y energía existentes se conservan.
- Sonido original de motor diésel, con muestra mono Ogg de un segundo y subtítulos; humo de escape cada medio segundo. Ambos se emiten únicamente en ticks de generación, nunca en espera o sin combustible. Las máquinas de frío conservan su sonido anterior.
- Los alimentos perecederos con hasta 200 ticks (10 segundos) de diferencia de frescura vuelven a apilarse mediante clic, shift-click, arrastre, recogida y unión en el suelo. Se normalizan al estado más viejo, sin renovar su vida útil. Se mantienen separados tipos de alimento, estados congelados/fríos diferentes y metadatos de otros mods. La igualdad general de objetos y la sincronización de inventario siguen siendo estrictas.
- Datos del panel sincronizados como pares de mitades de 16 bits, evitando valores negativos de energía o truncamiento de combustibles largos.

## Agua y balance en 1.0.3

Refrigeradoras: 1 FE/t; freezer: 2 FE/t; hornos eléctricos: 30 FE/t, sin cambio. Las capacidades por generador se aplican en generación, pausa, capacidad FE, carga de guardados y sincronización del panel mediante nueve campos de 16 bits. Se conserva la energía anterior al actualizar.

Los dispensadores existentes de Doomsday se identifican por sus IDs reales (variantes de `waterdispenser` / `watercooler`); se agregan al tipo de entidad de máquina mediante las claves del mapa de bloques registrados, sin introducir IDs inexistentes. Un BlockItem propio `zomboid_survival:water_dispenser` apunta al bloque existente y proporciona la salida estable de la receta. Si no hay un bloque de dispensador en la dependencia, no se registra ese alias y la condición `neoforge:item_exists` omite su receta, evitando referencias a objetos inexistentes.

El tanque es exclusivo para agua: se llena solo con cubetas de agua, devuelve la cubeta vacía, contiene 32 porciones y requiere 100 ticks de electricidad para filtrar cada porción. La demanda con agua es 1 FE/t, sin consumo con tanque vacío. Las botellas se usan solo para extraer. El agua fría usa el sistema térmico de FoodClock y la hidratación existente de DrinkItem (+8 frente a +6 sin enfriar). Tanque y filtrado parcial se guardan en NBT. El dispensador no admite comida por sus ranuras de automatización.

La recolección de agua con botellas vanilla o del mod entrega `contaminated_water`. Se bloquea su uso directo en el DrinkItem original. Las recetas de smelting, smoking y campfire_cooking producen la botella de agua limpia existente; el StoveBlockEntity de Farmer's Delight usa CAMPFIRE_COOKING. Los formatos de recetas se contrastaron con los datos de Minecraft 1.21.1.

La comprobación WaterPowerChecks ejecuta los generadores y dispensadores sobre el MachineEntity real parcheado y también el DrinkItem real para comprobar consumo, capacidad, pausa, NBT, campos de red de 32 bits, registro de IDs, cubetas, filtros, salida fría, hidratación y bloqueo de agua contaminada. Los demás tipos de la API son dobles de prueba. No se ejecutó Minecraft ni la dependencia Doomsday completa en este entorno; deben comprobarse los IDs y la interacción del dispensador, la receta y las recetas de calentamiento en el juego.

## Corrección de arranque en 1.0.2

El log de 1.0.1 identifica `FoodMenuMergeMixin is missing an @Mixin annotation`. La definición de prueba de `@Mixin` usaba retención RUNTIME, pero la API oficial utiliza CLASS y Mixin 0.8.7 busca la anotación entre las anotaciones invisibles del bytecode. Se corrigió la retención y se reconstruyeron los cuatro mixins de apilado. Las anotaciones de inyección conservan su retención RUNTIME.

La comprobación `MixinAnnotationChecks` reproduce el fallo con el JAR 1.0.1 y comprueba en el JAR final los ocho mixins registrados y sus seis métodos de inyección alimentaria. Esta comprobación se ejecuta antes de aceptar el JAR en cada compilación; no sustituye la prueba completa de arranque de Minecraft.

## Reproducir y comprobar

Con Python 3 y Java 21:

```sh
python3 generator-patch/build.py
```

Descarga ECJ 3.39.0 y ASM 9.7.1 desde Maven Central con TLS y SHA-256 fijados. El audio original se puede regenerar con `python3 generator-patch/make_generator_sound.py` si está instalado ffmpeg; la compilación normal utiliza el Ogg incluido. Compila las clases añadidas, aplica el parche sobre el JAR anterior incluido en `mods`, ejecuta los checks y escribe el JAR final. No incluye herramientas ni clases de prueba en el mod.

Los checks ejecutan el bytecode **real del MachineEntity parcheado** con dobles de las API de Minecraft/NeoForge, y verifican consumo exacto, pausa/reanudación, guardado/carga y migración, límites del radio, transferencias de inventario, sincronización y llamadas de dibujo del panel. También se comprueban las emisiones de sonido/partículas en funcionamiento y espera, el límite exacto de tolerancia alimentaria, metadatos distintos y la ausencia de renovación de frescura en mezclas repetidas. También verifican el JAR y que los demás archivos del mod no cambien. Los dobles usan las firmas conocidas de estas API; no sustituyen una compilación contra el entorno completo ni una prueba dentro del juego.

Pendiente de comprobar en Minecraft: cargar con los demás mods, abrir cada generador, verificar el aspecto y las interacciones del panel, y comprobar un generador existente en una partida guardada. Los mixins de unión de alimentos requieren además validación de sus puntos de inyección con Minecraft/NeoForge y los demás mods. No se ejecutó Minecraft en este entorno.
