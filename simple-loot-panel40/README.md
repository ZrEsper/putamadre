# Panel sencillo y loot efectivo · Hordes .40

[Guía](GUIA.md). Parche sobre el .39 exacto del usuario. Pantalla por defecto con cinco perfiles directos y dos bloques de cinco campos. Generación por cantidad absoluta media, probabilidades directas de armas, ropa y medicinas y munición condicionada al arma. Permite resultado original vacío en barriles; conserva colección FastUtil y rutas especiales de militares/NPC/mutantes. Profesiones avanzadas siguen seleccionando suministros dentro de los límites del panel. Revisa también la antigua vista de ejemplo para evitar datos de un algoritmo que ya no se usa.

Desde la raíz: `python simple-loot-panel40/build.py`. Java 21 y ECJ/ASM con hashes fijados del entorno existente. Entrada exacta fijada en `inputs.sha256.json`. Solo se alteran las siete clases indicadas en `validation.json` y se añaden tres helpers; recursos, mixins y clases de IA/spawn se conservan.

70 comprobaciones y 2.500 inventarios simulados. Los tests ejecutan los helpers reales, el preparado nativo parcheado y el guardado/carga reales de WorldTuning sobre disco con servicios del juego simulados. Las pruebas de pantalla usan widgets y APIs simuladas: no prueban el renderizador ni la red de Minecraft. No se empaquetan los fixtures.

`python simple-loot-panel40/package.py` genera el ZIP de reemplazo con guía sin conexión e informe.
