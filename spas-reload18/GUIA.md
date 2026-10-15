ZOMBIES POLDO — CORRECCIÓN DE RECARGA SPAS-12 (.18)

1. Cierra Minecraft y el servidor.
2. Quita de «mods» el TaCZ anterior (.16 o .17). No dejes dos TaCZ.
3. Pon el TaCZ .18 de este ZIP en «mods», tanto en el servidor como en los clientes.
4. Conserva tu Tactical «tracers-impacts-scope-limits.1» y los demás mods.
5. Abre el juego y prueba la SPAS-12: recarga normal, cargador vacío e interrupción.

Qué corrige
El log muestra un cierre del servidor al recargar la SPAS-12: el script intentaba
leer «interrupted_time» de una caché inexistente. Si esa información se pierde,
esta versión cancela la recarga sin añadir munición. Pulsa R para volver a recargar.
La protección funciona también con el script predeterminado ya extraído del pack:
no necesitas borrar tus packs ni configuraciones.

Se usa como base exactamente el TaCZ .17 que enviaste. Se mantienen sus cambios
de trazadoras, impactos y límites de zoom. Tactical no se modifica ni se incluye.
No contiene cambios de daño, loot, visión nocturna ni otros mods.

Validación
Se reprodujo el error original con LuaJ 3.0.8 del propio JAR. Pasaron 6.421
comprobaciones de cancelación, reinicio, recarga e interrupción. Se verificó la
estructura del bytecode y que solo cambian la llamada de recarga y el script SPAS.
No se ha ejecutado Minecraft en este entorno; hace falta comprobarlo en el juego.

Si vuelve a cerrarse, conserva el nuevo latest.log y el crash-report de esa prueba.
El log anterior correspondía a TaCZ .16 y Fracture Point NVG .1.
