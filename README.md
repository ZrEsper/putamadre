# Mods de Zomboid

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
