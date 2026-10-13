# Valorant + LRArmor en cadáveres y Mosslorn

Archivos:

- `mods/zombie-remains-loot-1.6.1-neoforge-1.21.1-military-nvg-mythic.5-valorant-lrarmor.jar`: sustituye Remains 4 en cliente y servidor.
- `mosslorn-expanded.12-valorant-lrarmor.zip`: sustituye Mosslorn 11 en el mundo y ejecutar `/reload`. No activar ambas versiones juntas.

Mantener instalados los packs de armas/cuchillos Valorant y sus dependencias (TACZ y LRTactical), LRArmor 1.21.1 y GeckoLib 4.9.3. La modificación no integra esos mods dentro del JAR ni cambia sus recursos. Se aplica a cadáveres nuevos y a futuras generaciones/reposiciones de loot; no fuerza un reinicio de cofres o cadáveres existentes.

## Catálogo y distribución

Datos extraídos de los JAR proporcionados: 10 armas (9 Valorant y `crystals:golden`), 6 melees, 62 piezas LRArmor, 5 tipos de munición propios del pack y 4 accesorios. `catalog.json` conserva IDs, capacidad, modo de disparo y munición real de cada arma. Por ejemplo, Vandal usa `tacz:762x39`, Phantom `tacz:556x45` y Golden `crystals:crystal_acp`; no se deduce la munición por el nombre del pack.

Melees: base `lrtactical:melee` con `MeleeWeaponId:"wtyj:..."`, conforme al lector real de IMeleeWeapon. Armas: `tacz:modern_kinetic_gun` con GunId, GunFireMode y GunCurrentAmmoCount. Munición y accesorios usan AmmoId y AttachmentId.

Cadáveres: probabilidades crecientes según calidad 0–5. Classic puede aparecer en común; Operator/Odin desde épico; Golden desde legendario. Las melees comienzan en poco común y las más potentes en raro. Las piezas LRArmor tienen daño reducido en cadáveres de mejor calidad. Todo se inserta preservando componentes y usando el inventario expandible: solo añade una fila si hace falta. Si un mod opcional no está instalado, su categoría se omite.

Mosslorn: 28 tablas nuevas y 27 tablas existentes ampliadas. Militar, policía y refugios reciben armas con munición compatible, accesorios y armadura. Camping/taller reciben melees y ropa apropiada; las casas reciben vestimenta civil y farmacia piezas médicas. El estado saqueado/normal/abastecido mantiene probabilidades distintas. Se conservan los pools originales (incluidas provisiones garantizadas), funciones de escaneo, índices y ritmos de Mosslorn 11.

El filtro de armas potentes de Remains sigue intacto: puede descartar un arma seleccionada por una tabla según sus reglas de rareza. El port no anula esos límites.

## Comprobación

- JAR: compilación Java 21 contra APIs reales de Minecraft/NeoForge 21.1.255, verificación ASM de 365 métodos; solo cambia una entrada nativa, ZombieRemainsLoot.class, para ejecutar el añadido después del loot original. El JAR de entrada coincide byte a byte con el subido por el usuario. Persistencia, GUI e inventario expandible se conservan.
- Datapack: integridad ZIP, 158 referencias validadas, cobertura de todos los IDs del catálogo y preservación de 4074 entradas originales ajenas al cambio. Todas las funciones mcfunction permanecen iguales.
- GameTests nativos en `/workspace/loot-validation`: fábricas e IDs de los 10 guns, índices registrados en TACZ, calibres/capacidad, tablas reales de armas y LRArmor, ampliación de cadáver lleno sin perder el contenido, y IDs de los accesorios leídos por IAttachment. `native-tests/Checks.java` es el test ejecutado; no se integra en el JAR.
- Las tablas se comprueban con getRandomItemsRaw para separar la definición de loot del filtro probabilístico original de Remains. Ese filtro no se desactiva en los archivos publicados.
- LRTactical/FirstAid/The Hordes no se cargaron en el servidor de validación: los 6 IDs melee y su clave NBT se verificaron contra los índices del pack y el lector real de IMeleeWeapon, pero no se probaron animaciones, ataques ni renderizado cliente.

`build.py` reutiliza el SDK real retenido en `/workspace/lrarmor-port/build/moddev/artifacts` y `/workspace/lrarmor-gradle-cache`; `extract_catalog.py` documenta los archivos originales de entrada, y `datapack.py` reproduce el ZIP. No se publican perfiles, logs ni mundos del usuario.
