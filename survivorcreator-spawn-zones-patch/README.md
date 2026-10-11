# Survivor Creator v23 — nueve zonas, interiores variados y linterna

Base: el JAR v22-scavenging-boosts enviado. Conserva byte por byte todos los archivos ajenos a Spawns, SpawnScreen y el catálogo de interiores; añade Zones y Flashlight. Se conservan Scavenging, profesiones, perks, perfiles, estamina y música de introducción/muerte.

## Instalar

Cerrar Minecraft y el servidor. Reemplazar únicamente Survivor Creator v22 por `survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v23-nine-spawn-zones.jar`; no instalar dos versiones. Instalar el mismo archivo en clientes y servidor. Conservar los otros mods del paquete anterior, incluido el mod Flashlight. No hace falta reconvertir Mosslorn.

## Zonas

Se mantienen los cuatro identificadores originales y se añaden cinco sectores separados:

| ID / tecla | Zona | Peligro orientativo | Suministros esperables |
|---|---|---|---|
| 0 / 1 | Residencial noroeste | Bajo | Comida, agua, ropa y provisiones domésticas |
| 1 / 2 | Residencial noreste | Bajo–medio | Comida, ropa, herramientas básicas |
| 2 / 3 | Ciudad suroeste | Alto | Provisiones, herramientas y suministros de oficina |
| 3 / 4 | Ciudad sureste | Alto | Suministros variados, medicamentos según contenedor |
| 4 / 5 | Periferia de la central | Extremo fuera del refugio | Materiales y herramientas; acceso al sector nuclear |
| 5 / 6 | Extremo noreste | Alto | Provisiones y materiales; sector próximo a la pirámide |
| 6 / 7 | Franja residencial oeste | Medio | Provisiones domésticas, ropa y herramientas |
| 7 / 8 | Esquina suroeste | Alto | Provisiones domésticas y de oficina |
| 8 / 9 | Extremo sureste | Alto | Comida, materiales y suministros de oficina |

Son descripciones orientativas de Mosslorn: este JAR no cambia las tasas de zombis ni reescribe las LootTables. Los objetos reales dependen de los mods y tablas instalados, del tipo/nombre de contenedor y de Scavenging. La aparición no concede automáticamente el loot valioso de una instalación peligrosa.

Seleccionar con 1–9, flechas, rueda, filas o marcadores; Enter para empezar. La lista desplaza sus filas en pantallas pequeñas y muestra peligro, disponibilidad de loot y descripción del sector seleccionado. Los marcadores son orientativos; se han ajustado usando las referencias de la pirámide, central y puente proporcionadas por el usuario.

## Interiores y alturas

1.374 puntos: 192/192/192/192/19/11/192/192/192. Se extraen del mundo proporcionado, junto a cofres/barriles, verificando suelo, dos bloques de aire, techo próximo y al menos dos lados con paredes. Se excluyen agua y superficies peligrosas; los interiores de cota muy baja sin indicios suficientes de vivienda se descartan. Los sectores incluyen distintas alturas y varios edificios cuando existen. No se inventan pisos ni sótanos donde no hay interiores aptos.

Las coordenadas Y permanecen en el catálogo: el servidor mezcla aleatoriamente los candidatos y puede elegir otra planta o edificio cercano. En cada aparición comprueba terreno actual, suelo, espacio, agua y monstruos en un radio aproximado de 16 bloques. Si no encuentra un punto apto, rechaza esa zona y permite elegir otra. Antes del teletransporte vuelve a comprobarlo. Se conserva la protección temporal original y las provisiones personales originales; no se borran monstruos ni se rellenan contenedores.

La clasificación offline de interiores es geométrica: no demuestra que cada habitación tenga una salida navegable o que corresponda a un número exacto de planta. La comprobación en vivo evita puntos ocupados/inseguros detectables, pero requiere probar la navegación y cada sector en el modpack.

## Linterna

Después de aparecer se equipa `flashlight:flashlight_on` en OFFHAND y se sincroniza `BatteryFlashlight = 100`. Se retira una linterna de inicio del inventario para evitar duplicarla. Si había otro objeto en la mano secundaria, se conserva en el inventario; si no cabe, se entrega al suelo. El encendido se solicita mediante el objeto nativo del mod, sin un efecto falso de visión nocturna. Si el objeto encendido no está registrado, se conserva la linterna original y se informa en el log.

## Validación

38 comprobaciones simuladas de catálogos, altura variable, zonas nuevas aceptadas, zonas inválidas/duplicadas, preservación de perks, rechazo por enemigos/terreno bloqueado, selección/desplazamiento y equipamiento/batería de linterna. ASM verifica los 35 métodos de las clases incluidas. Integridad ZIP, SHA-256 y conservación de todas las entradas ajenas comprobados. Los dobles de API no se incluyen en el JAR.

**No se ejecutó Minecraft.** Falta verificar visualmente el selector, el teletransporte, la salida de las habitaciones y la iluminación nativa de Flashlight en mano secundaria en el modpack real.

Reconstruir: `python -B survivorcreator-spawn-zones-patch/build.py RUTA_V22.jar CARPETA_ECJ_ASM`. Reutiliza los dobles API del parche anterior. `catalog.py` regenera los candidatos leyendo las regiones originales locales (ruta configurable en el script; requiere nbtlib). `homes.json` conserva las posiciones y los contenedores de referencia para revisar el catálogo sin modificar el mundo.
