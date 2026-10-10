# Mosslorn 4: loot por distrito

Datapack completo para Minecraft 1.21.1, formato 48. Basado en la información del creador: norte principalmente residencial y sur principalmente rascacielos. La frontera Z=240 es provisional, sugerida por el espacio entre las concentraciones de contenedores del mapa enviado; no está confirmada por el autor ni por una visita dentro del juego. Puede regenerarse con `python build.py --split-z NUMERO`.

La posición de generación determina el distrito: Z<240 norte y Z>=240 sur. Minecraft norte corresponde a Z decreciente. No identifica edificios individualmente: es una distribución regional de probabilidad, no detección de farmacias u hospitales.

## Comportamiento

- world_mix favorece vivienda/comida/ropa doméstica en el norte y oficina/electrónica/medicina en el sur.
- crate_office conserva sus 11 perfiles, con pesos distintos según distrito: más alimentos y ropa civil en el norte; más papelería, electrónica y medicina en el sur.
- Frigoríficos, cocinas, maleteros y otras tablas específicas conservan su categoría. No se reclasifican por el nombre.
- Los 18 contextos mantienen el pool de un objeto básico garantizado, además del loot adicional con estados de escasez. No repone inventarios ya generados ni impide que un jugador vacíe el contenedor.
- Si la generación carece de origen/posición, se utiliza la mezcla anterior como respaldo.
- No se asignaron gasolineras: el creador estima 3–4, pero todavía no se tienen sus coordenadas. No se inventaron ubicaciones.

## Instalar

Haz una copia de seguridad. Coloca mosslorn-expanded.4-districts.zip en la carpeta datapacks del mundo, sin descomprimirlo. Es un pack completo que sustituye las versiones ampliadas anteriores:

```
/datapack enable "file/mosslorn-expanded.4-districts.zip" last
/reload
```

Deshabilita las versiones anteriores si siguen activas (solo las instaladas):

```
/datapack disable "file/mosslorn-expanded.3-office.zip"
/datapack disable "file/mosslorn-expanded.1.zip"
```

No ejecutes otra conversión. Afecta a LootTable pendiente; no borra ni regenera Items existentes. Prueba `/loot give @s loot mosslorn:chests/crate_office` desde ambos distritos y comprueba que /reload no registra errores. Una única muestra no demuestra los porcentajes.

## Validación y límites

127 tablas, referencias internas válidas, pesos regionales y de estados suman 100. Los 18 contextos conservan el pool sin condiciones de al menos un objeto. Se verifica que los predicados de ubicación abarcan ambos lados del límite y existe respaldo sin posición. ZIP reproducible. No se ejecutó Minecraft; falta comprobar codecs y generación real en el modpack.

Las probabilidades completas figuran en validation.json. Los perfiles de oficina tienen 50 % saqueado, 40 % normal y 10 % abastecido para el loot adicional; sus provisiones mínimas se generan siempre.
