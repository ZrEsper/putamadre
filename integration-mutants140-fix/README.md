# Integration 1.7.8 · Compatibilidad declarada con Mutants L4D2 1.4.0

El log enviado muestra el primer bloqueo de carga: Integration 1.7.7 exige `[1.3.0,1.4)` para `mutants_l4d2`, pero está instalado el addon 1.4.0. El extremo superior es exclusivo: NeoForge rechaza esa combinación incluso siendo una dependencia opcional. Los mensajes posteriores de `broken mod state` son consecuencia del bloqueo.

## Instalar

Cierra Minecraft y el servidor. Retira `zomboid-integration-1.7.7-neoforge-1.21.1-zone-admin-archive-controls.jar` y coloca `zomboid-integration-1.7.8-neoforge-1.21.1-mutants-1.4-compat.jar`. No dejes ambos Integration en `mods`. Actualiza clientes y servidor.

Conserva `mutants-l4d2-addon-1.4.0-neoforge-1.21.1-screamer-vomit-bile.jar`, Hordes .35, Remains .11 y los demás mods. No hace falta bajar de versión el addon ni borrar el mundo.

## Cambio y comprobaciones

Solo cambia `META-INF/neoforge.mods.toml`: Integration se identifica como 1.7.8 y admite el intervalo `[1.3.0,1.4.0]`, que incluye exactamente 1.4.0 sin declarar compatibles futuras versiones desconocidas. Todas las clases, recursos, mixins y las demás restricciones permanecen idénticos al JAR adjunto.

Se verifican las cuatro referencias a funciones públicas del addon, los dos métodos de destino de sus mixins y tres miembros Shadow contra el JAR 1.4.0 adjunto. Se comprueban firmas y métodos estáticos, integridad ZIP y conservación de todos los archivos. Estas comprobaciones estructurales no ejecutan el transformador Mixin ni Minecraft; falta confirmar el arranque en tu instalación.

Construir desde la raíz: `python integration-mutants140-fix/build.py`. Requiere Java 21 y los ECJ/ASM del entorno existente, con hashes fijados. Los JAR originales de entrada también se fijan por SHA-256 en `inputs.sha256.json`. El informe es `validation.json`.
