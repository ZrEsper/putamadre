# Visor y visión nocturna vinculados — Fracture Point .2

## Instalar

1. Cierra Minecraft y el servidor.
2. Quita `fracturepoint-1.21.1-3.0.0-PT2-nvg-15min.1.jar` de `mods`.
3. Pon el JAR de esta descarga en `mods`, tanto en clientes como en servidor. No dejes dos Fracture Point.

## Una sola tecla: N

**N** sube o baja el visor con la animación original:

- **Visor bajado:** activa la visión especial de las gafas instaladas, si tienen batería.
- **Visor levantado:** desactiva la visión y deja de gastar batería.
- **Sin gafas o sin batería:** el visor puede moverse, pero la visión no funciona gratis.

La antigua tecla independiente de visión deja de aparecer como otra acción asignable. En Controles busca **«Subir/bajar visor y visión nocturna»**. El antiguo valor por defecto **H** se migra a **N** y se elimina la antigua asignación independiente a N. Si habías elegido otra tecla personalizada, se conserva; puedes cambiarla a N en Controles.

Los cascos de otros mods admitidos por la configuración de Fracture Point que no tienen visor físico usan **N** para encender/apagar sus gafas directamente.

## Cambio de casco

Al cambiar de casco, se leen las gafas y la batería del casco nuevo. Si su visor está bajado, se activa su modo correspondiente sin tener que apagarlo y volverlo a encender. También funciona al cambiar entre dos ejemplares del mismo modelo de casco que llevan gafas distintas.

La batería anterior conserva la energía que había gastado; el casco nuevo no hereda la carga de la anterior. Si el visor nuevo está levantado, no se enciende hasta bajarlo.

## Disparos y efectos visuales

El gestor comprueba si el shader de visión sigue siendo el que cargó. Si un efecto de daño u otro efecto lo elimina o sustituye mientras la visión debe estar activa, invalida el estado antiguo y permite cargar de nuevo el modo correcto en la siguiente pasada de render. No requiere pulsar N otra vez ni cambiar de casco.

También se restablece el modo correcto si se pierde su indicador de activación. El shader no se recarga cada fotograma cuando funciona normalmente.

Se conservan los filtros, colores, máscaras y texturas originales: nocturna verde, nocturna simple, digital, térmica, blanco caliente y negro caliente. No modifica el daño recibido ni elimina los indicadores de heridas del HUD.

## Batería

Se mantiene la duración de **15 minutos de uso activo por batería completa** y el efecto de visión nocturna mientras tenga carga. Levantar el visor detiene el consumo. Cuando se agota, la visión se apaga y no entra en un bucle de reactivación por datos antiguos del cliente.

## Prueba en tu mundo

Prueba en una copia del mundo: baja/sube el visor con N; cambia entre dos cascos de modos distintos y luego entre dos del mismo modelo; recibe un disparo con la visión encendida; finalmente prueba una batería casi vacía. Comprueba también el cambio tras reconectar al servidor.

Pasaron **930 comprobaciones con APIs simuladas**, incluidas las 900 fases de duración de batería, además de las comprobaciones de conexión del bytecode. **No se ha ejecutado Minecraft en este entorno:** falta validar la animación y los filtros con tu conjunto completo de mods. Si falla, envía el `latest.log` de esa sesión y los nombres del casco y de las gafas.
