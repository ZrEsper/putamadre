# Guía fácil del panel de hordas

## Instalar esta actualización

Cierra Minecraft y detén el servidor. Sustituye **Zomboid Hordes .28** por **Hordes .29** y **Global Melee v12** por **Global Melee v13**, en cliente y servidor. Deja un solo JAR de cada mod en `mods`; conserva los demás mods del pack.

Entra como administrador y escribe `/zhorda panel`. Pasa el ratón sobre cualquier control para leer su explicación. Editar un número no lo guarda: pulsa **Aplicar**. **Cerrar** sale del panel. **Cambiar preset** prepara valores, pero también hay que aplicarlos. **Ver ejemplo** simula loot: no entrega los objetos ni garantiza que te tocarán.

## Cómo leer los números

**1x es el valor original; 0.5x es la mitad; 2x es el doble.** En cooldowns se multiplica el tiempo de espera: **0.5x permite atacar antes; 2x obliga a esperar más**.

Un porcentaje es una posibilidad, no una cuota exacta. Con 20 %, de muchos intentos aproximadamente una quinta parte pasa el sorteo. Puede haber rachas. Cuando los números son **pesos**, se reparten proporcionalmente: 8/1.5/0.5 funciona igual que 80/15/5.

**-1 significa usar el comportamiento original**, únicamente en controles que lo permiten. No pongas -1 en cualquier casilla.

## 1. Dificultad

| Control | Explicación sencilla |
|---|---|
| Zombis agresivos | Cuántos reciben el perfil extra rápido y fuerte. 0 = ninguno; 100 = todos; -1 = original. Es distinto de la insistencia de su IA. |
| Frecuencia de aparición | Qué rápido se reponen zombis y llegan oleadas. Menos = más lento; más = más frecuente. |
| Población por chunk | Cantidad objetivo en cada área de 16×16 bloques. Cambia la cantidad, no el daño. Respeta las reservas y el límite por zona. |
| Mutantes | Porcentaje usado por los selectores compatibles de hordas. No controla generadores independientes de otros mods. |
| Máximo de mutantes | Tope del selector de Zomboid Hordes. No elimina los que ya existen. |
| Visión | Distancia a la que pueden verte. 0 = sin visión; -1 = visión original. Las paredes siguen contando. |
| Asediadores | Porcentaje de nuevos zombis con el comportamiento de asedio. Admite hasta 1 %; -1 conserva el comportamiento original. No es un multiplicador de daño. |
| Todos rompen bloques | Da permiso de romper a los zombis compatibles. Respeta `mobGriefing` y las protecciones originales de inventarios. |
| Todos construyen | Da permiso para su construcción limitada. También respeta `mobGriefing`. |

## 2. Spawn e IA

| Control | Para qué sirve |
|---|---|
| Tontos: peso | Zombis de reacción lenta y comportamiento torpe. 0 evita que aparezcan nuevos de este tipo. |
| Brutos: peso | Personalidad de reacción intermedia. **No es la especie mutant_brute**; una cosa es la personalidad y otra la especie. |
| Astutos: peso | Personalidad que detecta y reacciona mejor, usando la IA inteligente que ya traía el mod. |
| Inteligencia / reacción | Más alto = detectan más rápido y recuerdan más tiempo. Se combina con las diferencias de cada personalidad. |
| Agresividad de la IA | Más alto = atacan más a menudo y persisten más al buscarte. **No aumenta el daño de cada golpe**. |
| Oído | Más alto = escuchan desde más lejos. 0 = sordos. Respeta el alcance máximo de cada clase de ruido. |
| Velocidad de aparición | El mismo ajuste de frecuencia de Dificultad, reunido aquí por comodidad. |
| Cantidad de zombis | El mismo ajuste de población de Dificultad. |
| Zombis enfurecidos | El mismo perfil extra rápido/fuerte explicado arriba. |
| Distancia de visión | El mismo ajuste de visión explicado arriba. |

**Ejemplo fácil:** tontos 80, brutos 15, astutos 5. El panel muestra los porcentajes reales mientras editas. Puedes excluir uno con 0, pero al menos uno debe tener peso positivo.

Los pesos solo eligen la personalidad de zombis **nuevos**. Los que ya están en el mundo no cambian de tipo al editar los pesos. Oído, reacción y agresividad sí se ajustan en los existentes.

El botón de presets recorre **Fácil → Equilibrado → Difícil**. Fácil prepara 80/15/5, reduce reacción/agresividad/oído a 0.65x y aparición/cantidad a 0.7x. Equilibrado prepara 45/40/15 y 1x. Difícil prepara 15/35/50, aumenta esos valores y activa el perfil enfurecido en 20 %. No regenera población ni cambia loot por sí solo.

## 3. Velocidad y daño

Hay un control de velocidad y otro de daño para **cada tipo: tontos, brutos y astutos**.

**Velocidad:** 0 = quieto; 0.5x = la mitad; 1x = original; 2x = el doble. Puedes poner de 0 a 3x. Se combina con el ritmo individual y los efectos originales del zombi.

**Daño:** 0 = sin daño de ese ataque cuerpo a cuerpo; 0.5x = mitad; 1x = original; 2x = doble. Puedes poner de 0 a 5x. No cambia su salud. Ataques especiales o proyectiles de otros mods que no usan el atributo de ataque mantienen sus propias reglas.

Ejemplo: brutos con velocidad 0.7x y daño 1.8x serán más lentos pero pegarán fuerte. Astutos con velocidad 1.3x y daño 0.8x serán más rápidos pero menos dañinos.

Los atributos cambian también en zombis existentes y pueden tardar alrededor de un segundo en actualizarse. Los presets de esta pestaña solo cambian estas seis casillas.

## 4. Población y spawn dentro de casas

| Control | Qué ocurre |
|---|---|
| Máximo por zona | Tope de zombis activos en una zona fija de 6×6 chunks. Los demás esperan en reserva. El valor normal es 32. |
| Retirar todos los zombis | **Al aplicar**, retira la población controlada y pausa nuevas apariciones. No da loot. |
| Regenerar población | **Al aplicar**, retira la población actual y solicita nuevos spawns con tus ajustes. Reactiva las apariciones. |
| Eliminar cadáveres y huesos | **Al aplicar**, elimina los restos compatibles y sus inventarios. Lo pendiente en áreas descargadas se procesa al cargarlas. |
| Reiniciar loot de cofres | **Al aplicar**, reinicia los cofres/barriles registrados por Mosslorn. El loot nuevo se genera al abrir. |
| Aparición y reposición | Activa deja aparecer y reponer; Pausada detiene nuevos spawns, pero los existentes siguen vivos y activos. |
| Spawn dentro de casas | Interruptor permanente. Desactivado conserva el spawn normal. Activado permite el reemplazo interior explicado abajo. |
| Reemplazo interior (%) | Posibilidad de intentar trasladar un spawn cercano a otro cuarto de la casa. 0 = nunca; 100 = siempre intenta, si cumple las comprobaciones. |

**Las acciones de retirar, regenerar, limpiar restos o reiniciar cofres pueden borrar población u objetos. No las actives si solo quieres cambiar una probabilidad.** Se ejecutan una vez al aplicar; el interruptor de spawn interior permanece activado hasta que lo apagues.

### Cómo funciona el spawn interior

Empieza **desactivado**, con probabilidad preparada de 20 %. Para probarlo, activa el interruptor, elige el porcentaje y pulsa Aplicar.

Se toma un zombi nuevo que el sistema ya iba a añadir cerca de un jugador de supervivencia, hasta 96 bloques en horizontal. Puede trasladarse al interior en lugar de aparecer afuera: **no crea un zombi extra** ni mueve zombis que ya existen.

El sistema busca suelo válido, techo de construcción y habitaciones conectadas por puertas, en la misma planta. Una puerta separa cuartos aunque esté abierta. No elige el cuarto del jugador, una puerta, agua, un lugar que choque con el cuerpo del zombi, ni un punto a menos de 6 bloques de los jugadores. También comprueba la línea de visión y conserva la zona original de población.

El zombi aparece **sin objetivo asignado y sin la posición del jugador puesta como pista del spawn**. Después puede oír tus pasos, disparos y acciones mediante el sistema normal de ruidos; también puede detectarte normalmente si te ve.

Si la casa es demasiado pequeña, no hay otro cuarto, todo es una sala abierta, el lugar es inseguro o no se reconoce la construcción, queda el spawn exterior. Se buscan áreas cargadas y se limita el trabajo por tick. Por eso **100 % no garantiza que todos acaben dentro**.

La detección usa techo, suelo y puertas, no un nombre de estructura guardado por Minecraft. Es conservadora: no garantiza reconocer cualquier edificio de cualquier mod, plantas conectadas solo por escaleras, techos de piedra natural ni cuartos separados únicamente por arcos abiertos. No fuerza la carga de chunks.

## 5. Cooldowns melee

Se regulan los ataques de Global Melee y su puente con LR Tactical. **Cooldown significa cuánto debes esperar entre ataques**. No es daño, alcance ni gasto de stamina.

| Control | A qué afecta |
|---|---|
| Ataque normal | Multiplica la espera del golpe normal. 1x conserva el ajuste original del mod, incluida su reducción original de espera. |
| Ataque pesado | Multiplica la espera del golpe pesado. Es independiente del normal. |
| Empujón | Multiplica la espera entre empujones. 1x conserva 15 ticks, aproximadamente 0.75 segundos. No cambia el aturdimiento. |
| Espadas | Multiplicador extra para armas reconocidas como espada. |
| Hachas | Multiplicador extra para hachas; no incluye picos. |
| Sartenes | Multiplicador extra para sartenes compatibles. |
| Otras armas | Bates, cuchillos, herramientas, puños y otros melee. Las armas LR Tactical que usan un ítem genérico entran aquí. |

Se puede ajustar entre **0.25x y 4x**. El tipo de ataque y la familia se multiplican: normal 0.5x y espadas 2x dejan el tiempo final de una espada igual al original. Normal 1x y espadas 0.5x hacen esperar la mitad para esa familia. El mínimo efectivo es un tick para cooldowns.

Los presets recorren **Rápido → Original → Lento**. Preparan 0.75x / 1x / 1.5x para normal, pesado y empujón, y 1x para las familias. No cambian daño, alcance ni stamina.

Los ajustes se envían a **todos los jugadores**, no solo a quien abrió el panel, al entrar y después de guardar. Pueden tardar hasta alrededor de un segundo en llegar. Cambian el cooldown y la preparación de ataques LR de forma coherente; las animaciones y el empujón corporal originales se conservan.

## 6. Loot y NPC

| Control | Explicación |
|---|---|
| Aparición de bandidos | Cuántos candidatos del generador TacZ NPCs se dejan aparecer. 0 = ninguno; 100 = todos los candidatos permitidos por sus propios límites. |
| Zombis militares | Porcentaje de nuevos zombis con ropa militar del pack. Necesita la integración compatible. |
| Bandidos militares | Porcentaje de nuevos NPC con atuendo militar. Conserva sus profesiones. |
| Épico, legendario y mítico | Probabilidades base de rareza en los sistemas compatibles. Si suman más de 100, se reparten proporcionalmente. Mítico es la categoría superior con suministros extra. |
| Visión nocturna: drop | Posibilidad de unas gafas compatibles en el cadáver, si existen y cabe el objeto. **No es la visión nocturna del zombi**. |
| Ropa militar: drop | Posibilidad de una pieza militar extra. No quita la ropa que ya llevaba el muerto. |
| Armas con accesorios | Porcentaje de armas nuevas generadas por Zombie Remains con accesorios compatibles. No cambia armas existentes de jugadores o NPC. |
| Armas con munición | Porcentaje de armas nuevas con algo de munición en el cargador. Las demás salen vacías. |

## 7. Cofres, drops y cadáveres

Son **tres configuraciones separadas**. Cofres/barriles cambia contenido nuevo de esos recipientes compatibles. **Drops** son objetos que aparecen en el suelo al morir una entidad compatible. **Cadáveres** es el loot integrado en los sistemas de cadáveres compatibles.

**Perfil Original** conserva las tablas del pack. **Personalizado** reemplaza el loot nuevo con la mezcla de esa pestaña. No modifica objetos que ya se generaron.

**Cantidad de selecciones** indica cuántos sorteos se hacen respecto al contenido de referencia: 0 = nada; 1 = normal; 2 = aproximadamente el doble, limitado por el espacio disponible. Una selección puede dar una pila de varias unidades, según la tabla del objeto.

### Qué contiene cada categoría

| Categoría | Qué significa y ejemplos |
|---|---|
| Armas | Objetos para combatir: armas TaCZ, cuchillos, cuchillas, sartenes y lanzables LR compatibles. Una granada completa es un arma, no una bala. |
| Medicinas | Objetos del sistema de curación: vendas, apósitos, tela, analgésicos, férulas e inyectores de First Aid; botiquines y consumibles médicos LR, compresas frías y sales compatibles. Su efecto lo decide el mod de cada objeto. |
| Comida | Alimentos y bebidas: verduras, carne, bocadillos, guisos, platos preparados y leche condensada. No todos vienen cocinados y no todos curan lesiones. |
| Herramientas | Equipo para trabajar o sobrevivir: hachas, picos, palas, azadas, cubos, cañas, tijeras, mechero, reloj, brújula, mapas, prismáticos, barcos, luz y equipo de campamento. |
| Ropa y armaduras | Uniformes, prendas, zapatos, botas, cascos y armaduras. Puede incluir mochilas o protección compatible del catálogo. |
| Munición | Balas y cartuchos. Cada calibre debe ser compatible con el arma; no entrega el arma completa. |
| Accesorios | Piezas para armas: miras, silenciadores, empuñaduras, compensadores y cargadores. No todas las armas aceptan todas las piezas. |
| Materiales | Ingredientes y suministros: lingotes, pólvora, piezas, semillas, componentes de fabricación, libros y otros materiales del catálogo. |

El número de cada categoría es **su peso en el sorteo**. 0 la excluye. Los pesos se normalizan automáticamente y no tienen que sumar 100.

Ejemplo: armas 10, medicinas 30, comida 60 y el resto 0 da aproximadamente 10 % / 30 % / 60 % de las selecciones a esas categorías. Dentro de cada categoría se vuelve a sortear un objeto. **30 % de medicinas no garantiza una venda en cada zombi**.

Si todas las categorías están en cero, no se selecciona loot. **Ver ejemplo** ayuda a comprobar la mezcla sin cambiar cofres reales. Los objetos de mods solo funcionan si esos mods y sus datos están instalados.

### Qué se corrigió y añadió

Se recolocaron entradas que estaban mal clasificadas: calzado a Ropa; comidas y bebidas a Comida; kits médicos, compresas y sales a Medicinas; lanzables a Armas; objetos de trabajo y supervivencia a Herramientas. Se conservaron sus datos, cantidades y variantes originales.

Se añadieron una **cama blanca** y un **pico de piedra** a Herramientas, para completar el equipo básico. No se añadió una novena categoría porque las ocho existentes cubren estos objetos. Estos cambios se usan al activar el perfil Personalizado correspondiente.

## 8. Limpieza

| Control | Explicación |
|---|---|
| Limpieza automática | Retira objetos tirados en el suelo. No vacía cofres ni inventarios de jugadores. |
| Mínimo y máximo entre limpiezas | Sortea el tiempo de espera entre ambos valores, en minutos. El máximo no puede ser menor que el mínimo. |
| Tiempo mínimo en suelo | Protege objetos recientes. Con 1 minuto, un objeto recién soltado no se borra inmediatamente. |
| Avisar en chat | Muestra el aviso previo y el resultado de la limpieza. |
| Antelación del aviso | Cuántos segundos antes aparece el aviso. Solo cuenta si activaste Avisar en chat. |
| Limpiar objetos ahora | Ejecuta una limpieza al aplicar, respetando el tiempo mínimo y el aviso. No queda repitiéndose por este botón. |
| Mensaje del aviso | Texto que aparece en chat. `{segundos}` se reemplaza por la cuenta atrás. Vacío usa el aviso original. |

## Guardar y comprobar

Los controles se guardan por mundo en `serverconfig/zomboid-hordes-controls.properties`. Solo un administrador puede aplicarlos. Si otra persona los cambia mientras tienes abierto el panel, el servidor rechaza el borrador viejo y refresca los valores para que no borres sus cambios.

Para empezar, usa Original o Equilibrado y cambia **una cosa cada vez**. Prueba primero cooldowns 0.5x y 2x. Luego activa interiores al 100 % en una casa con dos cuartos separados por puerta; si no cumple las condiciones, seguirá apareciendo afuera. Al terminar, elige el porcentaje que prefieras.

Se aprobaron 99 comprobaciones de lógica con dobles de las APIs y las comprobaciones de integridad de los JAR. **No se ejecutó Minecraft en este entorno**: falta comprobar el panel, las animaciones, la red real y la detección de tus edificios dentro del juego.
