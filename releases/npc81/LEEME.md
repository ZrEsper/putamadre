# TaCZ NPCs .45 — puertas, charlas y seguimiento

NeoForge / Minecraft 1.21.1. Sustituir .44 (o fix4), en cliente y servidor. Una sola
versión de TaCZ NPCs instalada. Conservar copia del mundo y JAR anterior.
Base .44: conserva las correcciones de inventario, contexto del chat, null-varargs,
chunks descargados y las de fix4. No borra identidades, confianza ni objetivos guardados.

## Cambios

- SEGUIR Y CONVERSAR: un NPC con orden follow hacia el mismo jugador de la conversación
  mantiene su navegación. Authority70 ya no le detiene la ruta cada seis ticks ni corta
  el chat solamente porque su dueño corra. La conversación sigue bloqueando ataques;
  peligro, daño, arma y caducidad mantienen sus reglas. Al ordenar esperar deja de caminar.
  Se actualiza la ruta hacia su dueño cada diez ticks mientras conversan, sin teletransporte.
- CORRER: las rutas tranquilas largas (más de nueve bloques horizontales) solicitan carrera.
  La limpieza de calma respeta esa solicitud mientras haya ruta, con vencimiento de 40 ticks.
  No se activa carrera durante una charla entre NPCs ni por desplazamientos cortos.
- ENTRE NPCS: los reclutados del mismo dueño pueden conversar si este está cerca, quieto
  y no hay peligro. Las charlas breves tienen prioridad sobre esperar al dueño quieto;
  vuelven a seguir cuando se mueve. Neutral/amigable pueden charlar sin exigir idéntico
  valor de disposición. El saludo automático al jugador cede cuando hay un compañero
  disponible para charla. Se busca pareja cada 100 ticks y se conserva el cooldown.
- VARIEDAD: la charla abierta de las ocho voces ya no exige confianza previa para un
  hostil sin historial grave con el jugador. El respaldo de una emboscada sin cobertura
  usa conversación normal/abierta, en vez de forzar siempre familiar con fiebre.
  Los encuentros médicos siguen existiendo, pero ya no son ese respaldo universal.
  Respeta la opción conversacion_abierta del JSON; con false se conserva el modo limitado.
- PUERTAS: los videos muestran atascos incluso con puerta abierta. El NPC usaba ancho
  0.75; el panel abierto ocupa 3/16 del bloque y solapa esa caja al caminar centrado.
  Se usa ancho 0.6 como el jugador, manteniendo altura 1.85 y modelo visual. Esto cambia
  también el ancho de la caja de impacto general de estos NPCs. Conserva OpenDoorGoal
  reinstalado de fix4; no rompe puertas, no atraviesa bloques, no abre puertas de hierro.

## Pruebas

Pasaron pruebas JVM sobre el JAR para seguimiento durante chat con Authority70 real,
retención al correr el dueño, navegación de acompañante, prioridad de charla entre
seguidores, selección de las ocho voces y solicitud/fin de carrera. También pasan las
pruebas de contexto .44, fix4 y regresiones de seguridad, relaciones, inventario y movimiento.
La geometría de puerta se comprobó matemáticamente. No se ejecutó Minecraft real:
no se afirma haber validado en juego todas las puertas, animaciones o navegación urbana.

Prueba en el mundo: /tick rate 20; dos seguidores compatibles a menos de 12 bloques con
el jugador quieto unos segundos; luego caminar, correr y abrir conversación con un
seguidor. Probar puerta de madera de un bloque, cerrada y abierta, entrando y saliendo.
Revisar /npcdebug si falla. El seguimiento conserva su orden mientras se habla, no una
orden temporal nueva. Las charlas no ocurren si hay combate, curación o conversación
activa con el jugador; después de hablar respetan el cooldown configurado.

## Alcance y límites

Life79 no se reescribe: se cambia únicamente la prioridad de charla entre compañeros
frente a seguimiento quieto. No implementa aún ocupación y acondicionamiento de cuartos,
sociedades, nuevos sistemas de memoria, ni una migración a SmartBrainLib. El terreno,
claims, enemigos, inventario y crafteo conservan sus límites. No hay certificación de que
haya desaparecido todo atasco. El cambio de ancho conserva el modelo pero requiere
comprobar en juego daño localizado y equipo con los otros mods instalados.

Fuentes cambiadas: Chat71, Free75, Ambient78, Flow73, Life79, Navigation78, Peace78,
Motion76, Escort81. Patch81 modifica Authority70.busy y AutoGunOperator (ancho/tick final).
Scripts usan la base .44 y herramientas/API de las entregas anteriores. No se incluyen
logs ni videos del usuario. Los textos existentes continúan en JSON; no se añaden textos
narrativos dentro de Java.
