# Hardcore True Darkness 6.1: noche algo más visible

Sustituir `hardcore_true_darkness-6.1.jar` por `mods/hardcore_true_darkness-6.1-night-visibility.jar`, con Minecraft cerrado. No mantener los dos JAR simultáneamente.

El accessor cliente `ClientDarknessState.RenderState.intensity()` devuelve el 82 % de la intensidad original cuando `night=true` y `cave=false`. Tanto `LightmapDarkener` como `FinalDarknessPostProcessor` leen ese valor: se suavizan juntos el oscurecimiento del lightmap y el filtro final, incluido su fallback. No se aumenta la gamma ni se añade luz artificial a superficies negras. La reducción de intensidad no equivale a un porcentaje fijo de brillo final: depende de luz local, shaders y preservación de zonas iluminadas.

Se conserva la curva de amanecer/anochecer. El día, las cuevas profundas y las dimensiones sin cielo mantienen el comportamiento original. No cambia los paquetes de red ni las reglas del servidor; es un ajuste de renderizado cliente y no requiere convertir el mundo.

Validación: 16 combinaciones sobre la clase real del JAR parcheado (día/noche, cueva/exterior e intensidades 0, 0.2, 0.5, 1), estado desactivado, verificador JVM, ASM BasicVerifier y comparación binaria de todas las entradas ajenas al cambio. No se ha ejecutado Minecraft en este entorno; el resultado visual con el modpack requiere comprobarlo dentro del juego.

Reconstrucción con Java 21 y herramientas ECJ/ASM:

```sh
python build.py /ruta/hardcore_true_darkness-6.1.jar /ruta/tools
```

La carpeta `tools` debe contener `ecj.jar`, `asm.jar`, `asm-tree.jar` y `asm-analysis.jar`.
