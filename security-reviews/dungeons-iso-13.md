# Revisión estática de Dungeons Iso .13

Archivo: `dungeons_iso-neoforge-3.6.2+1.21.1-zomboid-tacz.13.jar`.

SHA-256: `e21ed57fe0afa3f62467bf89adf3be2838a721c9c737124f2a620c97f569f75c`.

Defender del usuario muestra `Trojan:Script/Wacatac.B!ml`. No se ha ejecutado Defender, otro antivirus ni Minecraft en este entorno. Esta revisión no determina si esa detección es un falso positivo, ni certifica ausencia de malware. No se ha verificado el hash del archivo en el equipo del usuario.

## Comprobaciones realizadas

- Descarga nueva de la URL raw de GitHub: idéntica byte a byte al JAR local revisado.
- Reconstrucción aislada con las fuentes publicadas y el JAR original del usuario: todas las entradas del ZIP idénticas. Los metadatos de fecha del ZIP pueden diferir; no se usa ese hash para comparar reconstrucciones.
- Integridad del ZIP correcta; sin nombres duplicados, rutas absolutas o rutas con `..`.
- Respecto al original solo cambian cuatro clases de mixins y `dungeons_iso.mixins.json`; se añaden quince clases del parche. Ningún recurso original ni dependencia se ha alterado.
- Inspección de bytecode de 120 clases del mod y 16 de `tiny-config-3.1.0-neoforge.jar`: sin llamadas directas a Runtime, ProcessBuilder, java.net, ScriptEngineManager, System.load/loadLibrary o métodos nativos; sin literales URL o comandos ejecutables en el filtro aplicado.
- Dependencia incluida `tiny-config`: idéntica a la del JAR original. No hay nuevos archivos EXE, DLL o scripts ejecutables. Se revisaron sus lecturas y escrituras de configuración.
- Descompilación del JAR final: no se encontró ejecución de procesos, descarga remota o carga de payloads en los cambios añadidos.
- El helper `R` contiene reflexión (`Class.forName`, acceso a campos/métodos y constructores); se revisaron sus llamadas en los helpers. Los destinos usados son clases del juego, TACZ, Dungeons Iso y JOML; no se observó obtención de nombres de clases desde una URL o ejecución de comandos.
- Las escrituras de configuración identificadas pertenecen al mod original y a tiny-config, preservados sin cambios. Los paquetes de interacción del parche son los del juego; no se añadió un canal de red externo.
- 132 comprobaciones funcionales con APIs simuladas y verificación ASM aprobadas. Son comprobaciones de lógica y bytecode, no un escaneo antivirus ni validación dentro de Minecraft.

## Resultado y límites

No se encontraron indicadores de malware en esta revisión estática del archivo publicado. Esto no constituye una garantía de seguridad. El filtrado de APIs/literales no cubre por sí solo todas las formas posibles de comportamiento malicioso; por eso también se compararon las entradas y se revisaron los helpers descompilados. No se comprobó con firmas de Defender ni se analizó la ejecución del mod junto con otras dependencias instaladas.

Mantener en cuarentena el archivo señalado mientras se investiga. No desactivar Defender ni añadir una exclusión basándose únicamente en este informe. La ruta de «Elementos afectados» y el hash del archivo detectado, si está disponible sin restaurarlo, permitirían confirmar que Defender señala exactamente esta copia.
