# TACZ 9: puntería, animaciones y compatibilidad isométrica

Reemplazo de TACZ 8 para Minecraft 1.21.1, NeoForge **21.1.255 o posterior**, Java 21. Incluye el port nativo de Tacz Animation Additions 1.2.5 (Locknar, MIT).

- Mira adaptativa en tercera persona normal, inspirada en TP Shooting: apunta desde la cámara y verifica las paredes desde los ojos del jugador. No requiere Shoulder Surfing. Primera persona y el zoom anterior de scopes se conservan.
- Indicador rojo breve de la dirección de los disparos recibidos, relativo al giro de la cámara.
- Destello blanco de scopes de snipers al apuntar hacia el observador, con comprobación de paredes y hasta 24 candidatos cercanos.
- Impactos más grandes en paredes, con textura irregular y tamaño según arma.
- Animaciones de inspección de munición, sprint, postura, espejo, patada y cuerpo a tierra migradas a registros, componentes y paquetes de NeoForge 1.21.1.
- En isométrica, el asiento/cabina de Immersive Vehicles no vuelve a sobrescribir la cámara. Sus ajustes funcionan normalmente al desactivar Iso. El zoom de scopes, animaciones de cámara, espejo y cámara de cuerpo a tierra respetan Iso.

## Instalación

Instalar únicamente `mods/tacz-neoforge-1.21.1-1.1.8-hotfix-r7-third-person-pz.9-tps-animations-feedback.jar` en cliente y servidor, reemplazando TACZ anterior. Quitar el JAR separado de Animation Additions y TP Shooting para evitar duplicar funciones. Mantener Dungeons Iso 22 y los packs de armas existentes. No instalar todas las versiones históricas del repositorio.

La corrección isométrica está en TACZ 9; no hace falta un nuevo JAR de Iso. MTS es opcional. No cambia sus físicas ni los controles de conducción.

## Construcción y comprobación

Desde esta carpeta:

```bash
python3 run-gradle.py build
python3 merge.py
cd validation
python3 run-gradle.py runGameTestServer
```

El helper permite `JAVA_HOME` y `GRADLE_USER_HOME` propios; en el entorno cloud usa el JDK 21 verificado y la caché ya preparados. El merge descarga ASM 9.7.1 verificando SHA-256. Usa TACZ 8 del repositorio como base y fija el `modid` de sus 38 suscriptores para evitar registrarlos dos veces al integrar ambos mods.

Se comprobaron cuatro GameTests nativos y un cliente gráfico con mundo aislado, Dungeons Iso 22 y MTS mechanics13. La prueba gráfica verificó atlas e impactos, mira, recepción del paquete de daño, cache de snipers y ejecución del handler real de cámara de MTS en Iso. No se ha probado una sesión larga conduciendo con el modpack completo. Los mundos y logs privados no están incluidos.

`validation/ClientSmoke` es exclusivamente una fixture de desarrollo; requiere un save aislado llamado FeedbackTest para QuickPlay y Xvfb/display. El resultado debe ser `PASS` y `CLIENT_SCENE_PASSED`, además del build. La reproducción no debe usar mundos del usuario.

Fuentes de Animation Additions: port de Locknar, licencia MIT declarada por el original; metadatos originales en `docs/`. El comportamiento de mira de TP Shooting se implementa de forma independiente; no se incluye su plugin ni sus dependencias.
