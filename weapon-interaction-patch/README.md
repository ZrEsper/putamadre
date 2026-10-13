# Clic derecho: interactuar antes de apuntar o atacar

Reemplazos de las versiones enviadas por el usuario:

- `tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.11-interaction-priority.jar`, construido sobre **.10 addon-compat** extraído de las tres partes 7z.
- `LesRaisins-Tactical-Equipements-1.21.1-0.4.3-zomboid-isometric-melee.2-interaction-priority.jar`, construido sobre **isometric-melee.1**.

Cerrar Minecraft/servidor, retirar las dos versiones anteriores y colocar estos dos JAR en cliente y servidor. Una sola versión de TACZ y una sola de LR Tactical. Los cambios son de entrada cliente; las versiones/identificadores internos y los protocolos se conservan.

Conservar Dungeons Iso .27 (o la .26 enviada), Quietly v15, Remains .7, TacZ NPCs .14, Global Melee/EzWeight v11 y los packs Apocalyptic Arsenal .3, Valorant Knife .1 y Valorant Gunpack .2. No hace falta reemplazarlos por este ajuste. Los tres packs son registradores/recursos que utilizan los tipos de arma de TACZ/LR, y no implementan estos bloqueos de entrada. Se inspeccionaron sus JAR y el de Global Melee sin modificarlos.

## Causa y comportamiento

`ClientPreventGunClick.onClickInput` de TACZ cancela la entrada vanilla con un IGun en la mano principal, salvo si está pulsada la tecla de interacción alternativa (O por defecto). LR Tactical aplica un bloqueo equivalente a sus ICustomItem mediante `ClientEventsHandler.onClickInput`. Dungeons Iso .26/.27 selecciona y abre el objetivo mediante `Minecraft.startUseItem`, que pasa por esos eventos; por eso también era bloqueado con armas. LR además consume clic derecho para el ataque pesado.

Ahora, cuando hay una puerta, trampilla, portón, botón, señal, contenedor con menú o cadáver válido al alcance, **la interacción con clic derecho tiene prioridad**. Las armas y melees dejan pasar esa entrada por el flujo normal del juego, sus eventos y sus paquetes. La mira se baja y no se inicia un ataque pesado sobre el objeto. El clic que se utilizó para interactuar no se convierte en un ataque al cerrar el menú. Si no hay un objetivo interactuable, se mantiene la mira/ataque pesado nativo. El ataque izquierdo, los disparos y las teclas alternativas se conservan.

En primera/tercera persona normal se usa el objetivo de Minecraft, con comprobación de alcance y obstáculos. En isométrica se consulta el selector fresco de Dungeons Iso y su comprobación de interacción; el punto de la mira del arma no sustituye al objeto bajo el cursor. Se conservan sus exclusiones de monturas/consumo y sus límites de alcance. El ajuste también funciona sin Dungeons Iso.

Las cancelaciones de otros mods no se deshacen. Las protecciones del servidor, los permisos, las restricciones de bloques y el sistema de búsqueda de Quietly siguen formando parte del flujo nativo. Para las puertas protegidas por Mission Score, el clic derecho reutiliza la comprobación y petición de apertura del addon de .10 y cancela el uso vanilla cuando el addon consume el intento. La petición se deduplica entre los dos bloqueadores para el mismo evento. No abre a la fuerza puertas cerradas, no convierte una puerta de hierro en una puerta manual y no envía paquetes directos de cambio de bloque/inventario.

## Verificación y límites

122 comprobaciones de los helpers reales y del bytecode exacto de los métodos de entrada parcheados, con dobles explícitos de las APIs:

- Armas/melees con cofres, barriles, puertas, trampillas, portones, botones, señales, cuerpos de bloque y cadáveres/entidades con inventario.
- Prioridad del uso, baja de mira, un solo despacho de interacción y conservación de ataques/disparos fuera del caso de uso.
- Paredes, alcance, objetivos no interactuables, menús abiertos, espectador, jugador ausente y cancelaciones externas.
- Mantener clic derecho después de interactuar, liberación, nueva pulsación y bindings alternativos.
- Cola del ataque pesado consumida al interactuar y ataque pesado nativo sin objetivo o con otra tecla.
- Selector isométrico, límites de su selector, monturas, consumo y cambio de jugador/mundo.
- Puertas protegidas: petición del addon, deduplicación, bloqueo revelado y comportamiento de manos sin armas.

Las fixtures conservan exactamente los métodos de entrada de los JAR generados, quitando solo los métodos/fields de renderizado ajenos para poder ejecutar esa entrada contra las APIs sustituidas. No equivalen a cargar el modpack completo. ASM verifica los 9.175 métodos de los dos JAR. Se comprueban integridad ZIP, bases/herramientas mediante SHA-256 y comparación de entradas: solo cambian dos clases existentes de cada mod, más los helpers nuevos. Recursos, mixins, configuraciones, animaciones, addon de .10 y demás entradas se conservan byte por byte. No se añaden mixins ni puntos de inyección nuevos, y los stubs/fixtures no se incluyen en los mods.

**No se ejecutó Minecraft.** Falta comprobar el arranque, la selección visual y la integración cliente/servidor en el modpack. Prueba en una copia del mundo: abrir una puerta y un cofre/cadáver con rifle y melee, en cámara normal/isométrica; mantener el clic y cerrar el menú; después apuntar/atacar al aire. Comprobar asimismo una puerta bloqueada y un objeto fuera de alcance/detrás de pared.

## Reconstrucción

Java 21 y Python 3:

```sh
python3 -B weapon-interaction-patch/build.py
```

El script verifica las bases de `base/`, descarga ECJ 3.39.0 y ASM 9.7.1 desde Maven Central con TLS/SHA-256 fijados, parchea y valida. Solo copia a `../mods/` tras pasar las comprobaciones. `validation.json` registra hashes y el número real de comprobaciones ejecutadas. `build/` y `output/` son salidas ignoradas.
