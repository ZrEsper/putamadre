# Mosslorn v8: loot pendiente hasta abrir

El v7 generaba objetos durante el escaneo. El v8 asigna `LootTable` y deja su generación al abrir el contenedor. El escaneo no consulta inventarios para generar loot y no ejecuta `loot insert`. Los contenedores que ya tienen objetos se conservan durante el juego; para sustituir su contenido por una tabla pendiente está la conversión offline.

## Instalar y convertir

1. Descarga y extrae `Mosslorn-v8-loot-al-abrir.zip` fuera de datapacks. Cierra Minecraft/servidor.
2. Ejecuta `APLICAR-WINDOWS.bat` y selecciona tu mundo actual (la carpeta que contiene `level.dat`). Usa Python y nbtlib 2.0.4; el lanzador instala nbtlib si falta. No usa PowerShell.
3. Se crea una copia nueva con sufijo `-v8-loot-pendiente`. La conversión sustituye los objetos actuales de todos los cofres, cofres trampa y barriles vanilla por loot pendiente; conserva sus nombres y usa las categorías del registro v7, sus tablas anteriores y los nombres reconocibles. También alcanza contenedores puestos por jugadores. No toca el mundo original. No convierte contenedores especiales de otros mods ni entidades.
4. Reemplaza en la carpeta mods de la instancia Quietly v6 por `quietly-1_0_0-cooldown-v7-pending-loot.jar`, y Jade por `Jade-1.21.1-NeoForge-15.10.6-pending-loot.jar`, incluidos en la subcarpeta mods del ZIP. Deja solo una versión de cada mod. En multijugador usa los mismos JAR en cliente/servidor.
5. Abre la nueva copia del mundo. El datapack v8 ya está instalado y habilitado allí. Conserva el mundo original hasta comprobarlo.

Si solo quieres evitar generaciones futuras y conservar inventarios ya llenos, instala el ZIP del datapack en lugar de v7 y los dos JAR; esa opción no convierte los objetos que ya estaban generados en loot pendiente.

## Vista previa

Quietly conserva los cambios del v6 enviado, incluido el contorno y el cooldown. Solo cambia el texto para loot pendiente a «Contenido por revisar», sin inferir categorías.

Jade 15.10.6 ya omitía loot pendiente en el proveedor normal, pero primero consultaba extensiones de otros mods. El parche adelanta esa comprobación al principio de `ItemStorageProvider.putData`, antes de invocar extensiones. Si existe una LootTable, solo envía el indicador de loot pendiente, sin obtener ítems. Cuando la tabla desaparece tras abrir, se conserva el comportamiento normal de Jade. No es un historial personal de apertura: si otro jugador abre el contenedor, su contenido ya está generado y puede mostrarse.

No impide que tolvas, otros mods o comandos ajenos soliciten generar el loot antes de abrir; la corrección cubre el escaneo/reposición Mosslorn y la vista de Jade/Quietly.

## Validación

Siete casos ejecutan el bytecode del guard añadido (pendiente, generado, objeto ajeno, null) y el método de texto parcheado. Se verifica pila/flujo ASM y que las entradas ajenas de ambos JAR no cambian. Se comprueban las referencias del datapack y la ausencia de comandos de generación inmediata. La conversión se prueba sobre una copia del mundo enviado. No se ejecutó Minecraft: falta la prueba real con el modpack.

## Reconstruir los JAR

`python build.py QUIETLY_V6.jar JADE_15.10.6.jar CARPETA_TOOLS`. Requiere Java 21, ECJ y ASM; los hashes de entradas/herramientas se comprueban. Las clases API de prueba no se incluyen en los JAR.

## Corrección 8.1 del log

Corrige los predicados NBT de `loot/scan/registrar` y `loot/validar`: `work {generado:1b}` debe ser `work{generado:1b}`, y `entrada {tabla:...}` debe ser `entrada{tabla:...}`. Si ya convertiste el mundo a loot pendiente, reemplaza el datapack anterior por `mosslorn-expanded.8.1-loot-al-abrir-fix.zip`; no vuelvas a convertir los inventarios. Los JAR siguen siendo los de la revisión pendiente. La corrección se comprobó contra los errores y líneas del log, sin ejecutar Minecraft.
