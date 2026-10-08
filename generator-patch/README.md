# Generador: interfaz, alcance y combustible

JAR para Minecraft 1.21.1 / NeoForge: `../mods/zomboid-survival-1.21.1-1.0.2-generator-ui.jar`.
Reemplazar el JAR anterior de Zomboid Survival, sin cargar dos versiones simultáneamente.
First Aid conserva su interfaz: la interceptación retirada anteriormente sigue desactivada.

- Menú registrado propio para los tres generadores, con dibujo del motor, entrada de combustible, reserva, barras de energía y combustión, estado y autonomía.
- Conserva y expone los 54 espacios existentes. La entrada y reserva aceptan combustible; los objetos anteriores se pueden retirar. Shift-click funciona en ambas direcciones.
- Alcance esférico de 25 bloques, incluyendo distancia vertical; requiere que el generador esté en un chunk cargado. Refrigeradoras, freezer y hornos conservan su menú anterior.
- 64 carbones o carbones vegetales duran exactamente 36 000 ticks de generación, 30 minutos a 20 TPS. Las demás fuentes de combustible usan la misma proporción respecto de su duración de horno. Se alternan 562 y 563 ticks por carbón mediante un residuo persistente.
- Salida de 80 FE/t y depósito de 64 000 FE. El combustible se pausa cuando no cabe otro tick completo de energía, y se reanuda al extraer energía.
- Persistencia de combustible y residuo; conversión única de la carga encendida de versiones anteriores. Inventarios y energía existentes se conservan.
- Sonido original de motor diésel, con muestra mono Ogg de un segundo y subtítulos; humo de escape cada medio segundo. Ambos se emiten únicamente en ticks de generación, nunca en espera o sin combustible. Las máquinas de frío conservan su sonido anterior.
- Los alimentos perecederos con hasta 200 ticks (10 segundos) de diferencia de frescura vuelven a apilarse mediante clic, shift-click, arrastre, recogida y unión en el suelo. Se normalizan al estado más viejo, sin renovar su vida útil. Se mantienen separados tipos de alimento, estados congelados/fríos diferentes y metadatos de otros mods. La igualdad general de objetos y la sincronización de inventario siguen siendo estrictas.
- Datos del panel sincronizados como pares de mitades de 16 bits, evitando valores negativos de energía o truncamiento de combustibles largos.

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
