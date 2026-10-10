# Solo zombis normales y mutantes — población y oleadas

Sustituye los dos JAR anteriores, no los dejes junto a estos:

- `zomboid-hordes-1.8.3-neoforge-1.21.1-zombies-mutants-only.2.jar`
- `The-Hordes-1.21.1-1.6.3f-zombies-mutants-only.1.jar`

Instalar las mismas versiones en cliente y servidor y reiniciar. Conservar MutantsZombies y las dependencias originales de The Hordes. No hace falta borrar el mundo ni la configuración antigua de The Hordes.

Ambos generadores seleccionan **99 % zombis normales y 1 % mutantes**: cinco veces la probabilidad anterior del sistema de población. Reparto por selección: Blister 0,25 %; Split Head 0,23 %; Crawler 0,18 %; Rotten 0,18 %; Spitter 0,10 %; Zombie Brute 0,05 %; Mutant Brute 0,01 %. Si falta una especie, The Hordes reasigna su peso al zombi normal; si no está MutantsZombies, usa 100 % zombis normales.

La población conserva el límite de dos mutantes vivos dentro de 128 bloques. Este límite pertenece al generador de población; las oleadas de The Hordes tienen su propio límite de evento y no adoptan ese límite local. Las probabilidades son de selección: terreno inválido, colisiones y límites pueden impedir aparecer.

The Hordes utiliza una lista fija al calcular cualquier tabla de oleada, sin importar día, tabla terrestre/oceánica o tablas antiguas del usuario. No modifica su calendario, tamaño de oleadas, infección ni sus tipos de búsqueda de terreno. La población deja de seleccionar entidades de zombie_variants y conserva sus objetivos por chunk, cooldowns y percepción.

El filtro existente se amplía para bloquear **nuevas apariciones** de husks, ahogados, zombis aldeanos, piglins y piglins brutos (AbstractPiglin), zoglins y hoglins. Mantiene el bloqueo de creepers, esqueletos, arañas, brujas, endermen y caballos zombis/esqueleto. Al impedir los caballos también impide crear esas monturas con jinete. Se aplica a los dos eventos nativos de aparición/join y a todas las dimensiones. No elimina criaturas ya existentes ni modifica bandidos o animales normales. Otros mods con criaturas propias no enumeradas pueden tener sus propios generadores.

Validación: 35 casos del predicado transformado y del helper real de tablas (dobles de API), ASM de 59 métodos modificados/revisados, pesos totales 10000 y reparto por especie, fallback sin mutantes, recursos/clases ajenos conservados byte por byte. No se ejecutó Minecraft: falta la prueba del modpack completo.

Construcción: Java 21, Python 3 y herramientas ECJ/ASM incluidas, verificadas por SHA-256. Ejecutar `python3 build.py bases/horde.jar bases/thehordes.jar`. No se incluyen clases de pruebas en los mods.
