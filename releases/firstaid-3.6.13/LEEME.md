# First Aid 3.6.13 — alcohol de 15 usos

Reemplaza First Aid 3.6.12 por este JAR en servidor y clientes. Conserva todos sus cambios de HUD, vendas e higiene.

El alcohol médico tiene 15 usos, no se apila y muestra sus usos restantes en la descripción y el desgaste del objeto. Cada fabricación que usa alcohol descuenta uno: esterilizar una venda o preparar un lote de dos telas estériles. En el uso número 15 devuelve una botella de cristal vacía.

Las recetas son las de 3.6.12. No cambia los ingredientes ni consume alcohol al consultar el resultado: solo cuando se fabrica. Los componentes del objeto se conservan al devolver la botella parcialmente utilizada. Las botellas antiguas sin desgaste empiezan con 15 usos.

Verificado: compilación Java 21, ASM de tres clases, integridad del JAR y conservación de recursos ajenos al cambio. Prueba con objetos simulados del método del nuevo Item: 15 fabricaciones consecutivas, descuento exacto, botella vacía final, conservación de componentes, resto de una unidad para pilas antiguas y descripción 14/15 tras el primer uso. No se ha ejecutado Minecraft ni probado su mesa de fabricación real en este entorno.
