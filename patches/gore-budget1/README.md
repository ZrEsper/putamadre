# Optimización del gore y ropa — 1

Reemplaza en `mods` los tres JAR anteriores por los de esta carpeta. No dejes dos versiones del mismo mod. Son cambios del cliente; no cambian registros, recetas, daño ni entidades jugables.

- Mob Dismemberment: máximo de 128 pedazos admitidos para actualización. Al llegar al límite retira los más antiguos. Vida máxima de 10 segundos de tiempo real desde su primera actualización; las reglas originales pueden retirarlos antes. El presupuesto se reinicia al cambiar de mundo.
- Splatter: mantiene la sangre y su iluminación suave. Recalcula luz como mínimo cada 10 ticks, respetando intervalos mayores ya configurados. Las renovaciones de cachés existentes se reparten en un presupuesto de 32 por tick; mientras tanto conservan la iluminación anterior. Los primeros cálculos de manchas nuevas todavía se realizan al aparecer. Los cambios de iluminación pueden tardar más en reflejarse cuando hay muchas manchas.
- Lucky's Wardrobe: reutiliza los modelos de las dos extensiones de armadura, por extensión, y renueva la caché si cambia el conjunto de modelos o su mapa de capas al recargar recursos. Conserva las animaciones y texturas originales.

EMF 3.3.11 y ETF 7.2.5 no necesitan sustituirse en esta entrega. La interfaz de EMF confirma el campo `roots` usado para detectar la renovación de modelos.

Validación: pruebas de 1.200 pedazos, límite de 128, expulsión, caducidad, cambio de mundo, presupuesto de iluminación y 1.000 reutilizaciones de modelos con renovación de caché. Verificación estructural del bytecode de los tres JAR. Comprobado que no incluyen clases duplicadas entre los JAR nuevos. No se ha ejecutado Minecraft ni medido la mejora de FPS.

Después de instalar, reproduce la misma situación de combate durante dos minutos y captura otro perfil de Spark del cliente para comparar con 9HQZUFwDmV.
