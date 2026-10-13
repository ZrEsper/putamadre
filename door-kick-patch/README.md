# TACZ 12: F para patear puertas en ambas mitades

Reemplaza TACZ 11 interaction-priority por `mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.12-instant-door-kick.jar` en cliente y servidor. Mantener los demás mods: Tactical, TACZ Addon, TACZ NPCs y LR Tactical no necesitan reemplazo para este cambio.

F muestra y ejecuta KICK tanto en la mitad inferior como en la superior, en lugar de OPEN en la superior. Se elimina la supresión especial de la mitad superior por MissionScore; la patada sigue utilizando su validación original del servidor. El objetivo superior se normaliza al bloque inferior en el servidor antes de comprobar alcance y visión. Se admiten puertas vanilla de hierro y cualquier puerta de mod que herede de DoorBlock; mecanismos personalizados que no sean DoorBlock requieren adaptación específica.

La animación y el impacto se ejecutan al procesar el paquete, sin la espera de 5 ticks ni la espera lateral adicional de 7 ticks. El tiempo real depende de red y TPS. Conserva el cooldown de 40 ticks, la probabilidad de éxito configurada, alcance, raycast y restricciones de espectador, cuerpo a tierra e inspección de munición. No garantiza abrir con cada intento. El espejo conserva su acción especial cuando se lleva equipado. Click derecho conserva el comportamiento normal de abrir puertas manuales; una puerta de hierro sigue necesitando redstone para abrirse normalmente.

Construcción: `python door-kick-patch/build.py`; comprobaciones: `python door-kick-patch/test.py`. Usa ECJ 3.39 y ASM 9.7.1 de las herramientas verificadas del entorno. Solo cambia DoorCompat, DoorTarget y ServerKickHandler; el resto del JAR, incluyendo el parche anterior de interacción, sus recursos y mixins, queda idéntico.

Validación: 7 comprobaciones ejecutan métodos extraídos del JAR con dobles mínimos de Minecraft: normalización superior/inferior, bloque sin HALF, puerta, bloque común, null y subclase reforzada. ASM verifica los 7.661 métodos del JAR. No se ejecutó Minecraft con este parche ni se verificó cada mod de puertas existente.
