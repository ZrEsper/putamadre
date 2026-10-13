# TZP Gore 9: base Gore.8 y piernas sincronizadas

Esta entrega usa EXACTAMENTE el ZIP TZP-1.21.1-Gore.8-local-blended-surfaces adjuntado por el usuario como base. El pack anterior que entregamos partia de otra version y no contenia sus superficies locales. Eso queda corregido.

## Instalacion

Cierra Minecraft. Guarda una copia de los packs actuales. Coloca el ZIP completo TZP_1.20.1_2.7.zip de esta entrega en resourcepacks y reemplaza el ZIP anterior con ese nombre. Retira/desactiva los otros ZIP TZP/Gore antiguos para que no sobreescriban esta version. Se conserva el nombre canonico TZP_1.20.1_2.7.zip para los ajustes de prioridad y la deteccion del mod de integracion; la descripcion en el menu sera TZP Gore 9.

Requiere EMF/ETF y Zombie Gore 1.0.7 weapon-trauma-crawl-fix de la entrega f759db6c7b4121ff9c725579794c792fa03a6870. Instala SOLO una version de Zombie Gore, coincidente en cliente y servidor. El resourcepack se instala en los clientes. Si ya instalaste ese Gore 1.0.7 no necesitas volver a reemplazarlo.

Activa este ZIP en el menu Resource Packs. Para comprobar su contenido sin alterar el orden de tus otros packs, evita tener otro pack TZP o modelos de zombie por encima de el. El ZIP conserva los sonidos/texturas/variantes del original; no necesita un parche adicional ni extraerse.

## Cambios

Se conservaron byte por byte 4051 de las 4056 entradas de Gore.8. Se modificaron solamente pack.mcmeta y cuatro modelos .jem: zombie, husk, drowned y drowned_outer. Todos los modelos, cajas, texturas y geometria de superficies de heridas se conservan. Las expresiones de las animaciones normales de Fresh Animations y las condiciones visuales de heridas permanecen originales.

El pack usaba zg_rear, que no es una variable registrada por el Gore 1.0.6/1.0.7 suministrado. Ahora var.zg_rear lee zg_exit, la mascara de heridas traseras/salida registrada por ese mod. Esto conserva el mapa delantero/trasero de sus superficies balisticas, golpes y cortes, y elimina la referencia indefinida de la primera tabla de animaciones.

El criterio visual de arrastre antes era perder cualquiera de las piernas o tener ambas fracturadas. Gore 1.0.7 activa su hitbox de arrastre cuando se pierden ambas piernas. El pack ahora usa EXACTAMENTE ese mismo criterio; una sola pierna perdida conserva postura erguida y caja normal. Las piernas perdidas se ocultan tambien en la capa exterior del ahogado.

La animacion de arrastre original giraba el helper root adjunto y despues mantenia varias partes con rotaciones distintas. El root de los cuatro .jem no contiene el cuerpo y las extremidades como submodelos. Esta entrega coloca directamente las partes reales: torso horizontal, cabeza baja orientada hacia la mirada y brazos alternados vinculados al desplazamiento. Al detenerse no bracea continuamente; la amplitud depende de limb_speed. Se mantienen las reglas de brazos heridos/fracturados del pack base. La capa exterior del ahogado sigue la misma pose y condiciones de piernas.

No hay cambios a la IA, velocidad, loot ni estadisticas en este resourcepack. La hitbox se corrige en el mod Gore 1.0.7: adulto 1.8 x 0.82 bloques, ojos a 0.5, escalada para variantes. Su mayor anchura puede afectar pasajes estrechos. Los ajustes de animacion se aplican a los tres tipos vanilla del pack y a la capa de drowned; no sustituyen modelos de MutantsZombies.

## Validacion

CRC correcto del ZIP completo; comprobacion de JSON de todos los .jem/.jpm/.mcmeta; conservacion byte a byte de todas las entradas ajenas a los cinco cambios; preservacion estructural de geometria/submodelos/cajas y grupos de animaciones normales/superficies. 1024 estados de piernas comprobaron que el criterio del pack coincide con Gore 1.0.7. Las referencias personalizadas usadas por los cuatro .jem se contrastaron con las variables registradas en el bytecode de Gore 1.0.6. 24576 proyecciones matematicas de esquinas de un humanoide adulto estandar (incluida capa de piel exterior de cabeza) caben en la caja 1.8 x 0.82 durante distintas fases y velocidades del arrastre. Se evaluaron las nuevas expresiones del pack para esa prueba.

No se ejecuto Minecraft/EMF. La proyeccion matematica verifica una envolvente humanoide estandar, no sustituye un render real del modelo ni una prueba de impactos. Para probar en tu instancia: F3+B, zombi sano, una pierna perdida, ambas piernas perdidas; apuntar al torso/cabeza que se vean dentro de la caja. Comprueba tambien las heridas delanteras y traseras, los ahogados y su capa exterior.

El original contiene assets/minecraft/sounds/mob/sounds.json con sintaxis no valida para un parser JSON estricto. Ese archivo no esta en la ruta de registro de sonidos assets/minecraft/sounds.json; se conserva exactamente como estaba. No se modifican sonidos en esta entrega.
