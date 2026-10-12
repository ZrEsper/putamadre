# Rendimiento: Integration 1.7.6, Hordes panel 18, Mosslorn 11 y Valorant fix 2

Cierra Minecraft y el servidor. Guarda una copia del mundo. El ZIP principal contiene `mods`, `datapacks`, `resourcepack-fix` y esta guia.

En mods de cliente y servidor, reemplaza las versiones anteriores por:
- zomboid-integration-1.7.6-neoforge-1.21.1-async-archive-item-merge.jar (retira 1.7.5 y otras versiones de Integration).
- zomboid-hordes-1.8.3-neoforge-1.21.1-admin-panel.18-path-budget.jar (retira panel 17 y anteriores).
- valorant-gunpack-0.1.4-neoforge-1.21.1-block-data-fix.2.jar (retira block-data-fix.1 y el port anterior). Mueve fuera de `tacz` la carpeta exportada `valorant_neoforge_1211` para regenerar sus archivos actualizados. Conserva GucciVuitton y Valorant Knife Pack.

En `saves/TU_MUNDO/datapacks`, reemplaza Mosslorn 10 por `mosslorn-expanded.11-paced-scans.zip`; en servidor es `world/datapacks`. No mantengas dos versiones activas. Al iniciar/reload se mantienen el registro de cofres, los ajustes del mundo y las tablas de loot. Como en la version anterior, load reinicia su cola de procesamiento temporal; el registro permanente se conserva.

Si aun no aplicaste el arreglo visual de TZP, extrae el ZIP de resourcepack-fix dentro de resourcepacks y ejecuta Aplicar-TZP.bat con Minecraft cerrado. Hace un respaldo y conserva el nombre del ZIP TZP y su prioridad. No actives el ZIP del parche como resourcepack. Esta correccion usa cuerpos completos de Fresh Animations; las amputaciones/quemaduras visuales personalizadas no se representan, aunque el mod conserva sus efectos de juego.

## Cambios de rendimiento

Integration: las lecturas de NBT, los renombrados de archivos al despertar y los borrados durante el juego se ejecutan en una unica cola de IO. Solo se consulta Future.get cuando el trabajo esta terminado. Hasta ocho lecturas pendientes por dimension y dos entidades restauradas por tick; se limita tambien el trabajo pendiente de IO que admiten los ciclos de guardado/restauracion. Las entidades se instancian y modifican en el hilo del servidor. El NBT se escribe y confirma antes de descartar la entidad distante. Si falla un renombrado, se conserva la entidad viva y se pausa el archivado; el respaldo se limpia correctamente si esa entidad muere. La lectura inicial del indice al abrir una dimension y la espera al cerrar el servidor siguen siendo sincronas. Se reutiliza la lista de posiciones de jugadores dentro del mismo tick. Se mantiene el radio, la vista por zoom, los UUID, la salud, el inventario, el movimiento virtual al ruido y su persistencia.

Objetos tirados: se amplian de 0.5 a 1.5 bloques los margenes horizontales de la busqueda nativa de apilado. Minecraft sigue decidiendo compatibilidad de componentes, propietarios y maximo de pila. No se borra loot ni se mezclan armas diferentes. Es un hook opcional: si otro mod ya sustituyo la constante, puede no aplicarse. Objetos distintos o muy separados no se pueden reducir con esta medida.

Hordes: se reutilizan las rutas de investigacion del ruido mientras siguen activas, con objetivos a menos de cuatro bloques del anterior, durante un maximo de 80 ticks. Las rutas fallidas esperan 30 ticks antes de repetir. Se admiten hasta cuatro solicitudes nuevas de ruta por dimension y tick; las restantes vuelven a intentarse en los ciclos existentes. Solo se cambia la investigacion del ruido; el combate nativo con un objetivo vivo no pasa por este filtro. Se conserva el alcance del oido y el comportamiento de silenciadores.

Mosslorn: los selectores de anclas solo se ejecutan durante un escaneo activo; por defecto, el trabajo se distribuye cada dos ticks. Si el presupuesto era el valor predeterminado 128, se reduce una vez a 64, conservando otros valores personalizados. Esto reduce aproximadamente a una cuarta parte las filas examinadas por tick con esos valores; completar un chunk tarda mas. `segundo` pasa de diez a veinte ticks. Las 64 condiciones del indice forzado solo se evaluan cuando se esta construyendo ese indice. El registro cercano forzado se consulta cada veinte ticks. Las tablas, los tags y la cantidad de loot permanecen identicos. Puedes ajustar el ritmo con `/scoreboard players set #scan_every ml_config 1` (cada tick) o 2 (cada dos), y `/scoreboard players set #scan_budget ml_config 128` para recuperar el presupuesto anterior.

Valorant: se corrige el convertidor de los iconos de las mesas para preservar las cadenas de ID, nombres y filtros. El ultimo log mostraba `TabConfig must have an id`; diez pestañas y dos filtros quedan comprobados. Se conservan las estadisticas, los sonidos y las animaciones.

## Validacion y limites

Pruebas simuladas pasadas: guardado antes de retirada; restauracion UUID/salud/inventario; reinicio; entidades muertas; fallo de escritura; fallo de renombrado con limpieza de respaldo; 300 entidades con iterador mutable; vista limitada a 64 entidades con IA dormida y 65 restauradas cerca; viaje virtual y silenciadores; 263 comprobaciones de rutas, incluido reparto de 256 solicitudes; fixtures que rechazan IO de ArchiveFiles en el hilo principal. Las clases se compilaron con stubs de Minecraft; estos no se incluyen en los mods. Verificacion de bytecode: Integration 87 clases/444 metodos, Hordes 85/702; integridad ZIP, referencias de funciones y preservacion de las tablas del datapack.

No se ejecuto Minecraft y no hay medicion de rendimiento posterior a esta entrega. El perfil original atribuia ~17.36% del tiempo muestreado al archivado, ~13.13% a funciones y ~8.95% a creacion de rutas; las categorias no equivalen a una ganancia de FPS garantizada. Los avisos de Lucky's Wardrobe y Self Expression corresponden a renderizado y no quedan resueltos por estas optimizaciones del servidor. No se dispone aqui del JAR Lucky's Wardrobe.

Despues de actualizar, captura `/spark profiler start --timeout 60` en la misma zona para comparar. Si siguen bajos los FPS, usa tambien `/sparkc profiler start --timeout 60` para separar el coste del cliente.
