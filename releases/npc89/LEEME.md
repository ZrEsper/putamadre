# tactical.55 — servicios nativos Doomsday

Reemplaza únicamente tactical.54 (o tactical.53 si aún no instalaste la .54). Mantén un solo tacznpcs. No reemplaces los otros mods. Incluye los cambios de tactical.54.

## Qué incorpora

- Constructores y guardias pueden fabricar e instalar un generador Doomsday, una refrigeradora y un aire acondicionado.
- Primero debe existir el huerto con agua y estar terminada la primera etapa del perímetro (`fase >= 1`). Los trabajos de oficio y la fortificación pendiente conservan prioridad sobre instalar máquinas nuevas. El mantenimiento de máquinas ya instaladas se atiende antes de aportar al cofre.
- Recetas comprobadas contra los JARs aportados: `zomboid_survival:generator`, `zomboid_survival:fridge` y `doomsday_decoration:aircondition`. Usa la receta activa de RecipeManager y consume objetos reales; respeta cambios de datapacks. Puede fabricar cofres y hornos intermedios con recetas vanilla reales.
- Reabastece con carbón o carbón vegetal de la mochila o del almacén comunal, hasta 16 unidades por carga. No sale a buscar combustible al cofre cuando este no tiene ninguno. No crea carbón, tiempo de combustión, FE ni contadores de conservación.
- Usa la generación y distribución nativas de `MachineEntity`. El generador básico produce 80 FE/tick según PowerPolicy de tu JAR; Doomsday AC toma 400 FE por segundo, tiene radio nativo de 15 bloques y aplica su propia mejora de cultivos. La refrigeradora nativa consume energía y aplica su conservación nativa. El NPC no modifica esos valores.
- Guarda comida excedente en la refrigeradora y conserva hasta cuatro unidades personales por tipo de alimento. No mete armas ni herramientas en la refrigeradora por esta tarea. La clasificación usa la categoría de comida ya existente.
- Las máquinas se ubican en espacios cargados y libres, usando las comprobaciones de volumen de Doomsday y evitando puertas. El AC se sitúa a diez bloques o menos del centro del huerto; las máquinas consumidoras quedan a veinte bloques o menos del generador. Conserva sus posiciones en el guardado social de la comunidad. No sustituye bloques de edificios ni crea máquinas repetidamente mientras la instalada siga existiendo.
- La colocación usa Base48.place y sus comprobaciones de permisos, eventos y consumo de ítems. Las transferencias conservan inventarios y revierten una escritura fallida.

## Cómo probar en Minecraft (pendiente de ejecutar aquí)

1. Mantén los JARs de Doomsday Decoration .5 y zomboid-survival generator-tiers.2 suministrados. Deja un constructor o guardia libre, sin orden de seguir/esperar ni amenaza real.
2. Usa una comunidad con huerto irrigado y la primera etapa de barricadas finalizada. No salta esos requisitos para mostrar una instalación antes de asegurar supervivencia.
3. Aporta al cofre ingotes de hierro, cobre, redstone, hielo compacto, carbón y materiales para cofres y horno. Las tres máquinas base requieren 15 ingotes de hierro en total; también necesitan sus demás ingredientes. No convierte minerales en ingotes por esta tarea. Se pueden aportar las máquinas ya fabricadas a la mochila del constructor.
4. Comprueba que el HUD muestre llevar/instalar máquinas, y que los materiales disminuyan. Al iniciar generador debe consumirse carbón mediante el mod original. Abre su interfaz para verificar combustible y FE.
5. Comprueba en la interfaz de la refrigeradora su estado eléctrico y los alimentos almacenados. El cuidado nuevo guarda excedentes que el NPC lleva; todavía no reclasifica automáticamente toda la comida acumulada previamente en cofres.
6. Comprueba que el AC indique encendido/con energía y que esté dentro del radio de cultivos. La mejora usa los eventos de crecimiento del mod original; no hace madurar cultivos instantáneamente.
7. Vacía el combustible: debe poder agotarse y dejar de alimentar las máquinas; no se regenera gratis. Guarda/carga para comprobar la persistencia real del asentamiento.

## Pruebas realizadas y límites

185 comprobaciones JVM con APIs simuladas: 90 de asentamientos/despacho de trabajo, 16 nuevas de servicios y conservación de inventario y 79 regresiones anteriores. Incluyen protección de terreno, combustible limitado, ausencia de energía fabricada, reversión tras fallo de escritura, reserva de comida y selección de las recetas correctas. Se verifica bytecode y compila con Java 21.

NO se ha ejecutado Minecraft/NeoForge real, ni la partida del usuario. Estas pruebas no demuestran navegación real hacia las máquinas, funcionamiento de interfaces del modpack, rendimiento con muchas comunidades ni una simulación completa de producción/consumo de energía. La capacidad de enfriar y mejorar cultivos se confirmó leyendo el código nativo de los JARs; falta observarla integrada en el juego.

Los doce candidatos de parcela y el perímetro geométrico heredados de .54/.53 pueden no adaptarse a edificios o terreno difíciles. Si ninguna posición admite el volumen de una máquina, el NPC no la coloca. Falta de hielo compacto, ingotes o materiales intermedios impide fabricar; no los concede automáticamente. Se omite la tarea si los mods/recetas no están disponibles.

Esta entrega integra los tres servicios funcionales Doomsday. No añade una red eléctrica compatible con generadores de Refurbished Furniture, sus recetas especiales de banco, nuevas decoraciones Supplementaries, ventanas arquitectónicas, entierros ni nuevos sistemas de duelo/venganza. Tampoco mejora un generador ya instalado a otro tier. Esas peticiones siguen pendientes y no se anuncian como realizadas.

## Archivos cambiados respecto a .54

- `Community87.class`: despacha mantenimiento y construcción de servicios.
- `Utilities89.class`: fabricación/ubicación, persistencia, abastecimiento y transferencia de excedentes.
- `data/tacznpcs/utilities89.json`: recetas explícitas de máquinas e intermedios.
- `META-INF/neoforge.mods.toml`: versión .55.

El ZIP de fuentes contiene código, scripts y pruebas de esta entrega. No incluye los JARs de terceros ni logs personales.
