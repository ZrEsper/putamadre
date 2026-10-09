# TacZ NPCs tactical.4

Para Minecraft 1.21.1 / NeoForge y TACZ. Reemplazar tactical.3 (y cualquier otra versión de TacZ NPCs) por `tacznpcs-2.1.0-1.21.1-tactical.4.jar`, en cliente y servidor. Mantener **Zombie Remains Loot 1.6.1 npc-compat**, Doomsday Decoration y las demás dependencias existentes. Remains no cambia en esta entrega. No borrar el mundo.

## Cambios respecto a .3

- La velocidad de mob pasa de 0.095 a 0.2375, sin sprint; las órdenes de navegación utilizan factores 0.95–1.0. La comparación anterior del atributo con el del jugador era incorrecta. Esto elimina la reducción artificial que provocaba la lentitud. El 5% es respecto a una base de mob 0.25, **no una medición de velocidad física frente al jugador**; peso de armas, terreno y mods influyen.
- Navegación flotante activada, sin pose agachada al saquear dentro del agua. Si falla el cálculo de ruta en agua, se solicita movimiento al control de natación hacia el destino. Se conserva FloatGoal. Las rutas se actualizan cada diez ticks y ya no se cancelan continuamente al alcanzar el umbral de distancia de combate.
- El atacante reciente tiene prioridad sobre zombis y objetivos previos, incluso cuando el NPC estaba ocupado. Las decisiones se revisan cada dos ticks. Los armados reaccionan a zombis que los están persiguiendo o se acercan a 4.5 bloques; los desarmados, a 3.5. Un zombi lejano y distraído no bloquea el saqueo. Se conserva el combate hostil contra jugadores detectados y la memoria breve al perder visión.
- Cabeza, cuerpo y mirada giran hacia el blanco antes de disparar. El giro está limitado por tick y no se dispara si todavía faltan más de quince grados para orientarse. Sigue existiendo dispersión de puntería, protección de aliados y recarga TACZ.
- Saqueo de cuerpos de Zombie Remains y Corpse, más recogida de objetos y materiales sueltos en doce bloques. Pose y movimiento de mano durante la búsqueda: dos segundos por objeto del cuerpo, un segundo por objeto suelto. No se minan bloques ni se destruyen construcciones para recolectar.
- Los materiales compatibles se apilan, conservando componentes y el límite nativo de cada stack, en la bolsa persistente de nueve entradas. No se duplica el botín ni se borra por falta de espacio. Se equipa el mejor equipo encontrado.
- Cada transferencia exitosa otorga experiencia persistente. Doce hallazgos aportan como máximo un nivel adicional de habilidad, dentro de un máximo global de tres. Mejora reacción y puntería mediante las reglas existentes; no aumenta salud ni daño.
- Cuando no hay amenazas ni botín, exploran con trayectos de cinco bloques y pausas más breves. Se mantienen la ropa de Selfexpression, el conjunto militar completo de Fracture Point con probabilidad 1%, los cadáveres compatibles y el huevo con textura de camuflaje: `/give @s tacznpcs:bandit_spawn_egg`.

## Compilación y pruebas

Extraer el archivo de fuentes y ejecutar con Java 21 y Python 3:

    python3 -B build.py /ruta/tacznpcs-2.1.0-1.21.1-tactical.1.jar /ruta/zombie-remains-loot-1.6.0-neoforge-1.21.1.jar

Las bases están en `base/` para reconstrucción, no para instalar. ECJ y ASM incluidos, con checksums verificados. `src/` contiene el controlador; `remains-src/` conserva la compatibilidad 1.6.1; `Patch.java` aplica las transformaciones. Los dobles de API y stubs no se incluyen en los mods.

Pasaron **1201 aserciones con dobles de API**, incluyendo prioridad del jugador atacante sobre un zombi, orientación de cabeza/cuerpo, saqueo con zombis distraídos, recogida y apilado de materiales, conservación de cantidades, experiencia persistente limitada, navegación flotante y recuperación cuando falla una ruta en agua. Se mantienen las pruebas de cadáveres, fuego amigo, equipo, guardado, animación de mano, huevo y spawn. ASM analiza 138 métodos y los helpers se ejecutan con `-Xverify:all`.

**No se ejecutó el modpack en Minecraft.** Estas pruebas verifican decisiones y llamadas de API; no miden locomoción física, rutas en esa calle inundada, animaciones ni compatibilidad completa con los demás mods. El log entregado carga .3 y Remains 1.6.1 y no muestra una excepción del controlador de IA. También contiene errores de registro de biomas de fabulous-furnished independientes del controlador.

Validación pendiente en juego: provocar con un jugador a un NPC que esté combatiendo un zombi; comprobar orientación y defensa; colocar materiales/cuerpos cerca de zombis distraídos; observar aproximación, animación, recogida y persistencia al recargar; probar calles inundadas y una horda. Una sola versión de cada mod en ambos lados.
