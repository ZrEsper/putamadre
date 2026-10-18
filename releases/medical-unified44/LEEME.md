# Salud unificada — First Aid, compat TaCZ y NPCs

Minecraft NeoForge 1.21.1. Cierra juego y servidor, extrae este ZIP y REEMPLAZA estos tres mods, sin duplicados:

- First Aid 3.6.13 -> 3.6.14-unified-medical-care.
- tacz-firstaid-compat 1.0.1 -> 1.0.2-localized-wound-sync.
- TaCZ NPCs tactical.27 -> tactical.28-unified-medical-care.

Instala los mismos tres JAR en clientes y servidor. El compat incluido exige exactamente el First Aid de este paquete para evitar cargar su nuevo registrador anatómico con una versión antigua. Se conservan los demás cambios previos, incluido el alcohol de 15 usos.

## Menú y representación
La pestaña nueva de Salud y las pantallas de tratamiento de otros jugadores/NPCs comparten silueta, dibujos de lesiones, colores y lista desplazable. Se redirige la pantalla anterior de Zomboid Integration al diseño nuevo; no se conserva su cuadrícula de rectángulos como alternativa.

Cada parte muestra todas sus lesiones registradas y el número total de balas alojadas. Hay múltiples marcadores de bala dentro de la parte. En un HUD pequeño, el espacio limita cuántos iconos distintos caben; la lista mantiene el recuento completo hasta el límite existente de 99 por parte. Los marcadores representan la región anatómica; no reconstruyen posiciones exactas de cada entrada de bala que el modelo no haya guardado.

Al vendar, el listado de esa parte muestra SOLO «Vendado», en verde. Se ocultan corte, bala, moretón, fractura y otros detalles cubiertos; al quitar la venda vuelven las lesiones que continúan presentes. Sus datos no se borran al ocultarlas. Las vendas sucias tienen una mancha visual. La venda de cabeza se coloca en la frente, dejando libre la zona de ojos. Tablillas con dos soportes y tres sujeciones; moretones morados y marcadores de proyectil separados.

Colores: vendado/tablilla verde; moretón morado; fractura naranja; bala beige; inflamación y suciedad ámbar; infección rosa; cortes y sangrado rojo.

## Lesiones, higiene y tratamiento
Los golpes contundentes que dañan una parte del jugador registran un moretón en esa parte, guardado y sincronizado. Dura aproximadamente cinco minutos sin tratar y se reduce más rápido con vendaje limpio. Las heridas pueden inflamarse por actividad intensa con vendajes envejecidos o por suciedad. La inflamación es ahora un estado persistente y pausa la recuperación. Desinfectar o aplicar material estéril la trata.

Desde el menú se puede vendar, poner tirita, extraer balas, inmovilizar, quitar vendajes y desinfectar. Para desinfectar, retira antes el vendaje y lleva alcohol médico: consume un uso de sus 15, también al curar a otro jugador o NPC. Se conservan las recetas anteriores.

La vía de Zomboid Integration conserva sus sesiones de paciente, alcance, visibilidad y validación del servidor. Las nuevas acciones de retirada y desinfección usan esas mismas comprobaciones. El menú traduce entre el orden visual de las ocho partes y sus identificadores internos: el torso del jugador no tiene el mismo índice numérico que el torso del NPC.

## NPCs
TaCZ NPCs ahora registra balas, cortes, moretones, vendajes, suciedad, inflamación y fracturas de piernas por impactos fuertes. Sus datos se guardan en los datos persistentes de la entidad y se envían al menú. Los impactos con posición TaCZ disponible usan esa posición para localizar la región; los golpes melee usan la dirección de ataque disponible.

Tratamientos dirigidos: venda/tablilla de tres segundos; extracción de cuatro segundos y una bala cada vez; desinfección de dos segundos. Necesita material, permanecer cerca, visibilidad y mantener el objeto hasta terminar. Se cancela si el curador cae o se aleja. El material se consume al completar. El vendaje limpia el sangrado; con balas todavía alojadas la recuperación del NPC espera a extraerlas. La recuperación de fractura requiere tablilla. Se mantiene la reanimación existente, accesible en el mismo diseño.

Los NPCs heridos ANTES de instalar este cambio solo guardaban vida por parte: no es posible reconstruir cuántas balas o qué golpes antiguos recibieron. Se mostrará la pérdida de salud que exista; los detalles nuevos se registran desde los siguientes impactos.

## Verificación
Compilación Java 21, verificación ASM de 35 clases, integridad de los JAR y conservación byte a byte de recursos y clases ajenos a los cambios. Pruebas con objetos simulados: regiones anatómicas e identificadores de las ocho partes de jugador/NPC, persistencia de moretón/inflamación, recuento sin duplicar impactos, catorce marcadores de bala, ocultación/restauración del listado, colores, venda fuera de los ojos, datos antiguos/estructurados y trabajos de curación del NPC con tiempos, consumo, retirada, alcance y alcohol.

Se revisó una previsualización del dibujo generado por los nuevos renderizadores. No se ejecutó Minecraft: falta comprobar carga de mixins, red y aspecto real con todo el modpack. La conexión opcional a Zomboid Integration está dirigida a sus clases Care/Medical/CareScreen; una versión que cambie esas interfaces requerirá adaptar esa conexión.
