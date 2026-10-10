# Faction Friction: Zomboid Clans .1

Parche del JAR original `factionfriction-1.8.0R-1.21.1-NEOFORGE.jar` proporcionado por el usuario. Minecraft 1.21.1 / NeoForge 21.1. Mantiene el modId, autor danialisntcool, licencia ARR, recursos originales, clanes existentes, protecciones y compatibilidades. No es una versión oficial del autor original.

## Instalación y uso

Sustituir el JAR anterior por `factionfriction-1.8.0R-1.21.1-NEOFORGE-zomboid-clans.1.jar` **en cliente y servidor**, sin duplicados. Reiniciar ambos. Los canales de red requieren esta misma revisión en ambos lados. Conservar las dependencias de los demás mods.

La tecla **K** (reasignable) abre el cuaderno de clanes. Sus páginas son:

- **Clan:** lema, cinco emblemas originales dibujados como sellos (radio, calavera, medicina, herramientas y estrella) y cinco identidades: Supervivientes, Nómadas, Milicia, Rescatistas y Recolectores. Las identidades son presentación; no conceden buffs. La administración original permite crear/invitar, gestionar rangos, nombre, sigla, colores, reclamaciones, alianzas y comercios.
- **Conflicto:** elegir rival por nombre o sigla y escoger Escaramuza o Asedio; confirmar la declaración. Muestra preparación, duración, puntuaciones y si hay defensor conectado. Permite abrir el mapa de operaciones y confirmar una rendición. Declaración y rendición tienen confirmaciones separadas.
- **Compañeros:** posiciones X/Y/Z, dimensión y rango de miembros conectados del propio clan, actualizadas cada dos segundos mientras está abierto el cuaderno. En ventanas anchas hay un radar de 128 bloques; los compañeros más lejanos aparecen en el borde y solo los de la misma dimensión aparecen en el radar. Los nombres y coordenadas de enemigos no se envían. La sincronización original para Xaero/JourneyMap sigue disponible con sus opciones y mods instalados.
- **Depósito:** recompensas compartidas, persistentes y reclamables por jefatura o comandantes. El inventario lleno conserva lo que no pudo entregarse. Cada pulsación entrega hasta un stack de cada objeto; se puede volver a recoger lo restante.

Interfaz oscura en carbón/oliva con separadores de expediente y texto claro, inspirada en Project Zomboid, usando dibujos propios. Las etiquetas de progreso son Banda, Refugio y Coalición; los identificadores internos TEAM/COLONY/COUNTRY y su progreso se conservan. La administración original tiene acceso al cuaderno arriba a la derecha.

## Guerras y territorios

Ambos modos tienen **cinco minutos de preparación** y **60 minutos de conflicto después** por defecto. Se usan ticks del mundo: a 20 TPS corresponden a esos tiempos; lag o servidor detenido alargan el tiempo real.

**Escaramuza:** cuenta bajas de jugadores enemigos para el marcador y las recompensas. Las operaciones territoriales permanecen bloqueadas y las reclamaciones protegidas.

**Asedio:** tras la preparación se puede seleccionar un chunk de frontera enemigo y lanzar una operación desde el mapa. Las bandas básicas también pueden capturar; conservan su límite de un objetivo. Refugios y coaliciones conservan sus límites nativos. Solo los chunks seleccionados en operaciones activas se vuelven atacables mediante las reglas originales, los permisos y las asignaciones. Se conserva el requisito de romper bloques y controlar el área para capturarla.

Se exige **al menos un miembro defensor conectado, de cualquier rango**. Se comprueba al lanzar, durante la operación y al resolver permisos de daño/destrucción y créditos de bloques. Cuando se desconecta el último defensor, la protección se restablece y la operación se pausa; al reconectar puede continuar. No se añade otra espera nativa de diez minutos después de los cinco minutos globales. Una operación nueva activa su fase de combate en el siguiente ciclo del servicio nativo.

El conflicto termina por tiempo, rendición o paz. El mayor marcador gana al agotarse el tiempo; empate si los marcadores coinciden. La rendición da la victoria al rival. Se cierran las operaciones y se mantienen los chunks ya transferidos. Los bypass de administrador y las demás opciones originales del servidor siguen siendo autoridad.

La captura de capital conserva la regla original de anexión y disolución del clan derrotado. Para proteger capitales, configurar `protect_capitals_from_attacks` en la configuración original de FactionFriction. Los límites originales de dimensiones, claims, rangos, fuerzas y enfriamientos de operaciones siguen vigentes.

Conflictos antiguos activos o justificaciones aprobadas se incorporan a un asedio con una nueva preparación de cinco minutos y marcador nuevo en cero. Se conservan los claims y las operaciones existentes; una operación antigua marcada como raid conserva su resolución original. Una solicitud pendiente de aprobación no se convierte automáticamente en guerra. Si `admin_approved_justifications` está activado, las declaraciones nuevas exigen también autoridad de clan y permisos administrativos de nivel 3; no implementa una nueva cola de solicitudes administrativas.

## Puntos, premios y guardado

Valores por defecto:

| Evento | Puntos |
| --- | ---: |
| Baja de jugador del clan rival, tras preparación | 5 |
| Captura de chunk, una vez por bando/chunk/conflicto | 20 |
| Objetivo defendido en operación activa, una vez por operación/objetivo | 10 |

No puntúan muertes canceladas, propias, amistosas o fuera del conflicto. El mismo jugador víctima tiene dos minutos de enfriamiento y un máximo de tres bajas puntuables por guerra, usando su cuenta real incluso con perfiles Switchy. Un borrador o una preparación cancelada no concede puntos de defensa.

Al finalizar, cada clan que obtuvo al menos cinco puntos recibe `1 + floor(puntos / 25)` lotes, con **dos lotes extra para el ganador**, hasta ocho en total por clan. Un clan sin contribución no recibe premios, aunque el rival se rinda. Los objetos llegan al depósito del clan; una victoria no crea entregas duplicadas al repetirse el tick. Hay 30 minutos de tregua entre ambos clanes después del conflicto.

Cada lote mezcla cuatro carnes cocinadas, una botella de miel, tres lingotes de hierro, ocho flechas, dos cueros, ocho antorchas, un hacha de hierro y una pechera de cuero. Los administradores pueden cambiar los IDs y cantidades por objetos de mods instalados. Se crean stacks normales: armas TACZ, cargadores u otros objetos que requieren componentes especiales necesitan integración adicional; el parche no inventa esos componentes. Si falta un objeto de la configuración, su recompensa permanece pendiente.

Se guarda `ZomboidClansV1` en el SavedData original de FactionManager: identidades, guerras, tiempos, marcador, controles contra bajas repetidas, objetivos ya puntuados, depósitos, resultados y treguas. Los inventarios, miembros y claims originales no se sustituyen. Los datos se guardan con el ciclo normal del mundo, igual que el resto de FactionFriction.

## Personalización del servidor

Al iniciarse el servidor se crea `config/factionfriction-zomboid-clans.properties`. El ejemplo incluido muestra las claves. Permite ajustar preparación, duración, tregua, límites de bajas, puntuación y objetos de cada lote. Editar con el servidor detenido y reiniciarlo. Las opciones originales de FactionFriction siguen controlando su sistema nativo.

## Validación y reconstrucción

**68 aserciones de integración** ejecutan los helpers publicados y los métodos nativos transformados de permisos con `java -Xverify:all`: preparación, defensor civil conectado, desconexión durante ataque, cierre del bypass central usado por las integraciones de explosiones y créditos, pausa/reanudación, escaramuzas, bajas, objetivos, ganador, rendición, tregua, inventario lleno/parcial, objetos ausentes, permisos, configuración administrativa, guardado/reinicio, migración de conflictos, privacidad de posiciones y codec real del paquete.

Análisis ASM de **427 métodos originales/transformados**. Se conservan byte por byte todas las entradas originales fuera de los cambios seleccionados. Ningún stub de Minecraft/NeoForge se empaqueta en el JAR.

**No se ejecutó Minecraft ni el modpack en este entorno.** Las pruebas usan dobles de las APIs; falta verificar visualmente el cuaderno y realizar un asedio multijugador con los mods de armas/explosiones/vehículos del servidor. El parche conserva las integraciones nativas, pero estas pruebas no prueban todos los mods externos. La lista pública se limita a 128 clanes y el snapshot a 30 000 caracteres; configuraciones con cientos de miembros por clan pueden omitir las últimas filas. También pueden seguir apareciendo textos en inglés de la administración original.

El ZIP de fuentes incluye helpers, transformación ASM, stubs de compilación, pruebas y herramientas con SHA-256 fijados. Requiere Java 21 y Python 3. Verifica el hash exacto del JAR original antes de modificarlo:

```bash
python3 -B build.py /ruta/factionfriction-1.8.0R-1.21.1-NEOFORGE.jar
```

El JAR se genera en `output/`. Se comprueba también la reconstrucción desde una extracción limpia de las fuentes y la igualdad del contenido de cada entrada de los JAR resultantes.
