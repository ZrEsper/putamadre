# First Aid 3.6.16 — corrección de la referencia de mordeduras

Cierra Minecraft y servidor. Reemplaza SOLO estos dos JAR en clientes y servidor, retirando los anteriores:

- First Aid `3.6.15-procedural-visible-wounds` → `3.6.16-bite-class-fix`.
- TaCZ First Aid Compat `1.0.3-impact-coordinates` → `1.0.4-firstaid-3.6.16`.

Conserva Gore .31, Ragdollified .24, Corpse .5 y NPCs .31. No dejes dos versiones de First Aid o del compat.

El log de las 11:55 muestra `ClassNotFoundException: ichttt.mods.firstaid.survival.TreatmentV33`. La clase existente está en `ichttt.mods.firstaid.compat.lrtactical.TreatmentV33`. Las heridas visibles buscaban el nombre equivocado al procesar daño de un jugador. Se corrige esa referencia y la de la clase interna `BodypartHitbox$BodypartSlot`, para que las marcas usen los límites corporales de TaCZ y no la ubicación aproximada de respaldo. El compat solo cambia sus metadatos para exigir el First Aid corregido; sus clases se conservan byte a byte.

En First Aid solo cambian Wounds45.class y los metadatos de versión. Se conservan inventarios, guardado, tratamientos, tiempos de recuperación y demás recursos. No se silencian excepciones ni se desactiva el registro de mordeduras.

La prueba usa Wounds45 y TreatmentV33 reales con objetos simulados para las entidades del juego: reproduce la misma clase ausente con la versión anterior y, con la corregida, comprueba tanto una fuente sin zombi como una mordedura aceptada, la región lesionada y la solicitud de sincronización. Se incluyen informe, fuentes y comprobaciones de integridad. No se ejecutó Minecraft con todo el modpack.
