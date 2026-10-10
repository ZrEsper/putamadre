# Bloqueo de criaturas — revisión 1

Sustituir solo el JAR de Zomboid Hordes en cliente y servidor por el de este paquete; no dejar versiones duplicadas. Base: rare-mutants-reactive-ai.1. Minecraft 1.21.1, NeoForge 21.1.255+, dependencias originales sin cambios.

Bloquea nuevos creepers, brujas, endermans, esqueletos y sus variantes heredadas (stray, bogged, wither skeleton), caballos zombi y caballos esqueleto. Mantiene el bloqueo de arañas que ya traía Hordes. Las trampas vanilla de caballos esqueleto/jinetes de tormenta quedan bloqueadas al impedir tanto la incorporación del caballo como la de los esqueletos.

El filtro por clase también cubre variantes de mods que hereden estas clases. El log aportado no identifica entidades separadas con el nombre «jinetes del apocalipsis»; se interpreta como los jinetes de caballo esqueleto vanilla. No se bloquean caballos normales, zombis, mutantes ni bandidos.

Se amplía el predicado original que consultan PositionCheck y EntityJoinLevelEvent. Deniega spawns naturales, spawners, huevos, comandos y eventos que incorporen una entidad nueva, en todas las dimensiones. Conserva la excepción original para entidades cargadas desde disco: no borra criaturas que ya estaban guardadas en el mundo. En una trampa previamente guardada, los nuevos esqueletos/caballos generados por el evento también se rechazan.

Mantiene íntegros la IA reactiva a 2/5 bloques, los mutantes raros, su integración de población y todos los demás recursos del parche anterior. Solo modifica el predicado `HordeEvents.blocked`; no cambia tablas de loot ni otros mods.

## Reproducir

Java 21 y Python 3; ECJ/ASM incluidos y verificados con SHA-256. La base publicada en mods/ se comprueba contra base.sha256.json.

```sh
python3 build.py /ruta/zomboid-hordes-1.8.3-neoforge-1.21.1-rare-mutants-reactive-ai.1.jar
```

Validación: 13 casos ejecutando el predicado transformado real sobre doubles de clases de Minecraft; incluye las siete clases bloqueadas, cuatro subclases, entidad ajena y null. Análisis ASM de 45 métodos y confirmación de las dos rutas nativas de eventos; integridad ZIP y comparación de todas las entradas no modificadas. Reconstrucción desde fuentes en directorio limpio. No se ejecutó el modpack/Minecraft aquí.
