# Mods de Zomboid

## TacZ NPCs tactical.13: saqueo prioritario, equipo y exploración

[Descargar tactical.13](mods/tacznpcs-2.1.0-1.21.1-tactical.13.jar). Sustituye tactical.12 y otras versiones de TacZ NPCs en cliente y servidor. Mantén las dependencias y Remains compatible que ya usabas.

- Revisan cuerpos y objetos a 24 bloques; priorizan mejoras de equipo y recogen stacks completos.
- Equipan mejores armas, herramientas y prendas; mochila persistente de 27 stacks con conservación del botín.
- Linterna en mano secundaria solo de noche y un segundo mínimo entre golpes.
- Buscan otras rutas cuando no queda botín cercano y eliminan cuerpos únicamente al comprobar que estén vacíos.
- Se construye sobre el JAR .12 suministrado, manteniendo sus recursos y funciones fuera de estos cambios.

Pruebas de regresión e integridad aprobadas. **Pendiente comprobar movimiento, luz y animaciones dentro del modpack completo.**

[Instalación, comportamiento y límites](tacznpcs-patch/README.md) · [Fuentes reproducibles](tacznpcs-patch/tacznpcs-tactical.13-fuentes.zip).

## Historial: TacZ NPCs tactical.5 (sustituido por .13): civiles con profesiones y combate vanilla adaptado a TACZ

[Descargar tactical.5](mods/tacznpcs-2.1.0-1.21.1-tactical.5.jar). Reemplazar **todas las versiones anteriores de TacZ NPCs** en cliente y servidor. Mantener [Zombie Remains Loot 1.6.1 npc-compat](mods/zombie-remains-loot-1.6.1-neoforge-1.21.1-npc-compat.jar), Self Expression 2.22a y las dependencias existentes.

- Cambia la armadura vanilla por uniformes civiles de Self Expression, también en NPCs existentes. Doce profesiones/uniformes con herramientas correspondientes; se conserva el armamento real.
- Goals vanilla independientes para ataque a distancia, melee, paseo y observación. Las decisiones no bloquean movimiento y mirada al estar ociosos.
- Permite correr al perseguir amenazas y retira las penalizaciones TACZ de movimiento de estos NPCs. Recuperación de rutas fallidas en agua.
- Conserva defensa prioritaria, saqueo de cadáveres/materiales, experiencia limitada, ropa militar rara, spawn natural y huevo de camuflaje.

Pruebas de decisiones, integridad de objetos y reconstrucción aprobadas. **Pendiente probar locomoción y animaciones en el modpack completo.**

[Profesiones, instalación y límites](tacznpcs-patch/README.md) · [Fuentes reproducibles](tacznpcs-patch/tacznpcs-tactical.5-y-remains-fuentes.zip).

## Historial: TacZ NPCs tactical.4 (sustituido por .5): defensa prioritaria, movimiento y saqueo oportunista

[Descargar tactical.4](mods/tacznpcs-2.1.0-1.21.1-tactical.4.jar). Reemplazar tactical.3 y otras versiones de NPCs en **cliente y servidor**. Mantener [Zombie Remains Loot 1.6.1 npc-compat](mods/zombie-remains-loot-1.6.1-neoforge-1.21.1-npc-compat.jar) y sus dependencias.

- Corrige la lentitud artificial; activa navegación flotante y recuperación de rutas fallidas en agua.
- El atacante reciente tiene prioridad. Los zombis distantes y distraídos permiten saquear; giran cabeza y cuerpo antes de disparar.
- Recogen materiales sueltos y saquean cadáveres con animación. Apilan objetos y guardan experiencia limitada de saqueo.
- Conserva ropa, cadáveres, protección de fuego amigo, spawn natural y huevo de camuflaje.

Pasaron **1201 aserciones con dobles de API** y análisis de **138 métodos**. **Pendiente validar movimiento físico y animaciones dentro del modpack.** La diferencia exacta del 5% frente al jugador no está medida.

[Instalación y límites](tacznpcs-patch/README.md) · [Fuentes reproducibles](tacznpcs-patch/tacznpcs-tactical.4-y-remains-fuentes.zip).

## Historial: TacZ NPCs tactical.2 (sustituido por tactical.4)

[Descargar TacZ NPCs tactical.2](mods/tacznpcs-2.1.0-1.21.1-tactical.2.jar).

Para **Minecraft 1.21.1 / NeoForge**, con **TACZ** y las mismas dependencias del mod original. Reemplaza el original o tactical.1 en **cliente y servidor**; instala solo una versión de TacZ NPCs.

- Huevo propio con textura de camuflaje, disponible en la pestaña de huevos y búsqueda de creativo: `/give @s tacznpcs:bandit_spawn_egg`.
- Reacción de 0,3–0,7 segundos, precisión variable y disparos hasta 24 bloques, con movimiento lateral intermitente.
- Al perder visión, buscan la última posición vista durante un máximo de ocho segundos, sin actualizarla a través de paredes. Se conserva la retirada existente ante zombis.
- Conserva los reemplazos en estructuras y el registro regional persistente. Corrige una llamada de coordenadas del spawn regional y respeta `doMobSpawning`, Pacífico y jugadores de supervivencia para esos encuentros.

Pasaron **1038 aserciones con dobles de API** y el análisis de **105 métodos de bytecode**. **Falta validar el arranque y las interacciones dentro del modpack completo.**

[Detalles e instalación](tacznpcs-patch/README.md) · [Fuentes y herramientas](tacznpcs-patch/tacznpcs-tactical.2-fuentes.zip).


## Hordes 1.8.3: mínimo por chunk y zombis con atención individual

[Descargar Hordes 1.8.3](mods/zomboid-hordes-1.8.3-neoforge-1.21.1-living-population.jar).

- **Mínimo un zombi por chunk** cargado cerca de jugadores en supervivencia, tanto de día como de noche. Objetivos de **1/2/3/4/5**, con chances **70/18/8/3/1 %** y cooldown propio de **5–10 minutos**. Se repone el déficit, sin acumular zombis indefinidamente.
- **`/zhorda poblar`** ejecuta la reposición de todos los jugadores en supervivencia sin esperar el cooldown, en lotes durante los siguientes segundos. Requiere trucos o permiso de operador; también funciona desde la consola del servidor.
- **`/zhorda horda`** y **`/zhorda helicoptero`** atraen a los zombis existentes. Conservan personalidad y sentidos: algunos ignoran el ruido, otros se distraen y los atentos persisten. Usan pistas audibles, visión real y rutas por tramos para acercarse más; no se teletransportan ni conocen al jugador a través de paredes.

Reemplaza Hordes 1.8.2 y versiones anteriores en **cliente y servidor**. Instala solo una versión del addon. Mantén las mismas dependencias; el melee 2.3.1 de abajo sigue vigente. El mínimo depende de terreno seguro y chunks cargados; la distancia mínima al jugador ahora es de ocho bloques para permitir la población de su propio chunk.

Las pruebas de población, guardado, IA, navegación, bytecode y comandos con Brigadier real pasaron. **Falta validar 1.8.3 dentro del modpack completo.**

[Detalles y comandos](hordes-patch/README.md) · [Fuentes](hordes-patch/zomboid-hordes-1.8.3-patch-source.zip) · [Validación](hordes-patch/VALIDATION-1.8.3.txt).


## Historial: Hordes 1.8.2 y melee 2.3.1 (melee vigente)

- [Descargar Hordes 1.8.2](mods/zomboid-hordes-1.8.2-neoforge-1.21.1-chance-population.jar): objetivo aleatorio de **0–5 zombis vivos por par de chunks**, con probabilidades **40/30/18/8/3/1 %**, respectivamente. Cinco es el resultado más raro (1 %). Cooldown compartido y persistente de **5–10 minutos**. Basta con un chunk del par cargado; las búsquedas se reparten para evitar que el terreno bloqueado acapare el presupuesto. Cuenta los zombis existentes y no elimina los que ya haya.
- [Descargar melee 2.3.1](mods/zomboid-global-melee-animations-2.3.1-range-plus-one.jar): **V pasa de 1,5 a 2,5 bloques** y el melee de LesRaisins, incluidas las espadas/sartén del puente, gana **1 bloque** en búsqueda y validación del golpe.

Reemplaza las versiones anteriores en **cliente y servidor**; no cargues dos versiones del mismo mod. Conserva sus dependencias. Las versiones de Hordes anteriores que aparecen más abajo son bases de reconstrucción, no deben instalarse junto con 1.8.2.

La población se activa en supervivencia, en el Overworld, fuera de Pacífico y con `doMobSpawning=true`. `/zhorda estado` muestra las condiciones de activación, pausa, población viva, objetivo y cooldown. El log aportado confirmó una prueba con seis zombis añadidos en supervivencia y pruebas bloqueadas en creativo.

Los checks de lógica, persistencia, bytecode, alcance y anotaciones pasaron con dobles de API. **Falta probar los nuevos JAR dentro del modpack completo.**

[Detalles y reproducción](chance-range-patch/README.md) · [Fuentes y herramientas](chance-range-patch/chance-range-patch-source.zip) · [Resultados de validación](chance-range-patch/VALIDATION.txt).


## Zomboid Hordes 1.8.1

[Descargar Zomboid Hordes actualizado](https://github.com/ZrEsper/putamadre/raw/refs/heads/main/mods/zomboid-hordes-1.8.1-neoforge-1.21.1-chunk-population.jar)

Para **Minecraft 1.21.1**, **NeoForge 21.1.255 o posterior** y **SoundAttract 6.3.8d**. Reemplaza el addon Zomboid Hordes anterior en **cliente y servidor**; no cargues ambas versiones simultáneamente.

- Sustituye las oleadas grandes por reposición hasta un **mínimo de dos zombis por par de chunks cargados** en el Overworld, contando los zombis vivos existentes.
- Reparto inicial: **1+1 (50 %), 2+0 (25 %) o 0+2 (25 %)**. Los pares son vecinos fijos hacia este/oeste.
- Cada par comparte un **cooldown aleatorio de 5–10 minutos**, conservado al guardar/reabrir. Si ya hay dos o más zombis, no añade nuevos.
- El ruido y el helicóptero atraen zombis existentes; el helicóptero conserva su sonido sin crear nuevas oleadas.
- Solo aparece en terreno apto, sin forzar chunks nuevos, agua, colisiones o apariciones a menos de 24 bloques de jugadores de supervivencia. El mínimo puede quedar pendiente cuando no haya una posición segura.

Las comprobaciones de lógica, guardado y bytecode pasaron con dobles de las API. **Falta probar el arranque y la integración dentro de Minecraft con el modpack completo.**

[Detalles, código del parche y comprobaciones](hordes-patch/README.md). La versión 1.8.0 se conserva como base de reconstrucción y no debe instalarse junto con la nueva.

## First Aid Zomboid 3.4.6

[Descargar First Aid actualizado](https://github.com/ZrEsper/putamadre/raw/refs/heads/main/mods/firstaid-1.21.1-ZOMBOID-v3.4.6-wetness-no-panic.jar)

Para **Minecraft 1.21.1** y **NeoForge 21.1.219 o posterior**, con las mismas dependencias de la versión anterior: **lrtactical 0.4.3** y **hordes 1.6.3f**. Reemplaza el First Aid anterior en **cliente y servidor**; no cargues dos versiones de First Aid simultáneamente.

- Desactiva el heartbeat, el pánico automático de encuentros/disparos cercanos y el contorno claro de pantalla.
- El contacto con agua moja las partes alcanzadas del cuerpo. Se secan gradualmente en unos **dos minutos** fuera del agua.
- La humedad prolongada puede provocar **gripe de dos minutos**, con estornudos cada 10–20 segundos.
- Los estornudos emiten sonido y partículas verdes desde la cara, y atraen zombis a **32 bloques**.
- La **toalla reutilizable** se fabrica con **tres telas de First Aid**, en cualquier distribución. Clic derecho seca el cuerpo; una gripe ya iniciada sigue su curso.
- El menú muestra humedad por extremidad y tiempo de gripe. Se conservan el menú de habilidades y los tratamientos existentes.

Las comprobaciones de lógica y bytecode pasaron con dobles de las API. **Falta probar el arranque y la integración dentro de Minecraft con el modpack completo.**

[Detalles, código del parche y comprobaciones](firstaid-patch/README.md). La versión 3.4.5 se conserva como base de reconstrucción; no debe instalarse junto con la nueva.

## Zomboid Survival 1.0.3

[Descargar el JAR actualizado](https://github.com/ZrEsper/putamadre/raw/refs/heads/main/mods/zomboid-survival-1.21.1-1.0.3-water-power.jar)

Para Minecraft 1.21.1, NeoForge y las dependencias del mod original. Reemplaza la versión anterior de **Zomboid Survival**, en cliente y servidor si juegas en uno. Instala solo una versión y conserva tu JAR de **First Aid**.

| Máquina | Consumo / capacidad |
| --- | --- |
| Refrigeradora | 1 FE/t (95 % menos) |
| Freezer | 2 FE/t (95 % menos) |
| Generador normal | 64 000 FE |
| Stationary / fijo | 256 000 FE |
| Trailer / remolque | 384 000 FE |
| Dispensador con agua | 1 FE/t |

El alcance de los generadores sigue siendo de 25 bloques. Conservan su panel propio, sonido de motor, humo y 30 minutos activos por 64 carbones, con pausa cuando el depósito está lleno. Las capacidades mayores también se muestran en el panel y se conservan al guardar.

### Dispensador de agua

Receta: **5 lingotes de hierro, 2 cristales, 1 carbón o carbón vegetal y 1 cubeta vacía**.

```text
Hierro  Hierro   Hierro
Cristal Carbón   Cristal
Hierro  Cubeta   Hierro
```

El dispensador aparece vacío. Haz clic con una **cubeta de agua** para llenarlo; no acepta botellas como entrada. Admite ocho cubetas, equivalentes a 32 botellas, y devuelve la cubeta vacía. Con electricidad filtra una botella cada cinco segundos y conserva el agua fría. Haz clic con una botella vacía de Minecraft o del mod para sacar agua filtrada.

El agua filtrada fría aporta **8 de hidratación**, frente a 6 del agua filtrada normal. Sin electricidad no filtra; el agua ya filtrada se puede sacar a temperatura ambiente. Se guardan el tanque y el progreso de filtrado.

Recoger agua del suelo con botellas produce **agua contaminada**, que no se puede beber directamente. Puedes hervir la botella en un horno (10 segundos), una estufa de Farmer's Delight o una fogata (10 segundos), o un ahumador (5 segundos), para obtener agua filtrada.

La versión conserva el apilado de alimentos con hasta 10 segundos de diferencia de frescura, usando la frescura más vieja, y mantiene la interfaz de First Aid.

Las comprobaciones de lógica, bytecode, capacidades, guardado, tanque, hidratación y anotaciones pasaron con dobles de las API. Falta comprobar el arranque y la integración con los demás mods dentro de Minecraft.

[Código del parche y comprobaciones](generator-patch/README.md). Los JAR anteriores se conservan como base o respaldo, no deben instalarse junto con la versión actual.
