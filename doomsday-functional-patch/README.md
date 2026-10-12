# Doomsday Decoration: muebles funcionales y ocupación real

Reemplazo del JAR aportado `doomsday_decoration-1.1.4.2-neoforge-1.21.1.jar`, para Minecraft 1.21.1 / NeoForge. Instalar la misma versión en cliente y servidor. Retirar el JAR anterior de Doomsday; conservar los demás mods. Hacer copia del mundo antes de cambiarlo: las nuevas partes e inventarios se guardan en él y requieren este JAR para conservarse.

## Funciones

71 bloques ahora tienen inventario: cajas de madera/plástico/cartón, botiquines, munición, herramientas, maletas, mochila decorativa, buzones, armarios/cajoneras, estantes, expositores de armas, vitrinas de laboratorio, neveras y expendedoras. Los objetos dibujados en un modelo siguen siendo decoración; el inventario no regala armas ni suministros.

| Tipo | Espacios |
| --- | ---: |
| Caja original `acrate`, buzones, pequeños accesorios/laboratorio | 9 |
| Maletas, botiquín, herramientas, munición, cartón | 18 |
| Cajas generales y expendedoras | 27 |
| Armarios, expositores de armas y neveras | 36 |
| Estantes grandes, armarios grandes y caja fuerte | 54 |

Clic derecho abre el inventario. Se conserva el mecanismo original de guardado y tablas de loot del contenedor; la caja original `acrate` mantiene sus 9 espacios y objetos. Se expone un proveedor de menú para la selección isométrica. Las partes vinculadas no tienen un segundo inventario ni aceptan objetos de tolvas.

Cada uno de estos 71 objetos tiene receta y desbloqueo del libro de recetas. Consultarlas en JEI. Los modelos alternativos se fabrican con el objeto base y un tinte; se evitan recetas iguales con resultados diferentes.

## Refrigeración

Expendedoras, neveras e insulation cabinet aceptan bebidas y reservan su último espacio al hielo. Hielo normal dura 60 segundos; hielo compacto 5 minutos; hielo azul 10 minutos. También pueden consumir 20 FE/s de un generador de Zomboid Survival en un radio de 25 bloques, si está disponible. Tardan 10 segundos en enfriar y mantienen la etiqueta de bebida fría mientras funcionan. El efecto al beber lo proporciona Zomboid Survival; se mantienen el tipo de agua, la botella vacía y las reglas originales de contaminación. Enfriar no purifica agua ni crea bebidas gratis.

Si Zomboid Survival ya sustituye una nevera por su propia máquina, conserva su inventario, energía, alimentos y refrigeración originales: esta modificación no borra ni reemplaza esos datos. No se toca su JAR.

## Casillas físicas

La corrección física cubre todos los bloques decorativos genéricos cuya forma original sobresale de una casilla, incluidos los que no llevan inventario. No sustituye las mitades originales de puertas vanilla. Se calcula el volumen de las formas originales en cada orientación y se añaden partes reales, invisibles, en cada casilla que ocupa. Un mueble de 1×2 ocupa dos casillas; un estante de 2×2 ocupa cuatro. La expendedora original también sobresale por los lados y debajo de su bloque de control: se respetan esas extensiones. Las partes tienen la selección/colisión recortada a la forma original, sin duplicar el modelo.

La colocación se rechaza si una de esas casillas tiene una pared u otro contenedor o está en un chunk descargado. Romper una parte dirige la rotura al bloque principal, con las comprobaciones de permisos y herramientas del servidor. El mueble y su contenido se sueltan una sola vez. Las explosiones/remoción de una parte también retiran el conjunto. Los muebles nuevos no pueden ser separados por pistones.

Los muebles existentes se registran al acercarse (radio de 5 bloques, altura ±3, cada 2 segundos) o al abrirlos. No se sobrescriben paredes ni inventarios. Si un mueble antiguo está ya metido dentro de una pared, recogerlo y recolocarlo dejando libres todas sus casillas. No se inventa espacio dentro de la pared.

## Verificación y límites

Se comprueba el ZIP/JAR, todos los JSON, recetas, nombres registrados, bytecode y pruebas con una API simulada: ocupación, cuatro orientaciones, casillas negativas, paredes, límites de chunks, propiedad de partes, guardado, eliminación, tamaños de inventario y filtros de bebidas/hielo. **No se ha ejecutado Minecraft en este entorno.** La integración real con el conjunto de mods y la cámara debe comprobarse en una copia del mundo.

Prueba sugerida: colocar una nevera y un estante en un patio libre, comprobar clic/rotura por arriba y por ambos lados, guardar/reabrir el mundo y verificar que su contenido se conserva. Intentar colocarlo pegado a una pared ocupando su casilla: debe rechazarlo. Revisar también una nevera del mod Survival sin perder sus objetos.

## Compilación reproducible

Java 21, Python 3, ECJ 3.40 y ASM 9.7.1; los artefactos se verifican mediante `tools.sha256.json`. Pasar el JAR original y una carpeta con `ecj.jar`, `asm.jar`, `asm-tree.jar`, `asm-analysis.jar`, `asm-util.jar`:

```bash
python doomsday-functional-patch/build.py /ruta/doomsday_decoration-1.1.4.2-neoforge-1.21.1.jar /ruta/herramientas
```

La salida va a `mods/doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-storage.1.jar`. Las clases de prueba no se incluyen. `validation.json` registra hashes y el alcance de las verificaciones. No incluye instaladores ni ejecutables externos.
