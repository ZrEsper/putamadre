# Zombis: daño localizado, vida y sangre

NeoForge 1.21.1. Sustituye los tres JAR anteriores por los tres incluidos, tanto en clientes como en servidor. No dejes duplicados. Conserva tu pack TZP compatible con Zomboid Gore y EMF/ETF: las piezas visibles siguen utilizando sus modelos y las variables existentes del gore. Este ZIP se extrae; no se instala como resource pack.

## Panel de hordas
En el panel sencillo, Ambiente/población: **Vida de zombis (40 = 20 corazones)**. También en Avanzado > Velocidad y daño. Se escribe el número o se usa la rueda. Valores entre 2 y 1000 puntos, normalmente 40. Pulsa Guardar cambios. Afecta a zombis nuevos y existentes en aproximadamente un segundo, sin cargar chunks; al cambiar conserva su porcentaje de vida. Los guardados reciben el ajuste al activarse. La vulnerabilidad balística de cabeza es independiente del valor. No cambia la vida de jugadores ni de entidades que no heredan Zombie.

## Disparos TaCZ
- Cabeza/mandíbula: dos impactos aceptados con armas normales; uno con armas clasificadas por TaCZ como sniper o shotgun. Se rompe la cabeza y se procesa la muerte. Los perdigones pueden contar como impactos distintos. Un impacto cancelado o sin daño no cuenta.
- Brazos, piernas y torso: daño global muy reducido y limitado a una fracción de la salud restante. Herida al primer impacto, fractura de extremidad al segundo y pérdida de esa zona al tercero; sniper/shotgun desprenden al segundo. Las etapas usan el sistema de piezas del pack existente; no se añaden modelos voxel de corte arbitrario.
- La detección lateral de brazos abarca una región mayor dentro de la caja de impacto existente. No se han añadido hitboxes animadas individuales fuera de esa caja: una mano extendida fuera de ella sigue teniendo esa limitación.
- Una pierna dañada: velocidad 62%, balanceo asimétrico mientras camina. Dos piernas: arrastre y su geometría previa. Se conserva el sistema anterior de combate melee.
- Se retira el antiguo multiplicador permanente x3 de vida del gore. La vida la controla ahora el panel.

## Sangre y coste
Chorros cortos desde cuello y muñones, usando partículas nativas de Splatter. Pulsan durante un máximo de cinco segundos tras una amputación observada; en decapitaciones terminan al retirarse la entidad muerta. Solo a 32 bloques, máximo 48 partículas de estos chorros por tick entre todos los zombis. No generan entidades físicas nuevas ni mensajes de partículas por cada gota.

Splatter: máximo 192 manchas frente a 512; las viejas ceden sitio a las nuevas. Máximo dos proyecciones por tick, 16 por segundo real y cuatro trabajos pendientes. Distancia de nuevas manchas: 40 bloques. Profundidad de proyección limitada a 2.5 bloques; radio aumentado 15%, con máximo de 1.6. Menos partículas de impacto, hasta ocho por impacto y 32 por tick, con gotas más grandes. Conserva el presupuesto anterior de iluminación. Se prioriza concentración visual y chorros breves, no miles de gotas.

## Verificación y límites
Compilación Java 21, verificación ASM de 15 clases, integridad ZIP y conservación de clases/recursos no modificados. Pruebas con objetos simulados: cabeza a uno/dos impactos, vida elevada, supervivencia a impactos corporales, pérdida de brazo, cojera/arrastre, límites del panel, cola de proyecciones y presupuesto de partículas.

No se ha ejecutado Minecraft aquí: no se ha validado la apariencia con tu TZP ni la compatibilidad real del servidor completo. La detección anatómica está integrada con el evento de impacto de proyectiles TaCZ que ya utiliza este gore; sistemas externos de hitscan que omitan ese evento requieren un adaptador. No incluye cambios de cámaras, First Aid, movimiento ni otros mods.
