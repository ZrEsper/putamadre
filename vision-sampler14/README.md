# Fracture Point .14: estado de texturas y samplers

`python vision-sampler14/build.py` reutiliza Java 21 y ECJ existentes y la base .13 fijada por SHA. Solo cambia Gpu12 y su State. El control de visor manual, caché de filtros, filtro después de manos/arma, shaders, modelos, sonidos y recursos permanecen idénticos.

El puente GPU usa el mismo código comprobado de Flashlight .14 para sincronizar bindings reales/caché, aislar samplers y restaurar el bloqueo de mezcla de Iris. Así el postproceso propio no hereda las reglas de muestreo del shaderpack ni deja su estado alterado. `test.py` verifica la equivalencia del puente y el diff de clases/recursos; Inspect.java verifica bytecode de los tres mods de la entrega. Los tests de bindings/samplers de producción y OpenGL están en flashlight-render14. No se ejecutó aquí Minecraft con MakeUp.
