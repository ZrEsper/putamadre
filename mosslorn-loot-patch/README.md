# Mosslorn ampliado — NeoForge / Minecraft 1.21.1

## Qué incluye

Datapack instalable `mosslorn-expanded.1.zip`, con `pack_format:48`, namespace `mosslorn` y carpeta `loot_table` singular. El ZIP original recibido era un fragmento de datos, sin pack.mcmeta ni raíz data; no era directamente instalable.

Se conservan las nueve categorías originales, ahora con tres variantes de cantidad/disponibilidad: **saqueada 30 %, normal 55 %, abastecida 15 %**. Estos son porcentajes de estado, no probabilidades de que el contenedor esté completamente vacío. Cada pool aplica una probabilidad de presencia adicional (20/65/100 %), y los rolls se limitan a 1/2/3 por pool. La variante abastecida no garantiza armas: conserva la rareza de cada pool original.

Se añaden **nueve categorías**: mecánica, farmacia, agricultura, taller, campamento, electrónica, policía, escondite de superviviente y depósito militar. Hay ropa temática de Self Expression, herramientas con durabilidad variable, materiales y objetos útiles de OK Additions (palanca, prismáticos, botiquines fríos, bebidas energéticas, etc.). No añade una nueva mecánica de vehículos, crafting o medicina: son los objetos reales de los mods.

Seis tablas de arma+munición: Glock/9mm, M1911/.45 ACP, M870/12g, AK47/7.62×39, M4A1/5.56×45 y M700/.308. Cada tabla entrega el arma y su munición desde pools separados con los GunId/AmmoId originales. Las cantidades se limitan a 6–12 cartuchos de escopeta u 8–24 de los otros calibres. Las tablas originales mantienen sus pools previos de armas/munición independientes.

Se corrigieron **28 entradas de libros**: StoredEnchantments metido dentro de custom_data no aplica encantamientos en 1.21.1; ahora usan `minecraft:set_enchantments`. Se conserva el loot de musgo y las referencias vanilla de la base, y se omite la carpeta antigua `loot_tables` plural. Total: **79 tablas**, 18 categorías × 3 estados + 18 selectores + 6 combinaciones de armas + mezcla general.

## Instalación del loot

Coloca `mosslorn-expanded.1.zip` en `<mundo>/datapacks/`. Actívalo con prioridad al final si otro pack sobrescribe Mosslorn:

```
/datapack enable "file/mosslorn-expanded.1.zip" last
/reload
/loot give @s loot mosslorn:chests/crate_pharmacy
```

Requiere los mods de las tablas originales: Farmer's Delight, First Aid, Lucky's Wardrobe, Lookin' Sharp, Fracture Point, LesRaisins Tactical Equipments y TaCZ; además Self Expression 2.22a y OK Additions 1.1.0 para las nuevas categorías. El log recibido muestra esos mods cargados. La expansión reutiliza los IDs/GunId/AmmoId del ZIP original, sin inventar nuevos IDs de esas dependencias. Los nuevos IDs se contrastaron con modelos de ítems de los dos JAR recibidos. Eso no sustituye verificar el registro real dentro del juego.

Las estructuras que ya referencian las nueve tablas originales reciben los tres estados. Las categorías nuevas necesitan una estructura que las referencie, usarlas con `/loot`, o la conversión siguiente. No se cambia la distribución de edificios de Lost Cities ni se identifica automáticamente la profesión de un edificio.

## Conversión única de todos los cofres y barriles existentes

La herramienta `convert_world.py` funciona **con Minecraft y el servidor cerrados**. Procesa los archivos de región ya guardados de todas las dimensiones, incluidos chunks que no están cargados. Abarca cofres normales, cofres trampa y barriles vanilla; no convierte almacenes especiales de Sophisticated Storage ni inventarios de otras máquinas.

Primero instala Python 3 y la dependencia:

```
python -m pip install -r requirements.txt
python convert_world.py "C:/ruta/al/mundo"
```

Ese comando solo muestra cuántos contenedores encuentra: no escribe nada. Para convertir una vez:

```
python convert_world.py "C:/ruta/al/mundo" --apply --output "C:/ruta/al/mundo-convertido"
```

La carpeta de salida debe ser nueva. Se copia el mundo completo y **solo se modifica esa copia**: los objetos de cofres y barriles se borran, se asigna LootTable y LootTableSeed, y se instala el datapack en la carpeta datapacks de esa copia. Si el contenedor conservaba una tabla conocida Mosslorn, mantiene su categoría; los demás reciben `mosslorn:chests/world_mix`. El loot se genera al abrirlo. El informe cuenta pilas de objetos sustituidas, no unidades individuales.

La mezcla general es doméstica 28 %, cocina 10 %, baño 8 %, oficina 8 %, libros 5 %, pesca 3 %, frigorífico 5 %, mecánica 7 %, farmacia 6 %, agricultura 5 %, taller 5 %, campamento 3 %, electrónica 3 %, policía 1 %, escondite 2 % y militar 1 %. **Es una mezcla aleatoria, no detección del edificio.**

Tras completar escribe `mosslorn-conversion-once.json` y rechaza volver a convertir esa copia. No es un sistema de reposición: contenedores colocados o chunks generados después no son convertidos por esta herramienta. Abre la copia como tu mundo y comprueba el datapack activo antes de abrir los contenedores.

Soporta regiones gzip, zlib y sin compresión. Chunks externos `.mcc` o con compresión LZ4 se conservan y aparecen como omitidos en el informe; si los hay, la conversión no es completa. El original se conserva incluso si el proceso falla. No uses una salida parcialmente convertida después de un error.

## Comprobaciones y límites

2.809 comprobaciones de referencias, IDs conocidos y formas básicas de JSON; 28 entradas de encantamientos reparadas. Pruebas reales de lectura/escritura NBT y regiones con las tres compresiones, inventarios ajenos conservados, CLI sobre copia, datapack instalado, original intacto y segunda conversión rechazada. **No se ejecutó Minecraft ni se convirtió el mundo del usuario**, que no fue adjuntado. No se afirma validación completa del codec/registro de Minecraft.

El log muestra problemas independientes: compatibilidad opcional de mochilas Sophisticated en taczaddon deshabilitada, textura ausente de OK Additions y repetida creación de modelos de Lucky's Wardrobe que EMF acaba ignorando. Esta ampliación de loot no modifica esos mods ni arregla esos errores.

## Fuentes y otras mejoras posibles

`python build.py` regenera el datapack desde las nueve tablas originales y el catálogo de nuevos IDs. `python test_conversion.py` verifica la conversión con regiones de prueba y necesita nbtlib. Las fuentes incluyen estas entradas y el script; no necesitan descomprimir todos los mods para regenerar el mismo pack.

Para otra revisión serían útiles perfiles por edificio de Lost Cities (hospital/comisaría/ferretería), escondites con cartas y coordenadas, comida caducada si un mod aporta esa mecánica, y material específico para reparar vehículos. Esas mecánicas no están implementadas aquí.
