# Tactical .52 — trabajos y convivencia visibles

Reemplaza tactical .51 por este .52; conserva Ragdollified .34, remains .28 y Gore .43. Reinicia cliente/servidor y prueba una copia del mundo a 20 TPS.

## Cambios
- Tala y minería reales: dirige la mirada al bloque de trabajo, muestra grietas conforme avanza la tarea y reproduce el sonido nativo de golpe del material. Usa las herramientas y movimientos que ya equipa/ejecuta la IA; no añade un modelo o animación nueva de cuerpo completo.
- Colocación: únicamente después de una colocación exitosa, gesto de mano, mirada al bloque y sonido nativo. No simula construcción si la operación falla. El método original y sus comprobaciones de inventario/permisos permanecen.
- Limpia las grietas al detenerse, cambiar de bloque, dormir o entrar en combate. No crea objetos, bloques, partículas de botín ni materiales para aparentar actividad.
- Frases contextuales sobre el trabajo real, incluyendo peticiones de materiales y avisos al jugador cercano. Máximo una apertura por NPC cada 1200 ticks; no se envía una frase por cada golpe.
- Un compañero cercano que comparte la misma base puede contestar 30 ticks después. Necesita estar vivo, despierto, sin combatir ni conversar con el jugador. No hace conversar hostiles con no hostiles. Las respuestas caducan si las condiciones dejan de cumplirse.
- La colaboración material usa los sistemas existentes: entregar regalos/materiales y guardar recursos en el cofre de la base para que la IA los utilice. Esta versión no introduce comandos nuevos de construcción ni coordinación nueva de cuadrillas.
- Textos editables: data/tacznpcs/work-actions86.json dentro del JAR.

## Qué verificar en Minecraft
1. NPC con herramienta, materiales/objetivo y permiso para trabajar: ver la herramienta y el swing existentes, escuchar golpes y ver grietas antes de que se retire el bloque.
2. NPC colocando muebles o bloques: gesto y sonido después de colocarlos; sin duplicar inventario.
3. Dos NPCs compatibles de la misma base y un jugador observando cerca: comentario relacionado con el trabajo y respuesta del compañero tras 1.5 segundos.
4. Hablar con ellos mientras trabajan: los comentarios ambientales deben ceder a la conversación del jugador.
5. Entregar materiales o dejarlos en el cofre y comprobar su utilización por los trabajos existentes. No todos los objetos son útiles para todas las tareas.

## Comprobaciones realizadas y límites
Pasaron 14 controles nuevos de efectos, sonidos, limpieza, límite de mensajes, ausencia de creación de recursos y respuesta con pausa; además las 65 comprobaciones anteriores de ADS, supervivencia, rutas, sueño, menús, recetas y colocación. Se verificó bytecode con ASM y se ejecutaron las pruebas con -Xverify:all. Se comprueba colocación usando el método Base48 de producción y un mundo simulado.

NO se ejecutó Minecraft real. Falta validar sonidos, apariencia, modelos/herramientas del modpack y convivencia con el resto de mods. Esto hace visibles las acciones que realmente arranca la IA; no resuelve por sí solo una tarea que no pueda iniciarse por falta de materiales, herramientas o ruta. No añade animaciones personalizadas, talleres con recetas nuevas, reparto de trabajo ni sociedades autónomas.

El log adjunto carga .51; el banner antiguo que dice tactical.48 es texto heredado. Las conversaciones reason=danger siguen indicando una interrupción por peligro, no prueban un crash. Los avisos de recursos y otros mods quedan fuera de este parche.

## Archivos cambiados
Base48.place se conserva como placeBefore86 y se envuelve para añadir el gesto solo tras éxito; Base48.mine y Shelter77.gatherBase47 añaden feedback en el golpe real; CalmBrain.tick limpia efectos y envía respuestas pendientes. Nuevos Work86/Work86$Action y JSON. Fuentes, parche ASM, pruebas y lista exacta de entradas cambiadas incluidos. Herramientas de compilación dependen de rutas/fixtures locales, no es un proyecto Minecraft independiente.
