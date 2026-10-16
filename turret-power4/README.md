# Turret Power .1 / Doomsday .4 / Survival generator tiers .2

Implementación sobre los dos JAR enviados por el usuario y Survival 1.1.0 compatible con Doomsday. [Guía del jugador](GUIA.txt).

- `Link`: valida el generador de Doomsday registrado en el mismo mundo, chunk cargado, radio 25 y extracción simulada de 100 FE. `tryShoot` comprueba objetivo, distancia máxima 20, vida, permisos, visión directa y cadencia de ráfaga. El evento nativo **GunFireEvent**, emitido por cada cartucho incluso en las tareas diferidas de TaCZ, repite esas comprobaciones y descuenta 100 FE. No modifica el daño, las balas ni el disparo de jugadores. La pausa se cuenta por tiros aceptados, no por intentos/cooldowns. La batería interna no autoriza disparos. El menú sincroniza energía y capacidad del generador y muestra el requisito aun con el antiguo ajuste desactivado.
- Ráfagas de 3, intervalo mínimo de 2 ticks y pausa de 12 ticks; siguen vigentes los cooldowns nativos y el modo conservador. Una interrupción larga reinicia la cuenta.
- Zombis/husks/drowned y mutantes reciben como objetivo una torreta próxima si están libres. Se conserva su objetivo vivo anterior. Ataque de contacto cada segundo, distancia máxima 2 y visión, usando `Mob.doHurtTarget`; no se elude su daño nativo ni la invulnerabilidad temporal del objetivo. La configuración global de inmunidad de torretas no bloquea el daño de zombis.
- Dos sonidos sintetizados mono: servo cada 80 ticks con energía/arma y alerta de adquisición limitada a una cada 60 ticks. Registro en el bus MOD; los eventos de disparo en el bus de juego. Subtítulos ES/EN/DE.
- `PowerPolicy`: almacenamiento de generadores 128k/512k/768k; salida 80/160/240 FE/tick. La conversión nativa del combustible se multiplica por 1/2/3 según modelo, conservando su fracción acumulada y persistencia. Los aparatos mantienen 64k y su demanda anterior. No se modifican el guardado ni la carga de inventarios, energía o combustible activo.
- `Boards`: multiplicador único 2 en ventanas, cristal, puertas y trampillas con una plank de Doomsday adyacente; ambas mitades de una puerta comparten el refuerzo. El puente existente de Hordes .43 utiliza el mismo factor. Receta 3 tablones → 6 refuerzos; variaciones mediante cortapiedras.
- `Beds`: conserva bloque, modelo, propiedades y huella del mueble. Usa `isBed` de NeoForge y el descanso nativo, sin escribir `OCCUPIED` en estados que no lo contienen. Comprueba ocupación con los jugadores dormidos. Un ámbito `ThreadLocal`, retirado en `finally`, cancela `PlayerSetSpawnEvent` únicamente para ese jugador y la posición de la cama durante la llamada nativa: dormir no modifica su reaparición, ni siquiera en los intentos fallidos por ser de día. Rechaza dimensiones sin camas; no explota. No fuerza curación.

## Reproducir

Desde el checkout existente, sin crear worktrees:

```sh
python turret-power4/build.py
python turret-power4/test.py
python turret-power4/package.py
```

Requiere Java 21, Python 3 y los ECJ/ASM fijados por SHA-256 del proyecto `hordes-melee-interiors-patch/build/tools`. Las entradas se verifican con `inputs.json`. No se descargan ni ejecutan instrucciones de los adjuntos. Los cambios de cada JAR y sus hashes están en `validation.json`.

Las 81 comprobaciones ejecutan helpers de producción con dobles de API y cuatro métodos del bytecode nativo de TurretEntity más la conversión nativa de combustible, extraídos con una superclase de prueba. El puente de barricadas se ejecuta desde el Hordes .43 real. ASM BasicVerifier revisa los métodos modificados. Es una validación de lógica y bytecode; no sustituye una partida: no se probaron render, sonido en juego, navegación completa de mutantes ni descanso en un cliente Minecraft.

El ZIP incluye Hordes .43 sin cambios para que el puente de barricadas esté presente y conserve las correcciones anteriores de cadáveres infectados. Reemplazar versiones anteriores de los cuatro mods tanto en clientes como en servidor; mantener el resto de dependencias del modpack.
