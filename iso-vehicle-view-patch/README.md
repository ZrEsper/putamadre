# Vista isométrica de vehículos (.20)

Entrada: Iso `.19-vehicles-food` aportado por el usuario. Salida: `.20-vehicle-visibility`.

La protección de entrada nativa de .19 terminaba `tickXIVHEAD` cuando el jugador estaba montado o comiendo, antes de `SodiumCompat.run()`. .20 conserva esa protección, pero ejecuta el mantenimiento visual antes de retornar. El recorte de líneas de visión usa el volumen del coche completo cuando el jugador va sentado; CTRL sigue desplazando ese foco. No se modifican las coordenadas de alcance del jugador ni la interacción de mochilas.

Mientras está activo el modo isométrico se mantienen visibles las entidades auxiliares `mcinterface1211.BuilderEntity*` cercanas. Si EntityCulling aporta sus métodos, se restablecen sus indicadores de ocultación y se solicita visibilidad forzada. No se cambian los indicadores de zombis u otras entidades; sin EntityCulling, las llamadas opcionales inexistentes se omiten.

Pruebas: 12 casos simulados de foco del coche, foco desplazado con CTRL, actualización visual montado y exclusión de entidades ajenas a MTS. Se analizaron 886 métodos con ASM y se verificó la integridad del JAR. No se ejecutó Minecraft: el aspecto del recorte y la compatibilidad con el conjunto real de optimizadores requieren prueba en el juego.

```sh
python iso-vehicle-view-patch/build.py ISO_19 TOOLS
```
