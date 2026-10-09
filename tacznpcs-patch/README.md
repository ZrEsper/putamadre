# TacZ NPCs tactical.13: saqueo, equipo, linterna y rutas

Actualización construida **sobre el JAR tactical.12 suministrado**, conservando sus recursos y las clases que no necesitan cambios. Minecraft 1.21.1 / NeoForge, TACZ y las mismas dependencias que .12.

## Instalación

Reemplazar tactical.12 y cualquier otra versión de TacZ NPCs por `tacznpcs-2.1.0-1.21.1-tactical.13.jar` en cliente y servidor. Mantener Self Expression, Flashlight, Fracture Point si se usa y la compatibilidad de Zombie Remains que ya estaba instalada. No es necesario borrar mundo, NPCs ni configuraciones. Una sola versión de TacZ NPCs en cada lado.

## Cambios

- **Golpes: mínimo 20 ticks entre intentos de daño por NPC**, un segundo a 20 TPS. Se aplica también al ataque cercano con armas de fuego, que .12 intentaba cada dos ticks. Cambiar de víctima no reinicia el cooldown. Se mantienen los valores nativos de daño y salud.
- **Linterna en mano secundaria únicamente de noche.** De día se guarda y se restaura el objeto desplazado. Se conserva el contenido/componentes al activar una linterna apagada. Durante la asistencia médica se respeta el objeto de cura en la mano secundaria. El objeto guardado y la herramienta desplazada se conservan al morir como drops, sin añadirlos repetidamente a la mochila.
- **Revisión de cuerpos y objetos sueltos dentro de 24 bloques**, incluyendo distintas alturas cercanas y coordenadas negativas. Se priorizan cuerpos que ofrecen mejoras de equipo, después suministros/materiales. Un zombi distante y distraído no cancela la búsqueda; un atacante reciente o un zombi que persigue al NPC sí interrumpe el saqueo.
- **Mejoras de arma, herramientas y armadura.** Se distinguen materiales de herramientas. Un civil puede ponerse equipo de Fracture Point encontrado como botín; se conservan las prendas civiles de Self Expression y la exclusión de armadura vanilla al vestir. La rareza del equipamiento militar inicial no se cambia. Al mejorar equipo, el objeto reemplazado se intercambia con el cuerpo, conservando el número total de objetos incluso si la mochila está llena.
- **Mochila de 27 stacks persistentes**, sin truncar las entradas previamente guardadas por .12 hasta 127 entradas de recuperación. Se recogen stacks completos o el espacio disponible para apilarlos, manteniendo componentes y límites nativos. El saqueo muestra pose y movimiento de mano, con una transferencia por segundo. Ya no se abandona un saqueo que está progresando por el temporizador fijo anterior de cinco segundos.
- **Los cuerpos dejan de usarse como almacenes de objetos.** Se retiran las rutinas que guardaban basura en cuerpos y reservaban ese botín durante un minuto. La investigación de ruido, la búsqueda de refugio y el paseo no desplazan una búsqueda activa de botín seguro. Se conservan prioridades de defensa, huida por heridas y asistencia médica.
- **Otra ruta cuando la zona esté limpia:** recorridos de 10–18 bloques con rutas alcanzables, dentro de chunks cargados, y memoria de ubicaciones recientes para reducir regresos. El trayecto vuelve a revisar botín y se interrumpe ante oportunidades o peligro. De noche, un NPC sano puede seguir explorando con linterna en una zona sin peligro inmediato.
- **Eliminación únicamente de cuerpos vacíos.** Para Zombie Remains se comprueban identidad del inventario, bloque y registro antes de retirarlo sin drops adicionales. Para Corpse se revisan inventarios principal, armadura, mano secundaria y objetos adicionales; también se sincroniza el equipo visual al intercambiar prendas. Si queda botín o falta espacio, el cuerpo permanece. Si otro jugador retira el cuerpo, el NPC deja de transferir desde ese inventario.

La compatibilidad previa de Remains 1.6.1 solo lee nueve entradas de bolsa al construir un cadáver. Antes de la muerte se conservan las entradas adicionales como drops normales, y las nueve primeras siguen entrando al cadáver por esa compatibilidad. No hace falta sustituir Remains para este parche. Objetos guardados de la linterna/mano secundaria también se dejan como drops al morir.

Se mantienen huevo y textura, spawn, profesiones, armas TACZ, ropa, puertas, animaciones y otras funciones de .12 fuera de los cambios descritos. Comando del huevo:

    /give @s tacznpcs:bandit_spawn_egg

## Reconstrucción

Extraer las fuentes; Java 21 y Python 3:

    python3 -B build.py /ruta/tacznpcs-2.1.0-1.21.1-tactical.12.jar

La base está en `base/` solo para reconstrucción. ECJ y ASM se incluyen con sus checksums. `src/` implementa el ajuste de supervivencia y el Goal de exploración; `Patch.java` hace las transformaciones localizadas; los dobles de `test-src/` y stubs no se empaquetan en el mod.

## Verificación y límites

Pasaron **169 aserciones de regresión** ejecutando los helpers reales parcheados de .12 contra dobles de API. Se cubren cooldown compartido, transición día/noche, componentes, inventario y drops conservados, prioridad de cuerpos equipados, herramientas superiores, armadura militar encontrada, bolsa llena sin pérdida, saqueo prolongado, retiro de cuerpos vacíos, inventarios separados de Corpse, cuerpo eliminado por un jugador, rollback, peligro inmediato, animación de saqueo y rutas nuevas. ASM analiza **209 métodos** de las seis clases existentes modificadas; los helpers se ejecutan con `-Xverify:all`. Se comprueba que todas las entradas no modificadas de .12 conserven exactamente sus bytes, y se reconstruye desde el archivo de fuentes en un directorio limpio.

**No se ejecutó Minecraft ni el modpack completo.** No están verificadas la locomoción física, la luz visual del mod Flashlight ni las animaciones dentro de esa instalación. Las pruebas no equivalen a una sesión real en el escenario de la captura.

Comprobación en juego: poner un cuerpo con arma, ropa y materiales junto a un NPC sano en zona segura; observar mejoras y recogida; esperar al vaciado y retiro; llenar su mochila y confirmar que un cuerpo con objetos permanece; comprobar otra ruta en zona despejada; pasar de día a noche y vuelta; contar intervalos de melee; guardar/reabrir y matar un NPC cargado para revisar conservación de objetos.
