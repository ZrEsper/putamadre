# Gore 1.0.6 + TZP Gore 7: marcas por tipo de impacto

Sustituir Gore 1.0.5 por `mods/zomboid-zombie-gore-1.0.6-neoforge-1.21.1-weapon-trauma.jar` en cliente y servidor. Activar `TZP-1.21.1-Gore.7-weapon-trauma.zip` encima de TZP, Fresh y otros modelos de zombis, desactivando todos los paquetes Gore anteriores. Reiniciar el cliente después de cambiar el JAR. No instalar dos JAR Gore simultáneamente.

La clasificación pistol/shotgun/sniper ya existía para los umbrales; no diferenciaba la superficie que veía el jugador. Ahora un segundo entero de datos de entidad sincroniza tres máscaras de ocho zonas: balas, golpes contundentes y cortes. Se guardan en NBT y pueden coexistir varias heridas de tipos distintos en una zona. No se envían paquetes manuales ni se consulta el arma por frame.

- TACZ: el contexto real de la bala marca perforación, incluso si otro mod cambia la fuente de daño a melee. Conserva identificación de escopeta y sniper, sus umbrales y la salida de proyectil sniper.
- Melee: el nombre registrado de traducción del objeto identifica espadas, cuchillos, machetes, hojas, dagas, katanas, hachas y cuchillas como corte; el resto (incluidos bate, martillo y mano vacía) usa trauma contundente. Los objetos de nombres no reconocidos se tratan como contundentes. No infiere mecánicas de armas ajenas a TACZ a partir de su aspecto.
- Balas: perforación circular. Contundentes: lesión irregular ancha, músculo rojo y pequeños fragmentos claros de hueso. Filo: corte diagonal. Las heridas importantes siguen abriendo cráneo y torso o inutilizando extremidades según el daño acumulado.
- La ubicación identifica la región alcanzada: cabeza, mandíbula, brazo/pierna izquierdo o derecho y mitad del torso. Las marcas se muestran en puntos definidos de esa región del modelo, no exactamente en las coordenadas UV de cada impacto. No se generan agujeros geométricos arbitrarios en la malla.
- Mantiene la triple vida y las fracturas de 1.0.5. Conserva estados antiguos; donde no hay tipo registrado, la marca antigua sigue visible. El paquete usa la sintaxis con cero inicial corregida en Gore 6.

Validación: 32 comprobaciones de lógica con dobles de APIs, incluidos tipos, persistencia, variables EMF, disparos con fuente modificada y daños bloqueados; verificación ASM de 166 métodos y preservación de las clases originales ajenas al cambio. El paquete añade 46 superposiciones tipadas, preserva 3941 imágenes/sonidos y simula 4800 frames de 1451 líneas de animación con comprobación de que cada tipo muestra únicamente su marca correspondiente.

No hay cliente Minecraft en este entorno. Las comprobaciones no son una prueba visual o de FPS dentro del juego. EMF/ETF sigue requerido. No se publican los logs ni el perfil aportados por el usuario.
