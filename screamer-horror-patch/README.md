# Screamer horror .1

[Guía de instalación, comportamiento y límites](GUIA.md). Añade huevo de aparición y sonido registrado, convierte el audio del usuario a Vorbis mono, acelera el screamer y superpone poses asimétricas sincronizadas con el grito. Mantiene el módulo de atracción de hordas existente y la versión interna 1.4.0 compatible con Integration 1.7.8.

Desde la raíz: `python screamer-horror-patch/build.py`. Requiere Java 21, FFmpeg/FFprobe y los ECJ/ASM fijados por SHA-256 del entorno existente. Entradas fijadas en `inputs.sha256.json`. `expected-changes.json` limita las entradas cambiadas. Todas las clases ajenas al screamer se conservan, salvo el gancho de registro en el constructor del addon. Las API de pruebas no se incluyen en el mod.

18.020 comprobaciones. Se ejecuta el tick real de Screamer con dobles de APIs; la pose y registro se prueban con fixtures. No se ejecuta Minecraft ni su bus/renderizador real. El constructor usado para el huevo es la API `DeferredSpawnEggItem(Supplier, int, int, Item.Properties)` de NeoForge 1.21.1, verificada contra su fuente oficial.

`python screamer-horror-patch/package.py` reúne este addon y el parche FirstAid de `armor-bullet-tolerance-patch` con guía y hashes.
