# Mods de Zomboid

## Zomboid Hordes 1.8.1

[Descargar Zomboid Hordes actualizado](https://github.com/ZrEsper/putamadre/raw/refs/heads/main/mods/zomboid-hordes-1.8.1-neoforge-1.21.1-chunk-population.jar)

Para **Minecraft 1.21.1**, **NeoForge 21.1.255 o posterior** y **SoundAttract 6.3.8d**. Reemplaza el addon Zomboid Hordes anterior en **cliente y servidor**; no cargues ambas versiones simultáneamente.

- Sustituye las oleadas grandes por reposición hasta un **mínimo de dos zombis por par de chunks cargados** en el Overworld, contando los zombis vivos existentes.
- Reparto inicial: **1+1 (50 %), 2+0 (25 %) o 0+2 (25 %)**. Los pares son vecinos fijos hacia este/oeste.
- Cada par comparte un **cooldown aleatorio de 5–10 minutos**, conservado al guardar/reabrir. Si ya hay dos o más zombis, no añade nuevos.
- El ruido y el helicóptero atraen zombis existentes; el helicóptero conserva su sonido sin crear nuevas oleadas.
- Solo aparece en terreno apto, sin forzar chunks nuevos, agua, colisiones o apariciones a menos de 24 bloques de jugadores de supervivencia. El mínimo puede quedar pendiente cuando no haya una posición segura.

Las comprobaciones de lógica, guardado y bytecode pasaron con dobles de las API. **Falta probar el arranque y la integración dentro de Minecraft con el modpack completo.**

[Detalles, código del parche y comprobaciones](hordes-patch/README.md). La versión 1.8.0 se conserva como base de reconstrucción y no debe instalarse junto con la nueva.

## First Aid Zomboid 3.4.6

[Descargar First Aid actualizado](https://github.com/ZrEsper/putamadre/raw/refs/heads/main/mods/firstaid-1.21.1-ZOMBOID-v3.4.6-wetness-no-panic.jar)

Para **Minecraft 1.21.1** y **NeoForge 21.1.219 o posterior**, con las mismas dependencias de la versión anterior: **lrtactical 0.4.3** y **hordes 1.6.3f**. Reemplaza el First Aid anterior en **cliente y servidor**; no cargues dos versiones de First Aid simultáneamente.

- Desactiva el heartbeat, el pánico automático de encuentros/disparos cercanos y el contorno claro de pantalla.
- El contacto con agua moja las partes alcanzadas del cuerpo. Se secan gradualmente en unos **dos minutos** fuera del agua.
- La humedad prolongada puede provocar **gripe de dos minutos**, con estornudos cada 10–20 segundos.
- Los estornudos emiten sonido y partículas verdes desde la cara, y atraen zombis a **32 bloques**.
- La **toalla reutilizable** se fabrica con **tres telas de First Aid**, en cualquier distribución. Clic derecho seca el cuerpo; una gripe ya iniciada sigue su curso.
- El menú muestra humedad por extremidad y tiempo de gripe. Se conservan el menú de habilidades y los tratamientos existentes.

Las comprobaciones de lógica y bytecode pasaron con dobles de las API. **Falta probar el arranque y la integración dentro de Minecraft con el modpack completo.**

[Detalles, código del parche y comprobaciones](firstaid-patch/README.md). La versión 3.4.5 se conserva como base de reconstrucción; no debe instalarse junto con la nueva.

## Zomboid Survival 1.0.3

[Descargar el JAR actualizado](https://github.com/ZrEsper/putamadre/raw/refs/heads/main/mods/zomboid-survival-1.21.1-1.0.3-water-power.jar)

Para Minecraft 1.21.1, NeoForge y las dependencias del mod original. Reemplaza la versión anterior de **Zomboid Survival**, en cliente y servidor si juegas en uno. Instala solo una versión y conserva tu JAR de **First Aid**.

| Máquina | Consumo / capacidad |
| --- | --- |
| Refrigeradora | 1 FE/t (95 % menos) |
| Freezer | 2 FE/t (95 % menos) |
| Generador normal | 64 000 FE |
| Stationary / fijo | 256 000 FE |
| Trailer / remolque | 384 000 FE |
| Dispensador con agua | 1 FE/t |

El alcance de los generadores sigue siendo de 25 bloques. Conservan su panel propio, sonido de motor, humo y 30 minutos activos por 64 carbones, con pausa cuando el depósito está lleno. Las capacidades mayores también se muestran en el panel y se conservan al guardar.

### Dispensador de agua

Receta: **5 lingotes de hierro, 2 cristales, 1 carbón o carbón vegetal y 1 cubeta vacía**.

```text
Hierro  Hierro   Hierro
Cristal Carbón   Cristal
Hierro  Cubeta   Hierro
```

El dispensador aparece vacío. Haz clic con una **cubeta de agua** para llenarlo; no acepta botellas como entrada. Admite ocho cubetas, equivalentes a 32 botellas, y devuelve la cubeta vacía. Con electricidad filtra una botella cada cinco segundos y conserva el agua fría. Haz clic con una botella vacía de Minecraft o del mod para sacar agua filtrada.

El agua filtrada fría aporta **8 de hidratación**, frente a 6 del agua filtrada normal. Sin electricidad no filtra; el agua ya filtrada se puede sacar a temperatura ambiente. Se guardan el tanque y el progreso de filtrado.

Recoger agua del suelo con botellas produce **agua contaminada**, que no se puede beber directamente. Puedes hervir la botella en un horno (10 segundos), una estufa de Farmer's Delight o una fogata (10 segundos), o un ahumador (5 segundos), para obtener agua filtrada.

La versión conserva el apilado de alimentos con hasta 10 segundos de diferencia de frescura, usando la frescura más vieja, y mantiene la interfaz de First Aid.

Las comprobaciones de lógica, bytecode, capacidades, guardado, tanque, hidratación y anotaciones pasaron con dobles de las API. Falta comprobar el arranque y la integración con los demás mods dentro de Minecraft.

[Código del parche y comprobaciones](generator-patch/README.md). Los JAR anteriores se conservan como base o respaldo, no deben instalarse junto con la versión actual.
