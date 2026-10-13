# Gore 1.0.7 + TZP Gore 8: lesiones locales en la piel

Reemplazar el JAR Gore anterior por `mods/zomboid-zombie-gore-1.0.7-neoforge-1.21.1-local-surfaces.jar` en cliente y servidor. Activar `TZP-1.21.1-Gore.8-local-blended-surfaces.zip` arriba de TZP, Fresh y otros modelos de zombis. Desactivar Gore 4/5/6/7 y reiniciar el juego. No instalar dos JAR Gore a la vez.

## Correcciones

- Izquierda/derecha se calcula con `yBodyRot`, la orientación del torso, en lugar del giro independiente de la cabeza. La marca de headshot de TACZ no fuerza una herida en la cabeza cuando el punto del impacto está por debajo de esa región.
- El último tipo de impacto reemplaza la marca anterior de esa zona. Un bate no reutiliza una perforación antigua. Las lesiones de saves antiguos sin tipo usan textura contundente, nunca una perforación como fallback.
- Se sincroniza delante/detrás por zona mediante los ocho bits restantes del entero de superficies; las marcas no se pintan en el frente si el impacto queda en la cara trasera.
- Al arrastrarse la región se calcula sobre el eje longitudinal del cuerpo, evitando normalizar todos los impactos por la altura reducida del crawler. Es una aproximación de regiones del modelo base, no detección exacta de la malla animada.
- El paquete parte otra vez de los modelos originales de Fresh/TZP. Conserva todas las cajas y pivotes originales. Quita el cráneo independiente, órganos cúbicos, muñones y mitades de cuerpo sustituidas: carne, sangre y hueso son superficies transparentes adheridas a la caja de la región afectada. Mantiene el cuerpo completo visualmente, conforme a la petición de cambiar solo la textura.
- Las nuevas texturas usan rojos oscuros desaturados, bordes irregulares y transparencia parcial. La superficie fuera de la lesión es transparente; el skin original permanece debajo. No hay skin quemado inicial. Las heridas graves amplían el área y dejan hueso dibujado sobre la piel, no un elemento flotante.
- La postura de arrastre rota el esqueleto completo desde `root`, con los puntos de unión de cabeza, torso y extremidades compartidos. Elimina los desplazamientos independientes de la versión anterior. El muñeco y los ojos siguen unidos a su cabeza original.

Se mantienen la triple vida, reducción de daño, fracturas y desaceleración. Los bits de lesión guardados siguen funcionando. Una zona conserva el daño acumulado pero muestra el tipo de su último impacto, no varias marcas mezcladas del mismo lugar.

## Validación y límites

`python build.py`: Java 21, verificación ASM de 167 métodos, 43 comprobaciones de lógica con dobles de API. Incluye impactos con torso girado, headshot fuera de cabeza, regiones del crawler, cambio bala→bate y frente→espalda, guardado/sincronización y umbrales anteriores. Se preservan las entradas nativas ajenas a Gore/Rules/Events/metadatos.

`python pack.py /ruta/TZP_1.20.1_2.7.zip`: utiliza el TZP original conectado a Fresh aportado; 320 comprobaciones de geometría original y capas pegadas, 3938 imágenes/sonidos originales byte a byte iguales. No cambia esos skins.

`python check_animations.py`: simulación de 1805 líneas y diez estados para los cuatro modelos. Comprueba que un impacto aislado en brazo solo encienda la textura de ese brazo, distingue delantero/trasero y comprueba los puntos de unión usados al arrastrarse. Mantiene la restricción de sintaxis estricta de EMF.

No hay cliente Minecraft en este entorno. Estas comprobaciones no validan la apariencia final, ni garantizan que coincida exactamente con brazos y piernas movidos por animaciones externas. Se identifica una de ocho regiones; no se dibuja una herida en la coordenada UV exacta del impacto ni se recorta la malla arbitrariamente. Un escopetazo con varios perdigones puede dañar varias regiones si cada uno realmente las alcanza. Los modelos particulares de infectados especiales no están adaptados.
