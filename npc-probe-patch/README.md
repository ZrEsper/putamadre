# TacZ NPCs tactical.15: NPC de pruebas con partes del cuerpo

Basado en el JAR `tacznpcs-2.1.0-1.21.1-tactical.14-corpse-cargo.jar` exacto enviado. [Guía completa de instalación y uso](GUIA.md).

Añade `/npcprueba` con permisos de administrador, NPC marcado y propietario, copia explícita de equipo, tres modos y preparación de estados. Conserva el pipeline nativo de TaCZ, la física, armadura, recursos, muerte definitiva y corpse/cargo. Solo las entidades marcadas usan el nuevo comportamiento. Las partes se guardan en datos persistentes; la marca se añade al tag de skin sincronizado para identificar el NPC también en cliente.

Modelo propio de ocho zonas con máximos copiados de First Aid cuando se pueden leer, y fallback documentado. El evento LivingDamageEvent.Post aporta el daño final; rayos de melee y posición del proyectil estiman la zona. Cabeza/torso críticos, penalizaciones por extremidades, derribo, reanimación de ocho segundos, tratamientos dirigidos y muerte definitiva. No sustituye el modelo interno Player de First Aid ni garantiza todas las APIs exclusivas de un jugador.

130 comprobaciones con dobles de APIs, ASM BasicVerifier, hashes de entradas/herramientas, CRC, inventario exacto de tres clases modificadas y recursos originales idénticos. **Minecraft no probado**, ni sus APIs de cliente/red/render/recarga/cargo en ejecución real. Las clases originales de corpse/cargo se preservan byte por byte.

`python3 build.py` con Java 21 reconstruye el JAR usando ECJ y ASM con hashes fijos de Maven Central y el archivo base en `base/`. No empaqueta los dobles ni herramientas de prueba. `output/validation.json` registra hashes y límites de validación.
