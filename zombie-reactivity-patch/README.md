# Zombis reactivos: ruido, sigilo, variación y esquiva

NeoForge 1.21.1. Parche construido sobre los JAR exactos enviados por el usuario. No requiere convertir el mapa ni cambiar el datapack de Mosslorn.

## Instalación

Cerrar Minecraft y el servidor. **Sustituir estos cuatro mods, sin duplicar sus versiones:**

| Original | Sustituto |
|---|---|
| Zomboid Hordes `mosslorn-danger-zones.3` | `zomboid-hordes-1.8.3-neoforge-1.21.1-reactive-noise-dodge.5.jar` |
| Zombie Awareness 1.13.2 | `zombieawareness-neoforge-1.21.0-1.13.2-reactive-noise.jar` |
| Survivor Creator v19 | `survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v20-elusive.jar` |
| Weaker Day Zombie 1.0.0 | `weaker_day_zombie_neoforge-1.0.0-neoforge-1.21.1-gentle-day.jar` |

Instalar también en el servidor para que controle percepción, rasgos y ataques. En una partida individual/LAN, el servidor integrado usa esos mismos JAR. Mantener las dependencias ya instaladas, como CoroUtil. **Conservar `The-Hordes-1.21.1-1.6.3f-zombies-mutants-only.1.jar`: no necesita reemplazo.** Sus oleadas e infección se conservan. También se conservan las zonas de peligro de Mosslorn, el filtro de spawns, la rareza de mutantes, el mapa de selección de spawn y la música de Survivor Creator.

## Percepción y movimiento

Se aplican a los mobs reconocidos por `ZombieIndividual.supported`: zombis vanilla, variantes reconocidas y los siete tipos de MutantsZombies soportados por el parche previo. El origen del spawn no importa. Los bandidos no se convierten en zombis.

- Personalidad persistente: 45 % torpes, 40 % normales, 15 % agresivos. Los perfiles existentes se actualizan una vez al recibir este parche; no se recalculan cada tick.
- Visión base: 12 / 18 / 26 bloques y conos de 100° / 120° / 150°. Se necesita línea de visión real. Agacharse reduce la distancia visual al 70 %. A dos bloques, con línea de visión, reconocen al jugador aunque esté detrás de su orientación inicial.
- Reacción inicial: aproximadamente 0.3–0.75 s / 0.1–0.3 s / 0–0.15 s, además del intervalo de actualización. La evaluación de percepción se reparte cada cuatro ticks por entidad.
- Menos pausas y más breves: el objetivo de reposo pasa de 65 % a 25 % para torpes y de 20 % a 8 % para los demás; sus pausas duran 8–32 ticks en vez de 20–80.
- Ritmo independiente de la personalidad: 55 % normales, 20 % lentos, 10 % muy lentos y 15 % corredores. Atributos de navegación de zombis normales: 0.28 / 0.22 / 0.18 / 0.34. No equivalen a bloques por segundo. Los mutantes conservan su velocidad base propia y reciben factores 1.05 / 0.90 / 0.80 / 1.25, sin quedar forzados al tamaño/ritmo de un zombi normal.
- Al perder línea de visión dejan de perseguir las coordenadas actuales del jugador. Investigan la posición vista u oída, durante 6 / 9 / 12 segundos. Moverse en silencio y apartarse de esa posición permite perderlos; seguir corriendo crea nuevos indicios.
- Se elimina el refresco antiguo que actualizaba la posición del jugador oculto tras una alerta. La ira tampoco permite ver a través de paredes. La navegación de investigación sigue perteneciendo a `InvestigateNoiseGoal`, para evitar dos controladores moviendo al mismo zombi.

## Ruido

Los pasos se obtienen del movimiento real del jugador en el servidor, no de cada sonido genérico `.step`, que podría pertenecer a otro mob. No se producen pasos al quedarse parado, teletransportarse, volar o ir en un vehículo. En el agua se permite ruido de desplazamiento.

| Ruido | Radio base |
|---|---:|
| Caminar | 6 bloques |
| Correr | 18 bloques |
| Agachado | 2.2 bloques |
| Estornudo/tos | Al menos 8 bloques |
| Cofre/barril | 8 bloques |
| Puerta | 12 bloques |
| Colocar/golpear, pistones/yunque | 14 bloques |
| Romper/minar bloques | 18 bloques |
| Disparo por el puente ambiental | 56 bloques |
| Explosión por el puente ambiental | 72 bloques |

El agua aumenta los pasos un 35 %. La sensibilidad individual multiplica el alcance: 0.85–1.10, 1.05–1.30 o 1.20–1.45. Una pared reduce el alcance de pasos al 70 %; escuchar no permite ver al emisor. Los radios ambientales dependen también del volumen, con un máximo de 96 para ese puente. Los eventos de armas, silenciadores y helicóptero existentes conservan sus propias reglas de alerta.

El puente de Zombie Awareness recoge sonidos del servidor y eventos de interacción/rotura de bloques. Sus controladores antiguos de olfato/persecución no se ejecutan sobre los zombis gestionados por Hordes. Los sonidos ambientales de los zombis no se convierten en alertas nuevas en el puente. Un sonido más fuerte puede imponerse a otro más débil en el mismo intervalo; se limitan los repetidos para reducir trabajo de IA.

## Escurridizo y mordidas

Nuevo rasgo **Escurridizo**, coste 4 puntos: reduce un 45 % el radio de sus pasos. Funciona también corriendo y se combina con agacharse. Disparos, explosiones y acciones de bloques conservan su ruido. El rasgo Torpe aumenta el radio de pasos un 20 %. También se consultan los rasgos concedidos por la profesión. Los personajes existentes necesitan seleccionar el nuevo rasgo para recibir su beneficio.

Los ataques cuerpo a cuerpo al jugador tienen preparación y un aviso en la barra de acción. Para un zombi normal, la preparación es de 12 / 10 / 8 ticks según personalidad, y el descanso entre intentos es de 28 / 22 / 18 ticks. Esto impide la sucesión de mordidas cada tick del mixin anterior. Los mutantes conservan además la cadencia y animaciones nativas de su propio objetivo de ataque; pueden tener una preparación efectiva más larga.

**Salta justo antes del impacto:** ESPACIO por defecto, o tu tecla de salto si la cambiaste. Se usa el evento de salto real del servidor. La ventana es de los cuatro ticks anteriores al intento de impacto (~200 ms), con 40 ticks de cooldown de esquiva (2 s). El salto se consume al esquivar y el atacante queda brevemente aturdido. Mantener ESPACIO no da inmunidad; saltos viejos y durante el cooldown no sirven. No permite esquivar proyectiles, explosiones ni habilidades especiales ajenas a `Mob.doHurtTarget`.

Una mordida esquivada se rechaza antes de aplicar el ataque, en vez de curar al jugador después. El flujo de infección de The Hordes recibe daño mediante `LivingDamageEvent.Post`; rechazar el golpe evita generar ese daño. Eso no cura infecciones anteriores.

## Weaker Day

Su original ponía velocidad cero con vida completa y, en el otro modo, Slowness III. El sustituto independiente usa Slowness I breve y permite desactivar esa penalización con `/wdztoggle`. **Cuando Hordes está instalado, su mixin de compatibilidad sigue anulando ese procedimiento y aplica su propia penalización diurna**, evitando acumulación. Por tanto, el código de congelación del original por sí solo no demuestra que fuera la causa del comportamiento observado con todos los mods juntos.

## Validación y límites

11 casos de reglas, 23 casos con entidades simuladas que ejercitan el helper compilado: adquisición visual, ocultarse tras pared, memoria de posiciones, ruido al correr, rasgo real, teletransporte, preparación, salto validado, cooldown, velocidades propias de mutantes y sonidos/eventos ambientales. Se verifican todas las clases originales modificadas con ASM BasicVerifier; las pruebas se ejecutan con `-Xverify:all`. Se comparan las entradas no modificadas de los JAR para conservar recursos y parches previos; los stubs de compilación no se distribuyen.

**No se ha ejecutado Minecraft ni un servidor real en este entorno.** Falta probar dentro del modpack el orden de mixins/eventos, la esquiva con latencia y la navegación de las distintas entidades. Los valores anteriores describen las reglas implementadas, no mediciones de una partida. El parche no cambia las animaciones/modelos de los mutantes ni garantiza que sus ataques especiales sigan el flujo de mordida común.

Para reconstruir, actualizar las rutas de los JAR en `inputs.json` (manteniendo sus hashes) y ejecutar con Java 21:

```sh
python build.py /ruta/tools
```

`tools` debe contener `ecj.jar`, `asm.jar`, `asm-tree.jar` y `asm-analysis.jar`.

## Corrección de arranque .5

El latest.log enviado muestra `InvalidMixinException: MutantBiteMixin is missing an @Mixin annotation`. El stub anterior declaraba retención RUNTIME, pero Mixin busca esta anotación en RuntimeInvisibleAnnotations (retención CLASS). Se corrigió la retención y el empaquetado ahora exige la anotación invisible correcta. Se repitieron 34 casos simulados y verificación de bytecode; no se ejecutó Minecraft. Sustituir .4 por .5 en cliente y servidor. Los otros tres JAR del paquete anterior no cambian.
