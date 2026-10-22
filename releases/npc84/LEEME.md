# Tactical .50 — corrección del crash al colocar muebles

## Instalar
Retira tactical .49 (deja una sola versión de tacznpcs) y coloca tactical .50. Conserva Ragdollified .34, remains .28 y Gore .43. Aplica también al servidor si corresponde y reinicia Minecraft.

## Causa y cambio
El informe señala NoSuchMethodError: Dialogue82.c(Object,String,Object[]). El parche ASM .49 había cambiado el propietario de 36 llamadas en Base48.place sin cambiar el nombre de c a call. Dialogue82 declara call; no declara c. Este error es del parche, no de tu configuración.

.50 cambia únicamente esas 36 referencias y la versión interna del mod. No cambia otras clases, recursos ni comportamientos de .49: conserva tablones exteriores, recetas, cajoneras y diálogos. El generador original del parche fue corregido para incluir el nombre de método y evitar repetir el fallo.

## Verificación
- La ejecución de Base48.place de .49 reproduce el mismo NoSuchMethodError usando un mundo simulado.
- Con .50, Base48.place coloca una mesa de crafteo y consume exactamente su objeto.
- Análisis de cada clase: los enlaces a métodos de Dialogue82 y Decor82 resuelven sus firmas; .49 tenía 36 enlaces rotos, .50 tiene cero.
- Verificación ASM de 201 clases y JVM -Xverify:all en las pruebas.
- Pasan las 15 comprobaciones previas de diálogo/peligro/horno/recetas/geometría exterior y las 10 de cadáveres/menús/protección.
- Comparación del JAR: solo Base48.class y META-INF/neoforge.mods.toml cambiaron.

NO se ejecutó Minecraft real. Estas comprobaciones no prueban navegación, renderizado ni convivencia completa del modpack. Al probar: carga una copia del mundo afectado, deja que el NPC siga colocando muebles y comprueba que no se repita el crash. Luego revisa el exterior de las ventanas y la colocación de cajoneras con recursos suficientes.

Incluidos SHA256SUMS.txt y fuentes-y-pruebas.zip. Las herramientas usan ASM/Java 21 y fixtures locales de compilaciones previas; no constituyen una instancia Minecraft independiente.
