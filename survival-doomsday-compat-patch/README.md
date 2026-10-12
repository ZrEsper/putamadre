# Survival 1.1.0 FF integrado: compatibilidad Doomsday Storage

El `latest.log` aportado registra el bloqueo antes de iniciar los mods:

```
Missing or unsupported mandatory dependencies:
Mod ID: 'doomsday_decoration', Requested by: 'zomboid_survival',
Expected range: '[1.1.4.2,1.1.4.2]', Actual version: '1.1.4.2-zomboid-storage.1'
```

La restricción exacta de Survival sólo permitía el Doomsday original. Este reemplazo modifica únicamente `META-INF/neoforge.mods.toml`, cambiando el rango a `[1.1.4.2,1.1.4.3)`. Acepta el original y las versiones Storage .1/.2, sin permitir otra revisión base. Se mantiene la versión publicada de Survival 1.1.0 para no romper dependencias de otros mods.

Todos los bytecodes, mixins, texturas, recetas, configuración y biblioteca FF integrada se conservan byte por byte. No se reemplaza el motor de caducidad ni se alteran objetos guardados.

## Instalar

Con Minecraft/servidor cerrado y copia del mundo:

1. Retirar `zomboid-survival-1.21.1-1.1.0-ff-preservation-integrated.jar` e instalar `zomboid-survival-1.21.1-1.1.0-ff-preservation-doomsday-compat.1.jar`.
2. Retirar Doomsday original/Storage .1 e instalar `doomsday_decoration-1.1.4.2-neoforge-1.21.1-zomboid-storage.2.jar`.
3. Hacerlo en cliente y servidor, dejando una sola copia de cada mod.

Storage .2 registra las casillas físicas en el constructor común `PortedBlock`, también cuando el mixin HEAD de Survival se salta `BlockFactory.create` para crear su propia máquina. Interactuar con una casilla vinculada reenvía la interacción al bloque principal, preservando su método original, incluido el refresco de alimentos FF y las funciones de agua de la nevera.

## Verificación

- Se reprodujo el rechazo con el analizador Maven real (`maven-artifact` 3.9.9) y se comprobaron siete casos: acepta Doomsday original, Storage .1 y .2; rechaza 1.1.4.1, 1.1.4.3 y 1.2.0.
- Bibliotecas de validación verificadas con los checksums publicados de Maven Central. No se incluyen en el mod.
- Validación TOML/JSON, integridad ZIP y comparación de todas las entradas: sólo cambia el TOML.
- Storage .2 incorpora regresiones de registro sin pasar por la fábrica y de interacción original de máquinas desde sus partes. Ver `../doomsday-functional-patch/validation.json`.
- **No se ha ejecutado Minecraft aquí.** Esta corrección elimina la restricción que aparece en el log; no garantiza que no haya otros fallos al avanzar la carga. Las advertencias de sonidos/modelos de otros paquetes no se modifican.

## Reproducir el parche

Python 3.11 o posterior:

```bash
python survival-doomsday-compat-patch/build.py /ruta/zomboid-survival-1.21.1-1.1.0-ff-preservation-integrated.jar
```

Se verifica el hash del JAR de entrada con `input.sha256.json`. `VersionChecks.java` puede compilarse con Java 21 contra las bibliotecas de validación detalladas en `version-parser-checksums.json` para repetir los casos Maven; no exige Minecraft ni modifica el JAR.
