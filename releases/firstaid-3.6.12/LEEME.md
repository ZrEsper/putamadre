# First Aid 3.6.12 — vendajes, HUD e higiene

Sustituye firstaid 3.6.11 por el JAR 3.6.12, sin dejar ambos. Usa la misma versión en servidor y clientes. Conserva los otros mods.

## Visualización
- La silueta del menú y las marcas comparten posición y escala; la selección usa esas mismas coordenadas.
- Heridas y vendas se anclan a cabeza, torso, brazos, piernas y pies, con recorte dentro de cada parte.
- Al vendar se oculta el dibujo de la lesión, sustituyéndolo por una banda ajustada al contorno. Al retirarla vuelve a mostrarse la lesión pendiente.
- Las partes vendadas siguen accesibles aunque hayan recuperado toda su vida, para poder quitar o cambiar la venda.
- En la lista se muestra el vendaje y su estado; los detalles de lesión cubierta se ocultan. Suciedad, irritación e infección mantienen sus avisos. Ocultar una herida no elimina su estado médico ni las balas alojadas.

## Actividad e higiene
Se conserva el envejecimiento base de vendajes de seis minutos. Correr, nadar, golpear o entrenar acelera su desgaste. Con actividad continua, una venda normal alcanza el umbral aproximadamente en un minuto y una estéril en dos; en reposo no se añade desgaste extra. Las pausas y las acciones breves modifican esos tiempos.

Una venda sucia sobre una herida activa representa irritación y pausa la recuperación clínica. La infección posterior es probabilística; se conserva la protección del antibiótico existente. Retira el vendaje, lava la tela mediante el sistema de agua existente y prepara/aplica material estéril. Cambiar una venda por otra normal ya no borra por sí solo una infección que existía antes de vendar.

La irritación es un estado derivado de herida activa + venda sucia; no añade una nueva enfermedad persistente independiente. Los demás medicamentos conservan sus funciones previas.

## Objetos y recetas sin forma
- Alcohol médico: botella de cristal + azúcar + trigo -> 1 alcohol.
- Tela estéril: 2 telas de First Aid + 1 alcohol -> 2 telas estériles.
- Venda estéril: 1 tela estéril + 1 hilo -> 1 venda estéril.
- Esterilizar venda existente: venda de First Aid + alcohol -> 1 venda estéril.

Alcohol es un reactivo de fabricación, no una bebida. La tela estéril y la venda estéril se aplican desde el tratamiento habitual o con su uso normal. Reutilizan los iconos base de tela/venda y se distinguen por su nombre. Traducciones español, inglés y alemán. Las recetas se incorporan al JAR y pueden aparecer en el libro de recetas.

## Comprobaciones
Compilación Java 21, verificación ASM de 13 clases, integridad del JAR y pruebas de lógica de marcas cubiertas/descubiertas, ocho posiciones anatómicas, tres escalas, conservación de infección al vendar, pausa por suciedad y desgaste de material estéril.

No se ejecutó Minecraft aquí. Falta comprobar visualmente el resultado y el ciclo completo de tratamiento en una partida real con el modpack. No incluye cambios de zombis, cámaras ni otros mods.
