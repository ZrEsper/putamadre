# Blue's + MutantsZombies — Gore revisión 1

Dos packs para Minecraft NeoForge 1.21.1. Usan Entity Model Features **3.3.11** y Entity Texture Features **7.2.5** aportados por el usuario. No se modifican sus JAR. Se conserva el trabajo original de Blue's, el estilo PZ/L4D2 y las animaciones base.

## Instalar

En `resourcepacks/`, sustituir los dos packs antiguos por los dos ZIP de esta revisión y activarlos. El de MutantsZombies debe tener prioridad sobre cualquier otro pack que cambie sus modelos. Los packs son de cliente; el servidor conserva sus mods actuales. EMF/ETF son de cliente, con sus dependencias ya presentes en el modpack. Habilitar en EMF modelos personalizados para **todos**, incluidos mods (ALL, valor predeterminado de la versión aportada). Recargar recursos con F3+T o reiniciar.

El paquete incluye los dos JAR EMF/ETF originales en `client-mods/` como referencia de instalación: no duplicarlos si ya están instalados. MutantsZombies debe estar instalado; los packs no añaden entidades ni cambian IA, atributos, spawns, habilidades o loot.

## Cambios

- Texturas con heridas, sangre oscura, ropa desgarrada, huesos/costillas y partes de cráneo expuestas. Se usan imágenes generadas específicamente para los atlas originales; el procesamiento con ImageMagick recupera exactamente sus dimensiones y máscara UV alfa. No se pintan huecos transparentes ni se cambia el tamaño de los atlas.
- Blue's conserva sus modelos anteriores y añade **seis variantes** a las selecciones generales de adultos: heridas frescas, huesos expuestos, ropa desgarrada, granjero ensangrentado, mandíbula deformada y reptador. Mantiene las reglas de nombres/profesiones y variantes infantiles.
- MutantsZombies adapta las **siete especies** al sistema de modelos y animaciones de Blue's mediante las capas modded de EMF. Tiene **tres variantes de modelo/textura por especie**, 21 modelos en total. Crawler utiliza la familia reptadora. Los brutos son mayores; las capas de Split Head y las múltiples cabezas de Spitter se adaptan a sus nombres nativos, sin intentar usar partes que su modelo no contiene.
- Balanceo, miembros asimétricos, reacción a golpes, mandíbula, poses y caídas proceden de las animaciones de Blue's. El daño visual se calcula como porcentaje de salud para que los mutantes con mucha vida también lo muestren.
- Brazos y cabeza pueden desprenderse visualmente por umbrales de salud distintos y variación por entidad. Su caída tiene desplazamiento acotado y una desaparición gradual después de unos tres segundos, en vez de salir disparados decenas de unidades. Las partes expuestas y huesos del modelo se conservan.
- Se conservan las máscaras emisivas originales; la sangre y los huesos no se hacen luminosos artificialmente. Las siete texturas de los modelos nativos de mutantes también se mejoran como fallback.

## Límites reales

El desprendimiento de Blue's es **visual dentro del modelo**, no crea un objeto físico, una entidad de loot ni una simulación de colisión contra terreno/agua. Las partes siguen perteneciendo al render del zombi. La física de muerte y los cadáveres de loot siguen dependiendo de Saros/Mob Dismemberment/Remains y no se modifican aquí. Un reinicio/recarga puede reiniciar el estado visual de animación. Las cajas de colisión nativas de mutantes se conservan aunque cambie su aspecto.

Se verificaron las capas de MutantsZombies contra su JAR 1.3.1 y el descubrimiento de modelos modded, variables `health`/`max_health`, nombres de archivos y directorios contra EMF 3.3.11. Validación de ZIP, dimensiones y alfa UV, modelos, texturas referenciadas, variantes/pesos, IDs y expresiones; reproducción del build desde fuentes. No se ha ejecutado Minecraft ni validado visualmente el modpack completo aquí. Si otro pack sobrescribe modelos, colocarlo debajo de estos dos.

## Reconstruir

Python 3 con Pillow (solo lectura/validación de imágenes) e ImageMagick `magick`. Las fuentes contienen las bases aportadas y el arte generado, con hashes SHA-256; no necesitan acceso a red.

```sh
python3 build.py
```

Los originales retienen sus autores y créditos. La aportación de esta revisión es la adaptación, reglas, arte de heridas y procesamiento de recursos, no la autoría del sistema original de Blue's.
