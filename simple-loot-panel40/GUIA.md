# Panel sencillo .40 · Barriles y cadáveres

## Instalar

Cierra Minecraft y el servidor. Quita Hordes .39 y pon el JAR .40 de este ZIP en la misma carpeta `mods`. Hazlo en todos los clientes y en el servidor. No dejes dos Hordes instalados.

Conserva los demás mods. Este reemplazo se construye sobre el .39 adjunto: mantiene los controles de velocidad de rotura y screamer, así como sus demás cambios. Para los cadáveres necesita el sistema de Remains compatible que ya usabas con .39; no lo sustituye.

## Usarlo

Abre `/zhorda panel`. Ahora aparece una sola pantalla sencilla.

1. Elige directamente Desolado, Medio, Normal, Sobrado o Inicio. Arriba verás el perfil seleccionado, no el siguiente que tocaría preparar.

2. Ajusta, si quieres, los dos bloques: Barriles/cofres y Cadáveres. Cada bloque tiene cinco campos.

3. Pulsa `Guardar cambios`. Espera la confirmación del servidor. Al cerrar y volver a abrir deben aparecer los valores guardados. Un valor incorrecto se marca como error; no aparece como guardado.

`Sin guardar` significa que todavía no se han enviado esos cambios. `Guardando` significa que se espera al servidor. `Guardado en este mundo` confirma los valores de la respuesta del servidor. Editar un perfil lo convierte en Personalizado; no se vuelve a imponer el preparado sobre tus cambios.

## Los cinco campos de cada bloque

| Campo | Qué hace |
| --- | --- |
| Cantidad | Media de pilas de suministros NUEVOS. No depende de cuántas pilas hubiera elegido el generador original. 0 no genera loot adicional; 0,3 produce suministros aproximadamente en tres de cada diez inventarios. El equipo extra se calcula aparte. |
| Armas (%) | Una tirada de arma o melee por inventario que genere suministros. 0 nunca; 100 siempre que exista un arma compatible. 7,69 equivale a uno de cada trece en promedio. |
| Munición (%) | Probabilidad de munición compatible si sale un arma de fuego. 0 no añade balas; 100 añade una pila compatible cuando hay arma de fuego. Un melee no recibe balas. |
| Ropa (%) | Probabilidad de una pieza militar registrada de FracturePoint. 0 no añade piezas aleatorias. No quita el equipo real de un militar. |
| Medicina (%) | Probabilidad de una pila pequeña de medicina extra. 0 desactiva la medicina aleatoria. No borra las vendas garantizadas de militares ni pertenencias reales. |

Los números de porcentaje se aplican de forma directa: no son pesos que luego se normalizan entre todas las categorías. No se vuelve a pasar el resultado por otro filtro de escasez que reduzca otra vez esas probabilidades.

Las pilas se mantienen pequeñas y se conserva el límite de una herramienta generada. Si una tabla de herramientas contiene un hacha clasificada como melee, no puede introducirla por detrás cuando Armas está en 0. Los objetos de medicina tampoco pueden saltarse Medicina 0 mediante el catálogo de profesión.

## Perfiles que cambian la generación

| Perfil | Cantidad base | Armas y ropa | Munición junto al arma | Medicina |
| --- | --- | --- | --- | --- |
| Desolado | 0,3 | 2% cada una | 10% | 3% |
| Medio | 2 | 5% cada una | 20% | 12% |
| Normal | 4 | 7,69% cada una | 35% | 25% |
| Sobrado | 8 | 15% cada una | 55% | 45% |
| Inicio | 12 | 25% cada una | 80% | 70% |

El preparado pone esos valores en ambos bloques y conserva el comportamiento de perfil de dificultad del panel anterior: también ajusta población, IA y dificultad según ese preparado. Puedes modificarlo antes de guardar. Los perfiles no ejecutan por sí mismos acciones de limpieza o borrado.

Los porcentajes se calculan cuando se genera un inventario con suministros. En Desolado, muchos inventarios estarán vacíos: el 2% de armas se aplica a los que pasan la generación, no obliga a dar armas en los vacíos.

En la prueba de 500 inventarios por perfil, usando catálogos simulados, la media de entradas resultó: Desolado 0,32; Medio 2,13; Normal 3,93; Sobrado 7,04; Inicio 10,31. No son promesas de una cantidad exacta por barril: verifican que el mismo generador produce diferencias claras entre perfiles.

## Qué cambia y qué se conserva

Se cambia el loot NUEVO de cofres y barriles que usan la generación de tablas de Minecraft y el loot aleatorio de cadáveres civiles compatibles. Abrir tu propio barril vacío de almacenamiento no lo convierte automáticamente en una máquina de generar objetos.

Los inventarios ya generados no se vacían ni se reescriben al guardar ajustes. Para comprobar un cambio, usa un contenedor natural que todavía no haya generado loot o un cadáver nuevo. No se elimina el equipo que llevaba realmente el muerto.

Los militares, NPC y mutantes conservan sus rutas especiales: equipo militar y vendas, bolsa real de NPC y restos de mutantes. Los porcentajes de loot civil no les quitan esas pertenencias ni inventan equipo que no llevaban.

## Avanzado

El botón Avanzado abre las opciones anteriores: profesiones, población, IA, medicina, limpieza y demás ajustes detallados. El botón Sencillo vuelve a los dos bloques principales.

Las profesiones siguen participando en el catálogo de suministros de los cadáveres, su cantidad y probabilidad. Los controles principales siguen mandando sobre las armas, ropa, munición y medicina extra: una profesión no permite saltarse un 0%.

Los pesos de comida, herramientas y materiales eligen la mezcla de suministros. El multiplicador avanzado de cantidad se aplica a la cantidad base del panel sencillo; normalmente se deja en 1. La cantidad profesional modifica los cadáveres con profesión reconocida o asignada. Si una categoría queda excluida por tus controles, se respetan esos límites.

Los antiguos controles de escasez de arma/ropa/munición de cadáveres están conectados a los mismos valores del panel sencillo. La vista de ejemplo de cofres/cadáveres ahora muestra los ajustes efectivos y deja de simular diez pilas con el algoritmo antiguo. La vista de drops de suelo conserva su funcionamiento anterior.

## Comprobaciones

70 comprobaciones de comportamiento con APIs simuladas: los diez campos en 0 y 100; generación de barriles incluso con resultado original vacío; cofre y cadáver conectados al generador; porcentajes sin escapes desde otros catálogos; perfiles diferentes; catálogos de profesión y cantidades; conservación de militares, NPC, mutantes y equipo real.

Se ejecutó el guardado real de WorldTuning en un archivo de prueba y su carga posterior: los valores y la cantidad se conservan y reaparecen al reabrir el panel simulado. Se comprueban revisiones antiguas para impedir sobrescribir cambios de otro administrador.

También se verifican el bytecode, hashes, integridad del JAR y conservación de recursos y clases de combate/spawn ajenas al cambio. No se ha ejecutado Minecraft: quedan por verificar en tu instalación la apariencia real, entrada de teclado, paquetes cliente-servidor y coexistencia con todos tus mods. El informe `validation.json` distingue esa limitación de las pruebas completadas.
