# NPC .24: cobertura, ráfagas y contacto compartido

Compilar con `python npc-tactics24/build.py` y verificar con `python npc-tactics24/test.py` desde la raíz. Java21 y ECJ/ASM ya instalados. Base .23 incluida y fijada por SHA. SmartBrainLib 1.16.11 del usuario puede permanecer: esta implementación conserva los objetivos y navegación nativos, sin instalar un segundo controlador de movimiento ni cambiar la dependencia.

Los perfiles hostiles armados usan decisiones tácticas en VanillaGunGoal. Al recargar examinan ocho posiciones locales a 3–5 bloques, con como máximo cuatro consultas de camino cada 60 ticks. Priorizan una posición alcanzable que bloquee la línea desde los ojos del enemigo a la cabeza agachada. Sin cobertura eligen un punto local alcanzable; si ninguno existe se quedan agachados. No consultan chunks ausentes. Se conserva la recarga militar de reserva ilimitada y se envía una vez su AnimationPacket nativo.

Ráfagas de 4–6 disparos con dos ticks mínimos entre intentos y pausa de 12 ticks entre ráfagas. TaCZ conserva sus restricciones de munición, cerrojo, draw y cadencia física. El ajuste de intervalo ocurre después de TeamBrain.cadence, pues CalmBrain vuelve a escribirlo después de notificar el disparo.

Cada 40 ticks, un contacto visible puede comunicarse a seis hostiles cercanos a16 bloques. Solo los compañeros con línea de visión reciben permiso de inspección completada. Los demás reciben coordenadas antiguas durante seis segundos. Un disparo del jugador alerta hasta12 hostiles a32 bloques, como máximo dos veces por segundo por jugador. No sigue al jugador oculto ni permite disparar a través de paredes. Se mantienen facción, creativo/espectador, derribados y conversación/inventario protegido.

Los puestos alternan avance, movimiento lateral y fuego cubierto según su fase. Se sincronizan sprint, agachado y postura tumbada; el FakePlayer recibe la interpolación tumbada, sin la bandera de nadar, para que TaCZ elija animaciones de suelo. Se reutilizan sus voces de radio/contacto/búsqueda y pasos nativos; no hay voz generada ni un nuevo paquete de sonidos.

Los militares con UUID par cambian el láser por una linterna sostenida. El cambio se aplica también a soldados .22/.23 existentes, una sola vez. Los impares conservan láser. Se añade WarbornGoggleMountLayer a ambos renderizadores internos del NPC, incluidos slim, para mostrar las gafas insertadas en Ratnik. No se modifica Fracture Point.

30 comprobaciones con entidades/mundo simulados: cobertura, fallback, alcance de búsqueda, presupuesto, ráfaga, movimiento, tumbado, coordinación, sonido sin seguimiento invisible, equipo y paquete de recarga. ASM BasicVerifier revisa nueve enganches. No se ejecutó Minecraft/NeoForge con el modpack: las animaciones y el comportamiento táctico aún requieren validación visual en el juego.
