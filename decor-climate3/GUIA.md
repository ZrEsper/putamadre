# Muebles, aire acondicionado y tablones

## Instalar

1. Cierra Minecraft y el servidor. Guarda una copia del mundo.
2. Quita las versiones anteriores de **Doomsday Decoration**, **Homeostatic**, **Tough As Nails** y **Zomboid Hordes** de `mods`.
3. Pon los cuatro JAR de este paquete en `mods`, tanto en clientes como en servidor. No dejes duplicados. Conserva los demás mods.

La electricidad usa el generador de **Zomboid Survival** que ya tienes. Para Doomsday Storage necesitas Survival **1.1.0 con el parche de compatibilidad Doomsday .1** publicado anteriormente. No cambies el motor FF ni sus datos. Este paquete conserva las versiones internas de los mods para respetar sus dependencias; los nombres de los archivos identifican la revisión.

## Escritorios, lavadoras y otros almacenes

Los escritorios, escritorios con divisores, pupitre, lavadora, lockers, barril y papeleras ahora tienen inventario. Se suman a los muebles que ya eran contenedores. Los lockers tienen 54 espacios; los muebles añadidos restantes tienen 27.

**Clic derecho** para abrir. Los muebles antiguos sin inventario se reparan al interactuar o al acercarse a cinco bloques; la revisión se hace cada dos segundos. Los inventarios que ya existen se conservan. Jade puede mostrar el nombre de un bloque aunque sea decorativo: antes estos muebles estaban registrados sin contenedor.

Las partes de un mueble grande siguen abriendo su bloque principal. Si un mueble antiguo invade una pared, recolócalo dejando libre todo su volumen; la reparación no sobrescribe la pared.

## Aire acondicionado

Funcionan los seis modelos: `aircondition`, `aircondition_2`, `airconditioning`, `airconditioning_2`, `centralairconditioning` y `centralairconditioning_2`. Cada modelo tiene el mismo alcance y consumo.

- Fabrica el modelo base con cinco lingotes de hierro, un polvo de redstone, dos lingotes de cobre y un bloque de hielo compacto. Consulta la colocación en JEI.
- Usa el **cortapiedras** para convertir el modelo base en cualquiera de los otros cinco, uno por uno.
- Pon un generador de Zomboid Survival con combustible a **25 bloques o menos** del bloque principal del aire. No hace falta cable.
- Cada aire funcionando consume **400 FE por segundo**, retirados de la reserva real del generador. Eso obliga al generador a reponer energía gastando su combustible. Más aires consumen más. No usa hielo como combustible alternativo.
- Necesita combustible ardiendo o cargado en su inventario, y energía suficiente. Sin ellos, se detienen sus efectos y el sonido; vuelve a funcionar cuando se recupera el suministro. La comprobación tarda como máximo aproximadamente un segundo.
- **Clic derecho:** muestra si funciona, está apagado o le falta energía. **Mayús + clic:** encender/apagar. El estado apagado se guarda al salir del mundo.
- Tiene sonido de ventilador mientras funciona y clic al cambiar el interruptor. Respeta el volumen de bloques del juego.

En un radio esférico de **15 bloques**:

- Normaliza la temperatura corporal del jugador en Homeostatic y Tough As Nails. Homeostatic calcula un ambiente local de 22 °C y temperatura corporal normal; TAN queda en nivel neutral. Esto actúa sobre la temperatura, no cura heridas ni modifica hidratación, infección o radiación.
- Los cultivos que utilizan `CropBlock` —incluidos trigo, zanahorias, patatas y remolacha— reciben una probabilidad del **35% de avanzar una etapa adicional después de un crecimiento normal exitoso**. Es un bono al ritmo de crecimiento, no una maduración inmediata. Conservan sus requisitos normales de crecimiento. No aplica a árboles, caña, cactus ni plantas con sistemas propios.
- Solapar varios aires **no acumula** el bono ni aumenta el alcance. Cada aire encendido sigue gastando su propia energía.

Los efectos sólo existen con el aire y el generador cargados. Salir del radio devuelve el cálculo habitual de temperatura; no cambia el clima del bioma ni mantiene cargados los chunks.

## Planks / tablones para tapiar

Pon **3 tablones de madera** en una fila de la mesa de trabajo para obtener **6 `planking_0`**. Se acepta cualquier madera del tag vanilla de tablones. Los demás diseños `planking_*` se obtienen en el **cortapiedras**, un tablón por un diseño, para poder elegirlos sin recetas idénticas que se pisen.

Los tablones decorativos tienen dureza 6 en lugar de 1. Un tablón colocado en una de las seis casillas contiguas a una ventana o vidrio hace que esa ventana tarde **tres veces más** en romperse. Se aplica a la minería del jugador y a las dos rutas de rotura de Hordes. No se multiplica por añadir más tablones; al retirarlos, la ventana recupera su resistencia habitual. Los zombis deben tener permiso de romper bloques y `mobGriefing` activo.

Hordes .42 conserva el panel sencillo, la corrección de paredes y los ajustes de estadísticas del screamer de .41. Conserva el addon Screamer Sprint .2 que ya usas.

## Verificación y prueba en tu mundo

Pasaron 297 comprobaciones con APIs simuladas, incluidas 20.000 tiradas de crecimiento para comparar un aire con dos solapados. Se verificaron los puntos de conexión del bytecode, las recetas, los JSON y el contenido de los cuatro JAR. **No se ha ejecutado Minecraft en este entorno.**

Prueba en una copia del mundo: abre un escritorio y una lavadora antiguos, guarda objetos y vuelve a entrar; enciende un aire con generador, apágalo y quítale combustible; comprueba la temperatura dentro/fuera del radio; finalmente prueba una ventana sola y la misma ventana con un tablón contiguo. Si algo falla, envía el `latest.log` generado después de realizar esa prueba y el nombre del bloque que no responde.
