# TaCZ .18: evita el cierre al recargar la SPAS-12

[Descargar reemplazo y guía](distribuciones/Zombies-Poldo-TaCZ-18-Recarga-SPAS.zip). Basado en el TaCZ .17 enviado: conserva trazadoras, impactos y límites de zoom. Cancela de forma segura una recarga SPAS sin caché, incluso con el pack predeterminado ya extraído; pulsa R para reiniciarla. Sustituye solo TaCZ en clientes y servidor; conserva Tactical .1. [Instalación y límites](spas-reload18/GUIA.md). Error original reproducido con el LuaJ incluido y 6.421 comprobaciones aprobadas; pendiente de prueba dentro de Minecraft.

# Fracture Point NVG .2: visor y visión en N

[Descargar reemplazo y guía](distribuciones/Zombies-Poldo-NVG-Visor-N-2.zip). N sube/baja el visor y vincula su visión a la posición física; cambia al modo de las gafas del casco nuevo, incluso entre cascos del mismo modelo. Recupera el shader si un efecto de daño lo sustituye y evita reactivar una batería agotada. Conserva los filtros originales y la batería de 15 minutos. Sustituye Fracture Point NVG .1 en clientes y servidor. [Controles, batería y límites](nvg-linked2/GUIA.md). 930 comprobaciones con APIs simuladas; pendiente de prueba visual dentro de Minecraft.

# TaCZ .17: trazadoras, marcas y zoom según la mira

[Descargar los dos reemplazos](distribuciones/Zombies-Poldo-TaCZ-17-Trazadoras-Miras.zip). Aísla las rotaciones del modelo de munición antes de dibujar trazadoras e inicializa los ángulos recibidos por red. Corrige el cruce entre atlas de bloques y partículas; marcas y tamaños según categoría de munición. La rueda respeta el mínimo y máximo declarados por la mira: punto rojo 1×, variable 4–25× hasta 25×. Elimina el multiplicador oculto de cámara. Reemplaza TaCZ .16 y Tactical scope-range64 juntos en clientes y servidor; conserva taczaddon. [Instalación y límites](tracer-scope17/GUIA.md). 310 comprobaciones con APIs simuladas y 332 del bytecode/transformaciones simbólicas; pendiente de prueba visual en Minecraft.

# Muebles, aires con generador y tablones — Doomsday .3

[Descargar los cuatro reemplazos](distribuciones/Zombies-Poldo-Muebles-Aires-Tablones-3.zip). Escritorios, lavadoras, lockers y otros muebles con inventario; reparación de los ya colocados sin reemplazar inventarios existentes. Los seis aires tienen receta, ventilador, interruptor, consumo real de 400 FE/s del generador, temperatura neutral y bono de crecimiento del 35% en 15 bloques, sin acumulación. Tres tablones de madera dan seis planks; diseños en cortapiedras y ventanas contiguas tres veces más resistentes frente a jugadores y Hordes. [Guía de instalación, recetas y límites](decor-climate3/GUIA.md). Reemplaza Doomsday, Homeostatic, Tough As Nails y Hordes en clientes y servidor. Conserva Survival y los demás mods. 297 comprobaciones con APIs simuladas; pendiente de prueba dentro de Minecraft.

# Hordes .41 y Screamer corredor: paredes y 40 de vida

[Descargar los dos reemplazos directamente](distribuciones/Zombies-Poldo-Paredes-41-Screamer-40HP.zip). Corrige la selección de paredes y los atascos con navegación activa; comprueba pies, cabeza y ancho del zombi. Screamer con 40 de vida, carrera propia sin el ritmo de zombi común y grito que reúne a los infectados disponibles alrededor del screamer (96 bloques), sin convertirlo en objetivo de ataque. Conserva el panel sencillo .40, el huevo, el audio y la animación anteriores. [Instalación](screamer-sprint2/GUIA.md) · [Rotura de paredes y permisos](wall-breach41/GUIA.md). Sustituye Hordes y el addon Mutants L4D2 en clientes y servidor; conserva los demás mods. 18.094 comprobaciones automatizadas con APIs simuladas y verificación del bytecode; pendiente de prueba dentro de Minecraft.

# Hordes .40: panel sencillo y loot efectivo

[Descargar reemplazo y guía](distribuciones/Zombies-Poldo-Panel-Sencillo-40.zip). Sustituye Hordes .39 en clientes y servidor. Cinco perfiles directos y dos bloques de cinco campos: barriles/cofres y cadáveres. Cantidades independientes del loot original, porcentajes directos y vista previa corregida. Opciones detalladas en Avanzado. [Uso y límites](simple-loot-panel40/GUIA.md). 70 comprobaciones, 2.500 inventarios simulados y guardado/carga real de ajustes sobre disco con APIs del juego simuladas; pendiente de prueba dentro de Minecraft. Conserva las funciones de rotura y screamer de .39.

# Screamer horror y FirstAid 3.6.2

[Descargar los dos reemplazos y guía](distribuciones/Zombies-Poldo-Screamer-Armadura-362.zip). Huevo del screamer, más velocidad, pose asimétrica y mandíbula abierta, audio del usuario al detectar al jugador y atracción de hordas. FirstAid: 8 puntos de armadura local equivalen nominalmente a 16 impactos totales de pistola; potencia, distancia y penetración conservan el daño efectivo de TaCZ. [Guía de instalación y límites](screamer-horror-patch/GUIA.md). Reemplaza el addon Mutants L4D2 1.4.0 y FirstAid 3.6.1 en clientes y servidor; conserva MutantsZombies base, Integration 1.7.8 y Hordes. 32.793 comprobaciones con APIs simuladas; pendiente de prueba dentro de Minecraft.

# Corrección Integration 1.7.8: permite Mutants L4D2 1.4.0

[Descargar Integration corregido](mods/zomboid-integration-1.7.8-neoforge-1.21.1-mutants-1.4-compat.jar). Reemplaza Integration 1.7.7 en clientes y servidor; conserva el addon Mutants L4D2 1.4.0. Corrige el rango de versiones que impedía cargar el juego. Código y recursos idénticos; verificadas cuatro llamadas al addon, dos destinos de mixin y tres miembros Shadow. [Diagnóstico e instalación](integration-mutants140-fix/README.md). Pendiente confirmar arranque dentro de Minecraft.

# Actualización .35: equipo militar completo y loot equilibrado

[Descargar Hordes .35 + Remains .11 y guía](distribuciones/Zombies-Poldo-Loot-Militar-35.zip). Sustituye Hordes .34 y Remains .10 en clientes y servidor. Una tirada de arma/melee por cuerpo civil (1/13), ropa militar con otra tirada de 1/13 y munición compatible en el 35% de armas de fuego. Militares conservan su equipo real y llevan dos vendas; mutantes con restos biológicos y objetos en casillas aleatorias. [Guía y controles](corpse-loot-balance35/GUIA.md). 16.221 comprobaciones de lógica y 7.800 cuerpos simulados; pendiente de prueba dentro de Minecraft. Conserva NPC .16, FirstAid 3.5.8 y FracturePoint NVG .1.

# Corrección .34: cierre al aparecer NPC

[Descargar solo Hordes corregido](mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.34-npc-mixin-fix.jar). Sustituye Hordes .33 en clientes y servidor; conserva los otros cuatro mods. Corrige la conexión `MilitaryBanditBalanceMixin` con el método de equipamiento de NPC .16. [Paquete completo actualizado](distribuciones/Zombies-Poldo-Escasez-NVG-Reanimacion-34.zip) · [Instalación y diagnóstico](hordes-npc-mixin-hotfix/GUIA.md). Fallo original reproducido y tres inyecciones verificadas para NPC .15 y .16; pendiente de prueba dentro de Minecraft.

# Actualización .33: loot escaso, bandidos y atención médica

[Descargar los cinco mods y guía](distribuciones/Zombies-Poldo-Escasez-NVG-Reanimacion-33.zip). Menos loot de común a mítico, armas y armaduras raras, bandidos nuevos casi siempre desarmados y recarga con munición recogida. Zombis sin cambios. Visión nocturna con batería de 15 minutos, prioridad del desfibrilador y reanimación sin bucle; barra y porcentaje para la inyección contra la infección zombi. [Instalación y controles](loot-bandit-balance-patch/GUIA.md). Reemplaza Hordes .32, Remains .9, TacZ NPCs .15, FirstAid 3.5.7 y FracturePoint PT2. 21.845 comprobaciones con APIs simuladas aprobadas; pendiente de prueba dentro de Minecraft.

# Actualización Hordes .32: una herramienta por cadáver

[Descargar actualización y guía](distribuciones/Zombies-Poldo-Apocalipsis-32.zip). El loot generado de cada cadáver contiene como máximo una herramienta de una unidad; mantiene los demás objetos y la carga real de NPC. Reemplazar Hordes .31 por .32. Pendiente de prueba dentro de Minecraft.

# Actualización Apocalipsis .31: profesiones, perfiles y cura zombi

[Descargar los cuatro mods y guía](distribuciones/Zombies-Poldo-Apocalipsis-31.zip). Cinco perfiles editables de escasez/dificultad, 37 tipos de cadáver profesional con pesos, siete mutantes configurables, cura de infección y curación por nivel opcional. Sustituir Hordes .30, Remains .8, FirstAid 3.5.6 y SurvivorCreator v32. [Guía sencilla](apocalypse-profiles-patch/GUIA.md). Reconocimiento de TZP por catálogo de apariencia editable; no identifica perfectamente todas las skins. 374 comprobaciones con dobles de API; pendiente de prueba dentro de Minecraft.

# Actualización TacZ NPCs tactical.15: NPC de pruebas por partes

[Descargar mod y guía](distribuciones/Zombies-Poldo-NPC-Pruebas-15.zip). `/npcprueba crear` crea un NPC de prueba con equipo copiado y ocho zonas de salud. Disparo/recarga TaCZ, melee nativo, derribo, curación dirigida y reanimación. [Uso y límites](npc-probe-patch/GUIA.md): es una adaptación Mob, no sustituye un jugador real para las APIs Player de First Aid. 130 comprobaciones con dobles de APIs aprobadas; pendiente de prueba en Minecraft.

# Actualización Hordes .29: cooldowns, interiores y guía de loot

[Paquete de los dos mods y guía fácil](distribuciones/Zombies-Poldo-Hordas-Melee-29.zip). Reemplaza Hordes .28 y Global Melee v12 en cliente y servidor. Cooldowns por ataque/familia sincronizados; spawn interior opcional en otro cuarto; categorías de loot corregidas y ayuda clara para todos los controles. [Guía completa](hordes-melee-interiors-patch/GUIA.md). 99 comprobaciones con dobles de APIs aprobadas; pendiente de prueba dentro de Minecraft.

# Actualización Hordes .28: spawn e IA

[Descargar Hordes .28](mods/zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.28-spawn-personality-pool.jar). Dos pestañas nuevas en `/zhorda panel`: mezcla normalizada de tontos/brutos/astutos; reacción, agresividad y oído; velocidad y daño por personalidad. Presets Fácil/Equilibrado/Difícil. Reemplazar el mod anterior en cliente y servidor. [Instrucciones y validación](hordes-spawn-pool-patch/README.md). 88 comprobaciones aprobadas con dobles de APIs; pendiente de prueba dentro de Minecraft.

# Actualización: apuntado manual y scopes

[Descarga e instrucciones de los tres mods](free-aim-scopes-patch/README.md). Retira sus versiones anteriores antes de instalar los nuevos JAR de `mods/`. Zoom óptico según la mira equipada, destello de sniper reforzado para otros jugadores y correcciones de asistencia/rotación al disparar. 33 comprobaciones de lógica e integridad aprobadas; pendiente de prueba visual dentro de Minecraft.

# Mods de Zomboid

## Armas y melees: clic derecho para interactuar

Reemplazar TACZ .10 por [TACZ .11: prioridad de interacción](mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.11-interaction-priority.jar) y LR Tactical isometric-melee.1 por [LR Tactical .2](mods/LesRaisins-Tactical-Equipements-1.21.1-0.4.3-zomboid-isometric-melee.2-interaction-priority.jar), en cliente y servidor, sin duplicarlos. Puertas, cofres, cadáveres y otros objetos interactuables al alcance tienen prioridad sobre la mira/ataque pesado al hacer clic derecho. Conserva los packs Valorant/Apocalyptic, Global Melee v11 y los cambios del addon TACZ .10. Dungeons Iso y Quietly v15 se conservan. 122 comprobaciones de entrada aprobadas; **pendiente de prueba dentro de Minecraft**. [Causa, instalación, fuentes y límites](weapon-interaction-patch/README.md).

## Cadáveres con contorno y botín dentro de cofres/cuerpos

**Actualización Quietly v15:** corrige el cierre al arrancar causado por el mixin de apertura de v14. [Descargar solo Quietly corregido](mods/quietly-1_0_0-cooldown-v15-neoforge-menu-fix.jar). Retirar v14; los otros tres reemplazos se conservan. [Diagnóstico del log](corpse-storage-patch/LOG_FIX.md).

[Descargar los cuatro reemplazos juntos](cadaveres-cofres-sin-desbordamiento.zip): Quietly v15, Zombie Remains .7, TacZ NPCs tactical.14 y Dungeons Iso .27, construido sobre la .26 enviada. Contorno al apuntar cadáveres y looteo con clic derecho; el botín adicional de búsqueda se guarda dentro del cofre, ampliando con páginas si falta espacio. Los NPC conservan toda la mochila, equipo y objetos guardados en su cadáver, sin el límite anterior de nueve entradas. Si no hay sitio para colocar el cuerpo, el botín queda en una entrega persistente pendiente, sin objetos flotantes.

Reemplazar versiones anteriores; actualizar Quietly, Remains y TacZ NPCs juntos en cliente y servidor. 164 comprobaciones con APIs simuladas, guardado/carga de clases reales parcheadas y verificación de bytecode aprobadas. **Pendiente probar el arranque, renderizado y sincronización dentro de Minecraft.** [Instalación, fuentes y límites](corpse-storage-patch/README.md).

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
