# Mods de Zomboid

## Cadáveres con contorno y botín dentro de cofres/cuerpos

[Descargar los cuatro reemplazos juntos](cadaveres-cofres-sin-desbordamiento.zip): Quietly v14, Zombie Remains .7, TacZ NPCs tactical.14 y Dungeons Iso .27, construido sobre la .26 enviada. Contorno al apuntar cadáveres y looteo con clic derecho; el botín adicional de búsqueda se guarda dentro del cofre, ampliando con páginas si falta espacio. Los NPC conservan toda la mochila, equipo y objetos guardados en su cadáver, sin el límite anterior de nueve entradas. Si no hay sitio para colocar el cuerpo, el botín queda en una entrega persistente pendiente, sin objetos flotantes.

Reemplazar versiones anteriores; actualizar Quietly, Remains y TacZ NPCs juntos en cliente y servidor. 138 comprobaciones con APIs simuladas, guardado/carga de clases reales parcheadas y verificación de bytecode aprobadas. **Pendiente probar el arranque, renderizado y sincronización dentro de Minecraft.** [Instalación, fuentes y límites](corpse-storage-patch/README.md).

## Dungeons Iso + TACZ: cámara isométrica .13

[Descargar Dungeons Iso modificado](mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.13.jar). Sustituir solo Dungeons Iso en el cliente; mantener TACZ hotfix-r7 y los mods de cadáveres/búsqueda.

Empieza desactivado y se activa con F4 o su tecla asignada. El cursor en los últimos cinco píxeles laterales gira la cámara; mantener CTRL cambia a desplazamiento de la vista por los bordes. Soltar CTRL devuelve la vista al centro. Selección de bloques no interactuables para romper bayas colgantes y restos, respetando alcance y paredes. Mantener clic derecho apunta sin pasar a primera persona; clic izquierdo dispara conservando la mira. Selección independiente de cadáveres y cofres para interactuar con clic derecho. Sprint en ocho direcciones.

132 comprobaciones con APIs simuladas y verificación de bytecode aprobadas. **Pendiente probar dentro de Minecraft.** [Instalación, comportamiento y límites](iso-tacz-patch/README.md).

En .6, mover bruscamente el cursor acelera el giro de 90°/s hasta 360°/s y la aceleración decae suavemente. La selección omite bloques ocultos por los filtros de Dungeons Iso, conserva la cara real del bloque y permite colocar bloques con clic derecho sobre superficies no interactuables. Los obstáculos físicos desde el jugador, alcance y permisos del servidor siguen vigentes.




Versión .10: sin filtro de modelos de entidades. El apuntado invierte la matriz real de proyección y sigue el rayo del cursor sin selección asistida; si no hay superficie ni entidad, usa un plano a media altura del jugador. La mira clara muestra la línea de tiro; las cruces naranjas breves marcan impactos en bloques recibidos mediante las partículas de TACZ (incluidos disparos cercanos de otros jugadores). No confirma impactos en entidades ni garantiza la trayectoria de cada bala antes de disparar. Con Sodium, el radio de renderizado isométrico baja al 70% del límite anterior, con mínimo de 48 bloques y sin superar el ajuste del usuario. La selección descarta bloques fuera del rayo antes de consultar formas. Rendimiento, aplicación de mixins y precisión visual pendientes de prueba en Minecraft.

Versión .11: el clic derecho con un melee de LR Tactical llega al ataque pesado nativo en lugar de consumirse como colocación de bloques. Los cofres y cadáveres interactuables conservan prioridad; el JAR de melee del usuario no necesita reemplazarse.

## TACZ: tercera persona normal, niebla densa, puñetazos y disparos más fuertes .6

[Descargar TACZ modificado](mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.7.jar). Sustituir el TACZ del cliente y servidor, sin duplicarlo. Disparos con el doble de alcance que .6, un poco más fuertes y con refuerzo bajo techo. Mira de círculo y línea al apuntar en tercera persona normal, sin Dungeons Iso. Niebla periódica y comandos `/zomboidfog prueba`, `/zomboidfog auto` y `/zomboidfog off`. [Instalación, validación y límites](tacz-third-person-patch/README.md).

## Gun Durability: destrabado al disparar .1

[Descargar Gun Durability modificado](mods/gundb-2.2.2-1.21.1-left-click-unjam.1.jar). Reemplaza el original en el cliente. Intentar disparar un arma trabada inicia su animación y tiempo de destrabado originales, sin reiniciarla al mantener el clic. [Instalación y validación](gundb-left-click-patch/README.md).

Versión .13: desplazamiento acumulado del recoil limitado a ±4° verticales y ±3° horizontales respecto al objetivo del cursor. Al soltar la tecla de disparo de TACZ, el desplazamiento se limpia en la siguiente actualización de apuntado; cambiar ampliamente de objetivo también descarta el desplazamiento anterior. Prueba con 600 actualizaciones de ráfaga intensa y retorno del jugador al objetivo aprobada con APIs simuladas. No modifica la cadencia, dispersión ni daño del servidor. Pendiente comprobar ráfagas y ritmo visual en Minecraft.

## Niebla densa y control local de acumulación

TACZ .3: niebla con entrada de 60 segundos, fase fuerte aleatoria de 2–10 minutos y salida de 60 segundos. Visibilidad aproximada de 12 bloques, compensando distancia de cámara. `/zomboidfog prueba` reproduce el episodio completo. 40 comprobaciones simuladas aprobadas.

[Hordes .6 con límite local](mods/zomboid-hordes-1.8.3-neoforge-1.21.1-reactive-density.6.jar): frena nuevos zombis cuando ya hay 24 cerca; no elimina los existentes ni evita que migren. Sustituir el addon en cliente y servidor. [Detalles y límites](zombie-density-patch/README.md). 13 comprobaciones aprobadas con APIs simuladas. Pendiente probar en Minecraft.

Versión .4: la cámara isométrica sobrescribía la distancia de niebla en su hook final de FogRenderer. Un hook de compatibilidad posterior, con prioridad 500, vuelve a aplicar el episodio atmosférico después del ajuste isométrico. Conserva la distancia compensada, transiciones y duración. Se probaron la escritura final de distancia y las exclusiones de agua/vista normal con APIs simuladas; la aplicación y orden de mixins del modpack siguen pendientes de validar en Minecraft.

## LesRaisins Tactical: melee isométrico .2

[Descargar LesRaisins modificado](mods/LesRaisins-Tactical-Equipements-1.21.1-0.4.3-zomboid-isometric-melee.2.jar). Reemplazar el LesRaisins del cliente; conservar el addon de melee, eZWeight y TACZ. Añade lectura del clic físico para el golpe normal en isométrica y dirección del cursor para ataques normales/pesados. Doce comprobaciones simuladas aprobadas; incluye corrección de dirección al calcular los objetivos del golpe retrasado. Pendiente verificar animaciones y daño en Minecraft. [Detalles y límites](lr-isometric-melee-patch/README.md).

Versión .5: con mano principal vacía y cámara isométrica, antes de `Minecraft.startAttack` restaura el objetivo de entidad bajo el cursor si está vivo, dentro del alcance vanilla y visible desde los ojos del jugador. Esto evita que la selección posterior de bloques del parche isométrico deje `hitResult` apuntando al suelo en lugar del zombi. No envía ataques extra ni cancela el ataque; conserva estamina, cooldown y validación del servidor. Ocho pruebas de restauración, paredes, alcance, objeto equipado, GUI, cámara normal y objetivo muerto/no entidad aprobadas con APIs simuladas. El rechazo por agotamiento de estamina sigue siendo posible y no se ha eliminado. Pendiente verificar el reporte en Minecraft.

Versión .6: además de las distancias, restaura el color de la niebla después de `CullingBackdrop` de Dungeons Iso, que podía llevar los colores a negro al ocultar techos. Actualiza los campos de FogRenderer, el color del shader y el fondo recortado al mismo tono atmosférico. Un segundo hook de color se ejecuta al final de setupColor; el hook de distancia reaplica también el color. Conserva agua/lava, modo apagado y cámara normal. Añade `/zomboidfog estado` para mostrar intensidad, cámara isométrica detectada y modo. Cuatro regresiones con APIs simuladas reproducen el color negro y comprueban su restauración en las tres salidas; el aspecto y el orden real con BetterFog/Sodium requieren validación en Minecraft.

## Survivor Creator v27: pantalla de muerte minimalista

[Descargar Survivor Creator v27](mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v27-small-death-first-person.jar). Reemplaza la versión anterior en el cliente. Pantalla sencilla con silueta pequeña a la derecha, marcas y notas de todas las lesiones registradas por zona y causa de muerte. Primera persona al entrar/reaparecer, sin bloquear cambios posteriores de cámara. [Detalles y validación](survivor-autopsy-patch/README.md). Pendiente comprobar el aspecto y los eventos en Minecraft.

## Faction Friction: clanes del apocalipsis .1

[Descargar Zomboid Clans .1](faction-clans-patch/faction-clans.1-mod.zip). Sustituir FactionFriction en **cliente y servidor**, sin duplicados. Tecla **K**: cuaderno de clanes con páginas de identidad, conflicto, compañeros y depósito.

- Estética oscura de expediente, emblemas, lema e identidades de supervivientes; mantiene administración, nombre, colores y rangos originales.
- Escaramuzas por puntos con territorios protegidos; asedios con **cinco minutos de preparación** y un defensor conectado de cualquier rango. Desconexión del último defensor restaura protección y pausa captura.
- Posiciones de compañeros, dimensión y radar en ventanas anchas; nunca envía posiciones rivales.
- Puntos por bajas, captura y defensa; ganador por marcador, recompensas mezcladas de suministros/equipo en depósito persistente, controles contra bajas repetidas y tregua entre guerras.

68 comprobaciones con APIs simuladas y métodos nativos transformados, más análisis ASM de 427 métodos. **Pendiente probar dentro del modpack y con varios jugadores.** Conserva las reglas originales de capitales, fuerzas y enfriamientos de operaciones.

[Instalación, puntuación y límites](faction-clans-patch/README.md) · [Fuentes reproducibles](faction-clans-patch/faction-clans.1-fuentes.zip) · [Configuración de ejemplo](faction-clans-patch/factionfriction-zomboid-clans.properties.example).

## Ragdoll y cadáveres: death-compat.4

[Descargar los tres JAR .4](death-compat-patch/death-compat.4-mods.zip). Sustituir los tres anteriores sin duplicados y reiniciar completamente Minecraft. Cliente: los tres. Servidor dedicado: Saros y Zombie Remains Loot `.4`.

- Elimina la pausa de cuatro segundos y reduce el fade de dos segundos a 0.4 segundos tras reposar el torso.
- Extremidades o cabeza aún en movimiento no retrasan el loot si el torso está apoyado y tranquilo.
- Reduce el frenado angular y vuelve al coste de simulación original: 60 pasos y cuatro subpasos.
- Conserva la reparación de carga nativa, posición del torso, cadáveres sobre agua e inventario persistente.

81 comprobaciones con dobles, análisis de 123 métodos y pruebas con Bullet nativo real en Linux aprobados. **Pendiente probar dentro del modpack y en Windows.** El cadáver de loot conserva su modelo de bloque y sus poses fijas.

[Instalación y límites](death-compat-patch/README.md) · [Fuentes .4](death-compat-patch/death-compat.4-fuentes.zip).

## Estamina v10, HUD v2 y Survivor Creator v18

Sustituir las tres versiones anteriores en cliente y servidor. Mantener First Aid y las otras dependencias.

- [eZWeight v10](mods/eZWeight-neoforge-1.21.1-1.8.4-stamina-v10-fitness-limbs.jar): Fitness +5% de capacidad por nivel, Sprinting +3% y Corredor +15% adicional.
- [HUD v2](mods/zomboid-project-progression-1.21.1-1.0.2-stamina-hud-v2.jar): barras horizontales con nueva textura, porcentajes y estado, junto a la hotbar.
- [Survivor Creator v18](mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v18-stamina.jar): muestra los bonus y actualiza el rasgo Corredor; conserva perfiles y contenido de v17.

Sobrecarga: daño solo al superar capacidad, cada cinco segundos a 20 TPS, dirigido a piernas y pies mediante First Aid.

69 comprobaciones automatizadas aprobadas. **Pendiente probar dentro del modpack.**

[Instalación, reglas y límites](stamina-patch/README.md) · [Fuentes reproducibles](stamina-patch/stamina-v10-hud-v2-survivor-v18-fuentes.zip).


## Historial: Survivor Creator v17 (sustituido por v18)

[Descargar Survivor Creator v17](mods/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v17-profiles.jar) · [OK Additions con requisito 1.21.1 ajustado](mods/okadditions-1.1.0-neoforge-1.21.1-compat.jar). Sustituye sus versiones anteriores en cliente y servidor; conserva GeckoLib 4 para NeoForge 1.21.1.

- Cinco puntos extra y selección sin límite de cantidad de rasgos, manteniendo presupuesto y conflictos.
- Selección/objetos iniciales y mejoras/niveles en paneles separados.
- Perfiles con nombre para guardar, cargar y reutilizar tras morir.
- 38 rasgos y 35 profesiones: 14 rasgos y 10 profesiones nuevos.
- Equipo inicial con 16 objetos verificados de OK Additions; niveles iniciales reales y aprendizaje rápido/lento.

**Pendiente probar dentro del modpack.** La copia de OK Additions cambia solo su requisito de versión: no demuestra compatibilidad binaria con 1.21.1.

[Instalación, contenido y límites](survivorcreator-patch/README.md) · [Fuentes reproducibles](survivorcreator-patch/survivorcreator-v17-fuentes.zip).


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

Versión .12: el retroceso temporal se separa del objetivo bajo el cursor y decae exponencialmente hacia él después de disparar; se conservan los impulsos nuevos de TACZ. El ajuste de regreso necesita validación visual dentro del juego.

## First Aid: derribo y reanimación asistida

[Descargar First Aid actualizado](mods/firstaid-1.21.1-ZOMBOID-v3.5.1-revival-timer-fix.jar). Reemplazar First Aid en cliente y servidor. +3 corazones máximos; cabeza o torso a cero no causan muerte instantánea. Derribo de 100 segundos, ayuda con clic derecho durante 8 segundos, reanimación propia con desfibrilador en mano, regreso con 3 corazones y 20 segundos sin daño de sangrado para vendarse. [Detalles y validación](firstaid-assist-patch/README.md).

## Quietly v11: cofres y barriles a cinco bloques

[Descargar Quietly v11](mods/quietly-1_0_0-cooldown-v11-five-block-reach.jar). Reemplaza v10 en cliente y servidor. Alcance mínimo de interacción de bloques de 5, conservando búsqueda, rareza y cooldown. [Detalles y validación](quietly-reach-patch/README.md).

## Zomboid Clans .2: defensa del nexo y menú simplificado

[Descargar Zomboid Clans .2](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.2-nexus-pve.jar). Reemplaza .1 en cliente y servidor. Menú contextual, límites 4/6/8 chunks, protección frente a spawns, nexo de 100 PV mejorable, defensas PvE de tres fases con especiales, recompensas, XP y top de clanes. [Uso, probabilidades y validación](faction-simple-patch/README.md). Pruebas simuladas; pendiente probarse en Minecraft.

## Zomboid Clans .3: nexo explicado y enemigos de oleada rojos

[Descargar .3](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.3-nexus-tooltip-fix.jar). Añade la descripción del nexo y contornos rojos para los enemigos de defensa. Corrige el crash al cargar NexusGoal con una ruta incorrecta. Reemplazar versiones anteriores en cliente y servidor. 106 comprobaciones simuladas; prueba en Minecraft pendiente. [Detalles y fuentes](faction-simple-patch/README.md).

## Zomboid Clans .4: aparición y defensa territorial

[Descargar .4](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.4-pve-spawn-territory-fix.jar). Busca suelo y rutas cerca del nexo en chunks vecinos, comprueba el primer spawn antes de iniciar y falla si no quedan participantes dentro del territorio. Conserva la oferta cuando no hay lugar válido para empezar. 110 comprobaciones simuladas; prueba en Minecraft pendiente.

## Zomboid Clans .5: salir de la base y final gradual

[Descargar .5](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.5-pve-roaming-gradual-end.jar). Salir del territorio ya no termina la defensa. La zona del nexo sigue en simulación, y al perder por destrucción o muertes de participantes los enemigos restantes mueren gradualmente. 120 comprobaciones simuladas; prueba en Minecraft pendiente. Reemplaza versiones anteriores en cliente y servidor.

## Zomboid Clans .6: suelo, entradas y prueba por comando

[Descargar .6](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.6-ground-entry-debug.jar). Prioriza suelo/primer piso, permite otras alturas, intenta puertas/trampillas/ventanas y añade rotura o peldaños como último recurso. `/clanpvetest` inicia una defensa común sin cooldown para operadores con clan y nexo. 135 comprobaciones simuladas; prueba real pendiente.

## Zomboid Clans .7: nexo generador, alertas y mejores recompensas

[Descargar .7](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.7-nexus-alerts-surface-rewards.jar). Motor y partículas, barra grande de vida/daño, sonidos por fase y resultado, iconos de depósito y recompensas de armas TACZ con munición compatible y armaduras FracturePoint. Spawns en superficies exteriores bajas y comprobación periódica de navegación. 160 comprobaciones simuladas; prueba real pendiente. [Detalles](faction-simple-patch/README.md).

## Dungeons Iso .18: mochilas y recorte con CTRL

[Descargar .18](mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.18-backpacks-ctrl-cutaway.jar). Corrige la clasificación de mochilas colocadas y el uso de mochilas en ambas manos tras una interacción PASS. El recorte de paredes/techos sigue el foco adelantado con CTRL. Reemplaza .17; Sophisticated Backpacks permanece sin modificar. 15 comprobaciones simuladas; prueba en Minecraft pendiente. [Detalles y fuentes](iso-backpack-patch/README.md).

## Zomboid Clans .8: nexo interior y losas

[Descargar .8](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.8-indoor-nexus-slab-spawns.jar). Permite generar fuera aunque el nexo esté resguardado sin ruta directa, admite suelos de losas con altura real y amplía la búsqueda exterior hasta 48 bloques en chunks cargados. Si falla, distingue falta de puntos, colisiones y cancelaciones. 166 comprobaciones simuladas; prueba en Mosslorn pendiente.

## Zomboid Clans .9: dificultad seleccionable y sin cooldown PvE

[Descargar .9](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.9-selectable-difficulty-no-cooldown.jar). En Oleadas PvE elige Fácil, Normal, Difícil, Extrema o Pesadilla e inicia directamente sin cooldown. Mutantes por fase: 0/3/8/14/24; recompensas y XP aumentan con dificultad. 172 comprobaciones simuladas; prueba en Minecraft pendiente. Actualizar cliente y servidor.

## Zomboid Clans .10: cadáveres de oleada solo por bajas de jugador

[Descargar .10](mods/factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.10-player-kill-corpse-loot.jar). Los enemigos de oleada muertos por entorno o limpieza automática no generan cadáver looteable, drops ni XP. Las bajas de jugadores/proyectiles y los zombis normales fuera de oleadas conservan su loot. 183 comprobaciones simuladas; prueba en Minecraft pendiente.

## Doomsday Decoration: almacenamiento y casillas reales

[Descargar Doomsday Storage .2](mods/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-storage.2.jar). Reemplaza el Doomsday original en cliente y servidor, con una copia previa del mundo. Añade inventarios y recetas a 71 muebles, refrigeración de bebidas y casillas físicas vinculadas para 278 decoraciones grandes. Los muebles antiguos que ya invaden una pared necesitan recolocarse. [Uso, límites, compilación y validación](doomsday-functional-patch/README.md). Verificado con API simulada y análisis de bytecode; pendiente de prueba real en Minecraft.

Survival 1.1.0 con FF integrado requiere también [este reemplazo compatible](mods/zomboid-survival-1.21.1-1.1.0-ff-preservation-doomsday-compat.1.jar): su dependencia exacta rechazaba la versión Storage. [Diagnóstico y verificación](survival-doomsday-compat-patch/README.md).

## Coches MTS: tablero y mecánica Zomboid

Reemplazar el mod y el pack juntos: [Immersive Vehicles Mechanics .3](mods/Immersive-Vehicles-1.21.1-24.0.0-zomboid-mechanics.3.jar) y [Official Pack Dashboard/Trunks .1](mods/MTS-Official-Pack-1.21.1-V29-zomboid-dashboard-trunks.1.jar). Tablero inferior, chequeador, reparación corregida, maletera trasera de 54 espacios, mayor resistencia y seis marchas. Automático cambia solo; manual con mano principal vacía: izquierda sube, derecha baja, con aviso y sonido. [Instalación, controles, validación y límites](mts-zomboid-patch/README.md). Pendiente de prueba en Minecraft; usar copia del mundo.

Isométrica: [Dungeons Iso .19: vehículos y comida](mods/dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.19-vehicles-food.jar), conserva mochilas/CTRL de .18 y deja los controles de monturas y consumibles a sus sistemas originales. Requiere reemplazar MTS Mechanics .1 por .2 para corregir el crash de anotación de mixin del log. [Diagnóstico, instalación y validación](iso-vehicle-food-patch/README.md).
