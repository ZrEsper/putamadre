# Posición de cadáveres y agua: death-compat.3

Sustituye death-compat.1 y death-compat.2. Se reconstruye sobre los mismos tres JAR originales del usuario, conservando los autores, licencias, recursos, loot, identificación de NPCs y dependencias. Minecraft 1.21.1 / NeoForge **21.1.255 o superior**, con Doomsday Decoration 1.1.4.2 o superior.

## Instalación

Eliminar las versiones anteriores de los tres mods; no instalar dos JAR con el mismo modId.

Cliente, incluido mundo de un jugador:

- `Saros-Mob-Ragdoll-NeoForge-1.21.1-1.0.1-death-compat.3.jar`
- `mobdismembermentneoforge-1.21.1-1.4.0-death-compat.3.jar`
- `zombie-remains-loot-1.6.2-neoforge-1.21.1-death-compat.3.jar`

Servidor dedicado: instalar las copias `.3` de **Saros y Zombie Remains Loot**. Mob Dismemberment sigue siendo solo visual de cliente. Los jugadores necesitan los tres parches `.3`. Se fijan las dependencias y la versión del canal de red para evitar mezclar versiones anteriores y originales.

Mantener Doomsday Decoration, TACZ, TacZ NPCs tactical.13, Self Expression y las otras dependencias existentes. No modifica la IA ni el equipo de los bandidos.

## Fallo confirmado en latest.log y transición visual

El log de la prueba con `.2` muestra `UnsatisfiedLinkError` en `BoxCollisionShape.createShape`: la DLL se cargaba desde TRANSFORMER y sus clases Java estaban en PLUGIN. Saros desactivaba la física, por lo que no había una posición física de aterrizaje y se recuperaba el loot desde el origen.

La revisión carga la biblioteca nativa mediante el cargador propio de Bullet, comprueba su SHA-256 contra el binario incluido en el JAR original y verifica que puede crear una forma nativa. No reutiliza a ciegas la DLL extraída anteriormente. Es necesario cerrar completamente Minecraft y volver a iniciarlo.

Se ajusta la detección continua de colisión al grosor de cada hueso y la simulación a 120 pasos por segundo con hasta ocho subpasos. Los fragmentos conservan su vida mientras dura la transición. Un error al consultar terreno conserva la última posición física obtenida y registra la excepción completa.

## Corrección de posición

La versión anterior convertía la posición del ragdoll en el centro de un bloque, escogía superficies más altas alrededor antes de buscar debajo del torso, rechazaba desplazamientos superiores a 16 bloques y retiraba el efecto antes de confirmar que podía colocarse el cadáver.

- Se leen las coordenadas reales del cuerpo rígido del torso desde Bullet, sin redondearlas antes de enviarlas al servidor.
- En desmembramiento se elige una vez el torso, dando preferencia a nombres body/torso/chest/spine/trunk y evitando cabeza, brazos y piernas. Se conserva su último punto si el fragmento desaparece; no se cambia a una extremidad lejana.
- La columna X/Z del torso tiene prioridad. La búsqueda local admite dos bloques verticales y solo un bloque lateral cuando esa columna está ocupada o no permite colocar el cadáver. Un escalón o techo lateral ya no gana por recorrer antes las capas altas.
- Se conserva el bloque validado hasta colocarlo; no se repite una búsqueda amplia que pueda cambiarlo. Si se ocupa entretanto, se busca únicamente alrededor del último torso.
- Hay actualizaciones de posición cada diez ticks durante la caída. La recuperación por tiempo utiliza el último punto recibido, en vez del punto de muerte inicial.
- El efecto espera la confirmación del servidor y reintenta el aviso de reposo cada diez ticks. La confirmación se emite después de colocar el bloque y registrar el loot. El cuerpo permanece cuatro segundos en reposo y se desvanece durante dos segundos antes de enviar la posición final. El desvanecimiento solo cambia opacidad: no desplaza el cuerpo hacia abajo.
- El observador más cercano a la muerte aporta la posición compartida. Si se desconecta, cambia de dimensión o se aleja, puede finalizar otro observador registrado.
- Se validan coordenadas finitas, identidad de muerte, observador, dimensión, chunks cargados y desplazamiento máximo de 128 bloques horizontales y 512 verticales. Se admiten caídas altas que `.1` rechazaba.

Los cadáveres de Doomsday Decoration son **bloques**: el cuerpo visual admite coordenadas continuas, pero el cadáver y su modelo quedan ajustados a la cuadrícula del mundo y a sus poses. No se promete coincidencia exacta al centímetro ni igualdad entre simulaciones físicas de clientes distintos. Esta revisión elimina las búsquedas y recuperaciones que podían trasladarlo varios bloques o devolverlo al origen.

## Agua

- Se reconoce agua normal/fluida mediante `FluidTags.WATER`, sin tratar la lava como soporte.
- Se coloca el cadáver en la primera celda seca sobre la columna de agua del torso, incluso sin un suelo sólido debajo. No se reemplaza ni vacía el agua.
- Como Bullet no proporciona flotación en este mod, al entrar el torso en agua se eleva el ragdoll a la superficie, se frena su movimiento y se anula la caída vertical. Así se evita que su referencia termine en el fondo de un lago mientras el cadáver aparece arriba.
- Los fragmentos pueden finalizar cuando tocan la superficie del agua aunque su animación de flotación los saque momentáneamente del fluido.
- También se aceptan superficies con colisión, como losas, sin exigir la cara superior completa de un bloque sólido.

## Loot, persistencia y recuperación

Se conservan las reglas `.1`: explosión o golpe fatal de al menos `max(24, 1.25 × salud máxima)` produce desmembramiento; las otras muertes usan ragdoll. No se generan ambos efectos en una misma muerte. Se respeta la resurrección del Mutant Zombie y los jugadores mantienen su sistema de cadáveres existente.

El inventario pendiente y su posición validada se guardan en el SavedData existente. Se pueden cargar pendientes de `.1`; ropa, armas, munición, componentes de ItemStack y calidad legendaria se preservan. Se mantienen los métodos originales de población de loot y su capacidad; este parche no amplía la lectura de los primeros nueve stacks almacenados de TacZ NPCs.

Sin confirmación de reposo, la recuperación empieza a los 1200 ticks (60 segundos a 20 TPS) desde la última posición recibida. Si no existe un sitio válido o el chunk está descargado, conserva el inventario y reintenta sin forzar chunks. En esa recuperación busca primero debajo de la misma columna, hasta 384 bloques, y después columnas próximas. Nunca sustituye otro cadáver ni un bloque sólido. El vacío o terreno totalmente ocupado puede dejar el inventario pendiente.

## Validación y reconstrucción

**79 aserciones** ejecutan los helpers publicados y el codec con dobles de Minecraft/NeoForge/física. Cubren la posición del torso sin redondeo, cabeza lejana, conservación de la última posición de un fragmento, bordes elevados, losas, agua profunda, flotación, no eliminación del agua, lava, caídas largas, cambio de observador, reintentos, ausencia de confirmación al fallar la colocación, loot, calidad, guardado, reinicio y las regresiones de `.1`. Análisis ASM de **123 métodos** originales/modificados. Se conservan byte por byte los recursos y clases originales fuera de las modificaciones seleccionadas. No se empaquetan las clases simuladas.

Se reprodujo el fallo JNI con la biblioteca Bullet real y dos cargadores de clases separados; la nueva carga lo corrigió. Tres pruebas nativas de caída con huesos de distinto grosor aterrizaron lejos del origen sin atravesar un suelo plano. Estas pruebas se ejecutaron en Linux.

**No se ejecutó Minecraft, Windows ni el modpack completo en este entorno.** Falta validar visualmente el renderizado, la geometría del modpack y la pose del cadáver sobre agua.

Requiere Java 21 y Python 3. El ZIP de fuentes incluye herramientas con hashes fijados, fuentes, dobles, pruebas y transformación ASM. Los SHA-256 de los originales se verifican:

```bash
python3 -B build.py /ruta/Saros-Mob-Ragdoll-NeoForge-1.21.1-1.0.1.jar /ruta/mobdismembermentneoforge-1.21.1-1.4.0.jar /ruta/zombie-remains-loot-1.6.1-neoforge-1.21.1-npc-compat.jar
```

Los tres JAR `.3` se generan en `output/`. También se comprobó reconstrucción desde una extracción limpia del ZIP de fuentes.
