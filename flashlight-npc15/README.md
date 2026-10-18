# Flashlight .15: fuentes NPC

Parte de la implementación .14 conservando shader, capturas, protección Iris, baterías y teclas. `python flashlight-npc15/build.py` y `python flashlight-npc15/test.py` usan el entorno Java21/ECJ/ASM existente. La base .8 permanece fijada por SHA en inputs.json.

Light6 ahora reconoce AutoGunOperator del mundo cliente con una linterna encendida en las manos. No presupone inventario de jugador ni crea bloques. Se origina cerca de la mano (desplazamiento aproximado desde los ojos), gira con la vista del NPC y usa el mismo cono, profundidad y oclusión de terreno del jugador. EntityLight14 acepta estas fuentes para iluminar texturas nativas.

Se conservan como máximo dos fuentes simultáneas: prioridad al jugador local y luego las fuentes más cercanas a menos de 48 bloques. Rayos de terreno limitados a48 por tick y de entidades a24. No carga chunks; NPC muertos, luces apagadas y cambios de mundo invalidan las fuentes. Esto prioriza rendimiento frente a dibujar decenas de linternas simultáneamente.

Las nueve pruebas nuevas verifican el tick/render de fuentes NPC, altura de origen, luz de texturas, retirada, límite y prioridad. Pasan también las regresiones de batería/inventario/sombras/cache/bytecode. Las clases de Minecraft son fixtures, no una ejecución del modpack: no se ha validado visualmente con MakeUp.
