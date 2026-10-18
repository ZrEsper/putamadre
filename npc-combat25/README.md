# NPC .25: corregir interrupción de combate

El latest.log del usuario registra `NoSuchMethodException: com.tacz.guns.entity.shooter.ShooterDataHolder.reloadStateType` en tactics24 combat. .24 intentaba invocar ese nombre como método. Es un campo: ahora usa Reflect.field, igual que la ruta original de CalmBrain.

El catch de la decisión táctica devuelve false, permitiendo a VanillaGunGoal continuar con su combate nativo si una incompatibilidad inesperada interrumpe la táctica. Antes devolvía true, consumiendo el tick sin actuar.

`python npc-combat25/build.py` y `python npc-combat25/test.py` desde la raíz reutilizan Java21/ECJ/ASM. Se conserva la base .23 fijada, los nueve enganches, ráfagas, cobertura, posturas, radio, equipo y capa NVG de .24. La fixture de CalmBrain.face ahora reproduce el retorno double del binario real.

37 comprobaciones. La prueba nueva entra en la decisión completa de combate y en la decisión de cobertura de recarga con un holder que tiene un campo público y ningún método reloadStateType. El control negativo ejecuta el JAR .24 publicado y falla precisamente con NoSuchMethodException; .25 pasa. También se verifica que un holder incompatible cede al controlador nativo. La rama de disparo físico TaCZ no se simula en esta prueba; se comprueban sus decisiones y navegación con fixtures.

No se ejecutó Minecraft con el modpack completo. Esta validación corrige el error concreto del log; no equivale a comprobar visualmente todos los movimientos o garantizar TPS.
