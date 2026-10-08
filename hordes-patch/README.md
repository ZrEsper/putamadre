# Zomboid Hordes 1.8.1: población por chunks

Parche de `zomboid-hordes-1.8.0-neoforge-1.21.1.jar` para Minecraft 1.21.1.
Conserva los requisitos de la base: **NeoForge 21.1.255 o posterior** y
**SoundAttract 6.3.8d**. Weaker Day Zombie 1.0.0 y Horde Hoard 1.1.0 siguen
siendo integraciones opcionales. El mod mantiene el ID `zomboid_hordes`.

[Descargar JAR actualizado](../mods/zomboid-hordes-1.8.1-neoforge-1.21.1-chunk-population.jar) ·
[Descargar código y herramientas](zomboid-hordes-1.8.1-patch-source.zip) ·
[Resultados de validación](VALIDATION.txt).

## Instalar

Reemplaza el addon Zomboid Hordes anterior por
`zomboid-hordes-1.8.1-neoforge-1.21.1-chunk-population.jar`.
Usa la misma versión en cliente y servidor y conserva SoundAttract.
No instales las dos versiones del addon simultáneamente.

## Población y cooldown

- Se reemplazan las oleadas automáticas diurnas, nocturnas y por ruido por
  **reposición hasta un mínimo de dos zombis por par de chunks**.
- Los pares son fijos y no se superponen: chunks X pares e impares vecinos
  hacia este/oeste en la misma fila Z. Las coordenadas negativas se emparejan
  correctamente. Cada chunk pertenece a un solo par.
- Para un par vacío se elige al azar el chunk de cada uno de sus dos zombis:
  **1+1 tiene 50 % de probabilidad, 2+0 tiene 25 % y 0+2 tiene 25 %**.
  Por tanto, un chunk individual recibe exactamente uno con probabilidad del
  50 %. Si una posición no es apta, se intenta el otro chunk del par.
- Los zombis vivos que ya estén en esos chunks cuentan. Si hay uno, se agrega
  uno; si ya hay dos o más, no se agrega ninguno. No se eliminan zombis existentes
  ni se impone un máximo de dos a los guardados anteriores o a spawns manuales.
- Cada par comparte un **cooldown aleatorio de 6000–12000 ticks**: cinco a diez
  minutos a 20 TPS. Se guarda con el mundo y no se reinicia por cambiar de jugador,
  descargar/cargar el chunk o reiniciar el servidor. Matar un zombi no permite
  una reposición inmediata. Las revisiones se ejecutan una vez por segundo.
- La primera visita rellena los pares aptos sin esperar el primer cooldown.
  La colocación inicial se reparte con un límite de 16 entidades y 256 intentos
  de posición por revisión para evitar una gran aparición de golpe.
- Solo se procesan pares con ambos chunks cargados alrededor de jugadores vivos
  de supervivencia en el **Overworld**. El radio por defecto es de cuatro chunks;
  se incluye el vecino necesario para completar los pares del borde.
- `config/zomboid-hordes.json` recibe el campo `populationRadiusChunks` (1–8,
  valor inicial 4) y `balanceVersion=9`. Se conservan las configuraciones ajenas
  a esta regla. Los antiguos tamaños de oleada siguen en el JSON por
  compatibilidad, pero ya no gobiernan el spawn automático.
- Los spawns naturales y de generación de chunks de zombis en el Overworld se
  bloquean para que no eludan el nuevo ritmo. Los huevos, comandos, spawners y
  eventos de otros mods permanecen disponibles. Los zombis que ya estaban
  guardados se conservan.
- Se respetan Pacífico y `doMobSpawning=false`. Creativo y espectador no activan
  áreas de población. Los zombis de población no desaparecen por distancia.

El mínimo depende de **terreno apto**. No se fuerza una colocación dentro de
agua, bloques, fuera del borde del mundo o a menos de 24 bloques horizontales
de otro jugador de supervivencia. Se usan posiciones de superficie a una altura
compatible con el jugador más cercano. Los pares sin una posición segura pueden
seguir bajo el mínimo. Una búsqueda completamente fallida vuelve a intentarse
tras diez segundos; si aparece algún zombi, empieza el cooldown completo.
No se generan ni fuerzan chunks nuevos para cumplir la cuota.

## Comportamientos conservados

Se mantienen la investigación de ruidos, integración de TaCZ, personalidad,
velocidad y combate de zombis, rotura de barreras, loot/restock e integraciones
del addon. El helicóptero conserva su evento diario y su sonido, pero **atrae
zombis existentes en lugar de generar una oleada nueva**. Se descartan las
oleadas pendientes heredadas al comenzar el nuevo sistema.

`/zhorda estado` informa pares activos, déficit y próximo cooldown.
`/zhorda parar` pausa la reposición en todo el Overworld durante 5–10 minutos.
Los comandos de prueba `dia`, `noche` y `ruido` revisan la densidad respetando
los cooldowns; `helicoptero` prueba además el sonido y la atracción. Las pruebas
no cambian la hora ni permiten acumular zombis por encima de la cuota.

## Reproducir y comprobar

Descarga y extrae [el paquete de código](zomboid-hordes-1.8.1-patch-source.zip).
El directorio extraído `hordes-patch/` contiene los scripts, fuentes, pruebas y
herramientas. La base está en [mods/](../mods/zomboid-hordes-1.8.0-neoforge-1.21.1.jar).

Necesitas Python 3 y Java 21. Desde el directorio extraído:

```sh
python3 -B build.py /ruta/zomboid-hordes-1.8.0-neoforge-1.21.1.jar
```

El hash de la base y de ECJ, ASM y Gson se verifica antes de usar las herramientas
incluidas. El resultado se escribe en `output/`. `src/` contiene la lógica nueva;
`tools/PatchHordes.java` define los cambios a las cuatro clases originales.
Las API dobles se usan únicamente durante las pruebas y no se incluyen en el mod.

Las comprobaciones verifican la distribución, mínimos, límites de cooldown,
coordenadas negativas, pares compartidos, posiciones de spawn, población sin
acumulación, guardado real de HordeData, migración real de HordeConfig con Gson,
comandos y límites de trabajo. También analizan el bytecode y las anotaciones de
eventos/Mixin y comprueban que todos los demás archivos de la base se conserven.

**No se ha arrancado Minecraft/NeoForge con el modpack completo en este entorno.**
La integración final pendiente es entrar en un mundo de supervivencia, observar
la distribución y los cooldowns con `/zhorda estado`, matar zombis y comprobar
la reposición tras 5–10 minutos, guardar/reabrir y verificar ruido, helicóptero y
compatibilidad de las IA de los demás mods.

Los cambios del addon se distribuyen bajo GPL-3.0-only, como la base. El audio
original se conserva sin cambios. ECJ, ASM y Gson mantienen sus propias licencias.
