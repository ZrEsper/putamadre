# Perfiles de apocalipsis 31

Parche reproducible para las cuatro versiones exactas adjuntas. NeoForge 1.21.1, Java 21.

`python build.py` compila, valida hashes de entradas/herramientas, aplica puntos de integración concretos y ejecuta pruebas con dobles de la API. Los dobles nunca se incluyen en los mods. Las herramientas verificadas están en `../hordes-melee-interiors-patch/build/tools`; se conservan en este entorno preparado. Pillow se usa únicamente para analizar las texturas del catálogo y generar el recurso del ítem.

Los cambios de aplicación corresponden a la solicitud explícita del usuario; el onboarding usa el checkout existente. No se necesita iniciar servicios para compilar ni ejecutar las pruebas de este parche. El cliente y servidor reales de Minecraft todavía deben probarse con el modpack completo.

La guía de uso está en GUIA.html y GUIA.md. El análisis de TZP es una heurística de ropa, editable, no una clasificación perfecta de todas las profesiones. El cliente con ETF informa la textura y su hash; el servidor acepta únicamente coincidencias de su catálogo para entidades próximas y visibles. Es información visual del cliente: en un servidor con clientes no confiables se puede desactivar «Reconocer equipo y nombres», que también desactiva reportes de textura. Las profesiones y equipo reales tienen prioridad.
