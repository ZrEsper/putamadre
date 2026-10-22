# Entrega 63 — decisiones de supervivencia con cámara isométrica

Minecraft 1.21.1 / NeoForge. Reemplaza estos tres JARs, dejando una sola versión de cada mod:

- First Aid .18 → **.19**.
- Survivor Creator v35 → **v36**.
- Dungeons Isometric .34 → **.35**.

En un servidor, actualiza también First Aid y Survivor Creator del servidor: allí se validan Rendirse, la reanimación y los contadores. La cámara se controla en el cliente. Conserva los demás mods, incluidas las linternas/faros de la entrega 62.

## Al quedar derribado

Se mantiene visible el mundo y se activa temporalmente la cámara isométrica, ortográfica, en tercera persona. Se oculta el HUD normal y el antiguo informe anatómico/pantalla negra se sustituye por una tarjeta compacta con colores apagados. Muestra el tiempo sobrevivido y los zombis abatidos.

La única opción fija es **Rendirse**. **Usar desfibrilador** aparece si el jugador puede autorreanimarse y tiene uno utilizable, también en el inventario; se excluyen las pilas vacías y los desfibriladores agotados. Se elimina Esperar ayuda como botón: puedes permanecer en la pantalla y recibir ayuda de otro jugador sin pulsar nada. El contador de inconsciencia sigue avanzando; la pantalla no pausa la partida.

La reanimación utiliza el mismo mensaje y la validación de First Aid en el servidor. No se conceden usos ni se cambia la lógica de consumo. El botón evita enviar solicitudes repetidas mientras está reanimando y vuelve a habilitarse tras diez segundos si continúa derribado. No se convierte un clic en el desfibrilador en un clic en Rendirse si cambia el inventario entre dibujar la pantalla y hacer clic.

Rendirse utiliza GIVE_UP en el servidor. Se amplía su condición de tipos de inconsciencia a cualquier jugador inconsciente; los jugadores conscientes siguen sin poder ejecutarlo. La muerte y el inventario siguen pasando por la lógica original de First Aid.

## Reanimación y muerte definitiva

Al recuperar al jugador se cierra la interfaz, se desactiva la cámara isométrica y se vuelve a **primera persona**, aunque llevara un arma. Se restauran el HUD y los ajustes temporales de proyección/zoom. La cámara no vuelve a activarse por la opción de inicio automático de Isometric; se puede volver a activar manualmente con su tecla habitual. No se reescriben archivos de configuración.

Si el jugador muere definitivamente, se muestra el resumen sobre el mundo con **Nuevo superviviente**. Ese botón solicita la reaparición normal y continúa al creador de personajes. La transición evita presentar otra vez el informe antiguo de Survivor Creator después de reaparecer. Se conservan la introducción normal del creador y su flujo de creación.

Tab/flechas permiten seleccionar una opción y Enter/Espacio activarla. Durante la inconsciencia, Esc permite acceder al menú de pausa habitual sin convertirlo en un tercer botón de la tarjeta.

## Estadísticas

Se agregan dos números al snapshot existente de Survivor Creator, que ya se envía una vez por segundo: tiempo de la vida actual y zombis abatidos en esa vida. Se usan los contadores persistentes del servidor; la reanimación no los reinicia. El tiempo usa veinte ticks por segundo, como el contador anterior, no la velocidad del ciclo día/noche. Puede haber hasta aproximadamente un segundo de retraso de sincronización. Si todavía no se recibió un snapshot válido, se muestra Sincronizando en lugar de inventar un cero. Los mensajes de otros UUID se ignoran.

El informe posterior a la reaparición conserva los datos de muerte originales para el caso de reaparición automática. No se sustituyen contadores por bajas globales de todas las vidas.

## Validación realizada

- 48 controles del código de producción de las pantallas y el controlador: inventario vacío/agotado/utilizable, mensajes estándar de desfibrilador y Rendirse, bloqueo de duplicados, cambio de inventario entre render y clic, cinco reanimaciones seguidas, cámara ortográfica, regreso a primera persona, desbloqueo manual de Isometric, protección de cámara con armas, muerte, solicitud única de reaparición, desconexión, snapshots reales Gson con mundo simulado, UUID y límites de botones a varias alturas.
- Se prueba el método canGiveUp extraído del JAR parcheado en una clase mínima ejecutada por la JVM: jugador consciente falso, inconsciente verdadero. Se comprueba la firma real de Component.literal para evitar un error de enlace por la API de compilación.
- Java 21 con verificación de la JVM en los ensayos y análisis ASM de 290 clases First Aid, 120 Survivor Creator y 151 Isometric. Dos puntos de modificación de First Aid, siete de Survivor Creator y tres de Isometric, además de los helpers y pantallas sustituidas.

Los ensayos usan simulaciones de Minecraft, inventario/modelo y configuración de cámara. **No se ejecutó el modpack completo ni una sesión gráfica de Minecraft.** El aspecto, las cámaras bajo otros mods/shaders y la transición de red completa requieren comprobarse en juego. La imagen de revisión generada durante el desarrollo no es una captura de Minecraft y no se incluye como prueba visual del juego.

Las fuentes incluyen API mínima usada solo para compilar/pruebas, el parche ASM, tests y Gson 2.10.1 para los ensayos. Ninguna clase de la API simulada se incluye en los JARs instalables. Para reconstruir, ajusta inputs.json a tus JARs base, usa Java 21 y define PATCH_TOOLS con ECJ y ASM/ASM-tree/ASM-analysis. Base GitHub: entrega 62, commit 04c9f65b3f4f26c9d0ee87c69723bd291c860155; las tres bases binarias son los adjuntos del usuario y sus hashes figuran en el manifiesto.
