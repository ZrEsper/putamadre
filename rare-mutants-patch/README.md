# Mutantes muy raros en la población de hordas — revisión 1

Minecraft 1.21.1, NeoForge 21.1.255+. Usa exactamente los dos JAR aportados por el usuario, incluido `living-population-no-distant-sound`; mantiene desactivados los sonidos distantes. Mantiene los recursos, autores y licencias originales.

## Instalación

Instalar en cliente y servidor. Hacer copia del mundo. Sustituir las versiones anteriores de Zomboid Hordes, MutantsZombies y Zombie Remains Loot por las tres de este paquete; **no dejar dos JAR del mismo mod**. El ZIP también incluye Saros y Mob Dismemberment death-compat.4 sin cambios para acompañar al nuevo Remains: sustituir sus versiones anteriores si corresponde. Conservar las dependencias del modpack: SoundAttract 6.3.8d y Doomsday Decoration 1.1.4.2+, entre otras declaradas por los originales.

El número de versión interno se conserva para cumplir las dependencias exactas entre los puentes de cadáveres. La revisión se identifica en el nombre del JAR y `META-INF/rare-mutants-patch.json`. El ZIP es un paquete de modificaciones, no un modpack completo.

## Aparición

No hay un segundo temporizador ni spawns añadidos encima de la población existente. Cada elección del generador de población tiene 0,2% de probabilidad de intentar un mutante, antes de comprobar su colisión real. Respeta chunks cargados, distancia mínima de ocho bloques a jugadores, terreno, borde, espacio libre, cupos y enfriamientos persistentes de la horda. Consume el mismo espacio que un zombi. Los mutantes se cuentan en la población como ya hacía el original.

Como máximo dos mutantes vivos de estas siete especies dentro de 128 bloques horizontales del punto candidato. Los mutantes creados con huevos/comandos también cuentan, aunque la restricción no impide esos spawns manuales. Se eliminan los siete modificadores de biome spawn del JAR MutantsZombies para que no aparezcan adicionalmente por el sistema vanilla. Se conservan huevos, IA, modelos y habilidades.

| Mutante | Peso entre elecciones de mutante | Suministros por muerte, sin bonus |
|---|---:|---:|
| Crawler | 18% | 3–6: cuerda, pedernal, pan |
| Blister Zombie | 25% | 6–13: pólvora, pepitas de hierro, comida |
| Split Head Zombie | 23% | 9–15: flechas, cuero, pan |
| Rotten Mutant | 18% | 12–20: hierro, carbón, carne |
| Spitter | 10% | 18–28: slime, redstone, comida |
| Zombie Brute | 5% | 26–38: hierro, pólvora, carne |
| Mutant Brute | 1% | 40–58: hierro, pólvora, carne |

Son probabilidades por intento, no encuentros garantizados: espacio, límites de población y mutantes ya presentes pueden reducirlas. Los brutos grandes necesitan espacio real para su caja de colisión. La horda conserva su perfil individual, pero no intenta aplicarles la función de atributos exclusiva de `Zombie`: los mutantes heredan de `Monster`.

## Botín y cadáveres

Siete tablas `loot_table/entities` diferentes. Las recompensas escalan según salud, armadura y peligrosidad nativas: Crawler es el más débil y Mutant Brute el más fuerte. Cada especie tiene un bonus aleatorio diferente cuando la mata un jugador: herramienta básica, casco, arco, pico, manzana dorada, hacha de diamante o pechera de diamante (no se garantiza). No se altera la fuerza ni se crean armas TACZ con componentes inválidos.

Zombie Remains death-compat.4 ya recogía los drops reales de mutantes, pero añadía el mismo gran premio aleatorio a todos. Esta revisión omite ese premio y los extras genéricos **solo para estas siete especies**, conserva las tablas reales en el inventario del cadáver y fija su calidad por especie. Los bandidos, zombis normales y otras entidades mantienen su ruta previa. La animación, posición final del ragdoll, caída sobre agua, desmembramiento y protocolo de red de death-compat.4 se conservan byte por byte fuera del método de drops modificado.

Los drops siguen las reglas de Minecraft (`doMobLoot`, modificadores externos y condición de muerte por jugador para el bonus). No se cambia el loot de cadáveres que ya estaban guardados.

## Reproducir y comprobar

Java 21 y Python 3. Herramientas ECJ/ASM incluidas y verificadas con SHA-256. Desde la carpeta descomprimida de fuentes:

```sh
python3 build.py /ruta/hordes-original-subido.jar /ruta/MutantsZombies-original-subido.jar /ruta/zombie-remains-death-compat.4.jar
```

Las bases se verifican contra `bases.sha256.json`. La tercera base es el Remains death-compat.4 publicado anteriormente en `mods/` del repositorio. Las fuentes, doubles de API y pruebas no se incluyen en los JAR de juego.

Comprobaciones ejecutadas: 28 casos de los helpers de producción con doubles de API (rareza, pesos, límite cercano, muertos/distantes, registro ausente, atributos de Zombie, calidad y omisión de extras); análisis ASM de 69 métodos originales/modificados; siete perfiles de loot con cantidades crecientes; integridad ZIP y comparación byte por byte de cada clase/recurso no modificado; reproducción en directorio limpio.

No se ha ejecutado Minecraft ni el modpack completo aquí. Prueba recomendada en una copia del mundo: revisar espacio y cupos de la población, matar con jugador cada especie y abrir su cadáver; comprobar que Crawler deja menos loot y Mutant Brute más, así como agua y carga/descarga de chunks. Los spawns nuevos son deliberadamente muy raros; no evaluar la frecuencia contando unos pocos zombis.
