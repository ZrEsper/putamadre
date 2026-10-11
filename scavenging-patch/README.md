# Scavenging, búsqueda, rareza y agua — NeoForge 1.21.1

Parche sobre los tres JAR exactos enviados y el Jade 15.10.6 con loot pendiente del repositorio. No convierte el mundo ni cambia el datapack Mosslorn. Sophisticated Storage 1.6.1.2147 se integra mediante su inventario y bloques originales; no se modifica ese JAR.

## Instalar

Cerrar Minecraft y servidor. Reemplazar, sin duplicar versiones:

| Quitar | Poner |
|---|---|
| Quietly v7 pending-loot | `quietly-1_0_0-cooldown-v8-scavenging-rarity.jar` |
| Survivor Creator v20 elusive | `survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v21-scavenging.jar` |
| Zomboid Survival 1.0.3 water-power | `zomboid-survival-1.21.1-1.0.4-water-varieties.jar` |
| Jade 15.10.6, incluida su revisión pending-loot | `Jade-1.21.1-NeoForge-15.10.6-scavenging-preview.jar` |

Usar los mismos JAR en cliente y servidor. Conservar Sophisticated Storage, Sophisticated Core y Zombie Remains Loot / Corpse con sus dependencias. Los archivos nuevos mantienen las versiones internas originales para no romper las dependencias exactas de otros mods. Quietly necesita Survivor Creator para la habilidad y Zomboid Survival para las bebidas añadidas al loot.

## Buscar y subir Scavenging

Clic derecho normal o agachado inicia la primera búsqueda de cofres, cofres trampa, barriles vanilla, almacenamiento Sophisticated, restos del mod Zombie Remains Loot y entidades de Corpse. Se muestran progreso y sonido de interacción. Reabrir un contenedor revisado con clic normal es inmediato y no repite premios; el modo agachado conserva el comportamiento silencioso de Quietly.

El tiempo baja de 30 ticks / 1,5 segundos a nivel 0 a 10 ticks / 0,5 segundos a nivel 10: 0,1 s menos por nivel. Son ticks de servidor; un servidor con TPS bajos demorará más en tiempo real. La búsqueda se cancela al alejarse más de 0,8 bloques desde donde comenzó, recibir daño, salir del alcance o abrir otro menú. Se ejecuta la interacción nativa al completarla y solo se premia si realmente abrió un inventario, conservando las comprobaciones de acceso del juego y los demás mods.

Scavenging aparece en las habilidades de Survivor Creator y usa su progresión y sincronización existentes, hasta nivel 10. Los cofres dan 8 + 6 × rareza XP y los cadáveres 5 + 6 × rareza XP, sujetos a los multiplicadores, creación de personaje y reglas de experiencia de Survivor Creator. No da XP en creativo. El registro de búsqueda se guarda en el bloque o en SavedData para cadáveres, de modo que persiste tras salir. Los cofres dobles se marcan juntos. Reponer una LootTable pendiente permite una nueva búsqueda después de un reinicio de loot.

No hay manera de deducir el historial de apertura de contenedores sin LootTable ni marcador anterior: los que ya estaban llenos antes de instalar también cuentan como una primera búsqueda una sola vez.

## Rareza y objetos

Probabilidades base a nivel 0 y suerte 0:

| Rareza | Probabilidad |
|---|---:|
| Común | 70 % |
| Poco común | 20 % |
| Raro | 8 % |
| Épico | 1,8 % |
| Legendario | 0,2 % |

La suerte real del jugador —incluidos Buena suerte / Mala suerte— y +0,35 de suerte de búsqueda por nivel modifican esas probabilidades. La rareza se decide al terminar, una sola vez. Da 1 + rareza + nivel/3 hallazgos adicionales, además del loot original; herramientas de mayor rareza tienen menos desgaste. Los premios consideran el nombre y la categoría de la LootTable anterior. Office Cabinet y categorías médicas incluyen medicamentos / provisiones; cocinas y alimentos incluyen comida y bebidas; talleres y planta nuclear priorizan materiales / herramientas. No reemplaza las tablas del mapa por un único inventario universal.

Los cadáveres respetan al menos su calidad original y añaden tiradas de minerales/materiales (70 %), cuero (60 %) y bebida (45 %), además de los hallazgos. Si no cabe un premio, se entrega al suelo junto al jugador en vez de perderlo.

Un cofre o barril vanilla individual puede convertirse en barril de mayor capacidad de Sophisticated: 5 % de los raros en cobre, 20 % de los épicos en hierro y todos los legendarios en diamante. Sin suerte ni habilidad, eso equivale a 0,96 % de aperturas elegibles. Se conserva el inventario y el nombre; los cofres dobles y bloqueados no se convierten. Si la conversión falla, se intenta restaurar el bloque y su NBT. Las capacidades son las configuradas en Sophisticated Storage, no tamaños inventados por este parche.

Jade / Quietly ocultan el inventario sin revisar, incluso si ya había objetos generados. La rareza se muestra después de revisarlo, sin cambiar nombres como Office Cabinet. Se mantienen las protecciones anteriores para LootTable pendiente. Esto no impide que tolvas u otros mods ajenos soliciten generar loot: el parche evita la lectura anticipada de la vista de Jade / Quietly.

## Agua

Cuatro iconos propios de 32×32, elaborados a partir del atlas generado incluido:

- Agua potable: +6 hidratación; se obtiene en dispensadores filtrados o rellenando una botella en un caldero con agua.
- Agua purificada: +8 hidratación; hervir agua contaminada en horno, ahumador o fogata.
- Agua mineral: +10 hidratación; aparece como suministro de loot, sin receta que convierta cualquier agua en mineral.
- Agua contaminada: sigue requiriendo hervirse antes de beber. Las fuentes abiertas de río/lago siguen rellenando agua contaminada.

Las tres aguas potables reutilizan DrinkItem: después de beber dan una botella vacía, respetan creativo, y conservan frío / congelación. El agua fría recibe el bonus de hidratación anterior. Un caldero pierde un nivel por botella en supervivencia; es una fuente de depósito/lluvia, no un detector de contaminación histórica. Se conserva la filtración eléctrica y el tanque existentes del dispensador.

## Validación y límites

Se verifican los hashes de entrada, integridad ZIP, bytecode de las clases modificadas y todas las nuevas, y que no se empaqueten clases API de prueba. Los mixins y sus configuraciones originales se conservan byte por byte. Las pruebas simuladas ejercitan reglas de rareza/tiempo, contenedores llenos, sobrantes, transferencia de inventario, historial, búsquedas, interrupciones, primera recompensa única, vista previa y relleno de agua. Las entidades y APIs de esas pruebas son dobles: **no se ejecutó Minecraft ni el modpack real**. La conversión de Sophisticated y los menús de Corpse necesitan validación dentro del juego.

Reconstruir: `python -B scavenging-patch/build.py /ruta/a/tools` con Java 21, ECJ y ASM. `inputs.json` contiene los JAR originales y sus SHA-256; ajustar solo sus rutas locales cuando los archivos estén en otra máquina, sin cambiar los hashes. El build usa los dobles API existentes de generator-patch y añade únicamente las clases de producción al ZIP.
