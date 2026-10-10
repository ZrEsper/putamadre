# Mosslorn: zonas de peligro y mejor loot

Incluye un JAR de reemplazo de Zomboid Hordes y un datapack completo Minecraft 1.21.1. La ubicación del puente enviada por el usuario fija la división norte/sur en Z=192, sustituyendo Z=240.

## Zonas

- Central nuclear: referencia cercado exterior (-550,65,-618). Caja aproximada X=-646..-454, Z=-714..-522. Objetivo de población local ×3 (3–15, según sorteo base); bonus de loot 65 %.
- Pirámide: referencia cúspide (90,165,-485). Caja aproximada X=-38..218, Z=-613..-357. Objetivo ×2 (2–10); bonus 40 %.
- Estructuras altas: 480 chunks inferidos de los archivos del mundo enviado, con al menos 30 contenedores en Y>=60 y 50 bloques de separación vertical. Objetivo ×2; bonus 35 %. Esta aproximación puede incluir otras estructuras de varias plantas; no reconoce visualmente edificios.

La referencia de la central es el cercado, no su centro: la caja alrededor de ese punto no garantiza cubrir todo el recinto. Las dos cajas son estimaciones y deben revisarse dentro del juego. Las referencias explícitas tienen prioridad sobre los chunks inferidos y las zonas no acumulan multiplicadores.

El bonus añade una selección de la variante abastecida de farmacia, electrónica, herramientas y otras categorías. La central incluye equipo militar con peso 10 % dentro de la selección de bonus; eso no garantiza un arma. Todos los contextos conservan el objeto básico garantizado y su loot normal. No añade radiación ni nuevo daño ambiental.

## Instalar

1. Copia de seguridad del mundo; cerrar Minecraft/servidor.
2. En mods, reemplazar zomboid-hordes-1.8.3-neoforge-1.21.1-zombies-mutants-only.2.jar por zomboid-hordes-1.8.3-neoforge-1.21.1-mosslorn-danger-zones.3.jar. Dejar solo un Zomboid Hordes; instalar también en servidor. The Hordes permanece igual.
3. Colocar mosslorn-expanded.5-danger-zones.zip en datapacks del mundo sin descomprimir.
4. Iniciar y ejecutar:

```
/datapack enable "file/mosslorn-expanded.5-danger-zones.zip" last
/reload
```

Deshabilitar versiones ampliadas anteriores si están activas. No reconvertir el mundo. Los contenedores ya generados no cambian.

El mod usa estas coordenadas en cualquier Overworld donde se instale: está preparado específicamente para Mosslorn. No se limita por nombre/UUID del mundo. Fuera de las zonas mantiene la población base. Conserva límite de 16 creaciones/256 intentos por revisión, cooldowns y límites de mutantes; no fuerza zombis dentro de bloques ni cerca del jugador. Las oleadas de The Hordes no se multiplican. El efecto es un objetivo por chunk, no un número instantáneo garantizado. Los chunks usan su centro para determinar el peligro; cerca de bordes puede diferir algunos bloques del bonus de loot, que usa la posición exacta del contenedor.

El comando de diagnóstico original muestra objetivos base 1–5, no los multiplicados: ese texto no se ha actualizado. La lógica efectiva de población sí usa el multiplicador.

## Comprobaciones y reconstrucción

11 casos del helper real, fronteras de zonas, objetivos y déficit; hook ASM único de la población existente; recursos y clases ajenos conservados; referencias del pack y provisiones garantizadas verificadas. Sin prueba dentro de Minecraft.

prepare.py vuelve a generar zonas y pack usando containers.csv local de análisis si está disponible. zones.json incluye candidatos y límites. Para reconstruir el JAR, compilar Zones.java con ECJ contra el JAR base, ejecutar Patch.java con ASM sobre el JAR base, añadir zones.csv como recurso mosslorn-danger-zones.csv y ejecutar package.py. Las rutas de herramientas y clases de esta sesión están en los comandos usados; package.py espera /workspace/danger-classes. No ejecutar prepare.py sin el inventario de contenedores si se desea conservar las 480 zonas inferidas.
