# Mosslorn ampliado 3: armarios variados y provisiones garantizadas

Datapack completo para Minecraft 1.21.1 (formato 48). Añade 11 perfiles a la tabla crate_office que utilizan 18.699 Office Cabinet pendientes en el mapa analizado; el total de contenedores con ese nombre es 18.707 y 8 usan world_mix. No reclasifica nombres ni modifica el mundo.

Perfiles de oficina: papelería 28 %, medicina 15 %, electrónica 10 %, mantenimiento 10 %, ropa civil 8 %, alimentos 8 %, investigación 6 %, campamento 5 %, biblioteca 4 %, jardinería 3 %, emergencia 3 %. Se elige un perfil por generación. Los medicamentos son objetos existentes de First Aid y OK Additions; las demás dependencias se mantienen respecto al pack anterior.

Los 18 selectores de categoría garantizan un objeto básico (pan, manzana, zanahoria, patata, papel o hilo) mediante un pool independiente sin condiciones. world_mix llama a esos selectores, por lo que también garantiza contenido. Las variantes de loot adicional de oficina son saqueado 50 %, normal 40 %, abastecido 10 %, con posibilidades de bonus 20/65/100 % y 1/1–2/1–3 rolls. Los otros perfiles mantienen sus variantes originales y suman las provisiones garantizadas. No añade armas a los nuevos perfiles de oficina.

## Instalar

Con una copia de seguridad, coloca mosslorn-expanded.3-office.zip en la carpeta datapacks del mundo. El pack es completo y sustituye la versión ampliada anterior. Después activa su prioridad al final:

```
/datapack enable "file/mosslorn-expanded.3-office.zip" last
/reload
/loot give @s loot mosslorn:chests/crate_office
```

Se puede deshabilitar el pack ampliado anterior con `/datapack disable "file/mosslorn-expanded.1.zip"`. No hace falta volver a convertir el mundo: afecta a los contenedores que aún tengan LootTable. No rellena contenedores cuyo inventario ya fue generado ni repone lo que un jugador saque. La garantía corresponde a las categorías de este pack, no a cualquier cofre vanilla, vagoneta o tabla de otro mod.

## Validación

115 tablas, referencias internas e IDs modded reutilizados del pack anterior, pesos de perfiles y estados suman 100. Se comprueba que los 18 pools garantizados tienen al menos un roll y entradas vanilla sin condiciones. Las variantes base se conservan byte por byte. No se ejecutó Minecraft; revisar registro de datapacks y probar generación dentro del modpack.

Regenerar: `python build.py`, requiere el ZIP base en ../mosslorn-loot-patch/mosslorn-expanded.1.zip. validation.json registra el SHA256 y comprobaciones.
