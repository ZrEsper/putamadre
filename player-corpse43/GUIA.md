JUGADOR INFECTADO → ZOMBI → CADÁVER CORPSE

Instalación
1. Cierra Minecraft y el servidor.
2. Reemplaza Hordes .42 por .43, Remains .11 por .12 y Corpse .1 por .2.
3. El ZIP también incluye Isométrica .30: reemplaza dungeons_iso .28 o .29 en los clientes.
4. No dejes dos versiones del mismo mod. Instala los tres primeros reemplazos tanto
   en servidor como en clientes; Isométrica se actualiza en los clientes.
5. Conserva Cosmetic Armor «functional.2», FirstAid 3.6.3, Les Raisins .4, The Hordes,
   TaCZ y Tactical que ya utilizas.

Qué ocurre al morir infectado
La conversión de The Hordes sigue creando el zombi del jugador. Se registra su identidad
y una copia de referencia de lo que llevaba puesto. El zombi muestra sus armas y, cuando
hay una pieza cosmética en esa parte, muestra la cosmética por encima de la normal.
Las piezas normales y cosméticas siguen formando parte del inventario recuperable.
Las copias usadas para mostrar el equipo no se añaden otra vez como botín.

Al matar ese zombi
Aparece una entidad tumbada del mod Corpse en el lugar donde murió el zombi, con la
identidad del jugador y su inventario guardado, incluidos los componentes de los objetos
(munición, accesorios y datos internos). No se convierte en un bloque de Remains.
El almacenamiento y los controles de acceso son los del propio Corpse.

Armadura cosmética
Se traslada la cosmética que corresponde a esa muerte; no se quitan piezas nuevas que
el jugador haya equipado después de reaparecer. Si la cosmética ya entró en los drops
originales, se conserva una vez y no se añade otra copia.
Si keepInventory o «conservar cosmética al morir» están activados, se respetan: esas
piezas se quedan con el jugador en lugar de duplicarse en el cadáver.

Si no puede crearse el Corpse
Se conservan los drops nativos, incluidos los cosméticos que se hayan trasladado en
ese mismo instante. El registro escribe el motivo bajo «PlayerCorpse43».
Los zombis de jugadores anteriores al parche pueden convertir el inventario que ya
tenían guardado; no se pueden reconstruir piezas que se perdieron antes de instalarlo.

Guardar y cargar
Corpse .2 conserva los huecos vacíos del equipo al guardar. Así un casco no pasa al
hueco de la mano al volver a cargar el cadáver.

Curaciones y desfibrilador
Isométrica .30 separa el botón de usar medicina de la navegación, reconoce los consumibles
de Les Raisins y evita el control al cursor durante el tratamiento. El desfibrilador
recibe el botón correcto para autorreanimarte. La infección terminal puede aplicar daño
letal e interrumpir la reanimación: el desfibrilador no sustituye la inyección de cura.
Consulta la guía de Isométrica incluida.

Validación
149 comprobaciones sobre transferencias y 64 combinaciones de equipo/carga; incluyen
muerte inmediata, cosmética ya entregada, fallo de creación, repetición del evento,
keepInventory y preservación de componentes. Dos procesos con el serializador real de
Corpse reproducen y corrigen el casco desplazado (hueco 0 antes, hueco 5 después).
El cambio de controles médicos pasó 69 comprobaciones y la comparación del botón antes/
después. Son pruebas con APIs simuladas; Minecraft, multijugador y renderizado real
siguen pendientes de prueba en el juego.
