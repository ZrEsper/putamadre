# Impactos, armaduras y cadáveres — revisión 49

Base revisada: ZrEsper/putamadre, rama codex/medical48-remains-impacts-bandages, commit 2a0d3cfcfa0f78dc49f5c136b520b4d1e4acf0f9. Main es anterior a esta rama. Los JAR adjuntos corresponden a esa revisión 48.

## Instalación
Cierra Minecraft y el servidor. Reemplaza únicamente Gore .32 y Ragdollified .25 por los dos JAR de mods/, en todos los clientes y el servidor. Deja una sola versión de cada mod.
Conserva Corpse .5, FirstAid 3.6.16, TaCZ FirstAid Compat 1.0.4, TaCZ NPCs .32 y Splatter blood-budget .2 que adjuntaste. No uses los antiguos archivos experimentales de otra revisión.

## Correcciones
- Mordidas, balas y vendas usan regiones distintas del atlas. Las mordidas muestran dos arcos de dientes; los datos médicos ya distinguían los tipos 1 y 2.
- La armadura conserva tamaño, UV y material: se elimina una división adicional por 16 que la hacía diminuta. Los orificios se calculan en píxeles del cuerpo y solo se prolongan hacia la superficie de la armadura. Las piezas ocultas no vuelven a dibujarse por la ruta original.
- El filtrado de islas usa las mismas unidades que la anatomía. Recorta la superficie completa de los fragmentos sueltos, incluyendo polígonos cuyo centro quedaba apoyado pero cuyos extremos flotaban.
- La localización viva considera el rayo de entrada, el giro propio de la cabeza y la postura del cuerpo. Evita convertir un punto de colisión de salida en una lesión al otro lado. No se amplía el alcance de disparos.
- El contenedor del inventario Corpse no bloquea la intersección con el ragdoll enlazado. Paredes y entidades más cercanas conservan su prioridad.
- Los zombis muertos esperan la creación física del servidor, en lugar de producir una copia solo visual en el cliente. Los derribos vivos mantienen su ruta existente.
- Los cortes de hoja repetidos pueden seguir profundizando. Los golpes iniciales siguen dejando cortes estrechos; los puños no se convierten en amputaciones.
- La expiración lee la rotación del campo real del paquete y retira el cuerpo físico incluso si no cabe un bloque de remains. No sobrescribe paredes ni inventarios.

## Verificación y límites
Compilación Java 21 y análisis de bytecode de 986 clases de los dos JAR. Pruebas con las funciones de producción y APIs simuladas: 430 controles anteriores, 304 controles de armadura/texturas/fragmentos/destrucción, 217 controles de coordenadas y entrada del disparo, ejecución real de Bullet13.tick con contenedor delante y con ragdoll posado, y 4030 controles de persistencia/curación/cadáver único.
No se ejecutó Minecraft ni se validó visualmente con tu modpack/shaders. El ajuste de coordenadas se comprueba con modelos humanoides y poses reproducidas; las animaciones externas pueden necesitar calibración adicional. El recorte de armadura cubre modelos humanoides ModelPart: las mallas GeoBone o renderizadores de terceros requieren su propia integración. Los cuerpos antiguos ya creados solo en un cliente no adquieren retroactivamente un registro del servidor; prueba con muertes nuevas tras reiniciar.

No se modifican los inventarios, protección/durabilidad de armadura, recetas ni el protocolo de red existente.
