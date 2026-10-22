# Validación de fix4

Se publica el JAR recibido sin modificar sus bytes y el ZIP original con fuentes y diffs.
Sustituye fix1, fix2, fix3 y la .43 anterior. Instalar una sola versión de TaCZ NPCs.

Revisión: siete clases modificadas respecto de .43, todas Java 21.
Pasaron 34 comprobaciones JVM contra las clases del JAR con APIs simuladas:
respeto de gathering.next, liberación de tarea durante espera, conservación de tarea
durante desplazamiento, reanudación del escaneo, backoff y liberación de ayudantes.

NO probado en Minecraft real. No se afirma validación visual, navegación real ni carga
del modpack. Pendiente: puertas, terreno irregular, mochila llena, pérdida de líder y
convivencia con Life79. Probar a 20 TPS con copia del mundo/JAR anterior.
