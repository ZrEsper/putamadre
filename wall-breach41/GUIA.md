# Zombis: abrir camino por las paredes — revisión 41

1. Cierra Minecraft y el servidor.
2. Quita el JAR anterior de **zomboid-hordes** de `mods`. Pon el JAR de esta descarga. No dejes dos versiones.
3. Haz lo mismo en los clientes y en el servidor. Conserva los demás mods.
4. En el panel, entra en **Avanzado** y activa la opción de permitir romper bloques para todos los zombis (`romper_todos`) si quieres que los normales también rompan paredes. Los asediadores mantienen su permiso propio.
5. El mundo debe permitir `mobGriefing`. La velocidad sigue siendo la que configures en **Velocidad de rotura (%)**: un valor mayor rompe más rápido.

## Qué cambia

- Si la navegación sigue activa pero el zombi lleva dos segundos sin avanzar más de 0,75 bloques, también revisa si tiene una pared delante.
- Busca obstáculos a la altura de los pies, el cuerpo y la cabeza, incluyendo los lados del cuerpo. Esto ayuda en esquinas y aproximaciones en diagonal.
- Mantiene el progreso sobre el mismo bloque mientras siga delante y al alcance. Si aparece otra pared por delante o cambia de dirección, vuelve a elegir.
- Después de quitar un bloque, puede seleccionar el otro bloque que impide pasar. Continúa persiguiendo al jugador con la lógica existente.
- Conserva la velocidad configurada, los permisos y las reglas que excluyen bloques irrompibles, líquidos y bloques con inventarios. Consulta también el permiso de destrucción de NeoForge antes de golpear.

El panel sencillo y los perfiles de la revisión 40 se conservan. No cambia el loot, los atributos de los zombis ni la probabilidad de aparición.

## Cómo comprobarlo en tu mundo

En una copia del mundo, coloca al zombi fuera de una pared de madera o piedra de dos bloques de alto y haz que te persiga. Prueba de frente y desde una esquina. Debe mostrar las grietas, terminar un bloque y continuar con el que aún le impida pasar. Para esta prueba usa un jugador en supervivencia; creativo y espectador se excluyen de la persecución.

Si todavía se atasca, envía un vídeo corto donde se vean la pared, el suelo y el zombi, junto con los valores de permitir romper y velocidad. Las protecciones de otros mods pueden impedir la destrucción.

Validación: 57 comprobaciones automatizadas de geometría, navegación y permisos, más verificación del bytecode. Son pruebas con dobles de las APIs de Minecraft: falta comprobarlo dentro del juego.

## Screamer y Hordes

Esta revisión también evita que Hordes sustituya las estadísticas del screamer por las de un zombi común. Su velocidad base es 0,50 y su vida máxima es 40. Retira los modificadores antiguos de ritmo y salud individual y evita su conversión aleatoria en un asediador de 300 de vida. Si tenía más de 40 de vida actual, lo baja a 40; si estaba herido, no lo cura.

Para la carrera y la llamada a la horda actualizadas, instala también **screamer-sprint.2** incluido en el paquete conjunto.
