MARCAS DE BALA — 3 MINUTOS

Cierra Minecraft. Quita Tactical «tracers-impacts-scope-limits.1» de mods y coloca
«tacz-tactical-1.2.4-neoforge-1.21.1-impact-expiry-180s.2.jar». No dupliques Tactical.
Actualiza los clientes. Si el servidor tiene Tactical instalado, reemplázalo allí también.
Conserva TaCZ .18 y los demás mods.

Cada marca de impacto caduca a los 180 segundos desde su creación (3.600 ticks del
cliente, a 20 ticks por segundo). También se elimina su registro de restauración:
volver a una zona descargada no la hace reaparecer. Si la partícula se recrea antes
de caducar, conserva el plazo original. No cambia las trazadoras de las balas en vuelo,
las texturas por munición, los límites de zoom ni el daño.

El límite de 2.048 marcas y la limpieza al cambiar un bloque se mantienen; por esos
motivos una marca puede desaparecer antes. El desvanecimiento visual usa la opción
original de TaCZ, aplicado ahora sobre una vida máxima de 3 minutos.

3.607 comprobaciones aprobadas con el registro y constructor reales de Tactical y
partículas simuladas. Bytecode verificado. Pendiente de prueba visual dentro de Minecraft.
