# Loot equilibrado · Hordes .35 y Remains .11

## Instalar

Cierra Minecraft y el servidor. Haz una copia de tu mundo. Quita Hordes .34 (o su versión anterior) y Zombie Remains .10. Copia los DOS JAR de la carpeta `mods` de este ZIP en la carpeta `mods` de tu instancia. Instala los mismos dos reemplazos en clientes y servidor. No dejes dos versiones del mismo mod.

Conserva TacZ NPCs .16, FirstAid 3.5.8, FracturePoint NVG .1 y los demás mods. Se mantiene la corrección del cierre por el ajuste militar de Hordes .34.

Los cambios afectan a cadáveres nuevos. Los cuerpos que ya tenían loot guardado mantienen su inventario. No necesitas borrar el mundo.

## Civiles: una tirada clara, sin varios filtros seguidos

| Objeto | Probabilidad por defecto |
| --- | --- |
| Un arma de fuego O un melee | 7,69% por cuerpo civil: aproximadamente uno de cada 13, en promedio |
| Una pieza de ropa militar | Otra tirada independiente del 7,69% por cuerpo civil |
| Munición junto al arma de fuego | 35% de las armas de fuego generadas llevan una pequeña pila de su calibre |

No significa que cada grupo exacto de 13 contenga necesariamente un arma. Es una probabilidad aleatoria. Se usa la misma probabilidad de aparición en común, poco común, raro, épico, legendario y mítico; la calidad del arma de fuego sigue dependiendo de la rareza del cuerpo y del catálogo de TaCZ.

Una tirada de arma puede dar arma de fuego o melee: por defecto se intenta arma de fuego el 65% de esas veces y melee el 35%. Si falta uno de los catálogos, se intenta el otro. Se usan objetos realmente registrados, no IDs de armas que no existen en tu instalación.

La munición adicional solo se genera con un arma de fuego y coincide con su calibre. Es una pila de 2 a 9 balas según calidad; no otro cargamento de pilas de 64. Las armas aleatorias aparecen vacías: así no hay otra probabilidad oculta de balas dentro del cargador. Los cargadores y balas del equipo real se conservan tal como estaban.

La ropa militar aleatoria se elige entre armaduras registradas de FracturePoint. No genera baterías, gafas NVG ni mochilas como si fueran ropa. Si no existe ninguna pieza compatible registrada, no puede inventarla.

Los límites de suministros pequeños y una herramienta generada por cuerpo se conservan. El equipo que llevaba realmente el muerto se guarda aparte, sin descartarlo por la tirada de loot.

## Militares: todo su equipo, siempre

Zombis militares, juggernauts militares y NPC/bandidos militares conservan las piezas que llevaban, armas en ambas manos y sus datos: daño, munición cargada, accesorios y componentes. No se les aplica la probabilidad de 1/13 para quitarles su propio equipo. Los NPC conservan también toda su bolsa y carga real, igual que antes.

El equipo se captura antes de que Minecraft procese los drops y vacíe las manos. Después se combinan esa captura y los drops reales, sin duplicar las piezas que aparecen en ambos sitios. El perfil de profesión ya no puede borrar ese equipo.

Cada militar recibe además DOS vendas militares. Son vendas utilizables de FirstAid, con el nombre traducido «Venda militar»; tienen el mismo tratamiento que su venda médica, no son un nuevo objeto con curación inventada.

La detección usa la profesión nativa `TacZNpcJob=military`, nombres/tipos y datos de militar/soldado/juggernaut, además del equipo de combate real de FracturePoint. Una profesión militar asignada al azar por el catálogo de cadáveres no convierte por sí sola al zombi en un soldado equipado. Una skin puramente visual sin nombre, datos ni equipo reconocibles no permite deducir con certeza que sea militar.

No se crean armas o armaduras que el militar no llevara: se entrega su equipo real. Tampoco se cambian los spawns, IA, velocidad, daño ni equipamiento inicial de zombis o bandidos.

## Mutantes: restos que tienen sentido

El loot aleatorio de los mutantes deja de usar las profesiones humanas. No se les inventan comida preparada, monedas, minerales ni ropa como premio genérico.

| Mutante | Restos aleatorios |
| --- | --- |
| Cabeza partida, podrido, zombi bruto y mutante bruto | Carne podrida y huesos |
| Rastrero | Carne podrida, huesos y posible hilo |
| Ampollado y escupidor | Carne podrida, huesos, posible bola de slime y ojo de araña |

Son pilas pequeñas de 1 a 3 unidades, con hasta 2-4 entradas según calidad y tipos disponibles. Conservan aparte lo que realmente llevaban o soltaron otros mods. Si además se identifica como militar, conserva su equipo y lleva las vendas. Un mutante sin equipo ya no genera automáticamente un arsenal o ropa militar de la nada.

## Objetos desordenados

Los objetos nuevos se reparten por casillas aleatorias, incluyendo los huecos vacíos, en civiles, militares, mutantes y NPC. No se juntan todos al principio. En NPC se hace también después de reconstruir la entrega pendiente, para que ese guardado intermedio no vuelva a agruparlos.

La mezcla cambia posiciones, no los objetos ni sus cantidades. Se conserva al guardar el cadáver. No se reorganizan continuamente los cuerpos que ya estás saqueando.

## Ajustarlo en el panel

Abre `/zhorda panel`, Loot y NPC, página Escasez y bandidos. Cambia los valores y pulsa Aplicar.

| Control | Significado |
| --- | --- |
| Arma/melee por cuerpo (%) | 7,69 por defecto: una sola tirada por cuerpo civil, sin multiplicar por rareza ni volver a filtrar el arma |
| Ropa militar por cuerpo (%) | 7,69 por defecto: una pieza aleatoria en civiles. No limita el equipo real de los militares |
| Munición junto al arma (%) | 35 por defecto, condicionado a que haya salido un arma de fuego compatible |
| Loot reducido | Mantiene los límites y las tiradas nuevas de civiles. Al desactivarlo, vuelve el perfil anterior para civiles; el equipo real y las reglas de mutantes se conservan |
| Suministros por cuerpo | Mantiene el multiplicador de suministros de .33; no reduce las pertenencias reales ni las vendas garantizadas |

Los controles de equipo inicial de bandidos, munición saqueada y tiempo de recarga siguen funcionando como en .33. No se restablecen al aplicar la migración .35.

Al cargar cada mundo por primera vez con .35 se ajustan solo las probabilidades nuevas de arma, ropa militar y munición, el valor base de ropa militar y el de armas aleatorias cargadas. Se guarda una marca para no sobrescribir tus siguientes cambios. El resto de tu configuración se conserva. Usa los tres controles de esta página para las probabilidades finales de cadáveres civiles: las probabilidades anteriores del generador no se multiplican sobre ellas.

Esta actualización se centra en cadáveres; los cofres y barriles conservan sus reglas anteriores.

## Validación y límites

Se ejecutaron 16.221 comprobaciones de lógica con dobles de las APIs, incluyendo el recorrido de los helpers reales de muerte, equipo capturado, drops, reconstrucción de NPC y colocación aleatoria. En 7.800 cuerpos simulados salieron 596 armas/melees, 583 piezas militares y 142 pilas compatibles para 404 armas de fuego: 35,1% de esas armas.

Se comprueba que solo cambian las clases y textos previstos, se verifica el bytecode y se conserva idéntico el mixin militar corregido de .34. Los demás recursos y las clases de IA/spawn de zombis se conservan. Las pruebas no ejecutan Minecraft ni su bus de eventos real: falta verificarlo en tu instalación, con todos los mods y en multijugador.
