# Survivor Creator v17: perfiles, rasgos y equipo inicial

Minecraft 1.21.1 / NeoForge. Construido sobre el v16 suministrado por el usuario, con música, efectos auditivos, habilidades y otras funciones originales conservadas fuera de los cambios descritos.

## Instalar

Reemplaza v16 por `survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v17-profiles.jar` en cliente y servidor. Mantén una sola copia de Survivor Creator. No requiere borrar el mundo ni su configuración.

Para los objetos nuevos de OK Additions, instala también `okadditions-1.1.0-neoforge-1.21.1-compat.jar` en ambos lados, sustituyendo el original. Mantén GeckoLib 4 para NeoForge 1.21.1 (el log aportado ya contiene GeckoLib 4.9.3).

**La copia de OK Additions cambia únicamente el requisito de Minecraft `[1.21]` a `[1.21,1.21.2)`.** Todas sus clases, recursos, licencia y créditos originales a darako se mantienen. Este ajuste permite aceptar 1.21.1 en los metadatos; no demuestra compatibilidad binaria ni funcionamiento en juego. No se modifican sus mecánicas.

## Creación de personaje

- Sin límites de cantidad de rasgos positivos o negativos. Se mantienen presupuesto, incompatibilidades, requisitos y protección contra duplicados. También se elimina el antiguo tope de 64 entradas en los codecs de confirmación y sincronización; siguen vigentes los límites nativos de paquetes de Minecraft.
- **Cinco puntos adicionales** sobre `startPoints`, tanto en cálculo del cliente como en validación del servidor. Con la configuración original son 15 puntos antes de descontar profesión y rasgos. La configuración ya guardada se respeta y recibe el mismo bonus.
- Panel superior: exclusivamente profesión elegida, rasgos elegidos/incluidos y los objetos iniciales que dan. Los rasgos incluidos se marcan con `*`; pulsar un rasgo elegido lo retira. No se impone el anterior límite visual de cinco objetos. Se desplaza con la rueda.
- Panel inferior: niveles iniciales y efectos reales de la selección, incluyendo fitness, fuerza, puntería, recarga, mantenimiento, trabajo, atributos, consumo de energía y daño recibido. Barras de diez niveles. Pasa el cursor para leer detalles completos. Las descripciones de rasgos/profesiones se muestran al pasar el cursor en sus listas.
- Se asignan niveles iniciales moderados a profesiones y algunos rasgos, usando solo habilidades activas del mod. Ejemplos: instructor de fitness +3 y Atlético +2 = fitness 5; policía +3 y Buena puntería +1 = puntería 4, además de recarga 2. Se usa el mismo cálculo para la vista previa y el servidor.
- Los niveles se aplican en la primera confirmación de un personaje, sin rebajar XP existente ni sumarla cada vez que se reabre el menú. Después de morir, el nuevo personaje vuelve a partir de sus niveles iniciales; un perfil no guarda XP ganada durante una partida.
- Aprendiz rápido ahora concede realmente +20% de XP de habilidades, conservando su bonus de suerte. Aprendiz lento concede −20%. Las reglas previas de progreso y protección durante la creación permanecen.

## Perfiles

Abre **Perfiles: guardar / cargar**, escribe un nombre y pulsa Guardar. Para volver a usarlo, selecciónalo y pulsa Cargar. Puedes sobrescribir por nombre o eliminarlo. Al confirmar también se guarda `Último personaje` como plantilla de esa selección validada localmente; un rechazo del servidor no convierte esa plantilla en un personaje confirmado.

Los perfiles se guardan de forma atómica en `config/survivorcreator-profiles.xml`, en la instancia del **cliente**. Sobreviven a morir, reiniciar y cambiar de mundo. Contienen profesión y rasgos, no inventario, objetos, XP ni estado de una vida. Cargar un perfil no entrega objetos ni confirma automáticamente: comprueba que las definiciones y combinaciones sigan siendo válidas y el servidor vuelve a validar al confirmar. Si falta una profesión/rasgo por cambio de datapack, se informa del problema sin cambiar la selección actual.

## Contenido nuevo

**38 rasgos y 35 profesiones en total**: 14 rasgos y 10 profesiones nuevos sobre v16.

Rasgos positivos nuevos: Tirador entrenado, Corredor de fondo, Peleador callejero, Socorrista de campo, Rebuscador, Pescador aficionado, Manitas de emergencia y Preparado para problemas.

Negativos: Huesos frágiles, Sedentario, Manos temblorosas, Se agota fácilmente, Aprendiz lento e Inexperto en peleas. Las penalizaciones de niveles reducen los bonus iniciales, con mínimo cero; no crean niveles negativos. Sus otras penalizaciones permanecen activas.

Profesiones nuevas: Paramédico, Cazador, Recolector de suministros, Repartidor, Técnico de mantenimiento, Pescador profesional, Carnicero, Chatarrero, Explorador y Músico.

## OK Additions

Se verificaron 16 identificadores contra el registro real del JAR suministrado. Ejemplos de equipo:

| Profesión/rasgo | Objetos de OK Additions |
|---|---|
| Ladrón / Recolector / Manitas de emergencia | Palanca |
| Carpintero / Obrero | Cinta métrica |
| Mecánico / Técnico / Manitas | Lubricante para herramientas |
| Leñador / Explorador | Tomahawk de piedra |
| Guardaparques / Cazador / Explorador | Prismáticos |
| Ganadero | Lazo |
| Bombero | Compresas frías |
| Paramédico | Sales aromáticas |
| Repartidor | Bebida energética |
| Guardia / Preparado para problemas | Escudo ligero |
| Cazador / Preparado para problemas | Bolas de piedra |
| Chatarrero | Pico romo y varillas de hierro |
| Explorador | Marcador |
| Músico | Guitarra acústica y flauta |

Los objetos opcionales se omiten si no están registrados; Survivor Creator puede iniciar sin OK Additions. Se conservan objetos vanilla como respaldo. Las profesiones existentes reciben esos extras sin borrar sus objetos anteriores. Si un datapack reemplaza una definición con el mismo ID, sus datos pueden sustituir este equipo: es el comportamiento nativo del cargador del mod.

## Reconstrucción y comprobaciones

Java 21 y Python 3; herramientas ECJ/ASM y checksums incluidos:

    python3 -B build.py /ruta/survivorcreator-neoforge-1.21.1-1.0.9-zomboid-v16-death-audio.jar
    python3 -B patch_ok.py /ruta/okadditions-1.1.0.jar

Se necesitan los JAR originales suministrados, verificados por sus checksums. Los stubs y dobles de API **no se incluyen** en los JAR finales. El script conserva todas las entradas originales que no necesitan cambios y valida JSON, referencias de rasgos, efectos y objetos de OK Additions.

Pasaron **45 comprobaciones** de perfiles persistentes/sobrescritura/eliminación, selección inválida, más de 64 rasgos, puntos, conflictos con rasgos incluidos, niveles iniciales, conservación de XP, ausencia de acumulación, modificadores de aprendizaje, protección original, equipo opcional presente/ausente y ejecución de la interfaz con dobles de API en distintos tamaños. Se ejecuta el bytecode real de `XpRules` parcheado y `StartItemEffect` original, con `-Xverify:all`; ASM analiza las cuatro clases transformadas.

**No se ejecutó Minecraft ni el modpack completo.** Estas pruebas no verifican el aspecto de las pantallas, las físicas ni la compatibilidad binaria de OK Additions con 1.21.1. Comprobar en juego: elegir más de seis rasgos de cada tipo compatibles, contrastar puntos/beneficios, guardar un perfil, confirmar, revisar niveles y equipo, morir y cargar el perfil, y probar los objetos de OK Additions.

El `latest.log` aportado muestra Survivor Creator cargado, callbacks instalados y motor OpenAL iniciado. No contiene errores de sus eventos de música que permitan determinar por qué no se escuchan. v17 conserva la lógica de audio de v16; el volumen Música sigue aplicándose.
