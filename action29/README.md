# Global Melee v29: carrera, render real de equipo y patada a puertas

[Instalación](GUIA.txt). Reemplaza únicamente Global Melee en clientes y servidor (`boxing29`). Conserva el TaCZ .19 enviado.

## Ruta de render corregida

Los videos mostraban manos estáticas al correr y ausencia del movimiento adicional del arma. El wrapper original de TaCZ intentaba inyectar en `GunItemRendererWrapper.renderFirstPerson`, pero ese método pertenece a `AnimateGeoItemRenderer`; el renderer concreto solo declara `renderFirstPersonInner`. La inyección opcional con `require=0` no entraba allí. V28 añadía el efecto a un helper alcanzado desde esa inyección ausente.

`BodycamWeapon27`, conservando su nombre interno para sustituirlo, ahora apunta directamente al método **declarado** `GunItemRendererWrapper.renderFirstPersonInner`. Se comprueba su descriptor contra la clase real suministrada. `Equipment29` rodea el render con el wrapper nativo y la inercia adicional, con cierres en orden inverso. El respaldo `TaczFirstPersonModelPoseHooks` cede ante el wrapper, por lo que la pose levantada ya no depende de una transformación aplicada en las coordenadas interiores del modelo. El movimiento adicional se atenúa en high-ready, patada y bloqueo ante pared; al completar ADS desaparece.

`Run29` añade fotogramas de brazos alternos a las manos propias de `First24`. Mantiene un controlador separado de la cámara y aplica la locomoción a los brazos del modelo en tercera persona e isométrica. Respeta la mano secundaria y cede a combate, patada y parkour. No altera posición, velocidad, daño ni puntería del jugador.

La patada a puertas sin arma no dibujaba el pie porque el render de manos propias cancela el método exterior y omite `RenderHandEvent`. `DoorFoot29` lee el progreso privado de la patada frontal de TaCZ y dibuja la pierna antes del flush del render de manos. Con arma permanece la ruta de pierna nativa; la lógica y daño de puertas no cambian.

## Validar

```sh
python action29/build.py
python action29/test.py
```

Los hashes de `base/render19-probes.jar` y de las bases previas están fijados. El archivo de comprobación contiene clases originales de TaCZ para inspección, no se empaqueta en el mod.

`Entry29` comprueba la cadena desde el método público del renderer padre hasta el método interior declarado por el hijo y valida los dos injectores nuevos. Contra v28 falla al comprobar la conexión directa. Comprueba también el progreso de patada y la cesión del respaldo del modelo al wrapper.

Las pruebas de ejecución usan APIs simuladas para brazos alternos, transición de carrera, prioridad de golpes, isométrica, balance de scopes nativo/equipo, high-ready y geometría de patada frontal. Las pruebas de clases originales comprueban estructura y descriptores; no ejecutan el motor gráfico de Minecraft. Continúan las regresiones de combate, túnel de un bloque, slabs, servidor integrado y EMF.

No se ha ejecutado Minecraft completo. El resultado visual y los conflictos específicos del pack requieren prueba dentro del juego.
