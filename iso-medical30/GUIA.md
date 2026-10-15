ISOMÉTRICA .30 — CURACIONES Y AUTORREANIMACIÓN

Instalación
Cierra Minecraft. Quita dungeons_iso .28 o .29 y coloca el .30 incluido en los clientes.
No dejes dos versiones de dungeons_iso. Conserva EXACTAMENTE tus mods:
- firstaid-1.21.1-ZOMBOID-v3.6.3-treatment-animations.jar
- LesRaisins-Tactical-Equipements-1.21.1-0.4.3-zomboid-isometric-melee.4-click-combat-hold-mining.jar
No necesitan reemplazarse para esta corrección. Conserva también TaCZ y Tactical.

Curaciones de Les Raisins
Se reconocen mediante su interfaz de consumible, no solo por el nombre del objeto.
Durante el uso médico, el clic de usar no se convierte en una orden de caminar y se
suspenden las correcciones de orientación al cursor. Al seleccionar un objeto médico
se limpia el destino anterior de caminar con clic; no se teletransporta ni cambia la
velocidad o salud del jugador. Al terminar el uso vuelve el control normal.
Se conservan las animaciones y los efectos originales de las curaciones.

Desfibrilador para ti mismo
Estando derribado, lleva un desfibrilador con usos restantes en una mano y mantén
pulsado tu botón de usar. El modo isométrico deja pasar ese botón sin reinterpretarlo.
No añade usos, no reduce el tiempo de reanimación ni modifica las comprobaciones del
servidor. También contempla llevarlo en la mano secundaria con un arma en la principal
mientras estás en estado crítico. No hace que la medicina secundaria robe el apuntado
normal de un jugador que no está derribado.

Se conservan el audio desde el jugador y el bloqueo de interacciones al apuntar de .29.

Pruebas
69 comprobaciones con APIs simuladas. Se reprodujo el fallo con NativeActions de .29:
el botón de usar físicamente pulsado llegaba como false; con .30 llega como true.
Se verificó el bytecode de los cuatro puntos modificados. No se ha ejecutado Minecraft:
falta comprobar en el juego las animaciones, la cámara y la reanimación completa.
Si aún falla, hace falta latest.log de una prueba nueva para revisar el lado servidor.
