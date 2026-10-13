# Panel 26: perfiles de loot separados

Reemplaza los JAR anteriores de Zomboid Hordes y Zombie Remains por los dos JAR de `mods/` de esta entrega, en cliente y servidor. No mantengas versiones duplicadas. El protocolo del panel cambia a 26. No necesitas instalar otro datapack: los catálogos están integrados en Hordes.

Abre `/zhorda panel` como administrador. Hay tres pestañas independientes: **Cofres/barriles**, **Drops** y **Cadáveres**.

Cada perfil comienza en **Original**, conservando el generador actual. Para usar los ajustes, cambia a **Personalizado**, modifica los valores y pulsa **Aplicar**.

- Cantidad: 0–10× el número original de pilas. 0 elimina el loot generado. Con una base vacía se usa una selección como base. Las cantidades fraccionarias se sortean; el resultado se limita al espacio del inventario y a 64 selecciones por generación.
- Categorías: armas, medicinas, comida, herramientas, ropa/armaduras, munición, accesorios y materiales.
- Porcentajes: pesos por selección, no probabilidad de que todo un contenedor contenga una categoría. Se normalizan: armas 20 + comida 80 implica 20% de las selecciones de armas y 80% de comida. Armas 100 y todas las otras categorías 0 implica solo armas. Todas a 0: sin loot. Las pilas pueden contener distintas cantidades según su tabla.
- Puedes escribir porcentajes o usar la rueda; los giros rápidos aceleran el ajuste.
- **Ver ejemplo** muestra la distribución normalizada y una simulación con una base ilustrativa de 10 pilas. No garantiza los ítems ni las cantidades concretas del próximo loot.

El perfil personalizado sustituye la mezcla original por las categorías configuradas, usando 775 variantes de entradas extraídas del Mosslorn 13 anterior y conservando sus funciones de ítem, IDs de armas, munición y accesorios. El modo Original conserva la distribución contextual y las reglas originales. Si falta un catálogo requerido, Aplicar rechaza la activación en lugar de guardar un perfil incompleto.

Cofres/barriles: se aplica al generar loot mediante `LootTable.fill` en contenedores de esas clases. No convierte cofres sin tabla ni modifica contenido ya generado o colocado manualmente. Otros generadores de mods que no usen esta vía pueden requerir una compatibilidad adicional. Para regenerar los contenedores registrados, usa la acción existente de reinicio de loot; reemplaza su contenido.

Drops: afecta a zombis, mutantes y bandidos/NPC reconocidos por su tipo. Protege drops de jugadores y animales. Zombie Remains 7 marca los casos convertidos en cadáver, para que no aparezca un segundo lote al suelo.

Cadáveres: Zombie Remains 7 aplica el perfil al crear un cuerpo, después del loot y los extras originales. Incluye el contenido heredado en la mezcla personalizada. No cambia cuerpos anteriores, restos ya recolectados o inventarios de jugadores. Conserva el contenido ampliado de la versión 6 al usar Original.

Se mantiene la prioridad de combate, el límite/reserva de zombis, las herramientas de reinicio y la limpieza temporizada del Hordes 25 enviado.

Validación: 100.000 muestras de distribución, exclusión y selección exclusiva, cantidades y fracciones, límites de espacio, aislamiento de perfiles y guardado/recarga por mundo. Esquema del panel: 7 pestañas y 62 ajustes. Verificación del bytecode de ambos JAR. No probado dentro de Minecraft.
