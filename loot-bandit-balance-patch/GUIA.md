# Zombies Poldo · Loot escaso, visión nocturna y reanimación

## Instalar en tres pasos

1. Cierra Minecraft y el servidor. Haz una copia del mundo y de su configuración.

2. Extrae este ZIP. En la carpeta `mods` de tu instancia, retira las versiones anteriores de Hordes (.32 o anteriores), Zombie Remains (.9 o anteriores), TacZ NPCs (tactical.15 o anteriores), FirstAid (3.5.7 o anteriores) y FracturePoint PT2. Copia los cinco JAR de la carpeta `mods` de este paquete. No dejes dos versiones del mismo mod.

3. Usa los mismos cinco reemplazos en los clientes y el servidor. Conserva SurvivorCreator v33 y los demás mods que ya usabas. Abre el mundo: `/zhorda panel`, pestaña Loot y NPC, página Escasez y bandidos. Cambia valores y pulsa Aplicar para guardarlos.

## Mucho menos loot

El límite se aplica al loot NUEVO generado en cadáveres y cofres/barriles. No vacía cuerpos ya guardados ni quita las pertenencias reales de jugadores o NPC. Se conservan los objetos que un zombi llevaba realmente equipados: no se modifica su equipo, IA, velocidad, daño ni población.

La primera vez que se carga cada mundo se aplican los valores de escasez una sola vez, aunque antes tuviera valores muy altos. Después puedes ajustarlos normalmente. Esto cambia los ajustes de loot y los nuevos controles de bandidos; conserva los ajustes de los zombis.

| Calidad del cuerpo | Suministros máximos | Probabilidad de un arma | Probabilidad de equipo de combate | Probabilidad de munición | Balas máximas |
| --- | --- | --- | --- | --- | --- |
| Común | 2 | 0,5% | 0,5% | 2,5% | 2 |
| Poco común | 3 | 1% | 1% | 5% | 3 |
| Raro | 4 | 2% | 2% | 10% | 4 |
| Épico | 5 | 3% | 3% | 15% | 6 |
| Legendario | 6 | 4% | 4% | 20% | 8 |
| Mítico | 7 | 5% | 5% | 25% | 12 |

Las probabilidades son límites adicionales: primero tiene que haberse generado el objeto. Hay una tirada por categoría y cuerpo, no una tirada por cada objeto. Como máximo queda un arma, una pieza de equipo de combate y una pila de munición generadas. Las pilas de comida y materiales son pequeñas. La ropa civil básica se conserva por separado, hasta dos prendas. Herramientas: máximo una, de una unidad. El equipo real del muerto se conserva aparte de estos límites.

Los objetos valiosos, como diamante o netherita, tienen también una tirada escasa y un límite de una entrada. Las armas generadas ya no vienen con cargadores llenos: como mucho cuatro balas y sin bala adicional en la recámara. Los cadáveres de NPC contienen su carga real: se eliminan los lotes de equipo gratuitos añadidos al morir.

## Qué significa cada nuevo control

| Control | Qué hace |
| --- | --- |
| Loot reducido | Activa los límites anteriores para nuevos cuerpos y cofres/barriles. Desactivarlo devuelve el filtrado anterior; los pesos de las categorías se ajustan por separado. |
| Arma: prob. base | Probabilidad para un cuerpo raro. Por defecto 2%; cada calidad usa el multiplicador de la tabla. Una tirada por cuerpo. |
| Armadura: prob. base | Igual, para equipo de combate, mochilas y visión nocturna. Por defecto 2%. La ropa civil básica y el equipo real se conservan. |
| Munición: prob. base | Por defecto 10% en cuerpos raros. Como máximo una pila pequeña. |
| Suministros por cuerpo | Multiplica los máximos de suministros de la tabla, entre 0 y 3. Por defecto 1. No elimina pertenencias reales. |
| Bandidos armados al nacer | Por defecto 2%: aproximadamente 98 de cada 100 NPC nuevos empiezan con las manos vacías. Los pocos armados tienen poca munición inicial. |
| Bandidos con armadura | Por defecto 2%. No cambia los zombis ni retira equipo de NPC existentes. |
| Bandidos con suministros | Por defecto 15%: como máximo una pequeña ración o medicina inicial. |
| Recarga con munición saqueada | Activado: los NPC nuevos gastan balas compatibles de su bolsa al recargar. Sin balas, no recargan. Desactivado: conserva la recarga nativa. El saqueo de armas y armaduras sigue funcionando. |
| Tiempo de recarga | De 0,5 a 10 segundos; por defecto 2. La carga se transfiere después de esperar, si sigue usando la misma arma. |
| Restaurar escasez | Prepara de nuevo los valores de loot escaso. Pulsa Aplicar para guardarlos. No restaura los ajustes de zombis. |

Los NPC nuevos siguen recogiendo objetos reales de cofres, cadáveres y suelo: pueden equipar mejores armas y armaduras, guardar las anteriores y ganar experiencia con el saqueo. Ya no reciben una pistola y munición gratis al comenzar. Estos cambios de equipo inicial y recarga se aplican a los nuevos NPC naturales; respetan NPC existentes y los NPC de pruebas creados con `/npcprueba`.

Los valores iniciales de loot épico, legendario y mítico pasan a 0,6%, 0,1% y 0,01%. Visión nocturna: 0,2%; ropa militar: 0,5%; armas con accesorios: 5%; armas con munición: 10%. Las categorías generales de armas, munición y accesorios también bajan. Puedes volver a configurarlas; un perfil de dificultad puede modificar esos pesos. Los límites de escasez siguen activos mientras Loot reducido esté activado.

## Visión nocturna

Todas las modalidades de visión nocturna de FracturePoint dan el efecto de visión nocturna sin cuenta atrás mientras estén encendidas y tengan energía. Una batería llena dura 15 minutos de uso activo a 20 ticks por segundo. Apagarla detiene el consumo. Con batería vacía, al apagarla o al quitar el casco, se retira el efecto concedido por el casco. Si ya tenías una poción de visión nocturna, se conserva su duración restante.

Se mantienen la capacidad de batería, las recetas y la recarga existentes. El tiempo se mide en tiempo del juego: un servidor con retraso puede tardar más de 15 minutos reales.

## Reanimación sin bucle

Con desfibrilador, la acción dura 3 segundos; una reanimación asistida sin él dura 8 segundos. Se prioriza el desfibrilador aunque lo lleves en la otra mano y tengas una venda en la principal. El progreso muestra barra y porcentaje.

Para autorreanimarte, debes seguir vivo, estar en estado crítico y tener el desfibrilador en una mano. Usa la acción indicada por el juego. Al terminar, consumes una carga y sales del estado crítico, incluso si quedaba una marca de inconsciencia desactualizada. No vuelve a consumir cargas en un bucle. No revive a un jugador que ya ha muerto.

La reanimación estabiliza las partes del cuerpo al menos al 30% y da 20 segundos de margen para vendarte. No elimina automáticamente heridas ni infección. Cambiar o quitar el desfibrilador durante la acción interrumpe el intento.

## Inyección contra la infección zombi

La inyección de FirstAid muestra una barra y el porcentaje durante su uso, también para este objeto que antes no se reconocía como medicina en la interfaz. Al completar la aplicación aparece una confirmación al 100%. Su uso normal dura 32 ticks, aproximadamente 1,6 segundos. Se conserva la cura de infección de la versión anterior: debes completar la aplicación. Los antibióticos también usan este indicador.

Los nuevos mensajes médicos y el texto de duración de batería están disponibles en español, inglés y alemán según el idioma de Minecraft.

## Qué se ha comprobado

21.845 comprobaciones de lógica con dobles de las APIs: límites por calidad, probabilidades, inventarios conservados, NPC desarmados, recarga sin duplicar balas, prioridad del desfibrilador, salida del estado crítico, indicador de medicina y consumo exacto de batería. Se verifica el bytecode modificado, la integridad de los JAR y la conservación de recursos y clases de zombis.

Pendiente de prueba dentro de Minecraft: arranque con todos tus mods, renderizado de los indicadores, sincronización multijugador y comportamiento visual. Este paquete modifica JAR existentes; las pruebas simuladas no sustituyen esa comprobación.
