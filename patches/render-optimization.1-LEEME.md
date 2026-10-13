# Correcciones de renderizado, fragmentos y Gore 1.0.7

Cierra Minecraft antes de cambiar archivos. Conserva una copia de tus versiones actuales.

## Instalacion

En mods, reemplaza Quietly v12 por quietly-1_0_0-cooldown-v13-reflection-cache.jar y Moonlight 3.7.1 por moonlight-1.21.1-3.7.1-neoforge-postshader-noop-fix.1.jar. Retira los JAR anteriores para evitar duplicados. Las versiones internas de Quietly/Moonlight se conservan para sus dependencias. Conserva Supplementaries 3.9.9 y FracturePoint PT2: no necesitan reemplazo en esta entrega.

Reemplaza cualquier Zombie Gore antiguo por zomboid-zombie-gore-1.0.7-neoforge-1.21.1-weapon-trauma-crawl-fix.jar, en cliente y servidor. Esta entrega parte EXACTAMENTE del Gore 1.0.6 weapon-trauma adjuntado por el usuario, no del antiguo 1.0.3. Conserva las dependencias EMF/ETF y TaCZ instaladas.

Para la pose de arrastre usa el pack completo resourcepacks/TZP_1.20.1_2.7.zip del commit ce2f9c22c5f436b376a9ed8f5cff6b236cc3a117. Si ya instalaste ese ZIP corregido no necesitas volver a descargarlo. Conserva su prioridad en resourcepacks: un pack viejo por encima puede sobrescribir las animaciones. El bundle de esta entrega incluye los cuatro JAR; TZP se descarga por separado.

Agrega zomboid-cosmetic-budget-1.0.0-neoforge-1.21.1.jar al servidor o al cliente que aloja tu mundo individual/LAN. En servidor dedicado basta instalar el mod de presupuesto alli; no contiene renderizadores ni paquetes personalizados. Instalarlo tambien en los clientes es valido. Los otros JAR conservan sus lados/dependencias originales; manten las versiones coincidentes de Gore en ambos lados.

## Cambios y evidencia

Moonlight: su cadena compuesta intentaba reconstruirse al quitar un grupo ausente, incluso cuando otro efecto seguia activo. Supplementaries hace esa peticion repetidamente. Ahora ese caso devuelve la misma cadena antes de buscar Minecraft, copiar mapas o crear/redimensionar efectos. Las altas, bajas reales, el orden de grupos y el resto de la implementacion original se conservan. No se desactivan efectos visuales.

Quietly: las clases positivas y ausentes, metodos, constructores y campos publicos se resuelven una vez y se reutilizan. Su comprobacion de clases opcionales devuelve falso cuando falta un mod, sin generar excepciones repetidamente para cada entidad visible. Se conserva el criterio original de sobrecargas/primitivos. Los demas archivos del JAR son identicos al original, incluidos vehiculos, menus medicos e interacciones de almacenamiento.

En lo1HxP1YfN el hilo de renderizado muestrea EMF ~27.42% y borrado de framebuffers ~8.34%. El wrapper de FracturePoint incluye el renderizado de otros mods: su porcentaje inclusivo no demuestra que FracturePoint consuma todo ese tiempo. FracturePoint ya cachea la comprobacion de casco por tick. Sus buffers termicos contienen estado mutable; reutilizarlos entre entidades sin una comprobacion de su ciclo de vida arriesga errores visuales. Se conserva ese mod en esta entrega.

En 3A1vdEvS4N aparecen 6317 entidades mobdismemberment:gib. El mod de presupuesto limita exclusivamente esa ID a 256 fragmentos por dimension, y elimina los fragmentos que llevan 1200 ticks (un minuto a 20 TPS) registrados. Descarta primero los mas antiguos al superar el limite. NO elimina objetos minecraft:item, cadaveres, restos looteables ni zombis. Los fragmentos cosméticos descartados no se recuperan; es el cambio visual intencional para evitar su acumulacion. Se ejecuta en el hilo del servidor, sin escanear todas las entidades ni cargar chunks. Usa referencias debiles y una cola limitada por dimension. El registro del tipo se resuelve una vez por tipo. Al salir del mundo se limpia el estado.

Configuracion generada al arrancar el mundo: config/zomboid-cosmetic-budget.properties. enabled=false desactiva el presupuesto, maxPartsPerDimension ajusta 1..4096 (defecto 256), lifetimeTicks ajusta 20..72000 (defecto 1200). Reinicia el mundo despues de editar. Si los fragmentos existentes se cargan al abrir el mundo, entran en el mismo presupuesto. El temporizador se cuenta desde su registro en esta sesion; no modifica su NBT.

Gore 1.0.7: solo cambia Rules.crawling y Gore.dimensions, y agrega CrawlGeometry. Se conservan los demas metodos y archivos de 1.0.6. Ambas piernas perdidas activan arrastre; una sola conserva dimensiones erguidas. La caja de arrastre adulta es 1.8 x 0.82 bloques, ojos a 0.5, escalada para variantes pequeñas/grandes. El guard impide escalar dos veces cuando pasan los hooks de Entity y LivingEntity. El trauma de armas, quemaduras, heridas guardadas, reacciones melee y cache de reflexion de 1.0.6 se conservan. La caja mas ancha puede afectar el paso por huecos estrechos.

## Verificacion

Pruebas simuladas: 90006 comprobaciones de cache de reflexion (herencia, sobrecargas, null, estaticos, campos mutables, constructores y concurrencia); 10007 comprobaciones de la clase Moonlight parcheada (10000 eliminaciones de un grupo ausente sin asignaciones/redimensionados/cierres, agregar/quitar efectos reales y quitar el ultimo); 6321 comprobaciones del presupuesto (6317 fragmentos -> maximo 256 y expiracion); fixture de eventos de servidor que comprueba el filtro de tipo, la preservacion de loot/cadaveres/zombis, la expiracion y la opcion de desactivar; 65570 comprobaciones de estados/dimensiones de Gore y 7680 proyecciones geometricas de su pose TZP. Verificacion de bytecode y CRC de los JAR. Las clases de prueba/stubs NO se incluyen en los mods.

No se ejecuto Minecraft ni se midieron FPS posteriores. Estas correcciones eliminan operaciones redundantes demostradas y limitan la acumulacion visual; no permiten prometer cero tirones, ni eliminan todo el coste de animar cientos de zombis.

Prueba el mismo recorrido y dispara en condiciones comparables. Comprueba los efectos de cascos, menus de Quietly y F3+B en zombis sanos, con una pierna y sin ambas piernas. Para comparar rendimiento, captura otro perfil CLIENTE de Spark de 120 segundos, con los mismos ajustes de distancia, shaders y resourcepacks que antes.
