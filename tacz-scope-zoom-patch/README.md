# TACZ 8: zoom de scopes en cámara normal

Sustituye el TACZ anterior por `mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.8-scope-zoom.jar`. Minecraft 1.21.1 / NeoForge. No instales dos TACZ a la vez.

Se habilita el zoom al apuntar también en tercera persona, conservando la cámara y la mira circular del parche anterior. Primera persona usa el mismo cálculo. No cambia cámara isométrica.

El aumento sale de `IGun.getAimingZoom` para el arma y scope actualmente instalados, incluidos scopes integrados. La selección de aumentos variables sigue usando la tecla «Cambiar aumento» de TACZ (V por defecto; se puede reasignar si entra en conflicto con las luces de vehículos). Las miras fijas usan su aumento fijo. No añade zoom libre con la rueda.

La fórmula óptica divide la tangente de medio FOV por el aumento. El progreso de apuntado se limita a 0..1, evitando que la dinámica de FOV sobrepase el aumento. Al terminar de apuntar, x4 y x25 alcanzan sus valores exactos. El máximo depende de la configuración real del gunpack, no de un nombre traducido: si un pack etiqueta x25 pero configura x8, debe corregirse ese pack.

Conserva todos los demás archivos del TACZ 7: sonidos, niebla, crosshair y golpes sin arma. Solo cambia el método de FOV y añade ScopeZoom.class. No modifica proyectiles ni daño.

Validación: 67 comprobaciones matemáticas y 8 de flujo con dobles de API (scope cambiado, fin de apuntado, exclusión de isométrica/modelo de arma y jugador ausente). ASM verifica 5743 métodos; ZIP íntegro y el resto del JAR conservado. No se ejecutó Minecraft; queda pendiente comprobar la experiencia real al apuntar, especialmente con scopes de gunpacks externos.

Reconstrucción en este entorno: `python tacz-scope-zoom-patch/build.py`, con Java 21, ECJ/ASM en `/workspace/zombies-only-patch/tools` y TACZ 7 en mods.
