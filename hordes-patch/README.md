# Zomboid Hordes 1.8.3: población por chunk y atención individual

[Descargar JAR 1.8.3](../mods/zomboid-hordes-1.8.3-neoforge-1.21.1-living-population.jar) · [Fuentes y herramientas](zomboid-hordes-1.8.3-patch-source.zip) · [Validación](VALIDATION-1.8.3.txt).

Las fuentes y resultados de 1.8.1 permanecen en este directorio como material histórico de reconstrucción.

## Instalar

Reemplaza Hordes 1.8.2 por 1.8.3 en cliente y servidor. No cargues dos versiones del addon. Conserva Minecraft 1.21.1, NeoForge 21.1.255 o posterior y SoundAttract 6.3.8d. Las dependencias e integraciones opcionales de la base no cambian.

## Población de día y de noche

Cada chunk cargado cerca de jugadores vivos en supervivencia tiene su propio objetivo y cooldown. El mínimo pasa de cero por par a **uno por chunk**; los extras conservan una distribución decreciente:

| Objetivo de zombis vivos por chunk | Probabilidad |
| --- | --- |
| 1 | 70 % |
| 2 | 18 % |
| 3 | 8 % |
| 4 | 3 % |
| 5 | 1 % |

Cada 6000–12000 ticks, de cinco a diez minutos a 20 TPS, se sortea el objetivo y se repone únicamente el déficit. No se añade otro zombi indefinidamente a un chunk ya poblado. El objetivo y los timers se guardan con el mundo; matar un zombi o guardar/reabrir no evita el cooldown.

Se cuentan todos los zombis compatibles vivos. El límite de cinco controla lo que añade este sistema: no elimina los zombis existentes, los que llegan caminando ni los creados por otros mods/comandos. Al migrar desde 1.8.2 se retiran los timers por pares y se inicializan los objetivos por chunk; la pausa explícita de /zhorda parar se conserva.

Se mantiene la aparición automática en supervivencia, Overworld, dificultad distinta de Pacífico y doMobSpawning=true, tanto de día como de noche. Solo se usan chunks ya cargados, suelo válido, espacio sin colisiones ni agua y posiciones dentro del borde del mundo. Para permitir la población del propio chunk del jugador, la distancia mínima horizontal baja de 24 a **8 bloques**. Se mantiene el margen de altura de 28 bloques respecto del jugador más cercano.

El mínimo queda pendiente si no hay terreno seguro. Las búsquedas fallidas reintentan tras diez segundos sin volver a sortear. No se fuerza terreno ni se coloca un zombi encima del jugador. Cada revisión tiene un límite de 16 entidades y 256 intentos, con orden repartido aleatoriamente.

## Comandos

Requieren permiso de operador nivel 2 o trucos habilitados.

- **/zhorda poblar**: ejecuta la reposición para todos los jugadores vivos en supervivencia del Overworld, omitiendo la espera de los cooldowns. Completa los objetivos existentes; no duplica la población de los chunks ya llenos. Cancela una pausa de reposición explícita. El primer lote se ejecuta inmediatamente y los demás durante los siguientes ticks de revisión, sin esperar cinco minutos. Usa lotes limitados para evitar una oleada masiva en un solo tick. También funciona desde la consola del servidor. Los chunks sin posiciones seguras siguen pendientes de la búsqueda normal.
- **/zhorda horda**: activa la atracción de una horda hacia la zona del jugador, usando zombis existentes y su atención individual.
- **/zhorda helicoptero**: conserva el anuncio y sonido originales y activa la atracción del helicóptero.
- **/zhorda dia**, **/zhorda noche** y **/zhorda ruido** siguen disponibles. Día comprueba población; noche y ruido también activan la atracción correspondiente. Estos comandos respetan el cooldown; poblar es el comando explícito para omitir la espera.
- **/zhorda estado**: muestra condiciones de activación, chunks activos, zombis vivos, objetivo total, próxima revisión y chunks en cola.
- **/zhorda zombis**: conserva el informe de torpes, normales, atentos, persecución y velocidades de la base.

Los comandos de eventos informan por separado cuántos zombis aparecieron y cuántos escucharon el evento. Un evento puede atraer zombis sin generar ninguno nuevo.

## Inteligencia durante helicóptero y hordas

Se conservan las personalidades persistentes de la base: 75 % torpes, 15 % normales y 10 % atentos, junto con sus diferencias de visión, oído, reacción, velocidad y combate.

Un helicóptero se puede oír hasta 192 bloques y una horda hasta 96, limitados por el oído de cada individuo. No todos responden: la probabilidad inicial depende de personalidad, distancia y capacidad auditiva. Las pistas de un torpe tienen más error que las de un atento.

- Los torpes se distraen con mayor frecuencia y retienen una pista hasta unos 30 segundos.
- Los normales investigan con más precisión y retienen una pista hasta un minuto.
- Los atentos suelen insistir, replanifican más a menudo y recuerdan la pista hasta un minuto y medio.

Mientras el evento siga siendo audible, pueden actualizar la pista de sonido: hasta 90 segundos para el helicóptero y 30 para la horda, dentro del tiempo de memoria individual. Después solo conocen la última posición oída. La distracción puede hacer que pierdan antes el interés; ver directamente al jugador mantiene la prioridad del combate.

Un sonido no concede un objetivo de ataque a través de paredes. Solo la percepción visual y las reglas de represalia existentes de ZombieIndividual pueden autorizar la persecución directa. Se respetan los aturdimientos y las rutas reales del juego.

La investigación se acerca hasta 1,5 bloques de la pista, en vez de detenerse a tres. Las rutas lejanas se solicitan en tramos de hasta 16 bloques; la navegación de Minecraft sigue resolviendo obstáculos. No se teletransportan entidades ni se aumenta artificialmente su velocidad. Un camino imposible puede impedir la aproximación. Un jugador muerto, desconectado, en otra dimensión, creativo o espectador deja de ser seguido por la pista del evento.

El procesamiento del evento se limita a 320 zombis cercanos. La atracción por ruido natural también usa estos mecanismos; no se restauran las antiguas oleadas grandes.

## Reproducir y validar

Extrae el paquete de fuentes. Python 3 y Java 21:

    python3 -B build_183.py /ruta/zomboid-hordes-1.8.2-neoforge-1.21.1-chance-population.jar

La base debe tener SHA-256 a6128c419a65154ea82ee11cdf3758ef35381048a3f71540d43fb65bb711bb42. Las herramientas ECJ, ASM, Gson y Brigadier se verifican con SHA-256 antes de usarlas. El resultado se escribe en output/. Las clases de prueba y las herramientas no se incluyen en el mod.

Brigadier se compiló desde el repositorio oficial Mojang/brigadier, commit 9ba4f13c0fe82b07c08c2dc2d8043f075ffd0d98, para probar la ejecución real de los comandos. Su licencia MIT se incluye dentro del JAR de la herramienta. El código del addon conserva GPL-3.0-only y ECJ, ASM y Gson sus licencias respectivas.

Los checks ejecutan las clases reales de población, HordeData, HordeConfig, EventAttraction, InvestigateNoiseGoal y el registro parcheado original de HordeCommands, con dobles de las API de Minecraft/NeoForge y el parser real de Brigadier. Verifican probabilidades, independencia por chunk, cooldowns, persistencia, multijugador, permisos, consola, cola inmediata, percepción, distracción, memoria, navegación, aturdimientos y preservación de entradas del JAR. También se analiza el bytecode de las clases originales modificadas y los hooks de tick/eventos.

**No se ha arrancado el modpack completo con 1.8.3 en este entorno.** La prueba final es instalarlo en cliente/servidor, entrar en supervivencia, ejecutar /zhorda poblar y /zhorda estado, esperar y guardar/reabrir; después probar /zhorda horda y /zhorda helicoptero en terreno con rutas transitables, observar /zhorda zombis y comprobar que algunos investigan, otros se distraen y los que ven al jugador atacan.
