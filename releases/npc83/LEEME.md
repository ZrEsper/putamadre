# Ragdollified .34 — corrección de dependencia

Retira ragdollified .33 (y cualquier otra copia del mismo mod) y coloca el JAR .34 de esta carpeta. Conserva Gore .43, tactical .49 y remains .28. Hazlo en cliente y servidor si utilizas ambos.

Causa: Gore .43 exige exactamente [1.1.5-gore20-perf57]. La entrega .33 cambió ese identificador interno y NeoForge rechazó la dependencia antes de iniciar.

Se restaura el identificador interno requerido. El nombre del archivo y el nombre visible del mod distinguen la entrega .34. Todas las clases y demás recursos son idénticos a .33: no revierte la corrección de cadáveres duplicados. Solo cambia META-INF/neoforge.mods.toml.

Verificación: TOML parseado, requisito exacto Gore -> Ragdollified satisfecho, versión de Gore 1.0.20.68 dentro del rango inverso [1.0.20.57,1.0.21), comparación de cada entrada del JAR confirma que ninguna clase cambió. No se ejecutó Minecraft real; esto corrige el rechazo de dependencia mostrado y no garantiza ausencia de otros fallos del modpack.

Incluye fix.py y SHA256SUMS.txt. El script contiene rutas locales de esta compilación y puede adaptarse para reproducir el cambio.
