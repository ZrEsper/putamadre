CÁMARA ISOMÉTRICA .29 — SONIDO E INTERACCIONES

Cierra Minecraft, quita dungeons_iso .28 y coloca el nuevo .29 en los clientes.
Conserva TaCZ, Tactical y los demás mods. No dejes dos dungeons_iso.

Sonido
En isométrica el punto de escucha queda en los ojos interpolados del jugador,
con la orientación de su cabeza, como en primera persona. Mover, alejar o rotar
la cámara no aleja los sonidos del entorno del personaje. La distancia para el
retardo de los sonidos utiliza ese mismo punto. No necesitas cambiar soundListenerBias.
Fuera de isométrica se conserva la actualización normal del motor de sonido.

Con un arma TaCZ
- Clic derecho: apuntar, aunque estés mirando una puerta o un contenedor.
- Mientras apuntas: se bloquean las interacciones normales con bloques y entidades.
- Suelta el clic derecho: dejas de apuntar y vuelven las interacciones.
- Para abrir algo con el arma en la mano: deja de apuntar y usa Mayús + clic derecho.
El bloqueo de interacciones normales durante el apuntado también cubre las otras
cámaras. No cancela el uso del arma ni sus disparos. Inventario y menús propios de
otros mods no se bloquean globalmente.

Pruebas
380 comprobaciones con APIs simuladas y verificación del bytecode: posición y
orientación, ejecución en el hilo de sonido, salvaguardas de inicio, apuntado,
liberación y entrada alternativa. Se conserva el resto de .28, incluida la estabilidad
del cursor médico. Falta probar sonido e interacciones dentro de Minecraft.
